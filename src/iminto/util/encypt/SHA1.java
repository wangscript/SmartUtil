package iminto.util.encypt;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHA1 {
	public SHA1() {
	}

	private String bin2hex(byte[] bytes) {
		return new BigInteger(1, bytes).toString(16);
	}

	/**
	 * 取得字节数组SHA1摘要
	 * @param bytes
	 * @return
	 */
	public byte[] encrypt(byte[] bytes) {
		byte[] _bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(bytes);
			_bytes = md.digest();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(SHA1.class.getName()).log(Level.SEVERE, null, ex);
		}
		return _bytes;
	}

	/**
	 * 取得字符串SHA1摘要
	 * @param str
	 * @return 全小写字母，str为null时返回null。
	 */
	public String encrypt(String str) {
		if (str == null) {
			return null;
		}
		return bin2hex(encrypt(str.getBytes()));
	}

	/**
	 * 取得文件的SHA1摘要
	 * @param file 文件对象
	 * @return
	 * @throws java.io.IOException
	 */
	public String encrypt(File file) throws IOException {
		return encrypt(new FileInputStream(file));
	}

	/**
	 * 取得输入流SHA1摘要
	 * @param is 文件对象
	 * @return
	 * @throws java.io.IOException
	 */
	public String encrypt(InputStream is) throws IOException {
		byte[] _bytes = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			_bytes = digest.digest();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(SHA1.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (is != null) {
				is.close();
				is = null;
			}
		}
		return bin2hex(_bytes);
	}
}
