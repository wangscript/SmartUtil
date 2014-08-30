package iminto.util.common;
import static iminto.util.Config.Constant.EMPTY;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串帮助类
 */
public final class StringHelper {

    /**
     * 锁定创建
     */
    private StringHelper() {
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0) ? true : false;
    }
    /**
	 * @param cs
	 *            字符串
	 * @return 是不是为空白字符串
	 */
	public static boolean isBlank(CharSequence cs) {
		if (null == cs)
			return true;
		int length = cs.length();
		for (int i = 0; i < length; i++) {
			if (!(Character.isWhitespace(cs.charAt(i))))
				return false;
		}
		return true;
	}

    /**
     * 格式化输出
     * @param str
     * @param args
     * @return
     */
    public static String format(String str, Object... args) {
        return (str == null || str.length() == 0) ? str : String.format(str, args);
    }

    /**
     * 剔除字符串两侧空格
     * @param str
     * @return
     */
    public static String trim(String str) {
        return ltrim(rtrim(str));
    }

    /**
     * 剔除字符串两侧字符
     * @param str
     * @param trimChars 需要剔除的字符
     * @return
     */
    public static String trim(String str, char[] trimChars) {
        return ltrim(rtrim(str, trimChars), trimChars);
    }

    /**
     * 切分字符串
     * @param str
     * @param sep
     * @return  无法切割时返回 0 长数组
     */
    public static String[] split(String str, char sep) {
        if (str == null) {
            return new String[0];
        }
        int length = str.length();
        char ch = 0;
        List<String> strList = new ArrayList<String>();
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            ch = str.charAt(i);
            if (ch == sep) {
                strList.add(strBuilder.toString());
                strBuilder = new StringBuilder();
            } else {
                strBuilder.append(ch);
            }
        }
        strList.add(strBuilder.toString());
        strBuilder = null;
        return strList.toArray(new String[strList.size()]);
    }
    
    public static boolean endsWithChar(String s, char c) {
		if (s.length() == 0) {
			return false;
		}
		return s.charAt(s.length() - 1) == c;
	}
    
    public static String[] split(String s, char c, int limit) {
        if (s == null) return null;
        ArrayList<Integer> pos = new ArrayList<Integer>();
        int i = -1;
        while ((i = s.indexOf((int) c, i + 1)) > 0) {
            pos.add(Integer.valueOf(i));
        }
        int n = pos.size();
        int[] p = new int[n];
        i = -1;
        for (int x : pos) {
            p[++i] = x;
        }
        if ((limit == 0) || (limit > n)) {
            limit = n + 1;
        }
        String[] result = new String[limit];
        if (n > 0) {
            result[0] = s.substring(0, p[0]);
        } else {
            result[0] = s;
        }
        for (i = 1; i < limit - 1; ++i) {
            result[i] = s.substring(p[i - 1] + 1, p[i]);
        }
        if (limit > 1) {
            result[limit - 1] = s.substring(p[limit - 2] + 1);
        }
        return result;
    }
    /**
     * 剔除字符串右侧空格
     * @param str
     * @return
     */
    public static String rtrim(String str) {
        if (str != null) {
            char[] chars = str.toCharArray();
            StringBuilder strBuilder = new StringBuilder();
            char ch;
            boolean isTrimed = false;
            for (int i = chars.length - 1; i >= 0; i--) {
                ch = chars[i];
                if (!isTrimed) {
                    if (Character.isWhitespace(ch)) {
                        continue;
                    } else {
                        isTrimed = true;
                    }
                }
                strBuilder.append(ch);
            }
            strBuilder.reverse();
            str = strBuilder.toString();
        }
        return str;
    }

    /**
     * 剔除字符串右侧字符
     * @param str
     * @param trimChars 需要剔除的字符
     * @return
     */
    public static String rtrim(String str, char[] trimChars) {
        if (str != null && trimChars != null && trimChars.length > 0) {
            char[] chars = str.toCharArray();
            StringBuilder strBuilder = new StringBuilder();
            char ch;
            boolean isTrimed = false;
            for (int i = chars.length - 1; i >= 0; i--) {
                ch = chars[i];
                if (!isTrimed) {
                    if (ArrayHelper.inArray(trimChars, ch)) {
                        continue;
                    } else {
                        isTrimed = true;
                    }
                }
                strBuilder.append(ch);
            }
            strBuilder.reverse();
            str = strBuilder.toString();
        }
        return str;
    }

    /**
     * 剔除字符串左侧空格
     * @param str
     * @return
     */
    public static String ltrim(String str) {
        if (str != null) {
            char[] chars = str.toCharArray();
            StringBuilder strBuilder = new StringBuilder();
            char ch;
            boolean isTrimed = false;
            for (int i = 0; i < chars.length; i++) {
                ch = chars[i];
                if (!isTrimed) {
                    if (Character.isWhitespace(ch)) {
                        continue;
                    } else {
                        isTrimed = true;
                    }
                }
                strBuilder.append(ch);
            }
            str = strBuilder.toString();
        }
        return str;
    }

    /**
     * 剔除字符串左侧字符
     * @param str
     * @param trimChars 需要剔除的字符
     * @return
     */
    public static String ltrim(String str, char[] trimChars) {
        if (str != null && trimChars != null && trimChars.length > 0) {
            char[] chars = str.toCharArray();
            StringBuilder strBuilder = new StringBuilder();
            char ch;
            boolean isTrimed = false;
            for (int i = 0; i < chars.length; i++) {
                ch = chars[i];
                if (!isTrimed) {
                    if (ArrayHelper.inArray(trimChars, ch)) {
                        continue;
                    } else {
                        isTrimed = true;
                    }
                }
                strBuilder.append(ch);
            }
            str = strBuilder.toString();
        }
        return str;
    }

    /**
     * 文本替换
     * @param source 原始字符串
     * @param search 替换字符
     * @param replace 替换目标字符
     * @return
     */
    public static String replace(String source, String search, String replace) {
        source = (source == null) ? "" : source;
        if (search == null) {
            return source;
        }
        replace = replace == null ? "" : replace;
        return Pattern.compile(Pattern.quote(search)).matcher(source).replaceAll(replace);
    }
    /**
	 * Replaces all occurrences of a certain pattern in a string with a
	 * replacement string. This is the fastest replace function known to author.
	 *
	 * @param s      string to be inspected
	 * @param sub    string pattern to be replaced
	 * @param with   string that should go where the pattern was
	 */
	public static String replace1(String s, String sub, String with) {
		int c = 0;
		int i = s.indexOf(sub, c);
		if (i == -1) {
			return s;
		}
		int length = s.length();
		StringBuilder sb = new StringBuilder(length + with.length());
		do {
			sb.append(s.substring(c, i));
			sb.append(with);
			c = i + sub.length();
		} while ((i = s.indexOf(sub, c)) != -1);
		if (c < length) {
			sb.append(s.substring(c, length));
		}
		return sb.toString();
	}

    /**
     * 文本替换
     * @param source 原始字符串
     * @param search 替换字符
     * @param replace 替换目标字符
     * @return
     */
    public static String replaceFirst(String source, String search, String replace) {
        source = (source == null) ? "" : source;
        if (search == null) {
            return source;
        }
        replace = replace == null ? "" : replace;
        return Pattern.compile(Pattern.quote(search)).matcher(source).replaceFirst(replace);
    }

    /**
     * 文本替换，大小写不敏感
     * @param source 原始字符串
     * @param search 替换字符
     * @param replace 替换目标字符
     * @return
     */
    public static String replaceIgnoreCase(String source, String search, String replace) {
        source = (source == null) ? "" : source;
        if (search == null) {
            return source;
        }
        replace = replace == null ? "" : replace;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("((?i)");
        strBuilder.append(Pattern.quote(search));
        strBuilder.append(")");
        return Pattern.compile(strBuilder.toString()).matcher(source).replaceAll(replace);
    }

    /**
     * 文本替换，大小写不敏感
     * @param source 原始字符串
     * @param search 替换字符
     * @param replace 替换目标字符
     * @return
     */
    public static String replaceFirstIgnoreCase(String source, String search, String replace) {
        source = (source == null) ? "" : source;
        if (search == null) {
            return source;
        }
        replace = replace == null ? "" : replace;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("((?i)");
        strBuilder.append(Pattern.quote(search));
        strBuilder.append(")");
        return Pattern.compile(strBuilder.toString()).matcher(source).replaceFirst(replace);
    }

    /**
     * 重复某字符串
     * @param str
     * @param count
     * @return 当str为null时返回null。
     */
    public static String repeat(String source, int count) {
		StringBand result = new StringBand(count);
		while (count > 0) {
			result.append(source);
			count--;
		}
		return result.toString();
	}

	public static String repeat(char c, int count) {
		char[] result = new char[count];
		for (int i = 0; i < count; i++) {
			result[i] = c;
		}
		return new String(result);
	}

    /**
     * 颠倒字符串
     * @param str
     * @return
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 剔除第一次出现的某字符串
     * @param str
     * @param needRemoved
     * @return 当str为null时返回null。
     */
    public static String removeFirst(String str, String needRemoved) {
        if (str == null) {
            return null;
        }
        if (needRemoved == null) {
            return str;
        }
        return str.replaceFirst(regexEscape(needRemoved), "");
    }

    /**
     * 剔除出现的某字符串
     * @param str
     * @param needRemoved
     * @return 当str为null时返回null。
     */
    public static String removeAll(String str, String needRemoved) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(regexEscape(needRemoved), "");
    }

    /**
     * <pre>
     * 字符集转换
     * Convert string to requested character encoding
     *
     * </pre>
     * @param fromCharset 来源字符集 如：ISO-8859-1
     * @param toCharset 目标字符集
     * @param str   需要转换的字符串
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws CharacterCodingException
     */
    public static String iconv(String fromCharset, String toCharset, String str)
            throws CharacterCodingException, UnsupportedEncodingException {
        if (fromCharset.equals(toCharset)) {
            return str;
        }
        return new String(str.getBytes(fromCharset), toCharset);
    }
    /**
	 *首字母大写
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(true, str);
	}

	/**
	 * 首字母小写
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(false, str);
	}

	/**
	 * Internal method for changing the first character case.
	 */
	private static String changeFirstCharacterCase(boolean capitalize, String string) {
		int strLen = string.length();
		if (strLen == 0) {
			return string;
		}

		char ch = string.charAt(0);
		char modifiedCh;
		if (capitalize) {
			modifiedCh = Character.toUpperCase(ch);
		} else {
			modifiedCh = Character.toLowerCase(ch);
		}

		if (modifiedCh == ch) {
			// no change, return unchanged string
			return string;

		}

		char chars[] = string.toCharArray();
		chars[0] = modifiedCh;
		return new String(chars);
	}

    /**
     * 正则转义
     * 注意：与 Pattern.quote()和Matcher.quoteReplacement(str)的区别
     * @see java.util.regex.Matcher#quoteReplacement(java.lang.String)
     * @see java.util.regex.Pattern#quote(java.lang.String)
     * @param str 需要转义的字符串
     * @return
     */
    public static String regexEscape(String str) {
        if (str == null) {
            return null;
        }

        StringBuffer result = new StringBuffer();
        StringCharacterIterator iterator = new StringCharacterIterator(str);
        char _char = iterator.current();
        while (_char != CharacterIterator.DONE) {
            if (_char == '.') {
                result.append("\\.");
            } else if (_char == ':') {
                result.append("\\:");
            } else if (_char == ')') {
                result.append("\\)");
            } else if (_char == '^') {
                result.append("\\^");
            } else if (_char == '$') {
                result.append("\\$");
            } else if (_char == '\\') {
                result.append("\\\\");
            } else if (_char == '{') {
                result.append("\\{");
            } else if (_char == '}') {
                result.append("\\}");
            } else if (_char == '[') {
                result.append("\\[");
            } else if (_char == ']') {
                result.append("\\]");
            } else if (_char == '(') {
                result.append("\\(");
            } else if (_char == '?') {
                result.append("\\?");
            } else if (_char == '*') {
                result.append("\\*");
            } else if (_char == '+') {
                result.append("\\+");
            } else if (_char == '&') {
                result.append("\\&");
            } else if (_char == '-') {
                result.append("\\-");
            } else {
                result.append(_char);
            }
            _char = iterator.next();
        }
        return result.toString();
    }

    /**
     * 
     * <pre>
     * 子字符串，柔化字符串处理。
     * 上下标越界皆以“界”为准。
     * end = -1 则是截取到最终。
     * 如：
     * subString("MoXie",-50,100);
     * 等同于
     * subString("MoXie",0,4)
     *
     * </pre>
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static String subString(String str, int start, int end) {
        int strLen = str.length();
        start = start > strLen ? strLen : start;
        //
        start = start > 0 ? start : 0;
        //
        if (end == -1) {
            end = strLen;
        } else {
            end = end > strLen ? strLen : end;
        }
        //
        return str.substring(start, end);
    }

    /**
     * <pre>
     * 照字面输出
     * Json.org 说明的需要转义的的字符
     * \"   \\  \/  \b  \f  \n  \r  \t \ u four-hex-digits
     * </pre>
     * @param str
     * @return 
     * @see  StringHelper#toUnicode(java.lang.String)
     **/
    public static String utf8_literal(String str) {
        StringBuffer strBuffer = new StringBuffer(str.length());
        CharacterIterator it = new StringCharacterIterator(str);
        for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
            switch (ch) {
                case '"':
                    strBuffer.append("\\\"");
                    break;
                case '\\':
                    strBuffer.append("\\\\");
                    break;
                case '/':
                    strBuffer.append("\\/");
                    break;
                case '\b':
                    strBuffer.append("\\b");
                    break;
                case '\f':
                    strBuffer.append("\\f");
                    break;
                case '\n':
                    strBuffer.append("\\n");
                    break;
                case '\r':
                    strBuffer.append("\\r");
                    break;
                case '\t':
                    strBuffer.append("\\t");
                    break;
                default:
                    if (ch < 26 || ch > 126) {
                        toUnicode(strBuffer, ch);
                    } else {
                        strBuffer.append(ch);
                    }
                    break;
            }
        }
        return strBuffer.toString();
    }
    private static final char[] hexs = "0123456789abcdef".toCharArray();

    /**
     * <pre>
     * Json.org 说明的需要转义的的字符
     * \"   \\  \/  \b  \f  \n  \r  \t \ u four-hex-digits
     * </pre>
     * @see  StringHelper#toUnicode(java.lang.String)
     **/
    private static void toUnicode(StringBuffer strBuffer, char ch) {
        strBuffer.append("\\u");
        /**
         * need more effective
         */
//        strBuffer.append(BigInteger.valueOf(ch).toString(16));
        for (int i = 4; i > 0; i--) {
            strBuffer.append(hexs[(ch & 0xf000) >> 12]);
            ch <<= 4;
        }
    }

    /**
     * 转化字母为转义后的值
     * @param ch
     * @return
     */
    public static char toEscapedVal(char ch) {
        if (ch == 't') {
            ch = '\t';
        } else if (ch == 'r') {
            ch = '\r';
        } else if (ch == 'n') {
            ch = '\n';
        } else if (ch == 'f') {
            ch = '\f';
        } else if (ch == 't') {
            ch = '\t';
        } else if (ch == 'b') {
            ch = '\b';
        }
        return ch;
    }
    /**
	 * @see #indexOfChars(String, String, int)
	 */
	public static int indexOfChars(String string, String chars) {
		return indexOfChars(string, chars, 0);
	}

	/**
	 * Returns the very first index of any char from provided string, starting from specified index offset.
	 * Returns index of founded char, or <code>-1</code> if nothing found.
	 */
	public static int indexOfChars(String string, String chars, int startindex) {
		int stringLen = string.length();
		int charsLen = chars.length();
		if (startindex < 0) {
			startindex = 0;
		}
		for (int i = startindex; i < stringLen; i++) {
			char c = string.charAt(i);
			for (int j = 0; j < charsLen; j++) {
				if (c == chars.charAt(j)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int indexOfChars(String string, char[] chars) {
		return indexOfChars(string, chars, 0);
	}

	/**
	 * Returns the very first index of any char from provided string, starting from specified index offset.
	 * Returns index of founded char, or <code>-1</code> if nothing found.
	 */
	public static int indexOfChars(String string, char[] chars, int startindex) {
		int stringLen = string.length();
		int charsLen = chars.length;
		for (int i = startindex; i < stringLen; i++) {
			char c = string.charAt(i);
			for (int j = 0; j < charsLen; j++) {
				if (c == chars[j]) {
					return i;
				}
			}
		}
		return -1;
	}
	/**
	 * Joins an array of strings into one string.
	 */
	public static String join(String... parts) {
		StringBand sb = new StringBand(parts.length);
		for (String part : parts) {
			sb.append(part);
		}
		return sb.toString();
	}

	/**
	 * Joins list of iterable elements. Separator string
	 * may be <code>null</code>.
	 */
	public static String join(Iterable<?> elements, String separator) {
		if (elements == null) {
			return EMPTY;
		}
		StringBand sb = new StringBand();
		for (Object o : elements) {
			if (sb.length() > 0) {
				if (separator != null) {
					sb.append(separator);
				}
			}
			sb.append(o);
		}
		return sb.toString();
	}
	public static String shorten(String s, int length, String suffix) {
		length -= suffix.length();

		if (s.length() > length) {
			for (int j = length; j >= 0; j--) {
				if (CharUtil.isWhitespace(s.charAt(j))) {
					length = j;
					break;
				}
			}
			String temp = s.substring(0, length);
			s = temp.concat(suffix);
		}

		return s;
	}
	/**
	 * Returns max common prefix of two strings.
	 */
	public static String maxCommonPrefix(String one, String two) {
        final int minLength = Math.min(one.length(), two.length());

        final StringBuilder sb = new StringBuilder(minLength);
        for (int pos = 0; pos < minLength; pos++) {
            final char currentChar = one.charAt(pos);
            if (currentChar != two.charAt(pos)) {
                break;
            }
            sb.append(currentChar);
        }

		return sb.toString();
	}
	public static String insert(String src, String insert) {
		return insert(src, insert, 0);
	}

	/**
	 * Inserts a string on provided offset.
	 */
	public static String insert(String src, String insert, int offset) {
		if (offset < 0) {
			offset = 0;
		}
		if (offset > src.length()) {
			offset = src.length();
		}
		StringBuilder sb = new StringBuilder(src);
		sb.insert(offset, insert);
		return sb.toString();
	}
	/**
	 * Splits a string in several parts (tokens) that are separated by delimiter.
	 * Delimiter is <b>always</b> surrounded by two strings! If there is no
	 * content between two delimiters, empty string will be returned for that
	 * token. Therefore, the length of the returned array will always be:
	 * #delimiters + 1.
	 * <p>
	 * Method is much, much faster then regexp <code>String.split()</code>,
	 * and a bit faster then <code>StringTokenizer</code>.
	 *
	 * @param src       string to split
	 * @param delimiter split delimiter
	 *
	 * @return array of split strings
	 */
	public static String[] split(String src, String delimiter) {
		int maxparts = (src.length() / delimiter.length()) + 2;		// one more for the last
		int[] positions = new int[maxparts];
		int dellen = delimiter.length();

		int i, j = 0;
		int count = 0;
		positions[0] = - dellen;
		while ((i = src.indexOf(delimiter, j)) != -1) {
			count++;
			positions[count] = i;
			j = i + dellen;
		}
		count++;
		positions[count] = src.length();

		String[] result = new String[count];

		for (i = 0; i < count; i++) {
			result[i] = src.substring(positions[i] + dellen, positions[i + 1]);
		}
		return result;
	}

	/**
	 * Splits a string in several parts (tokens) that are separated by delimiter
	 * characters. Delimiter may contains any number of character and it is
	 * always surrounded by two strings.
	 *
	 * @param src    source to examine
	 * @param d      string with delimiter characters
	 *
	 * @return array of tokens
	 */
	public static String[] splitc(String src, String d) {
		if ((d.length() == 0) || (src.length() == 0) ) {
			return new String[] {src};
		}
		char[] delimiters = d.toCharArray();
		char[] srcc = src.toCharArray();

		int maxparts = srcc.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];

		int count = 0;

		start[0] = 0;
		int s = 0, e;
		if (CharUtil.equalsOne(srcc[0], delimiters) == true) {	// string starts with delimiter
			end[0] = 0;
			count++;
			s = CharUtil.findFirstDiff(srcc, 1, delimiters);
			if (s == -1) {							// nothing after delimiters
				return new String[] {EMPTY, EMPTY};
			}
			start[1] = s;							// new start
		}
		while (true) {
			// find new end
			e = CharUtil.findFirstEqual(srcc, s, delimiters);
			if (e == -1) {
				end[count] = srcc.length;
				break;
			}
			end[count] = e;

			// find new start
			count++;
			s = CharUtil.findFirstDiff(srcc, e, delimiters);
			if (s == -1) {
				start[count] = end[count] = srcc.length;
				break;
			}
			start[count] = s;
		}
		count++;
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = src.substring(start[i], end[i]);
		}
		return result;
	}

	/**
	 * Splits a string in several parts (tokens) that are separated by single delimiter
	 * characters. Delimiter is always surrounded by two strings.
	 *
	 * @param src           source to examine
	 * @param delimiter     delimiter character
	 *
	 * @return array of tokens
	 */
	public static String[] splitc(String src, char delimiter) {
		if (src.length() == 0) {
			return new String[] {EMPTY};
		}
		char[] srcc = src.toCharArray();

		int maxparts = srcc.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];

		int count = 0;

		start[0] = 0;
		int s = 0, e;
		if (srcc[0] == delimiter) {	// string starts with delimiter
			end[0] = 0;
			count++;
			s = CharUtil.findFirstDiff(srcc, 1, delimiter);
			if (s == -1) {							// nothing after delimiters
				return new String[] {EMPTY, EMPTY};
			}
			start[1] = s;							// new start
		}
		while (true) {
			// find new end
			e = CharUtil.findFirstEqual(srcc, s, delimiter);
			if (e == -1) {
				end[count] = srcc.length;
				break;
			}
			end[count] = e;

			// find new start
			count++;
			s = CharUtil.findFirstDiff(srcc, e, delimiter);
			if (s == -1) {
				start[count] = end[count] = srcc.length;
				break;
			}
			start[count] = s;
		}
		count++;
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = src.substring(start[i], end[i]);
		}
		return result;
	}
	/**
	 * Finds first occurrence of a substring in the given source but within limited range [start, end).
	 * It is fastest possible code, but still original <code>String.indexOf(String, int)</code>
	 * is much faster (since it uses char[] value directly) and should be used when no range is needed.
	 *
	 * @param src		source string for examination
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		ending index
	 * @return index of founded substring or -1 if substring not found
	 */
	public static int indexOf(String src, String sub, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}
		int sublen = sub.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : startIndex;
		}

		int total = endIndex - sublen + 1;
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (src.charAt(i) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub.charAt(j) != src.charAt(k)) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of a character in the given source but within limited range (start, end].
	 */
	public static int indexOf(String src, char c, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}
		for (int i = startIndex; i < endIndex; i++) {
			if (src.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of a character in the given source but within limited range (start, end].
	 */
	public static int indexOfIgnoreCase(String src, char c, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}
		c = Character.toLowerCase(c);
		for (int i = startIndex; i < endIndex; i++) {
			if (Character.toLowerCase(src.charAt(i)) == c) {
				return i;
			}
		}
		return -1;
	}



	/**
	 * Finds first index of a substring in the given source string with ignored case.
	 *
	 * @param src    source string for examination
	 * @param subS   substring to find
	 *
	 * @return index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 */
	public static int indexOfIgnoreCase(String src, String subS) {
		return indexOfIgnoreCase(src, subS, 0, src.length());
	}

	/**
	 * Finds first index of a substring in the given source string with ignored
	 * case. This seems to be the fastest way doing this, with common string
	 * length and content (of course, with no use of Boyer-Mayer type of
	 * algorithms). Other implementations are slower: getting char array first,
	 * lower casing the source string, using String.regionMatch etc.
	 *
	 * @param src        source string for examination
	 * @param subS       substring to find
	 * @param startIndex starting index from where search begins
	 *
	 * @return index of founded substring or -1 if substring is not found
	 */
	public static int indexOfIgnoreCase(String src, String subS, int startIndex) {
		return indexOfIgnoreCase(src, subS, startIndex, src.length());
	}
	/**
	 * Finds first index of a substring in the given source string and range with
	 * ignored case.
	 *
	 * @param src		source string for examination
	 * @param sub		substring to find
	 * @param startIndex	starting index from where search begins
	 * @param endIndex		endint index
	 * @return index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 */
	public static int indexOfIgnoreCase(String src, String sub, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}

		int sublen = sub.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : startIndex;
		}
		sub = sub.toLowerCase();
		int total = endIndex - sublen + 1;
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (Character.toLowerCase(src.charAt(i)) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				char source = Character.toLowerCase(src.charAt(k));
				if (sub.charAt(j) != source) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}


	/**
	 * Finds last index of a substring in the given source string with ignored
	 * case.
	 *
	 * @param s      source string
	 * @param subS   substring to find
	 *
	 * @return last index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 * @see #lastIndexOfIgnoreCase(String, String, int)
	 */
	public static int lastIndexOfIgnoreCase(String s, String subS) {
		return lastIndexOfIgnoreCase(s, subS, s.length(), 0);
	}

	/**
	 * Finds last index of a substring in the given source string with ignored
	 * case.
	 *
	 * @param src        source string for examination
	 * @param subS       substring to find
	 * @param startIndex starting index from where search begins
	 *
	 * @return last index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 */
	public static int lastIndexOfIgnoreCase(String src, String subS, int startIndex) {
		return lastIndexOfIgnoreCase(src, subS, startIndex, 0);
	}
	/**
	 * Finds last index of a substring in the given source string with ignored
	 * case in specified range.
	 *
	 * @param src		source to examine
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		end index
	 * @return last index of founded substring or -1 if substring is not found
	 */
	public static int lastIndexOfIgnoreCase(String src, String sub, int startIndex, int endIndex) {
		int sublen = sub.length();
		int srclen = src.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : (startIndex < -1 ? -1 : startIndex);
		}
		sub = sub.toLowerCase();
		int total = srclen - sublen;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.toLowerCase(src.charAt(i)) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				char source = Character.toLowerCase(src.charAt(k));
				if (sub.charAt(j) != source) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds last index of a substring in the given source string in specified range [end, start]
	 * See {@link #indexOf(String, String, int, int)}  for details about the speed.
	 *
	 * @param src		source to examine
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		end index
	 * @return last index of founded substring or -1 if substring is not found
	 */
	public static int lastIndexOf(String src, String sub, int startIndex, int endIndex) {
		int sublen = sub.length();
		int srclen = src.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : (startIndex < -1 ? -1 : startIndex);
		}
		int total = srclen - sublen;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i >= endIndex; i--) {
			if (src.charAt(i) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub.charAt(j) != src.charAt(k)) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds last index of a character in the given source string in specified range [end, start]
	 */
	public static int lastIndexOf(String src, char c, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		for (int i = startIndex; i >= endIndex; i--) {
			if (src.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds last index of a character in the given source string in specified range [end, start]
	 */
	public static int lastIndexOfIgnoreCase(String src, char c, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		c = Character.toLowerCase(c);
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.toLowerCase(src.charAt(i)) == c) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOfWhitespace(String src) {
		return lastIndexOfWhitespace(src, src.length(), 0);
	}

	/**
	 * Returns last index of a whitespace.
	 */
	public static int lastIndexOfWhitespace(String src, int startIndex) {
		return lastIndexOfWhitespace(src, startIndex, 0);
	}

	/**
	 * Returns last index of a whitespace.
	 */
	public static int lastIndexOfWhitespace(String src, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.isWhitespace(src.charAt(i))) {
				return i;
			}
		}
		return -1;
	}


	public static int lastIndexOfNonWhitespace(String src) {
		return lastIndexOfNonWhitespace(src, src.length(), 0);
	}
	public static int lastIndexOfNonWhitespace(String src, int startIndex) {
		return lastIndexOfNonWhitespace(src, startIndex, 0);
	}
	public static int lastIndexOfNonWhitespace(String src, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.isWhitespace(src.charAt(i)) == false) {
				return i;
			}
		}
		return -1;
	}
	
	public static String CutString(String str, int startIndex, int length) {
		if (startIndex >= 0) {
			if (length < 0) {
				length = length * -1;
				if (startIndex - length < 0) {
					length = startIndex;
					startIndex = 0;
				} else {
					startIndex = startIndex - length;
				}
			}
			if (startIndex > str.length()) {
				return "";
			}

		} else {
			if (length < 0) {
				return "";
			} else {
				if (length + startIndex > 0) {
					length = length + startIndex;
					startIndex = 0;
				} else {
					return "";
				}
			}
		}

		if (str.length() - startIndex < length) {
			length = str.length() - startIndex;
		}
		return str.substring(startIndex, startIndex + length);
	}
	
}
