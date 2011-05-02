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

/**
 * Unit tests for the {@link SpanishWordParser}.
 * 
 * @author Ignasi Barrera
 */
public class SpanishWordParserTest extends AbstractWordParserTest {
	@Override
	protected WordParser getWordParser() {
		return new SpanishWordParser();
	}

	@Override
	public void testGetDefaultRhyme() {
		Assert.assertEquals(this.wordParser.getDefaultRhyme(),
				SpanishWordParser.DEFAULT_RHYME);
	}

	@Override
	public void testIsLetter() {
		// Valid letters

		Assert.assertTrue(SpanishWordParser.isLetter('a'));
		Assert.assertTrue(SpanishWordParser.isLetter('A'));
		Assert.assertTrue(SpanishWordParser.isLetter('z'));
		Assert.assertTrue(SpanishWordParser.isLetter('Z'));
		Assert.assertTrue(SpanishWordParser.isLetter('m'));
		Assert.assertTrue(SpanishWordParser.isLetter('M'));

		Assert.assertTrue(SpanishWordParser.isLetter('á'));
		Assert.assertTrue(SpanishWordParser.isLetter('é'));
		Assert.assertTrue(SpanishWordParser.isLetter('í'));
		Assert.assertTrue(SpanishWordParser.isLetter('ó'));
		Assert.assertTrue(SpanishWordParser.isLetter('ú'));
		Assert.assertTrue(SpanishWordParser.isLetter('ü'));

		Assert.assertTrue(SpanishWordParser.isLetter('Á'));
		Assert.assertTrue(SpanishWordParser.isLetter('É'));
		Assert.assertTrue(SpanishWordParser.isLetter('Í'));
		Assert.assertTrue(SpanishWordParser.isLetter('Ó'));
		Assert.assertTrue(SpanishWordParser.isLetter('Ú'));
		Assert.assertTrue(SpanishWordParser.isLetter('Ü'));

		Assert.assertTrue(SpanishWordParser.isLetter('Ñ'));
		Assert.assertTrue(SpanishWordParser.isLetter('ñ'));

		// Invalid Letters

		Assert.assertFalse(SpanishWordParser.isLetter(';'));
		Assert.assertFalse(SpanishWordParser.isLetter(' '));
		Assert.assertFalse(SpanishWordParser.isLetter('&'));
		Assert.assertFalse(SpanishWordParser.isLetter('.'));
		Assert.assertFalse(SpanishWordParser.isLetter(','));
		Assert.assertFalse(SpanishWordParser.isLetter(';'));
		Assert.assertFalse(SpanishWordParser.isLetter('-'));
	}

	@Override
	public void testIsWord() {
		// Valid words
		Assert.assertTrue(this.wordParser.isWord("hola"));
		Assert.assertTrue(this.wordParser.isWord("test"));
		Assert.assertTrue(this.wordParser.isWord("adiós"));
		Assert.assertTrue(this.wordParser.isWord("valid!"));
		Assert.assertTrue(this.wordParser.isWord("logroño"));
		Assert.assertTrue(this.wordParser.isWord("LOGROÑO"));

		// Invalid words
		Assert.assertFalse(this.wordParser.isWord("25"));
		Assert.assertFalse(this.wordParser.isWord("hola.adios"));
		Assert.assertFalse(this.wordParser.isWord("ab23cd"));
	}

	@Override
	public void testPhoneticRhymePart() {
		Assert.assertEquals(this.wordParser.phoneticRhymePart(""), "");

		// Monosilabos
		Assert.assertEquals(this.wordParser.phoneticRhymePart("pez"), "ez");

		// Agudas
		Assert.assertEquals(this.wordParser.phoneticRhymePart("correr"), "er");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("melón"), "on");

		// Llanas
		Assert.assertEquals(this.wordParser.phoneticRhymePart("lío"), "io");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("carromato"),
				"ato");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("Telecinco"),
				"inco");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("abogado"), "ado");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("auriculares"),
				"ares");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("canoa"), "oa");

		// Esdrújulcas y sobreesdrújulas
		Assert.assertEquals(this.wordParser.phoneticRhymePart("cáspita"),
				"aspita");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("recuérdamelo"),
				"erdamelo");

		// Casos foneticos especiales => sustitucion de consonantes
		Assert.assertEquals(this.wordParser.phoneticRhymePart("suyo"), "ullo");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("barullo"),
				"ullo");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("barba"), "arva");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("parva"), "arva");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("gong"), "ong");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("falange"),
				"anje");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("alfanje"),
				"anje");
		Assert.assertEquals(this.wordParser.phoneticRhymePart("cacho"), "acho");

		// Palabra imposible pero caso contemplado
		Assert.assertEquals(this.wordParser.phoneticRhymePart("gargáreha"),
				"area");
	}

	@Override
	public void testRhyme() {
		// Rhymes withoud punctuation
		Assert.assertTrue(this.wordParser.rhyme("", ""));
		Assert.assertTrue(this.wordParser.rhyme("pez", "hez"));
		Assert.assertTrue(this.wordParser.rhyme("tres", "revés"));
		Assert.assertTrue(this.wordParser.rhyme("Telecinco", "hinco"));
		Assert.assertTrue(this.wordParser.rhyme("nabo", "centavo"));
		Assert.assertTrue(this.wordParser.rhyme("falange", "alfanje"));
		Assert.assertTrue(this.wordParser.rhyme("parva", "escarba"));
		Assert.assertTrue(this.wordParser.rhyme("tuyo", "murmullo"));

		// Rhymes with punctuation
		Assert.assertTrue(this.wordParser.rhyme("cantar.", "pescar"));
		Assert.assertTrue(this.wordParser.rhyme("calor!", "motor?"));
		Assert.assertTrue(this.wordParser.rhyme("calor  ", "motor&;'?="));
	}

	@Override
	public void testStressType() {
		Assert.assertEquals(this.wordParser.stressType("pez"), StressType.LAST);
		Assert.assertEquals(this.wordParser.stressType("correr"),
				StressType.LAST);
		Assert.assertEquals(this.wordParser.stressType("lío"),
				StressType.SECOND_LAST);
		Assert.assertEquals(this.wordParser.stressType("carromato"),
				StressType.SECOND_LAST);
		Assert.assertEquals(this.wordParser.stressType("cáspita"),
				StressType.THIRD_LAST);
		Assert.assertEquals(this.wordParser.stressType("recuérdamelo"),
				StressType.FOURTH_LAST);
	}

}
