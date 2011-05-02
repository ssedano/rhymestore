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

import java.math.BigDecimal;

/**
 * Parses words to identify the part that is used to conform a consonant rhyme.
 * 
 * @author Ignasi Barrera
 * @see WordParser
 * @see StressType
 */
public class SpanishWordParser implements WordParser {

	public static enum SpanishNumber {
		UNO("1", "uno"), DOS("2", "dos"), TRES("3", "tres"), CUATRO("4",
				"cuatro"), CINCO("5", "cinco"), SEIS("6", "seis"), SIETE("7",
				"siete"), OCHO("8", "ocho"), NUEVE("9", "nueve"), DIEZ("10",
				"diez"), ONCE("11", "once"), DOCE("12", "doce"), TRECE("13",
				"trece"), CATORCE("14", "catorce"), QUINCE("15", "quince"), CERO(
				"0", "cero"), VEINTE("20", "veinte"), DECENAS("70", "enta"), CIEN(
				"100", "cien"), CIENTOS("700", "cientos"), MIL("7000", "mil"), MILLON(
				"100000000", "millón"), MILLONES("700000000", "millones");
		public static String getBaseSound(long number) {
			return SpanishNumber.getBaseSound(String.valueOf(number));
		}

		public static String getBaseSound(String number) {
			if ((number == null) || "".equals(number)) {
				return null;
			}
			if (new BigDecimal(number).compareTo(BigDecimal
					.valueOf(Long.MAX_VALUE)) > 0) {
				number = SpanishNumber.handlingValue(number);
			}
			Long n = Long.valueOf(number);
			// no leading zeros
			number = n.toString();
			char[] digits = number.toCharArray();
			switch (number.length()) {
			case 1: {
				return SpanishNumber.getWordByNumber(number);

			}
			case 2: {
				return SpanishNumber.tenners(n);
			}
			case 3: {

				// es un ciento *
				if (n % 100 == 0) {
					// 100
					if (digits[0] == '1') {
						return SpanishNumber.getWordByNumber(n);
					}
					return SpanishNumber.getWordByNumber("700");
				}
				// 10 - 15
				return SpanishNumber.tenners(Integer.parseInt(digits[1] + ""
						+ digits[2]));
			}
			case 4:
			case 5: {
				// mil
				if (n % 1000 == 0) {
					return SpanishNumber.getWordByNumber("7000");
				}
				if (n % 100 == 0) {
					// 100
					if (digits[0] == 1) {
						return SpanishNumber.getWordByNumber(n);
					}
					return SpanishNumber.getWordByNumber(700);
				}
				// 10 - 15
				return SpanishNumber.tenners(Integer
						.parseInt(digits[digits.length - 2] + ""
								+ digits[digits.length - 1]));
			}
			default: {
				// recursividad!

				if (n % 1000000 == 0) {
					return SpanishNumber.MILLONES.getWord();
				} else if (n % 100000 == 0) {
					return SpanishNumber.MIL.getWord();
				}
				if (number.length() == 1) {
					return SpanishNumber.getWordByNumber(number);
				}
				StringBuffer re = new StringBuffer();
				for (int i = 1; digits.length > i; i++) {
					re.append(digits[i]);
				}
				return SpanishNumber.getBaseSound(re.toString());
			}
			}

		}

		private static String getSound(String number, int idx) {
			char[] digits = number.toCharArray();
			String sound = null;
			for (int i = digits.length; i >= 0; i--) {

			}
			return sound;
		}

		public static String getWordByNumber(long number) {
			for (SpanishNumber n : SpanishNumber.values()) {
				if (n.getN() == (number)) {
					return n.getWord();
				}
			}
			return null;
		}

		public static String getWordByNumber(String number) {
			for (SpanishNumber n : SpanishNumber.values()) {
				if (n.getNumber().equalsIgnoreCase(number)) {
					return n.getWord();
				}
			}
			return null;
		}

		static private String handlingValue(String number) {
			return number.substring(number.length() - 7, number.length());
		}

		private static String tenners(long n) {
			long rest = n % 10;
			if ((n == 10) || (n == 20)) {
				return SpanishNumber.getWordByNumber(n);
			}
			if (n >= 20) {
				if (rest == 0) {
					return SpanishNumber.DECENAS.getWord();
				}
				return SpanishNumber.getWordByNumber(rest);
			}
			if (rest > 5) {
				return SpanishNumber.getWordByNumber(rest);
			}
			return SpanishNumber.getWordByNumber(n);
		}

		private final String number;
		private final String word;

		private final int n;

		private SpanishNumber(String number, String word) {
			this.number = number;
			this.word = word;
			this.n = Integer.valueOf(number);
		}

		private int getN() {
			return this.n;
		}

		private String getNumber() {
			return this.number;
		}

		private String getWord() {
			return this.word;
		}
	}

	static final String DEFAULT_RHYME = "Patada en los cojones";

	private static final boolean acento(final char letter) {
		switch (letter) {
		case 225: // a con acento
		case 233: // e con acento
		case 237: // i con acento
		case 243: // o con acento
		case 250: // u con acento
			return true;
		default:
			return false;
		}
	}

	private static final boolean acento(final String word) {
		char[] letters = word.toCharArray();
		for (char letter : letters) {
			if (SpanishWordParser.isVocal(letter)
					&& SpanishWordParser.acento(letter)) {
				return true;
			}
		}
		return false;
	}

	private static boolean aguda(final String[] silabas) {
		String silaba = silabas[silabas.length - 1];
		char last = silaba.charAt(silaba.length() - 1);

		// Si termina en vocal acentuada => aguda
		if (SpanishWordParser.acento(last)) {
			return true;
		}

		// si termina en vocal 'n' o 's' y tiene acento => aguda
		char lastVocal = silaba
				.charAt(SpanishWordParser.lastVocalIndex(silaba));
		if (((last == 'n') || (last == 's') || SpanishWordParser.isVocal(last))
				&& SpanishWordParser.acento(lastVocal)) {
			return true;
		}

		if ((last == 'n') || (last == 's') || SpanishWordParser.isVocal(last)) {
			return false;
		}

		if ((last != 'n') && (last != 's')) {
			for (String s : silabas) {
				if (SpanishWordParser.acento(s)) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	private static boolean esdrujula(final String[] silabas) {
		int i = 0;
		for (i = 0; i < silabas.length; i++) {
			if (SpanishWordParser.acento(silabas[i])) {
				break;
			}
		}

		return i == silabas.length - 3;
	}

	public static boolean isLetter(final char letter) {
		boolean isLetter = (letter >= 97) && (letter <= 122); // a-z
		isLetter = isLetter || ((letter >= 65) && (letter <= 90)); // A-Z

		if (isLetter) {
			return true;
		}

		// others: check extended ascii codes specific letters

		switch (letter) {
		case 193: // A con acento
		case 201: // E con acento
		case 205: // I con acento
		case 209: // enye mayuscula
		case 211: // O con acento
		case 218: // U con acento
		case 220: // U con dieresis
		case 225: // a con acento
		case 233: // e con acento
		case 237: // i con acento
		case 241: // enye minuscula
		case 243: // o con acento
		case 250: // u con acento
		case 252: // u con dieresis
			return true;
		default:
			return false;
		}
	}

	private static final boolean isVocal(final char letter) {
		switch (letter) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
		case 225: // a con acento
		case 233: // e con acento
		case 237: // i con acento
		case 243: // o con acento
		case 250: // u con acento
			return true;
		default:
			return false;
		}
	}

	private static int lastVocalIndex(final String syllable) {
		char[] letters = syllable.toCharArray();
		for (int i = letters.length - 1; i >= 0; i--) {
			if (SpanishWordParser.isVocal(letters[i])) {
				return i;
			}
		}

		throw new IllegalArgumentException(
				"It is impossible to have a word without vowels");
	}

	private static boolean llana(final String[] silabas) {
		String silaba = silabas[silabas.length - 2];
		int vocalIndex = SpanishWordParser.lastVocalIndex(silaba);
		char vocal = silaba.charAt(vocalIndex);

		if (!SpanishWordParser.aguda(silabas)) {
			if (SpanishWordParser.acento(vocal)) {
				return true;
			} else {
				for (String s : silabas) {
					if (SpanishWordParser.acento(s)) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Removes the trailing punctuation from the given string
	 * 
	 * @param str
	 *            The String to parse.
	 * @return The String without the trailing punctuation
	 */
	static String removeTrailingPunctuation(final String str) {
		if (str.length() == 0) {
			return str;
		}

		char[] chars = str.toCharArray();

		int i = chars.length - 1;
		while (i >= 0) {
			if (SpanishWordParser.isLetter(chars[i])
					|| Character.isDigit(chars[i])) {
				break;
			}
			i--;
		}

		// variable 'i' holds the last letter index
		return str.substring(0, i + 1);
	}

	private boolean consonantes1(final char a, final char b) {
		boolean cer;
		cer = false;
		if ((a == 'b') || (a == 'c') || (a == 'd') || (a == 'f') || (a == 'g')
				|| (a == 'p') || (a == 'r') || (a == 't')) {
			if (b == 'r') {
				cer = true;
			}
		}
		if ((a == 'b') || (a == 'c') || (a == 'f') || (a == 'g') || (a == 'p')
				|| (a == 't') || (a == 'l') || (a == 'k')) {
			if (b == 'l') {
				cer = true;
			}
		}
		if (b == 'h') {
			if (a == 'c') {
				cer = true;
			}
		}
		return cer;
	}

	@Override
	public String getDefaultRhyme() {
		return SpanishWordParser.DEFAULT_RHYME;
	}

	private boolean hiato(final char v, final char v2) { // Estable si hay
															// separacion
		boolean cer = false;
		if (this.letra(v) < 4) { // VA + ?
			if (this.letra(v2) < 4) {
				cer = true;
			} else { // VA+ VC
				if ((v2 == 237) || (v2 == 250)) // i o u con acento
				{
					cer = true;
				} else {
					cer = false;
				}
			}
		} else { // VC + ?
			if (this.letra(v2) < 4) { // VC + VA
				if ((v == 237) || (v == 250)) // i o u con acento
				{
					cer = true;
				} else {
					cer = false;
				}
			} else {// VC + VC
				if (v == v2) {
					cer = true;
				} else {
					cer = false;
				}
			}
		}
		return cer;
	}

	@Override
	public boolean isNumber(String word) {
		char[] letters = SpanishWordParser.removeTrailingPunctuation(word)
				.toCharArray();
		for (char c : letters) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return !false;
	}

	@Override
	public boolean isWord(final String text) {
		char[] letters = SpanishWordParser.removeTrailingPunctuation(text)
				.toCharArray();

		if (letters.length == 0) {
			return false;
		}

		for (char letter : letters) {
			if (!SpanishWordParser.isLetter(letter)) {
				return false;
			}
		}

		return true;
	}

	private int letra(final char c) {
		int i = -1;
		int ascii;
		ascii = c;
		if (ascii != -1) {
			switch (ascii) {
			case 97: // a
				i = 1;
				break;
			case 101: // e
				i = 2;
				break;
			case 104: // h
				i = 6;
				break;
			case 105: // i
				i = 4;
				break;
			case 111: // o
				i = 3;
				break;
			case 117: // u
				i = 5;
				break;
			case 225: // a con acento
				i = 1;
				break;
			case 233: // e con acento
				i = 2;
				break;
			case 237: // i con acento
				i = 4;
				break;
			case 243: // o con acento
				i = 3;
				break;
			case 250: // u con acento
				i = 5;
				break;
			case 252: // u con dieresis
				i = 5;
				break;
			default:
				i = 19;
				break;
			}
		}
		return i;
	}

	@Override
	public String phoneticRhymePart(final String word) {
		String withoutPunctuation = SpanishWordParser
				.removeTrailingPunctuation(word);
		String rhymePart = this.rhymePart(withoutPunctuation);

		StringBuilder result = new StringBuilder();
		char[] letters = rhymePart.toCharArray();

		for (int i = 0; i < letters.length; i++) {
			switch (letters[i]) {
			// Vocales
			case 225: // a con acento
				result.append('a');
				break;
			case 233: // e con acento
				result.append('e');
				break;
			case 237: // i con acento
				result.append('i');
				break;
			case 243: // o con acento
				result.append('o');
				break;
			case 250: // u con acento
				result.append('u');
				break;
			case 252: // u con dieresis
				result.append('u');
				break;

			// Consonantes
			case 'b':
				result.append('v');
				break;
			case 'y':
				result.append("ll");
				break;

			// h => añadirla solo si es una 'ch'
			case 'h':
				if ((i > 0) && (letters[i - 1] == 'c')) {
					result.append('h');
				}
				break;

			// g => transformarla en 'j' si va antes de 'e' o 'i'
			case 'g':
				if ((i + 1 < letters.length)
						&& ((letters[i + 1] == 'e') || (letters[i + 1] == 'i'))) {
					result.append('j');
				} else {
					result.append('g');
				}
				break;

			// Otros
			default:
				result.append(letters[i]);
				break;
			}
		}

		return result.toString();
	}

	@Override
	public boolean rhyme(final String word1, final String word2) {
		String rhyme1 = this.phoneticRhymePart(word1);
		String rhyme2 = this.phoneticRhymePart(word2);

		return rhyme1.equalsIgnoreCase(rhyme2);
	}

	private String rhymePart(String word) {
		if (word.length() == 0) {
			return "";
		}

		String[] syllables = this.silabas(word.toLowerCase());

		// Monosilabo
		if (syllables.length == 1) {
			int lastVocal = SpanishWordParser.lastVocalIndex(syllables[0]);
			return syllables[0].substring(lastVocal);
		}

		// Palabra aguda
		if (SpanishWordParser.aguda(syllables)) {
			int lastVocal = SpanishWordParser
					.lastVocalIndex(syllables[syllables.length - 1]);
			return syllables[syllables.length - 1].substring(lastVocal);
		}

		// Palabra llana
		if (SpanishWordParser.llana(syllables)) {
			int lastVocal = SpanishWordParser
					.lastVocalIndex(syllables[syllables.length - 2]);
			return syllables[syllables.length - 2].substring(lastVocal)
					+ syllables[syllables.length - 1];
		}

		// Esdrujula
		String parte = "";
		boolean found = false;
		for (String silaba : syllables) {
			if (found) {
				parte += silaba;
			} else if (SpanishWordParser.acento(silaba)) {
				found = true;
				parte = silaba.substring(SpanishWordParser
						.lastVocalIndex(silaba));
			}
		}

		return parte;
	}

	private String silaba(final String str) {
		String temp = "";
		String s = "";
		char x, y, z;
		if (str.length() < 3) {
			if (str.length() == 2) {
				x = str.charAt(0);
				y = str.charAt(1);
				if ((this.letra(x) < 6) && (this.letra(y) < 6)) {
					if (this.hiato(x, y)) {
						s = str.substring(0, 1);
					} else {
						s = str;
					}
				} else {
					s = str;
				}
			} else {
				s = str;
			}
		} else {
			x = str.charAt(0);
			y = str.charAt(1);
			z = str.charAt(2);
			if (this.letra(x) < 6) { // V ? ?
				if (this.letra(y) < 6) { // V V ?
					if (this.letra(z) < 6) { // V V V
						if (this.hiato(x, y)) {
							s = str.substring(0, 1);
						} else {
							if (this.hiato(y, z)) {
								s = str.substring(0, 2);
							} else {
								s = str.substring(0, 3);
							}
						}
					} else { // V V C
						if (this.hiato(x, y)) {
							s = str.substring(0, 1);
						} else {
							s = str.substring(0, 2);
						}
					}
				} else { // V C ?
					if (this.letra(z) < 6) { // V C V
						if (this.letra(y) == 6) { // V H C
							if (this.hiato(x, z)) {
								s = str.substring(0, 1);
							} else {
								s = str.substring(0, 3);
							}
						} else {
							s = str.substring(0, 1);
						}
					} else { // V C C
						if (this.consonantes1(y, z)) {
							s = str.substring(0, 1);
						} else {
							s = str.substring(0, 2);
						}
					}
				}
			} else { // C ??
				if (this.letra(y) < 6) { // C V ?
					if (this.letra(z) < 6) { // C V V
						temp = str.substring(0, 3);
						if (temp.equals("que") || temp.equals("qui")
								|| temp.equals("gue") || temp.equals("gui")) {
							s = str.substring(0, 3);
						} else {
							if (this.hiato(y, z)) {
								s = str.substring(0, 2);
							} else {
								s = str.substring(0, 3);
							}
						}
					} else { // C V C
						s = str.substring(0, 2);
					}
				} else { // C C ?
					if (this.letra(z) < 6) { // C C V
						if (this.consonantes1(x, y)) {
							s = str.substring(0, 3);
						} else {
							s = str.substring(0, 1);
						}
					} else { // C C C
						if (this.consonantes1(y, z)) {
							s = str.substring(0, 1);
						} else {
							s = str.substring(0, 1);
						}
					}
				}
			}
		}
		return s;
	}

	private String silabaRest(final String str) {
		String s2;
		s2 = this.silaba(str);
		return str.substring(s2.length());
	}

	private String[] silabas(String cadena) {
		String temp;
		String s = "";
		int i, k;
		k = cadena.length();
		temp = cadena;

		for (i = 0; i < k; i++) {
			temp = this.silaba(cadena);
			if (i == 0) {
				s = s + temp;
			} else {
				if (this.strConsonantes(temp)) {
					s = s + temp;
				} else {
					if (this.strVVstr(s, temp)) {
						s = s + temp;
					} else {
						if (this.strConsonantes(s)) {
							s = s + temp;
						} else {
							s = s + "-" + temp;
						}
					}
				}
			}
			i = i + temp.length() - 1;
			cadena = this.silabaRest(cadena);
		}
		return s.split("-");
	}

	private boolean strConsonantes(final String str) {
		boolean cer = false;
		int i;
		byte noConsonante = 0;
		char c[] = str.toCharArray();
		for (i = 0; (i < str.length()) && (noConsonante == 0); i++) {
			if (this.letra(c[i]) < 6) {
				noConsonante++;
			}
		}
		if (noConsonante == 0) {
			cer = true;
		}
		return cer;
	}

	@Override
	public StressType stressType(final String word) {
		String[] silabas = this.silabas(SpanishWordParser
				.removeTrailingPunctuation(word));

		if (silabas.length == 1) {
			return StressType.LAST;
		} else if (SpanishWordParser.esdrujula(silabas)) {
			return StressType.THIRD_LAST;
		} else if (SpanishWordParser.aguda(silabas)) {
			return StressType.LAST;
		} else if (SpanishWordParser.llana(silabas)) {
			return StressType.SECOND_LAST;
		} else {
			return StressType.FOURTH_LAST;
		}
	}

	private boolean strVVstr(final String s1, final String s2) { // Estable si
																	// hay union
		boolean cer;
		char c1, c2;
		c1 = s1.charAt(s1.length() - 1);
		c2 = s2.charAt(0);
		cer = false;
		if ((this.letra(c1) < 6) && (this.letra(c2) < 6)) {
			if (this.hiato(c1, c2)) {
				cer = false;
			} else {
				cer = true;
			}
		}
		return cer;
	}
}
