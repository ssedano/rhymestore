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

package com.rhymestore.lang;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Base class for all {@link WordParser} unit testing.
 * <p>
 * All <code>WordParser</code> must implement this test class.
 * 
 * @author Ignasi Barrera
 */
public abstract class AbstractWordParserTest {
	/** The word parser. */
	protected WordParser wordParser;

	/**
	 * Gets the {@link WordParser} to test.
	 * 
	 * @return The <code>WordParser</code> to test
	 */
	protected abstract WordParser getWordParser();

	@BeforeMethod
	public void setUp() {
		this.wordParser = this.getWordParser();
	}

	// Common tests

	@Test
	public void testCapitalize() {
		Assert.assertEquals(WordUtils.capitalize(""), "");
		Assert.assertEquals(WordUtils.capitalize("a"), "A");
		Assert.assertEquals(WordUtils.capitalize("word"), "Word");
		Assert.assertEquals(WordUtils.capitalize("capitalize test"),
		"Capitalize test");
	}

	/**
	 * Tests the {@link WordParser#getDefaultRhyme()} method.
	 */
	@Test
	public abstract void testGetDefaultRhyme();

	// Tests to be implemented by each WordParser implementation tests

	@Test
	public void testGetLastWord() {
		Assert.assertEquals(WordUtils.getLastWord(""), "");
		Assert.assertEquals(WordUtils.getLastWord("test"), "test");
		Assert.assertEquals(WordUtils.getLastWord("two words"), "words");
	}

	/**
	 * Tests the {@link WordParser#isLetter(char)} method.
	 */
	@Test
	public abstract void testIsLetter();

	/**
	 * Tests the {@link WordParser#isNumber(String))} method.
	 */
	@Test
	public abstract void testIsNumber();

	/**
	 * Tests the {@link WordParser#isWord(String)} method.
	 */
	@Test
	public abstract void testIsWord();

	/**
	 * Tests the {@link WordParser#phoneticRhymePart(String)} method.
	 */
	@Test
	public abstract void testPhoneticRhymePart();

	/**
	 * Tests the {@link WordParser#rhyme(String, String)} method.
	 */
	@Test
	public abstract void testRhyme();

	/**
	 * Tests the {@link WordParser#stressType(String)} method.
	 */
	@Test
	public abstract void testStressType();

}
