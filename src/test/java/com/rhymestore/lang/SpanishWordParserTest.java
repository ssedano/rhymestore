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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Random;

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
		assertEquals(wordParser.phoneticRhymePart(""), "");

		// Monosilabos
		assertEquals(wordParser.phoneticRhymePart("pez"), "ez");

		// Agudas
		assertEquals(wordParser.phoneticRhymePart("correr"), "er");
		assertEquals(wordParser.phoneticRhymePart("melón"), "on");

		// Llanas
		assertEquals(wordParser.phoneticRhymePart("lío"), "io");
		assertEquals(wordParser.phoneticRhymePart("carromato"), "ato");
		assertEquals(wordParser.phoneticRhymePart("Telecinco"), "inco");
		assertEquals(wordParser.phoneticRhymePart("abogado"), "ado");
		assertEquals(wordParser.phoneticRhymePart("auriculares"), "ares");
		assertEquals(wordParser.phoneticRhymePart("canoa"), "oa");

		// Esdrújulcas y sobreesdrújulas
		assertEquals(wordParser.phoneticRhymePart("cáspita"), "aspita");
		assertEquals(wordParser.phoneticRhymePart("recuérdamelo"), "erdamelo");

		// Casos foneticos especiales => sustitucion de consonantes
		assertEquals(wordParser.phoneticRhymePart("suyo"), "ullo");
		assertEquals(wordParser.phoneticRhymePart("barullo"), "ullo");
		assertEquals(wordParser.phoneticRhymePart("barba"), "arva");
		assertEquals(wordParser.phoneticRhymePart("parva"), "arva");
		assertEquals(wordParser.phoneticRhymePart("gong"), "ong");
		assertEquals(wordParser.phoneticRhymePart("falange"), "anje");
		assertEquals(wordParser.phoneticRhymePart("alfanje"), "anje");
		assertEquals(wordParser.phoneticRhymePart("cacho"), "acho");

		// Palabra imposible pero caso contemplado
		assertEquals(wordParser.phoneticRhymePart("gargáreha"), "area");
	}

	@Override
	public void testStressType()
	{
		assertEquals(wordParser.stressType("pez"), StressType.LAST);
		assertEquals(wordParser.stressType("correr"), StressType.LAST);
		assertEquals(wordParser.stressType("lío"), StressType.SECOND_LAST);
		assertEquals(wordParser.stressType("carromato"), StressType.SECOND_LAST);
		assertEquals(wordParser.stressType("cáspita"), StressType.THIRD_LAST);
		assertEquals(wordParser.stressType("recuérdamelo"),
				StressType.FOURTH_LAST);
	}

	@Override
	public void testRhyme()
	{
		// Rhymes withoud punctuation
		assertTrue(wordParser.rhyme("", ""));
		assertTrue(wordParser.rhyme("pez", "hez"));
		assertTrue(wordParser.rhyme("tres", "revés"));
		assertTrue(wordParser.rhyme("Telecinco", "hinco"));
		assertTrue(wordParser.rhyme("nabo", "centavo"));
		assertTrue(wordParser.rhyme("falange", "alfanje"));
		assertTrue(wordParser.rhyme("parva", "escarba"));
		assertTrue(wordParser.rhyme("tuyo", "murmullo"));

		// Rhymes with punctuation
		assertTrue(wordParser.rhyme("cantar.", "pescar"));
		assertTrue(wordParser.rhyme("calor!", "motor?"));
		assertTrue(wordParser.rhyme("calor  ", "motor&;'?="));

		// TODO one rhyme with every number (sound).Rhymes with numbers
		assertTrue(wordParser.rhyme(
				SpanishWordParser.SpanishNumber.getBaseSound(1L), "tuno"));
		assertTrue(wordParser.rhyme(
				SpanishWordParser.SpanishNumber.getBaseSound("88.88"), "cocho"));
		assertTrue(wordParser.rhyme(
				SpanishWordParser.SpanishNumber.getBaseSound("88,88"), "cocho"));
	}

	@Override
	public void testIsLetter()
	{
		// Valid letters

		assertTrue(wordParser.isLetter('a'));
		assertTrue(wordParser.isLetter('A'));
		assertTrue(wordParser.isLetter('z'));
		assertTrue(wordParser.isLetter('Z'));
		assertTrue(wordParser.isLetter('m'));
		assertTrue(wordParser.isLetter('M'));

		assertTrue(wordParser.isLetter('á'));
		assertTrue(wordParser.isLetter('é'));
		assertTrue(wordParser.isLetter('í'));
		assertTrue(wordParser.isLetter('ó'));
		assertTrue(wordParser.isLetter('ú'));
		assertTrue(wordParser.isLetter('ü'));

		assertTrue(wordParser.isLetter('Á'));
		assertTrue(wordParser.isLetter('É'));
		assertTrue(wordParser.isLetter('Í'));
		assertTrue(wordParser.isLetter('Ó'));
		assertTrue(wordParser.isLetter('Ú'));
		assertTrue(wordParser.isLetter('Ü'));

		assertTrue(wordParser.isLetter('Ñ'));
		assertTrue(wordParser.isLetter('ñ'));

		// Invalid Letters

		assertFalse(wordParser.isLetter(';'));
		assertFalse(wordParser.isLetter(' '));
		assertFalse(wordParser.isLetter('&'));
		assertFalse(wordParser.isLetter('.'));
		assertFalse(wordParser.isLetter(','));
		assertFalse(wordParser.isLetter(';'));
		assertFalse(wordParser.isLetter('-'));
	}

	@Override
	public void testIsWord()
	{
		// Valid words
		assertTrue(wordParser.isWord("hola"));
		assertTrue(wordParser.isWord("test"));
		assertTrue(wordParser.isWord("adiós"));
		assertTrue(wordParser.isWord("valid!"));
		assertTrue(wordParser.isWord("logroño"));
		assertTrue(wordParser.isWord("LOGROÑO"));

		// Invalid words
		assertFalse(wordParser.isWord("25"));
		assertFalse(wordParser.isWord("hola.adios"));
		assertFalse(wordParser.isWord("ab23cd"));
	}

	@Override
	public void testGetDefaultRhyme()
	{
		String rhyme0 = ((SpanishWordParser) wordParser).defaultRhymes.get(0);
		String rhyme1 = ((SpanishWordParser) wordParser).defaultRhymes.get(1);

		assertEquals(wordParser.getDefaultRhyme(), rhyme0);
		assertEquals(wordParser.getDefaultRhyme(), rhyme1);
		assertEquals(wordParser.getDefaultRhyme(), rhyme0);
		assertEquals(wordParser.getDefaultRhyme(), rhyme1);
	}

	@Override
	public void testIsNumber()
	{
		String number = String.valueOf(new Random().nextInt());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + number);
		assertTrue(wordParser.isNumber(number), number);
		assertFalse(wordParser.isNumber(String.valueOf("No number")));
	}

	@Test
	public void testSpanishNumber()
	{
		assertEquals("cero", SpanishWordParser.SpanishNumber.getBaseSound(0));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(1));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(2));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(3));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(4));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(5));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(6));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(7));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(8));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(9));
		assertEquals("diez", SpanishWordParser.SpanishNumber.getBaseSound(10));
		assertEquals("once", SpanishWordParser.SpanishNumber.getBaseSound(11));
		assertEquals("doce", SpanishWordParser.SpanishNumber.getBaseSound(12));
		assertEquals("trece", SpanishWordParser.SpanishNumber.getBaseSound(13));
		assertEquals("catorce",
				SpanishWordParser.SpanishNumber.getBaseSound(14));
		assertEquals("quince", SpanishWordParser.SpanishNumber.getBaseSound(15));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(16));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(17));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(18));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(19));
		assertEquals("veinte", SpanishWordParser.SpanishNumber.getBaseSound(20));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(21));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(22));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(23));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(24));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(25));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(26));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(27));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(28));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(29));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(30));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(31));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(32));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(33));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(34));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(35));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(36));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(37));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(38));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(39));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(40));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(41));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(42));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(43));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(44));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(45));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(46));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(47));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(48));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(49));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(50));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(51));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(52));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(53));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(54));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(55));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(56));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(57));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(58));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(59));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(60));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(61));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(62));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(63));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(64));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(65));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(66));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(67));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(68));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(69));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(70));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(71));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(72));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(73));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(74));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(75));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(76));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(77));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(78));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(79));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(80));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(81));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(82));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(83));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(84));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(85));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(86));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(87));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(88));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(89));
		assertEquals("enta", SpanishWordParser.SpanishNumber.getBaseSound(90));
		assertEquals("uno", SpanishWordParser.SpanishNumber.getBaseSound(91));
		assertEquals("dos", SpanishWordParser.SpanishNumber.getBaseSound(92));
		assertEquals("tres", SpanishWordParser.SpanishNumber.getBaseSound(93));
		assertEquals("cuatro", SpanishWordParser.SpanishNumber.getBaseSound(94));
		assertEquals("cinco", SpanishWordParser.SpanishNumber.getBaseSound(95));
		assertEquals("seis", SpanishWordParser.SpanishNumber.getBaseSound(96));
		assertEquals("siete", SpanishWordParser.SpanishNumber.getBaseSound(97));
		assertEquals("ocho", SpanishWordParser.SpanishNumber.getBaseSound(98));
		assertEquals("nueve", SpanishWordParser.SpanishNumber.getBaseSound(99));
		assertEquals("cien", SpanishWordParser.SpanishNumber.getBaseSound(100));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(200));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(300));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(400));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(500));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(600));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(700));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(800));
		assertEquals("cientos",
				SpanishWordParser.SpanishNumber.getBaseSound(900));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(1000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(2000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(3000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(4000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(5000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(6000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(7000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(8000));
		assertEquals("mil", SpanishWordParser.SpanishNumber.getBaseSound(9000));

	}
}
