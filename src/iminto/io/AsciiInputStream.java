package iminto.io;
import iminto.util.common.CharUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Simple InputStream subclass to fetch low order bytes from a String.
 * @see StringInputStream
 * @see CharUtil#toAscii(char) 
 */
public class AsciiInputStream extends InputStream implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int strOffset;
	protected int available;
	protected final String str;

	public AsciiInputStream(String s) {
		str = s;
		available = s.length();
	}

	@Override
	public int read() throws IOException {
		if (available == 0) {
			return -1;
		}
		available--;
		return CharUtil.toAscii(str.charAt(strOffset++));
	}

	@Override
	public int available() throws IOException {
		return available;
	}
}
