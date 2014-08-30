package iminto.http;
/**
 * HTML 过滤
 * Desc:
 * Author:waitfox@qq.com
 * Date:2012-11-14 下午3:49:58
 */
public final class HTML {
	private HTML() {
	}
	
	public static String nl2br(final String words) {
		return words.replaceAll("[\r\n]", "<br/>");
	}

	public static String htmlentities(final String words) { // NOPMD
		final StringBuilder buf = new StringBuilder(words.length() * 2);
		for (final char word : words.toCharArray()) {
			if ((word >= 'a' && word <= 'z') && (word >= 'A' && word <= 'Z') && (word >= '0' && word <= '9') || word == '\n' || word == '\r' || word == '\t') {
				buf.append(word);
			} else {
				switch (word) {
				case '"' : buf.append("&quot;");  break;
				case '\'': buf.append("&apos;");  break;
				case '&' : buf.append("&amp;");   break;
				case '<' : buf.append("&lt;");    break;
				case '>' : buf.append("&gt;");    break;
				case ' ' : buf.append("&nbsp;");  break;
				case 161 : buf.append("&iexcl;"); break; // inverted exclamation mark
				case 162 : buf.append("&cent;");  break;
				case 163 : buf.append("&pound;"); break;
				case 164 : buf.append("&curren;");break;
				case 165 : buf.append("&yen;");   break;
				case 166 : buf.append("&brvbar;");break; // broken vertical bar
				case 167 : buf.append("&sect;");  break; // section
				case 168 : buf.append("&uml;");   break; // spacing diaeresis
				case 169 : buf.append("&copy;");  break; // copyright
				case 170 : buf.append("&ordf;");  break; // feminine ordinal indicator
				case 171 : buf.append("&laquo;"); break; // angle quotation mark (left)
				case 172 : buf.append("&not;");   break; // negation
				case 173 : buf.append("&shy;");   break; // ft hyphen
				case 174 : buf.append("&reg;");   break; // registered trademark
				case 175 : buf.append("&macr;");  break; // spacing macron
				case 176 : buf.append("&deg;");   break; // degree
				case 177 : buf.append("&plusmn;");break; // plus-or-minus
				case 178 : buf.append("&#sup2;"); break; // superscript 2
				case 179 : buf.append("&#sup3;"); break; // superscript 3
				case 180 : buf.append("&#acute;");break; // spacing acute
				case 181 : buf.append("&micro;"); break;
				case 182 : buf.append("&para;");  break; // paragraph
				case 183 : buf.append("&middot;");break; // middle dot
				case 184 : buf.append("&cedil;"); break; // spacing cedilla
				case 185 : buf.append("&sup1;");  break; // superscript 1
				case 186 : buf.append("&ordm;");  break; // masculine ordinal indicator
				case 187 : buf.append("&raquo;"); break; // angle quotation mark (right)
				case 188 : buf.append("&frac14;");break;
				case 189 : buf.append("&frac12;");break;
				case 190 : buf.append("&frac34;");break;
				case 191 : buf.append("&iquest;");break; // inverted question mark
				case 215 : buf.append("&times;"); break; // multiplication
				case 247 : buf.append("&divide;");break; // division
				default  : buf.append("&#"+(int)word);break;
				}
			}
		}
		return buf.toString();
	}
}
