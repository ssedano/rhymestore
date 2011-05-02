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
public class RhymeStore {
	/** The logger. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RhymeStore.class);

	/** The Redis database API. */
	protected final Jedis redis;

	/** Redis namespace for sentences. */
	private final Keymaker sentencens = new Keymaker("sentence");

	/** Redis namespace for index. */
	private final Keymaker indexns = new Keymaker("index");

	/** The character encoding to use. */
	private final String encoding = "UTF-8";

	/** The singleton instance of the store. */
	public static RhymeStore instance;

	/**
	 * Gets the singleton instance of the store.
	 * 
	 * @return The singleton instance of the store.
	 */
	public static RhymeStore getInstance() {
		if (RhymeStore.instance == null) {
			RhymeStore.instance = new RhymeStore();
		}

		return RhymeStore.instance;
	}

	/** The DAO used to persist the rhymes. */
	// private RedisDao<Rhyme> rhymeDAO;

	/** Parses the words to get the part used to rhyme. */
	private final WordParser wordParser;

	/**
	 * Creates a new <code>RhymeStore</code> connecting to
	 * <code>localhost</code> and the default Redis port.
	 */
	protected RhymeStore() {
		String host = Configuration
				.getConfigValue(Configuration.REDIS_HOST_PROPERTY);
		String port = Configuration
				.getConfigValue(Configuration.REDIS_PORT_PROPERTY);

		this.redis = new Jedis(host, Integer.valueOf(port));
		this.wordParser = WordParserFactory.getWordParser();
		// rhymeDAO = RedisDaoFactory.getDAO(Rhyme.class);
	}

	/**
	 * Adds the given rhyme to the Redis database.
	 * 
	 * @param sentence
	 *            The rhyme to add.
	 * @throws IOException
	 *             If an error occurs while adding the rhyme.
	 */
	public void add(final String sentence) throws IOException {
		String word = WordUtils.getLastWord(sentence);

		if (word.isEmpty()) {
			return;
		}

		// Get the rhyme and type (and check that the word is valid before
		// adding)
		String rhyme = this.normalizeString(this.wordParser
				.phoneticRhymePart(word));
		StressType type = this.wordParser.stressType(word);

		this.connect();

		String sentenceId = this.getUniqueId(this.sentencens,
				this.normalizeString(sentence));
		sentenceId = this.sentencens.build(sentenceId).toString();

		if (this.redis.exists(sentenceId) == 1) {
			this.disconnect();
			return;
		}

		// Insert sentence
		this.redis.set(sentenceId, URLEncoder.encode(sentence, this.encoding));

		// Index sentence
		String indexId = this.getUniqueId(this.indexns,
				this.buildUniqueToken(rhyme, type));
		indexId = this.indexns.build(indexId).toString();

		this.redis.sadd(indexId, sentenceId);

		this.disconnect();

		RhymeStore.LOGGER.info("Added rhyme: {}", sentence);
	}

	protected String buildUniqueToken(final String rhyme, final StressType type) {
		return this.sum(type.name().concat(rhyme));
	}

	/**
	 * Connects to the Redis database.
	 * 
	 * @throws UnknownHostException
	 *             If the target host does not respond.
	 * @throws IOException
	 *             If an error occurs while connecting.
	 */
	protected void connect() throws UnknownHostException, IOException {
		if (!this.redis.isConnected()) {
			this.redis.connect();
		}
	}

	/**
	 * Deletes the given rhyme from the Redis database.
	 * 
	 * @param sentence
	 *            The rhyme to delete.
	 * @throws IOException
	 *             If an error occurs while deleting the rhyme.
	 */
	public void delete(final String sentence) throws IOException {
		throw new UnsupportedOperationException(
				"Delete operation is not implemented yet.");
	}

	/**
	 * Disconnects from the Redis database.
	 * 
	 * @throws IOException
	 *             If an error occurs while disconnecting.
	 */
	protected void disconnect() throws IOException {
		if (this.redis.isConnected()) {
			this.redis.disconnect();
		}
	}

	/**
	 * Gets all the stored rhymes.
	 * 
	 * @return A <code>Set</code> with all the stored rhymes.
	 * @throws IOException
	 *             If the rhymes cannot be obtained.
	 */
	public Set<String> findAll() throws IOException {
		Set<String> rhymes = new HashSet<String>();

		this.connect();

		String lastId = this.getLastId(this.sentencens);

		if (lastId != null) {
			Integer n = Integer.parseInt(this.getLastId(this.sentencens));

			for (int i = 1; i <= n; i++) {
				String id = this.sentencens.build(String.valueOf(i)).toString();

				if (this.redis.exists(id) == 1) {
					rhymes.add(URLDecoder.decode(this.redis.get(id),
							this.encoding));
				}
			}
		}

		this.disconnect();

		return rhymes;
	}

	protected String getLastId(final Keymaker ns) {
		return this.redis.get(ns.build("next.id").toString());
	}

	/**
	 * Gets a rhyme for the given sentence.
	 * 
	 * @param sentence
	 *            The sentence to rhyme.
	 * @return The rhyme.
	 */
	public String getRhyme(final String sentence) throws IOException {
		String lastWord = WordUtils.getLastWord(sentence);

		String rhymepart = this.wordParser.phoneticRhymePart(lastWord);
		StressType type = this.wordParser.stressType(lastWord);

		RhymeStore.LOGGER.debug("Finding rhymes for {}", sentence);

		this.connect();

		Set<String> rhymes = this.search(rhymepart, type);

		this.disconnect();

		if (rhymes.isEmpty()) {
			// If no rhyme is found, return the default rhyme
			return this.wordParser.getDefaultRhyme();
		} else {
			// Otherwise, return a random rhyme
			List<String> rhymeList = new ArrayList<String>(rhymes);

			Random random = new Random(System.currentTimeMillis());
			int index = random.nextInt(rhymeList.size());

			return rhymeList.get(index);
		}
	}

	protected String getUniqueId(final Keymaker ns, final String token) {
		String key = this.getUniqueIdKey(ns, token);
		String id = this.redis.get(key);

		if (id != null) {
			return id;
		}

		Integer next = this.redis.incr(ns.build("next.id").toString());
		id = next.toString();

		if (this.redis.setnx(key, id) == 0) {
			id = this.redis.get(key);
		}

		return id;
	}

	protected String getUniqueIdKey(final Keymaker ns, final String token) {
		String md = this.sum(token);
		return ns.build(md, "id").toString();
	}

	protected String normalizeString(final String value) {
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
	 * @param rhyme
	 *            The rhyme to search.
	 * @param type
	 *            The <code>StressType</code> of the rhyme to search.
	 * @return A <code>Set</code> of rhymes for the given sentence.
	 * @throws IOException
	 *             If an error occurs while searching for the rhymes.
	 */
	protected Set<String> search(final String rhyme, final StressType type)
			throws IOException {
		Set<String> rhymes = new HashSet<String>();
		String norm = this.normalizeString(rhyme);

		String uniqueId = this.getUniqueIdKey(this.indexns,
				this.buildUniqueToken(norm, type));

		if (this.redis.exists(uniqueId) == 1) {
			String indexId = this.redis.get(uniqueId);
			indexId = this.indexns.build(indexId).toString();

			if (this.redis.exists(indexId) == 1) {
				for (String id : this.redis.smembers(indexId)) {
					if (this.redis.exists(id) == 1) {
						rhymes.add(URLDecoder.decode(this.redis.get(id),
								this.encoding));
					}
				}
			}
		}

		return rhymes;
	}

	/**
	 * Makes a md5 sum of the given text.
	 * 
	 * @param value
	 *            The text to sum.
	 * @return The md5 sum of the given text.
	 */
	protected String sum(final String value) {
		return DigestUtils.md5Hex(value.getBytes());
	}
}
