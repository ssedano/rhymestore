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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link SpanishWordParser}.
 * 
 * @author Ignasi Barrera
 */
public class SpanishWordParserTest extends AbstractWordParserTest
{
	@Override
	protected WordParser getWordParser()
	{
		return new SpanishWordParser();
	}

	@Override
	public void testPhoneticRhymePart()
	{
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
	public void testStressType()
	{
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

	@Override
	public void testRhyme()
	{
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
		Assert.assertTrue(wordParser.rhyme("cantar.", "pescar"));
		Assert.assertTrue(wordParser.rhyme("calor!", "motor?"));
		Assert.assertTrue(wordParser.rhyme("calor  ", "motor&;'?="));

		// TODO one rhyme with every number (sound).Rhymes with numbers
		Assert.assertTrue(wordParser.rhyme(
				SpanishWordParser.SpanishNumber.getBaseSound(1L), "tuno"));
		Assert.assertTrue(wordParser.rhyme(
				SpanishWordParser.SpanishNumber.getBaseSound("88.88"), "cocho"));
		Assert.assertTrue(wordParser.rhyme(
				SpanishWordParser.SpanishNumber.getBaseSound("88,88"), "cocho"));
	}

	@Override
	public void testIsLetter()
	{
		// Valid letters

		Assert.assertTrue(this.wordParser.isLetter('a'));
		Assert.assertTrue(this.wordParser.isLetter('A'));
		Assert.assertTrue(this.wordParser.isLetter('z'));
		Assert.assertTrue(this.wordParser.isLetter('Z'));
		Assert.assertTrue(this.wordParser.isLetter('m'));
		Assert.assertTrue(this.wordParser.isLetter('M'));

		Assert.assertTrue(this.wordParser.isLetter('á'));
		Assert.assertTrue(this.wordParser.isLetter('é'));
		Assert.assertTrue(this.wordParser.isLetter('í'));
		Assert.assertTrue(this.wordParser.isLetter('ó'));
		Assert.assertTrue(this.wordParser.isLetter('ú'));
		Assert.assertTrue(this.wordParser.isLetter('ü'));

		Assert.assertTrue(this.wordParser.isLetter('Á'));
		Assert.assertTrue(this.wordParser.isLetter('É'));
		Assert.assertTrue(this.wordParser.isLetter('Í'));
		Assert.assertTrue(this.wordParser.isLetter('Ó'));
		Assert.assertTrue(this.wordParser.isLetter('Ú'));
		Assert.assertTrue(this.wordParser.isLetter('Ü'));

		Assert.assertTrue(this.wordParser.isLetter('Ñ'));
		Assert.assertTrue(this.wordParser.isLetter('ñ'));

		// Invalid Letters

		Assert.assertFalse(this.wordParser.isLetter(';'));
		Assert.assertFalse(this.wordParser.isLetter(' '));
		Assert.assertFalse(this.wordParser.isLetter('&'));
		Assert.assertFalse(this.wordParser.isLetter('.'));
		Assert.assertFalse(this.wordParser.isLetter(','));
		Assert.assertFalse(this.wordParser.isLetter(';'));
		Assert.assertFalse(this.wordParser.isLetter('-'));
	}

	@Override
	public void testIsWord()
	{
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
	public void testGetDefaultRhyme()
	{
		String rhyme0 = ((SpanishWordParser) this.wordParser).defaultRhymes
				.get(0);
		String rhyme1 = ((SpanishWordParser) this.wordParser).defaultRhymes
				.get(1);

		Assert.assertEquals(this.wordParser.getDefaultRhyme(), rhyme0);
		Assert.assertEquals(this.wordParser.getDefaultRhyme(), rhyme1);
		Assert.assertEquals(this.wordParser.getDefaultRhyme(), rhyme0);
		Assert.assertEquals(this.wordParser.getDefaultRhyme(), rhyme1);
	}

	@Override
	public void testIsNumber()
	{
		String number = String.valueOf(new Random().nextInt());
		Assert.assertTrue(wordParser.isNumber(number), number);
		Assert.assertFalse(wordParser.isNumber(String.valueOf("No number")));
	}

	@Test
	public void testSpanishNumber()
	{
		Assert.assertEquals("cero",
				SpanishWordParser.SpanishNumber.getBaseSound(0));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(1));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(2));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(3));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(4));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(5));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(6));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(7));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(8));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(9));
		Assert.assertEquals("diez",
				SpanishWordParser.SpanishNumber.getBaseSound(10));
		Assert.assertEquals("once",
				SpanishWordParser.SpanishNumber.getBaseSound(11));
		Assert.assertEquals("doce",
				SpanishWordParser.SpanishNumber.getBaseSound(12));
		Assert.assertEquals("trece",
				SpanishWordParser.SpanishNumber.getBaseSound(13));
		Assert.assertEquals("catorce",
				SpanishWordParser.SpanishNumber.getBaseSound(14));
		Assert.assertEquals("quince",
				SpanishWordParser.SpanishNumber.getBaseSound(15));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(16));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(17));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(18));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(19));
		Assert.assertEquals("veinte",
				SpanishWordParser.SpanishNumber.getBaseSound(20));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(21));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(22));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(23));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(24));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(25));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(26));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(27));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(28));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(29));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(30));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(31));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(32));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(33));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(34));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(35));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(36));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(37));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(38));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(39));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(40));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(41));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(42));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(43));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(44));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(45));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(46));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(47));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(48));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(49));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(50));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(51));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(52));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(53));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(54));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(55));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(56));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(57));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(58));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(59));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(60));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(61));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(62));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(63));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(64));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(65));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(66));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(67));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(68));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(69));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(70));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(71));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(72));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(73));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(74));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(75));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(76));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(77));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(78));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(79));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(80));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(81));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(82));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(83));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(84));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(85));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(86));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(87));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(88));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(89));
		Assert.assertEquals("enta",
				SpanishWordParser.SpanishNumber.getBaseSound(90));
		Assert.assertEquals("uno",
				SpanishWordParser.SpanishNumber.getBaseSound(91));
		Assert.assertEquals("dos",
				SpanishWordParser.SpanishNumber.getBaseSound(92));
		Assert.assertEquals("tres",
				SpanishWordParser.SpanishNumber.getBaseSound(93));
		Assert.assertEquals("cuatro",
				SpanishWordParser.SpanishNumber.getBaseSound(94));
		Assert.assertEquals("cinco",
				SpanishWordParser.SpanishNumber.getBaseSound(95));
		Assert.assertEquals("seis",
				SpanishWordParser.SpanishNumber.getBaseSound(96));
		Assert.assertEquals("siete",
				SpanishWordParser.SpanishNumber.getBaseSound(97));
		Assert.assertEquals("ocho",
				SpanishWordParser.SpanishNumber.getBaseSound(98));
		Assert.assertEquals("nueve",
				SpanishWordParser.SpanishNumber.getBaseSound(99));
		Assert.assertEquals("cien",
				SpanishWordParser.SpanishNumber.getBaseSound(100));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(200));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(300));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(400));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(500));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(600));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(700));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(800));
		Assert.assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(900));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(1000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(2000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(3000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(4000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(5000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(6000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(7000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(8000));
		Assert.assertEquals("mil",
				SpanishWordParser.SpanishNumber.getBaseSound(9000));

	}
}
