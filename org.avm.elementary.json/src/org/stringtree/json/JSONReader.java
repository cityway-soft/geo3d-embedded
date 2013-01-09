package org.stringtree.json;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONReader {

	private static final Object OBJECT_END = new Object();
	private static final Object ARRAY_END = new Object();
	private static final Object COLON = new Object();
	private static final Object COMMA = new Object();

	private static Map escapes = new HashMap();
	static {
		escapes.put(new Character('"'), new Character('"'));
		escapes.put(new Character('\\'), new Character('\\'));
		escapes.put(new Character('/'), new Character('/'));
		escapes.put(new Character('b'), new Character('\b'));
		escapes.put(new Character('f'), new Character('\f'));
		escapes.put(new Character('n'), new Character('\n'));
		escapes.put(new Character('r'), new Character('\r'));
		escapes.put(new Character('t'), new Character('\t'));
	}

	private CharacterIterator it;
	private char c;
	private Object token;
	private StringBuffer buf = new StringBuffer();

	private char next() {
		c = it.next();
		return c;
	}

	private void skipWhiteSpace() {
		while (Character.isWhitespace(c)) {
			next();
		}
	}

	public Object read(String string) {
		it = new StringCharacterIterator(string);
		c = it.first();
		return read();
	}

	private Object read() {
		Object ret = null;
		skipWhiteSpace();

		if (c == '"') {
			next();
			ret = string();
		} else if (c == '[') {
			next();
			ret = array();
		} else if (c == ']') {
			ret = ARRAY_END;
		} else if (c == ',') {
			ret = COMMA;
			next();
		} else if (c == '{') {
			next();
			ret = object();
		} else if (c == '}') {
			ret = OBJECT_END;
			next();
		} else if (c == ':') {
			ret = COLON;
			next();
		} else if (c == 't' && next() == 'r' && next() == 'u' && next() == 'e') {
			ret = Boolean.TRUE;
		} else if (c == 'f' && next() == 'a' && next() == 'l' && next() == 's'
				&& next() == 'e') {
			ret = Boolean.FALSE;
		} else if (c == 'n' && next() == 'u' && next() == 'l' && next() == 'l') {
			ret = null;
		} else if (Character.isDigit(c) || c == '-') {
			ret = number();
		}

		token = ret;
		return ret;
	}

	private Object object() {
		Map ret = new HashMap();
		Object key = read();
		while (token != OBJECT_END) {
			read(); // colon
			ret.put(key, read());
			if (read() == COMMA) {
				key = read();
			}
		}

		return ret;
	}

	private Object array() {
		List ret = new ArrayList();
		Object value = read();
		while (token != ARRAY_END) {
			ret.add(value);
			if (read() == COMMA) {
				value = read();
			}
		}
		return ret.toArray();
	}

	private Object number() {
		buf.setLength(0);
		if (c == '-') {
			add();
		}
		addDigits();
		if (c == '.') {
			add();
			addDigits();
		}
		if (c == 'e' || c == 'E') {
			add();
			if (c == '+' || c == '-') {
				add();
			}
			addDigits();
		}

		return new Double(buf.toString());
	}

	private Object string() {
		buf.setLength(0);
		while (c != '"') {
			if (c == '\\') {
				next();
				if (c == 'u') {
					add(unicode());
				} else {
					Object value = escapes.get(new Character(c));
					if (value != null) {
						add(((Character) value).charValue());
					}
				}
			} else {
				add();
			}
		}
		next();

		return buf.toString();
	}

	private void add(char cc) {
		buf.append(cc);
		next();
	}

	private void add() {
		add(c);
	}

	private void addDigits() {
		while (Character.isDigit(c)) {
			add();
		}
	}

	private char unicode() {
		int value = 0;
		for (int i = 0; i < 4; ++i) {
			switch (next()) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				value = (value << 4) + c - '0';
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				value = (value << 4) + c - 'k';
				break;
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				value = (value << 4) + c - 'K';
				break;
			}
		}
		return (char) value;
	}
}