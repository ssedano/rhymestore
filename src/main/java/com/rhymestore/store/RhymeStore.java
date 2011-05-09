/**
 * Copyright (c) 2010 Enric Ruiz, Ignasi Barrera
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.rhymestore.store;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.rhymestore.config.Configuration;
import com.rhymestore.lang.StressType;
import com.rhymestore.lang.WordParser;
import com.rhymestore.lang.WordParserFactory;
import com.rhymestore.lang.WordUtils;

/**
 * Manages the Redis database to store and search rhymes.
 * 
 * @author Enric Ruiz
 * @see Keymaker
 * @see Jedis
 * @see WordParser
 */
public class RhymeStore
{
	/** The logger. */
	private static final Logger LOGGER = LoggerFactory
	.getLogger(RhymeStore.class);

	/** The key used to store the next id value. */
	private static final String NEXT_ID_KEY = "next.id";

	/** Redis namespace for sentences. */
	private final Keymaker sentencens = new Keymaker("sentence");

	/** Redis namespace for index. */
	private final Keymaker indexns = new Keymaker("index");

	/** The character encoding to use. */
	private final String encoding = "UTF-8";

	/** The singleton instance of the store. */
	private static RhymeStore instance;

	/**
	 * Gets the singleton instance of the store.
	 * 
	 * @return The singleton instance of the store.
	 */
	public static RhymeStore getInstance()
	{
		if (RhymeStore.instance == null)
		{
			RhymeStore.instance = new RhymeStore();
		}

		return RhymeStore.instance;
	}

	/** Parses the words to get the part used to rhyme. */
	private final WordParser wordParser;

	/** The Redis database API. */
	protected final Jedis redis;

	/**
	 * Creates a new <code>RhymeStore</code> connecting to the configured Redis
	 * database.
	 */
	protected RhymeStore()
	{
		String host = Configuration
		.getRequiredConfigValue(Configuration.REDIS_HOST_PROPERTY);
		String port = Configuration
		.getRequiredConfigValue(Configuration.REDIS_PORT_PROPERTY);

		this.redis = new Jedis(host, Integer.valueOf(port));
		this.wordParser = WordParserFactory.getWordParser();
	}

	/**
	 * Adds the given rhyme to the Redis database.
	 * 
	 * @param sentence The rhyme to add.
	 * @throws IOException If an error occurs while adding the rhyme.
	 */
	public void add(final String sentence) throws IOException
	{
		String word = WordUtils.getLastWord(sentence);

		if (word.isEmpty())
		{
			return;
		}

		// Get the rhyme and type (and check that the word is valid before
		// adding)
		String rhyme = this.normalizeString(this.wordParser
				.phoneticRhymePart(word));
		StressType type = this.wordParser.stressType(word);

		this.connect();

		String sentenceKey = this.getUniqueId(this.sentencens,
				this.normalizeString(sentence));
		sentenceKey = this.sentencens.build(sentenceKey).toString();

		if (this.redis.exists(sentenceKey) == 1)
		{
			this.disconnect();
			return;
		}

		// Insert sentence
		this.redis.set(sentenceKey, URLEncoder.encode(sentence, this.encoding));

		// Index sentence
		String indexKey = this.getUniqueId(this.indexns,
				this.buildUniqueToken(rhyme, type));
		indexKey = this.indexns.build(indexKey).toString();

		this.redis.sadd(indexKey, sentenceKey);

		this.disconnect();

		RhymeStore.LOGGER.info("Added rhyme: {}", sentence);
	}

	/**
	 * Build a unique token for the given rhyme to be used to index it.
	 * 
	 * @param rhyme The rhyme part of the sentence.
	 * @param type The stress type of the rhyme.
	 * @return The unique token for the rhyme.
	 */
	private String buildUniqueToken(final String rhyme, final StressType type)
	{
		return this.sum(type.name().concat(rhyme));
	}

	/**
	 * Connects to the Redis database.
	 * 
	 * @throws UnknownHostException If the target host does not respond.
	 * @throws IOException If an error occurs while connecting.
	 */
	protected void connect() throws UnknownHostException, IOException
	{
		if (!this.redis.isConnected())
		{
			this.redis.connect();
		}
	}

	/**
	 * Deletes the given rhyme from the Redis database.
	 * 
	 * @param sentence The rhyme to delete.
	 * @throws IOException If an error occurs while deleting the rhyme.
	 */
	public void delete(final String sentence) throws IOException
	{
		String word = WordUtils.getLastWord(sentence);

		if (word.isEmpty())
		{
			return;
		}

		String rhyme = this.normalizeString(this.wordParser
				.phoneticRhymePart(word));
		StressType type = this.wordParser.stressType(word);

		this.connect();

		String sentenceKey = this.getUniqueIdKey(this.sentencens,
				this.normalizeString(sentence));

		if (this.redis.exists(sentenceKey) == 0)
		{
			this.disconnect();
			throw new IOException("The element to remove does not exist.");
		}

		String indexKey = this.getUniqueIdKey(this.indexns,
				this.buildUniqueToken(rhyme, type));
		String sentenceId = this.redis.get(sentenceKey);
		sentenceId = this.sentencens.build(sentenceId).toString();

		// Remove the index
		if (this.redis.exists(indexKey) == 1)
		{
			String indexId = this.redis.get(indexKey);
			indexId = this.indexns.build(indexId).toString();

			// Remove the sentence from the index
			if (this.redis.exists(indexId) == 1)
			{
				this.redis.srem(indexId, sentenceId);
			}

			// Remove the index if empty
			if (this.redis.smembers(indexId).isEmpty())
			{
				this.redis.del(indexId, indexKey);
			}
		}

		// Remove the key
		this.redis.del(sentenceId, sentenceKey);

		this.disconnect();

		RhymeStore.LOGGER.info("Deleted rhyme: {}", sentence);
	}

	/**
	 * Disconnects from the Redis database.
	 * 
	 * @throws IOException If an error occurs while disconnecting.
	 */
	protected void disconnect() throws IOException
	{
		if (this.redis.isConnected())
		{
			this.redis.disconnect();
		}
	}

	/**
	 * Gets all the stored rhymes.
	 * 
	 * @return A <code>Set</code> with all the stored rhymes.
	 * @throws IOException If the rhymes cannot be obtained.
	 */
	public Set<String> findAll() throws IOException
	{
		Set<String> rhymes = new HashSet<String>();

		this.connect();

		String lastId = this.getLastId(this.sentencens);

		if (lastId != null)
		{
			Integer n = Integer.parseInt(this.getLastId(this.sentencens));

			for (int i = 1; i <= n; i++)
			{
				String id = this.sentencens.build(String.valueOf(i)).toString();

				if (this.redis.exists(id) == 1)
				{
					rhymes.add(URLDecoder.decode(this.redis.get(id),
							this.encoding));
				}
			}
		}

		this.disconnect();

		return rhymes;
	}

	/**
	 * Get the last used id in the given namespace.
	 * 
	 * @param ns The namespace.
	 * @return The last used id in the given namespace.
	 */
	private String getLastId(final Keymaker ns)
	{
		return this.redis.get(ns.build(RhymeStore.NEXT_ID_KEY).toString());
	}

	/**
	 * Gets a rhyme for the given sentence.
	 * 
	 * @param sentence The sentence to rhyme.
	 * @return The rhyme.
	 */
	public String getRhyme(final String sentence) throws IOException
	{
		String lastWord = WordUtils.getLastWord(sentence);

		String rhymepart = this.wordParser.phoneticRhymePart(lastWord);
		StressType type = this.wordParser.stressType(lastWord);

		RhymeStore.LOGGER.debug("Finding rhymes for {}", sentence);

		this.connect();

		Set<String> rhymes = this.search(rhymepart, type);

		this.disconnect();

		if (rhymes.isEmpty())
		{
			// If no rhyme is found, return the default rhyme
			return this.wordParser.getDefaultRhyme();
		}
		else
		{
			// Otherwise, return a random rhyme
			List<String> rhymeList = new ArrayList<String>(rhymes);

			Random random = new Random(System.currentTimeMillis());
			int index = random.nextInt(rhymeList.size());

			return rhymeList.get(index);
		}
	}

	/**
	 * Get a unique id id for the given token.
	 * 
	 * @param ns The namespace of the id.
	 * @param token The token which id is requested.
	 * @return The id for the given token.
	 */
	private String getUniqueId(final Keymaker ns, final String token)
	{
		String key = this.getUniqueIdKey(ns, token);
		String id = this.redis.get(key);

		if (id != null)
		{
			return id;
		}

		Integer next = this.redis.incr(ns.build(RhymeStore.NEXT_ID_KEY)
				.toString());
		id = next.toString();

		if (this.redis.setnx(key, id) == 0)
		{
			id = this.redis.get(key);
		}

		return id;
	}

	/**
	 * Get the key of the id for the given token.
	 * 
	 * @param ns The namespace of the key.
	 * @param token The token which key is requested.
	 * @return The key for the given token.
	 */
	private String getUniqueIdKey(final Keymaker ns, final String token)
	{
		String md = this.sum(token);
		return ns.build(md, "id").toString();
	}

	/**
	 * Normalizes the given string.
	 * 
	 * @param value The string to be normalized.
	 * @return The normalized string.
	 */
	private String normalizeString(final String value)
	{
		// To lower case
		String token = value.toLowerCase();

		// Remove diacritics
		token = Normalizer.normalize(token, Form.NFD);
		token = token.replaceAll("[^\\p{ASCII}]", "");

		// Remove non alphanumeric characters
		token = token.replaceAll("[^a-zA-Z0-9]", "");

		return token;
	}

	/**
	 * Search for rhymes for the given sentence.
	 * 
	 * @param rhyme The rhyme to search.
	 * @param type The <code>StressType</code> of the rhyme to search.
	 * @return A <code>Set</code> of rhymes for the given sentence.
	 * @throws IOException If an error occurs while searching for the rhymes.
	 */
	private Set<String> search(final String rhyme, final StressType type)
	throws IOException
	{
		Set<String> rhymes = new HashSet<String>();
		String norm = this.normalizeString(rhyme);

		String indexKey = this.getUniqueIdKey(this.indexns,
				this.buildUniqueToken(norm, type));

		if (this.redis.exists(indexKey) == 1)
		{
			String indexId = this.redis.get(indexKey);
			indexId = this.indexns.build(indexId).toString();

			if (this.redis.exists(indexId) == 1)
			{
				for (String sentenceKey : this.redis.smembers(indexId))
				{
					if (this.redis.exists(sentenceKey) == 1)
					{
						rhymes.add(URLDecoder.decode(
								this.redis.get(sentenceKey), this.encoding));
					}
				}
			}
		}

		return rhymes;
	}

	/**
	 * Makes a md5 sum of the given text.
	 * 
	 * @param value The text to sum.
	 * @return The md5 sum of the given text.
	 */
	private String sum(final String value)
	{
		return DigestUtils.md5Hex(value.getBytes());
	}

}
