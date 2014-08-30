package iminto.io;
import static iminto.util.Config.Constant.ioBufferSize;
import iminto.util.Config.Constant;
import iminto.util.common.FileHelper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Optimized byte and character stream utilities.
 */
public class StreamUtil {
	private static final int BUF_SIZE = 8192;


	/**
	 * Closes an input stream and releases any system resources associated with
	 * this stream. No exception will be thrown if an I/O error occurs.
	 */
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	/**
	 * Closes an output stream and releases any system resources associated with
	 * this stream. No exception will be thrown if an I/O error occurs.
	 */
	public static void close(OutputStream out) {
		if (out != null) {
			try {
				out.flush();
			} catch (IOException ioex) {
				// ignore
			}
			try {
				out.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	/**
	 * Closes a character-input stream and releases any system resources
	 * associated with this stream. No exception will be thrown if an I/O error
	 * occurs.
	 */
	public static void close(Reader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	/**
	 * Closes a character-output stream and releases any system resources
	 * associated with this stream. No exception will be thrown if an I/O error
	 * occurs.
	 */
	public static void close(Writer out) {
		if (out != null) {
			try {
				out.flush();
			} catch (IOException ioex) {
				// ignore
			}
			try {
				out.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	// ---------------------------------------------------------------- copy

	/**
	 * Copies input stream to output stream using buffer. Streams don't have to
	 * be wrapped to buffered, since copying is already optimized.
	 */
	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[ioBufferSize];
		int count = 0;
		int read;
		while (true) {
			read = input.read(buffer, 0, ioBufferSize);
			if (read == -1) {
				break;
			}
			output.write(buffer, 0, read);
			count += read;
		}
		return count;
	}

	/**
	 * Copies specified number of bytes from input stream to output stream using
	 * buffer.
	 */
	public static int copy(InputStream input, OutputStream output, int byteCount)
			throws IOException {
		byte buffer[] = new byte[ioBufferSize];
		int count = 0;
		int read;
		while (byteCount > 0) {
			if (byteCount < ioBufferSize) {
				read = input.read(buffer, 0, byteCount);
			} else {
				read = input.read(buffer, 0, ioBufferSize);
			}
			if (read == -1) {
				break;
			}
			byteCount -= read;
			count += read;
			output.write(buffer, 0, read);
		}
		return count;
	}

	/**
	 * 将输出流写入一个输出流。
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输入/出流
	 * 
	 * @param ops
	 *            输出流
	 * @param ins
	 *            输入流
	 * @param bufferSize
	 *            缓冲块大小
	 * 
	 * @return 写入的字节数
	 * 
	 * @throws IOException
	 */
	public static int write(OutputStream ops, InputStream ins, int bufferSize)
			throws IOException {
		if (null == ops || null == ins)
			return 0;

		byte[] buf = new byte[bufferSize];
		int len;
		int re = 0;
		while (-1 != (len = ins.read(buf))) {
			re += len;
			ops.write(buf, 0, len);
		}
		ops.flush();
		return re;
	}

	/**
	 * 关闭一个可关闭对象，可以接受 null。如果成功关闭，返回 true，发生异常 返回 false
	 * 
	 * @param cb
	 *            可关闭对象
	 * @return 是否成功关闭
	 */
	public static boolean safeClose(Closeable cb) {
		if (null != cb)
			try {
				cb.close();
			} catch (IOException e) {
				return false;
			}
		return true;
	}

	public static void safeFlush(Flushable fa) {
		if (null != fa)
			try {
				fa.flush();
			} catch (IOException e) {
			}
	}

	/**
	 * 将一段文本全部写入一个writer。
	 * <p>
	 * <b style=color:red>注意</b>，它会关闭输出流
	 * 
	 * @param writer
	 *            输出流
	 * @param cs
	 *            文本
	 */
	public static void writeAndClose(Writer writer, CharSequence cs) {
		try {
			write(writer, cs);
		} catch (IOException e) {
		} finally {
			safeClose(writer);
		}
	}

	/**
	 * 将输出流写入一个输出流。块大小为 8192
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输入/出流
	 * 
	 * @param ops
	 *            输出流
	 * @param ins
	 *            输入流
	 * 
	 * @return 写入的字节数
	 * @throws IOException
	 */
	public static int write(OutputStream ops, InputStream ins)
			throws IOException {
		return write(ops, ins, 8192);
	}

	/**
	 * Copies input stream to writer using buffer.
	 */
	public static void copy(InputStream input, Writer output)
			throws IOException {
		copy(input, output, Constant.CHARSET);
	}

	/**
	 * Copies specified number of bytes from input stream to writer using
	 * buffer.
	 */
	public static void copy(InputStream input, Writer output, int byteCount)
			throws IOException {
		copy(input, output, Constant.CHARSET, byteCount);
	}

	/**
	 * Copies input stream to writer using buffer and specified encoding.
	 */
	public static void copy(InputStream input, Writer output, String encoding)
			throws IOException {
		copy(new InputStreamReader(input, encoding), output);
	}

	/**
	 * Copies specified number of bytes from input stream to writer using buffer
	 * and specified encoding.
	 */
	public static void copy(InputStream input, Writer output, String encoding,
			int byteCount) throws IOException {
		copy(new InputStreamReader(input, encoding), output, byteCount);
	}

	/**
	 * Copies reader to writer using buffer. Streams don't have to be wrapped to
	 * buffered, since copying is already optimized.
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[ioBufferSize];
		int count = 0;
		int read;
		while ((read = input.read(buffer, 0, ioBufferSize)) >= 0) {
			output.write(buffer, 0, read);
			count += read;
		}
		output.flush();
		return count;
	}

	/**
	 * Copies specified number of characters from reader to writer using buffer.
	 */
	public static int copy(Reader input, Writer output, int charCount)
			throws IOException {
		char buffer[] = new char[ioBufferSize];
		int count = 0;
		int read;
		while (charCount > 0) {
			if (charCount < ioBufferSize) {
				read = input.read(buffer, 0, charCount);
			} else {
				read = input.read(buffer, 0, ioBufferSize);
			}
			if (read == -1) {
				break;
			}
			charCount -= read;
			count += read;
			output.write(buffer, 0, read);
		}
		return count;
	}

	/**
	 * Copies reader to output stream using buffer.
	 */
	public static void copy(Reader input, OutputStream output)
			throws IOException {
		copy(input, output, Constant.CHARSET);
	}

	/**
	 * Copies specified number of characters from reader to output stream using
	 * buffer.
	 */
	public static void copy(Reader input, OutputStream output, int charCount)
			throws IOException {
		copy(input, output, Constant.CHARSET, charCount);
	}

	/**
	 * Copies reader to output stream using buffer and specified encoding.
	 */
	public static void copy(Reader input, OutputStream output, String encoding)
			throws IOException {
		Writer out = new OutputStreamWriter(output, encoding);
		copy(input, out);
		out.flush();
	}

	/**
	 * Copies specified number of characters from reader to output stream using
	 * buffer and specified encoding.
	 */
	public static void copy(Reader input, OutputStream output, String encoding,
			int charCount) throws IOException {
		Writer out = new OutputStreamWriter(output, encoding);
		copy(input, out, charCount);
		out.flush();
	}

	// ---------------------------------------------------------------- read
	// bytes

	/**
	 * Reads all available bytes from InputStream as a byte array. Uses
	 * <code>in.available()</code> to determine the size of input stream. This
	 * is the fastest method for reading input stream to byte array, but depends
	 * on stream implementation of <code>available()</code>. Buffered
	 * internally.
	 */
	public static byte[] readAvailableBytes(InputStream in) throws IOException {
		int l = in.available();
		byte byteArray[] = new byte[l];
		int i = 0, j;
		while ((i < l) && (j = in.read(byteArray, i, l - i)) >= 0) {
			i += j;
		}
		if (i < l) {
			throw new IOException("Failed to completely read input stream");
		}
		return byteArray;
	}

	public static byte[] readBytes(InputStream input) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	public static byte[] readBytes(InputStream input, int byteCount)
			throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, byteCount);
		return output.toByteArray();
	}

	public static byte[] readBytes(Reader input) throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	public static byte[] readBytes(Reader input, int byteCount)
			throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, byteCount);
		return output.toByteArray();
	}

	public static byte[] readBytes(Reader input, String encoding)
			throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, encoding);
		return output.toByteArray();
	}

	public static byte[] readBytes(Reader input, String encoding, int byteCount)
			throws IOException {
		FastByteArrayOutputStream output = new FastByteArrayOutputStream();
		copy(input, output, encoding, byteCount);
		return output.toByteArray();
	}

	// ---------------------------------------------------------------- read
	// chars

	public static char[] readChars(InputStream input) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output);
		return output.toCharArray();
	}

	public static char[] readChars(InputStream input, int charCount)
			throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output, charCount);
		return output.toCharArray();
	}

	public static char[] readChars(InputStream input, String encoding)
			throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output, encoding);
		return output.toCharArray();
	}

	public static char[] readChars(InputStream input, String encoding,
			int charCount) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output, encoding, charCount);
		return output.toCharArray();
	}

	public static char[] readChars(Reader input) throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output);
		return output.toCharArray();
	}

	public static char[] readChars(Reader input, int charCount)
			throws IOException {
		FastCharArrayWriter output = new FastCharArrayWriter();
		copy(input, output, charCount);
		return output.toCharArray();
	}

	// ---------------------------------------------------------------- compare
	// content

	/**
	 * Compares the content of two byte streams.
	 * 
	 * @return <code>true</code> if the content of the first stream is equal to
	 *         the content of the second stream.
	 */
	public static boolean compare(InputStream input1, InputStream input2)
			throws IOException {
		if (!(input1 instanceof BufferedInputStream)) {
			input1 = new BufferedInputStream(input1);
		}
		if (!(input2 instanceof BufferedInputStream)) {
			input2 = new BufferedInputStream(input2);
		}
		int ch = input1.read();
		while (ch != -1) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
		}
		int ch2 = input2.read();
		return (ch2 == -1);
	}

	/**
	 * Compares the content of two character streams.
	 * 
	 * @return <code>true</code> if the content of the first stream is equal to
	 *         the content of the second stream.
	 */
	public static boolean compare(Reader input1, Reader input2)
			throws IOException {
		if (!(input1 instanceof BufferedReader)) {
			input1 = new BufferedReader(input1);
		}
		if (!(input2 instanceof BufferedReader)) {
			input2 = new BufferedReader(input2);
		}

		int ch = input1.read();
		while (ch != -1) {
			int ch2 = input2.read();
			if (ch != ch2) {
				return false;
			}
			ch = input1.read();
		}
		int ch2 = input2.read();
		return (ch2 == -1);
	}

	/**
	 * 将一段文本全部写入一个writer。
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输出流
	 * 
	 * @param writer
	 * 
	 * @param cs
	 *            文本
	 * @throws IOException
	 */
	public static void write(Writer writer, CharSequence cs) throws IOException {
		if (null != cs && null != writer) {
			writer.write(cs.toString());
			writer.flush();
		}
	}

	/**
	 * 将文本输出流写入一个文本输出流。块大小为 8192
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输入/出流
	 * 
	 * @param writer
	 *            输出流
	 * @param reader
	 *            输入流
	 * @throws IOException
	 */
	public static void write(Writer writer, Reader reader) throws IOException {
		if (null == writer || null == reader)
			return;

		char[] cbuf = new char[BUF_SIZE];
		int len;
		while (-1 != (len = reader.read(cbuf))) {
			writer.write(cbuf, 0, len);
		}
	}

	/**
	 * 从一个文本流中读取全部内容并返回
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输出流
	 * 
	 * @param reader
	 *            文本输出流
	 * @return 文本内容
	 * @throws IOException
	 */
	public static StringBuilder read(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] cbuf = new char[BUF_SIZE];
		int len;
		while (-1 != (len = reader.read(cbuf))) {
			sb.append(cbuf, 0, len);
		}
		return sb;
	}

	/**
	 * 从一个文本流中读取全部内容并返回
	 * <p>
	 * <b style=color:red>注意</b>，它会关闭输入流
	 * 
	 * @param reader
	 *            文本输入流
	 * @return 文本内容
	 * @throws IOException
	 */
	public static String readAndClose(Reader reader) {
		String string = null;
		try {
			string = read(reader).toString();
		} catch (IOException e) {
		}
		return string;
	}

	/**
	 * 读取一个输入流中所有的字节，并关闭输入流
	 * 
	 * @param ins
	 *            输入流，必须支持 available()
	 * @return 一个字节数组
	 * @throws IOException
	 */
	public static byte[] readBytesAndClose(InputStream ins) {
		byte[] bytes = null;
		try {
			bytes = readBytes(ins);
		} catch (IOException e) {
		} finally {
			safeClose(ins);
		}
		return bytes;
	}

	/**
	 * 为一个输入流包裹一个缓冲流。如果这个输入流本身就是缓冲流，则直接返回
	 * 
	 * @param ins
	 *            输入流。
	 * @return 缓冲输入流
	 */
	public static BufferedInputStream buff(InputStream ins) {
		if (ins == null)
			throw new NullPointerException("ins is null!");
		if (ins instanceof BufferedInputStream)
			return (BufferedInputStream) ins;
		// BufferedInputStream的构造方法,竟然是允许null参数的!! 我&$#^$&%
		return new BufferedInputStream(ins);
	}

	/**
	 * 为一个输出流包裹一个缓冲流。如果这个输出流本身就是缓冲流，则直接返回
	 * 
	 * @param ops
	 *            输出流。
	 * @return 缓冲输出流
	 */
	public static BufferedOutputStream buff(OutputStream ops) {
		if (ops == null)
			throw new NullPointerException("ops is null!");
		if (ops instanceof BufferedOutputStream)
			return (BufferedOutputStream) ops;
		return new BufferedOutputStream(ops);
	}

	/**
	 * 为一个文本输入流包裹一个缓冲流。如果这个输入流本身就是缓冲流，则直接返回
	 * 
	 * @param reader
	 *            文本输入流。
	 * @return 缓冲文本输入流
	 */
	public static BufferedReader buffr(Reader reader) {
		if (reader instanceof BufferedReader)
			return (BufferedReader) reader;
		return new BufferedReader(reader);
	}

	/**
	 * 为一个文本输出流包裹一个缓冲流。如果这个文本输出流本身就是缓冲流，则直接返回
	 * 
	 * @param ops
	 *            文本输出流。
	 * @return 缓冲文本输出流
	 */
	public static BufferedWriter buffw(Writer ops) {
		if (ops instanceof BufferedWriter)
			return (BufferedWriter) ops;
		return new BufferedWriter(ops);
	}

	private static final byte[] UTF_BOM = new byte[] { (byte) 0xEF,
			(byte) 0xBB, (byte) 0xBF };

	/**
	 * 判断并移除UTF-8的BOM头
	 * 
	 * @throws IOException
	 */
	public static InputStream utf8filte(InputStream in) throws IOException {
		if (in.available() == -1)
			return in;
		PushbackInputStream pis = new PushbackInputStream(in, 3);
		byte[] header = new byte[3];
		int len = pis.read(header, 0, 3);
		if (len < 1)
			return in;
		if (header[0] != UTF_BOM[0] || header[1] != UTF_BOM[1]
				|| header[2] != UTF_BOM[2]) {
			pis.unread(header, 0, len);
		}
		return pis;
	}

	/**
	 * 根据一个文件路径建立一个输出流
	 * 
	 * @param path
	 *            文件路径
	 * @return 输出流
	 * @throws FileNotFoundException
	 */
	public static OutputStream fileOut(String path)
			throws FileNotFoundException {
		return fileOut(FileHelper.findFile(path));
	}

	/**
	 * 根据一个文件建立一个输出流
	 * 
	 * @param file
	 *            文件
	 * @return 输出流
	 * @throws FileNotFoundException
	 */
	public static OutputStream fileOut(File file) throws FileNotFoundException {
		return buff(new FileOutputStream(file));
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8 文本输出流
	 * 
	 * @param path
	 *            文件路径
	 * @return 文本输出流
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static Writer fileOutw(String path)
			throws UnsupportedEncodingException, FileNotFoundException {
		return fileOutw(FileHelper.findFile(path));
	}

	/**
	 * 根据一个文件建立一个 UTF-8 文本输出流
	 * 
	 * @param file
	 *            文件
	 * @return 输出流
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static Writer fileOutw(File file)
			throws UnsupportedEncodingException, FileNotFoundException {
		return utf8w(fileOut(file));
	}

	public static Reader utf8r(InputStream is) throws IOException {
		return new InputStreamReader(utf8filte(is), Constant.CHARSET);
	}

	public static Writer utf8w(OutputStream os)
			throws UnsupportedEncodingException {
		return new OutputStreamWriter(os, Constant.CHARSET);
	}

	public static int writeAndClose(OutputStream ops, InputStream ins) {
		int i = 0;
		try {
			i = write(ops, ins);
			return i;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			safeClose(ops);
			safeClose(ins);
		}
		return i;
	}
	public static void writeAndClose(OutputStream ops, byte[] bytes) {
		try {
			write(ops, bytes);
		}
		catch (IOException e) {
			
		}
		finally {
			safeClose(ops);
		}
	}
	/**
	 * 将一个字节数组写入一个输出流。
	 * <p>
	 * <b style=color:red>注意</b>，它并不会关闭输出流
	 * 
	 * @param ops
	 *            输出流
	 * @param bytes
	 *            字节数组
	 * @throws IOException
	 */
	public static void write(OutputStream ops, byte[] bytes) throws IOException {
		if (null == ops || null == bytes || bytes.length == 0)
			return;
		ops.write(bytes);
	}
	public static void writeAndClose(Writer writer, Reader reader) {
		try {
			write(writer, reader);
		}
		catch (IOException e) {
			
		}
		finally {
			safeClose(writer);
			safeClose(reader);
		}
	}
	
	public static BufferedReader Stream2Reader(InputStream a){
		return new BufferedReader(new InputStreamReader(a));
	}
}
