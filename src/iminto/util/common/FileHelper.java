package iminto.util.common;

import iminto.io.StreamUtil;
import iminto.util.Config.Constant;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * <pre>
 * 文件操作辅助类
 * 注意：某些操作比较危险，如 {@link   tryDelete(java.io.File, boolean)}
 * </pre>
 */
public class FileHelper {

	/**
	 * 锁定创建
	 */
	private FileHelper() {
	}

	/**
	 * 目标文件存在则替换
	 */
	public static final int COPY_REPLACE = ParamHelper.genParam(0);
	/**
	 * 复制成功则删除源文件
	 */
	public static final int COPY_DELETE_ORIGINAL = ParamHelper.genParam(1);
	/**
	 * 复制成功则删除源文件时递归删除
	 */
	public static final int COPY_DELETE_ORIGINAL_RECURSIVE = ParamHelper
			.genParam(2);
	/**
	 * 当目标文件夹不存在时自动创建（递归式创建）
	 */
	public static final int COPY_DIR_AUTOCREATE = ParamHelper.genParam(3);

	/**
	 * <pre>
	 * 移动文件
	 * 注意：源文件将会被删除，目标文件自动建立，且会覆盖已有文件。
	 * </pre>
	 * 
	 * @param original
	 * @param target
	 * @throws IOException
	 */
	public static void move(File original, File target) throws IOException {
		copy(original, target, COPY_DELETE_ORIGINAL & COPY_DIR_AUTOCREATE
				& COPY_REPLACE);
	}

	/**
	 * <pre>
	 * 文件复制
	 *      目标目录不存在自动创建
	 *      目标文件存在则立即中断，即不覆盖
	 * </pre>
	 * 
	 * @param original
	 *            源文件
	 * @param target
	 *            目标文件
	 * @throws IOException
	 */
	public static void copy(File original, File target) throws IOException {
		copy(original, target, COPY_DIR_AUTOCREATE);
	}

	/**
	 * <pre>
	 * 文件/目录 的 复制/移动
	 * 文件 -> 文件 覆盖/非覆盖
	 * 文件 -> 目录 
	 * 目录 -> 目录 互不嵌套
	 * 注意：
	 *      1、源为目录，目标不存在，则目标被创建为目录。
	 *      2、源为文件，目标不存在，则目标被创建为文件。
	 * </pre>
	 * 
	 * @see #COPY_REPLACE
	 * @see #COPY_DELETE_ORIGINAL
	 * @see #COPY_DELETE_ORIGINAL_RECURSIVE
	 * @see #COPY_DIR_AUTOCREATE
	 * @param original
	 * @param options
	 * @param target
	 * @throws IOException
	 */
	public static void copy(File original, File target, int options)
			throws IOException {
		do {

			/**
			 * 目标与源相同
			 */
			if (target.equals(original)) {
				break;
			}
			/**
			 * 源文件不存在,无法复制
			 */
			if (!original.exists()) {
				throw new FileNotFoundException(original.getAbsolutePath());
			}
			/**
			 * 目标文件存在且不允许替换
			 */
			if (target.exists() && !ParamHelper.contain(options, COPY_REPLACE)) {
				break;
			}
			/**
			 * 目标文件目录不存在且不允许创建
			 */
			if (!target.exists()
					&& !ParamHelper.contain(options, COPY_DIR_AUTOCREATE)) {
				break;
			} else if (!target.exists()) {
				if (original.isDirectory()) {
					target.mkdirs();
				} else {
					tryCreate(target);
				}
			}
			/**
			 * 文件夹到文件，不可复制
			 */
			if (original.isDirectory() && target.isFile()) {
				break;
			}
			/**
			 * 文件到未知类型 转为 文件到文件
			 */
			if (original.isFile() && !target.isDirectory()) {
				target.createNewFile();
			}
			/**
			 * 文件夹到未知类型 转为 文件夹到文件夹
			 */
			if (original.isDirectory() && !target.isFile()) {
				if (!target.isDirectory()) {
					target.mkdirs();
				}
			}
			String oriDir = original.getAbsolutePath();
			String tarDir = target.getAbsolutePath();

			/**
			 * 目录有包含关系，不可复制
			 */
			if (original.isDirectory() && target.isDirectory()) {

				if (oriDir.endsWith(tarDir) || tarDir.endsWith(oriDir)) {
					break;
				}
				/**
				 * 目录 到 目录的复制
				 */
				List<File> oriList = listFilesRecusive(original);
				File copyFile;
				for (File oriFile : oriList) {
					copyFile = new File(oriFile.getAbsolutePath().replace(
							oriDir, tarDir));
					if (copyFile.exists()
							&& !ParamHelper.contain(options,
									COPY_DIR_AUTOCREATE)) {
						continue;
					}
					if (!copyFile.exists()) {
						if (!ParamHelper.contain(options, COPY_DIR_AUTOCREATE)) {
							continue;
						} else {
							tryCreate(copyFile);
						}
					}
					_copy(oriFile, copyFile);
				}
			}
			/**
			 * 文件 到 文件 的复制
			 */
			if (original.isFile() && target.isFile()) {
				_copy(original, target);
				/**
				 * 文件到目录的复制
				 */
			} else if (original.isFile() && target.isDirectory()) {
				_copy(original, new File(backToslash(target.getAbsolutePath()
						+ '/' + original.getName())));
			}
		} while (false);
		/**
		 * 删除源文件
		 */
		if (ParamHelper.contain(options, COPY_DELETE_ORIGINAL)) {
			tryDelete(original, false);
		} else if (ParamHelper.contain(options, COPY_DELETE_ORIGINAL_RECURSIVE)) {
			tryDelete(original, true);
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param originalFile
	 * @param targetFile
	 * @throws java.io.IOException
	 */
	private static void _copy(File originalFile, File targetFile)
			throws IOException {
		FileLocker flockOri = new FileLocker(originalFile);
		FileLocker flockTar = new FileLocker(targetFile);
		InputStream is = null;
		BufferedOutputStream bout = null;
		try {
			flockOri.lockRead();
			flockTar.lockWrite();
			is = new FileInputStream(originalFile);
			bout = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] buffer = new byte[Constant.BUFFER_BYTE_SIZE];
			int read = 0;
			while ((read = is.read(buffer)) > 0) {
				bout.write(buffer, 0, read);
			}
			buffer = null;
			bout.flush();
		} finally {
			if (is != null) {
				is.close();
				is = null;
			}
			if (bout != null) {
				bout.close();
				bout = null;
			}
		}
		flockOri.releaseRead();
		flockTar.releaseWrite();
	}

	/**
	 * <pre>
	 * 尝试建立目录。
	 * 父级目录将被自动创建。
	 * 注意：此方法使用了 {@link FileLocker}，嵌套使用 {@link FileLocker} 时可能造成永久锁。
	 * @see FileLocker#writeLock(java.io.File)
	 * </pre>
	 * 
	 * @param dir
	 */
	public static void tryMakeDirs(File dir) {
		FileLocker flock = new FileLocker(dir);
		try {
			flock.lockWrite();
			if (!dir.isFile()) {
				dir.mkdirs();
			}
		} finally {
			flock.releaseWrite();
		}
	}

	/**
	 * <pre>
	 * 设定文件的访问和修改时间
	 * 注意：此方法使用了 {@link FileLocker}，嵌套使用 {@link FileLocker} 时可能造成永久锁。
	 * @see FileLocker#writeLock(java.io.File)
	 * </pre>
	 * 
	 * @param file
	 */
	public static void touch(File file) throws IOException {
		FileLocker flock = new FileLocker(file);
		try {
			flock.lockWrite();
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
			}
		} finally {
			flock.releaseWrite();
		}
	}

	/**
	 * <pre>
	 * 尝试自动建立文件。
	 * 父级目录将被自动创建。
	 * 注意：此方法使用了 {@link FileLocker}，嵌套使用 {@link FileLocker} 时可能造成永久锁。
	 * @see FileLocker#writeLock(java.io.File)
	 * </pre>
	 * 
	 * @param file
	 * @throws java.io.IOException
	 */
	public static void tryCreate(File file) throws IOException {
		FileLocker flock = new FileLocker(file);
		try {
			flock.lockWrite();
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				if (!file.isDirectory() && !file.isFile()) {
					file.createNewFile();
				}
			}
		} finally {
			flock.releaseWrite();
		}

	}

	/**
	 * <pre>
	 * 递归的列出文件
	 * </pre>
	 * 
	 * @param file
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<File> listFilesRecusive(File file) throws IOException {
		return listFiles(file, true, null);
	}

	/**
	 * <pre>
	 * 递归的列出所有文件
	 * </pre>
	 * 
	 * @param file
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<File> listFiles(File file) throws IOException {
		return listFiles(file, true, null);
	}

	/**
	 * <pre>
	 * 递归的列出文件
	 * 
	 * fileFilter ex.
	 * fileFilter = new FileFilter() {
	 *       public boolean accept(File pathname) {
	 *           return !".svn".equals(pathname.getAbsolutePath());
	 *       }
	 *  };
	 * </pre>
	 * 
	 * @param file
	 * @param recusive
	 * @param filter
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<File> listFiles(File file, boolean recusive,
			FileFilter filter) //
			throws IOException {
		List<File> fileList = new ArrayList<File>();
		if (file.isDirectory()) {
			Stack<File> stack = new Stack<File>();
			stack.push(file);
			File[] files;
			while (!stack.isEmpty()) {
				files = stack.pop().listFiles(filter);
				for (File _file : files) {
					if (_file.isDirectory() && recusive) {
						stack.push(_file);
					} else if (_file.isFile()) {
						fileList.add(_file);
					}
				}
			}
		} else {
			if (file.exists()) {
				fileList.add(file);
			}
		}
		return fileList;
	}

	/**
	 * <pre>
	 * 删除文件或文件夹（不删除主目录）
	 * 注意：此方法使用了 {@link FileLocker}，嵌套使用 {@link FileLocker} 时可能造成永久锁。
	 * </pre>
	 * 
	 * @param file
	 *            文件或文件夹
	 */
	public static void tryDelete(File file) {
		tryDelete(file, false);
	}

	/**
	 * <pre>
	 * 删除文件或文件夹（不删除主目录）
	 * 注意：此方法使用了 {@link FileLocker}，嵌套使用 {@link FileLocker} 时可能造成永久锁。
	 * </pre>
	 * 
	 * @param file
	 *            文件或文件夹
	 * @param recursive
	 *            在file为目录时 {@code true}<b color="red">递归删除此目录下所有目录内的文件</b>
	 *            {@code false}非递归删除file目录下所有文件遇到文件夹时跳过
	 */
	public static void tryDelete(File file, boolean recursive) {
		tryDelete(file, recursive, 0);
	}

	/**
	 * <pre>
	 * 删除文件或文件夹（不删除主目录）
	 * 注意：此方法使用了 {@link FileLocker}，嵌套使用 {@link FileLocker} 时可能造成永久锁。
	 * </pre>
	 * 
	 * @see FileLocker#writeLock(java.io.File)
	 * @param file
	 *            文件或文件夹
	 * @param recursive
	 *            在file为目录时 {@code true}<b color="red">递归删除此目录下所有目录内的文件</b>
	 *            {@code false}非递归删除file目录下所有文件遇到文件夹时跳过
	 * @param deep
	 *            所处位置标识
	 */
	private static void tryDelete(File file, boolean recursive, int deep) {
		if (file == null) {
			return;
		}
		deep++;
		FileLocker flock = new FileLocker(file);
		try {
			flock.lockWrite();
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File _file : files) {
					if (_file.isFile()) {
						tryDelete(_file, false, deep);
					}
					if (recursive) {
						if (_file.isDirectory()) {
							tryDelete(_file, true, deep);
						}
					}
				}
			}
			/**
			 * 删除目录
			 */
			if (recursive && deep != 1) { // 不删除主目录
				file.delete();
			}
		} finally {
			flock.releaseWrite();
		}
	}

	/**
	 * <pre>
	 * 将相对路径转化为绝对路径
	 * 
	 * ex.1
	 *  relativePath ../../index.html
	 *  absoluteDir d:/webRoot/script/pages/
	 *  replacePartCount -1
	 *  return d:/webRoot/index.html
	 * 
	 *  ex.2
	 *  relativePath ../../index.html
	 *  absoluteDir d:/webRoot/script/pages/
	 *  replacePartCount 1
	 *  return d:/webRoot/script/../index.html
	 * </pre>
	 * 
	 * @param relativePath
	 *            相对路径
	 * @param absoluteDir
	 *            绝对目录
	 * @param replacePartCount
	 *            允许转换的范围 当为 -1 时 转换所有相对目录,0 不进行转换。 可用于控制目录访问权限。
	 * @return
	 */
	public static String getAbsolutePath(String relativePath,
			String absoluteDir, int replacePartCount) {
		String absolutePath = null;
		do {
			absoluteDir = backToslash(absoluteDir + relativePath);
			if (replacePartCount == -1) {
				replacePartCount = absoluteDir.length();
			}
			for (int i = 0; i < replacePartCount; i++) {
				if (absoluteDir.indexOf("../") == -1) {
					break;
				}
				absoluteDir = absoluteDir.replaceFirst("/[^/]+/\\.\\./", "/");
			}
			absolutePath = absoluteDir;
		} while (false);
		return absolutePath;
	}

	/**
	 * <pre>
	 * 
	 * 反斜线转换为斜线
	 * 
	 * 1. "\" -> "/"
	 * 2. "./" -> ""
	 * 3. "//" -> "/" , "///////" -> "/"
	 * </pre>
	 * 
	 * @param fileStr
	 * @return 文件名为 null 时 返回 null
	 */
	public static String backToslash(String fileStr) {
		if (fileStr != null) {
			return fileStr.replaceAll("\\\\", "/").replaceAll("[^.]\\./", "")
					.replaceAll("//+", "/");
		}
		return null;
	}

	/**
	 * 非法文件名字符
	 */
	private static char[] FILE_UNSAFE_CHARS = new char[] { '"', '\'', '*', '/',
			':', '<', '>', '?', '\\', '|', };

	/**
	 * <pre>
	 *  替换字符串中文件名非法字符
	 *  backslash and slash to instead
	 *  注意：单引号 ' 也被视为非法字符。 当 instead 也为非法字符时 instead 会被自动设定为 - 。
	 *   " ' * / : < > ? \ | (已排列顺序)
	 *  params: ("script/article/edit.js" , '-') or ("script\\article\\edit.js" , '-') or ("script/article\\edit.js" , '-')
	 *  result: "script-article-edit.js"
	 * </pre>
	 * 
	 * @param fileName
	 * @param instead
	 *            非法字符替换为
	 * @param isRemoveSpace
	 *            是否移除空白字符
	 * @return fileStr 为 null 时,返回 null
	 */
	public static String fileNameEscape(String fileName, char instead,
			boolean isRemoveSpace) {
		if (fileName != null) {
			/**
			 * 需要替换的也是非法字符时改用 -
			 */
			if (Arrays.binarySearch(FILE_UNSAFE_CHARS, instead) != -1) {
				instead = '-';
			}

			StringBuilder strBuilder = new StringBuilder(fileName.length());
			char[] names = fileName.toCharArray();
			/**
			 * [/\\\\:\\*\\?\"'<>|]
			 */
			for (char ch : names) {
				//
				if (Arrays.binarySearch(FILE_UNSAFE_CHARS, ch) > -1) {
					strBuilder.append(instead);
					continue;
				}
				if (isRemoveSpace && Character.isWhitespace(ch)) {
					continue;
				}
				strBuilder.append(ch);
			}
			return strBuilder.toString();
		}
		return null;
	}

	public static String getSuffixName(File f) {
		if (null == f)
			return null;
		return getSuffixName(f.getAbsolutePath());
	}

	/**
	 * 获取文件后缀名，不包括 '.'，如 'abc.gif','，则返回 'gif'
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件后缀名
	 */
	public static String getSuffixName(String path) {
		if (null == path)
			return null;
		int pos = path.lastIndexOf('.');
		if (-1 == pos)
			return "";
		return path.substring(pos + 1);
	}

	public static boolean createNewFile(File f) throws IOException {
		if (null == f || f.exists())
			return false;
		makeDir(f.getParentFile());
		return f.createNewFile();
	}

	public static File createDirIfNoExists(String path) {
		String thePath = FileHelper.absolute(path);
		if (null == thePath)
			thePath = FileHelper.normalize(path);
		File f = new File(thePath);
		if (!f.exists())
			FileHelper.makeDir(f);
		if (!f.isDirectory())
			System.out.printf("'%s' should be a directory!", path);
		return f;
	}

	public static boolean makeDir(File dir) {
		if (null == dir || dir.exists())
			return false;
		return dir.mkdirs();
	}

	public static String absolute(String path) {
		return absolute(path, FileHelper.class.getClassLoader(),
				Constant.CHARSET);
	}

	public static String absolute(String path, ClassLoader klassLoader,
			String enc) {
		path = normalize(path, enc);
		if (StringHelper.isEmpty(path))
			return null;

		File f = new File(path);
		if (!f.exists()) {
			URL url = null;
			try {
				url = klassLoader.getResource(path);
				if (null == url)
					url = Thread.currentThread().getContextClassLoader()
							.getResource(path);
				if (null == url)
					url = ClassLoader.getSystemResource(path);
			} catch (Throwable e) {
			}
			if (null != url)
				return normalize(url.getPath(), Constant.CHARSET);// 通过URL获取String,一律使用UTF-8编码进行解码
			return null;
		}
		return path;
	}

	/**
	 * 让路径变成正常路径，将 ~ 替换成用户主目录
	 * 
	 * @param path
	 *            路径
	 * @return 正常化后的路径
	 */
	public static String normalize(String path) {
		return normalize(path, Constant.CHARSET);
	}

	/**
	 * 让路径变成正常路径，将 ~ 替换成用户主目录
	 * 
	 * @param path
	 *            路径
	 * @param enc
	 *            路径编码方式
	 * @return 正常化后的路径
	 */
	public static String normalize(String path, String enc) {
		if (StringHelper.isEmpty(path))
			return null;
		if (path.charAt(0) == '~')
			path = System.getProperty("user.home") + path.substring(1);
		try {
			return URLDecoder.decode(path, enc);
		} catch (UnsupportedEncodingException e) {
			return null;
		}

	}
	public static ZipEntry[] findEntryInZip(ZipFile zip, String regex) {
		List<ZipEntry> list = new LinkedList<ZipEntry>();
		Enumeration<? extends ZipEntry> en = zip.entries();
		while (en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if (null == regex || ze.getName().matches(regex))
				list.add(ze);
		}
		return list.toArray(new ZipEntry[list.size()]);
	}
	/**
	 * 获取文件主名。 即去掉后缀的名称
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件主名
	 */
	public static String getMajorName(String path) {
		int len = path.length();
		int l = 0;
		int r = len;
		for (int i = r - 1; i > 0; i--) {
			if (r == len)
				if (path.charAt(i) == '.') {
					r = i;
				}
			if (path.charAt(i) == '/' || path.charAt(i) == '\\') {
				l = i + 1;
				break;
			}
		}
		return path.substring(l, r);
	}
	/**
	 * 将文件路径后缀改名，从而生成一个新的文件路径。
	 * 
	 * @param path
	 *            文件路径
	 * @param suffix
	 *            新后缀， 比如 ".gif" 或者 ".jpg"
	 * @return 新文件后缀
	 */
	public static String renameSuffix(String path, String suffix) {
		int pos = path.length();
		for (--pos; pos > 0; pos--) {
			if (path.charAt(pos) == '.')
				break;
			if (path.charAt(pos) == '/' || path.charAt(pos) == '\\') {
				pos = -1;
				break;
			}
		}
		if (0 >= pos)
			return path + suffix;
		return path.substring(0, pos) + suffix;
	}
	/**
	 * 将文件后缀改名，从而生成一个新的文件对象。但是并不在磁盘上创建它
	 * 
	 * @param f
	 *            文件
	 * @param suffix
	 *            新后缀， 比如 ".gif" 或者 ".jpg"
	 * @return 新文件对象
	 */
	public static File renameSuffix(File f, String suffix) {
		if (null == f || null == suffix || suffix.length() == 0)
			return f;
		return new File(renameSuffix(f.getAbsolutePath(), suffix));
	}
	/**
	 * 获取一个目录下所有子目录。子目录如果以 '.' 开头，将被忽略
	 * 
	 * @param dir
	 *            目录
	 * @return 子目录数组
	 */
	public static File[] dirs(File dir) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return !f.isHidden() && f.isDirectory()
						&& !f.getName().startsWith(".");
			}
		});
	}
	/**
	 * 从 CLASSPATH 下或从指定的本机器路径下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param klassLoader
	 *            参考 ClassLoader
	 * @param enc
	 *            文件路径编码
	 * 
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path, ClassLoader klassLoader, String enc) {
		path = absolute(path, klassLoader, enc);
		if (null == path)
			return null;
		return new File(path);
	}

	/**
	 * 从 CLASSPATH 下或从指定的本机器路径下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param enc
	 *            文件路径编码
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path, String enc) {
		return findFile(path, FileHelper.class.getClassLoader(), enc);
	}

	/**
	 * 从 CLASSPATH 下或从指定的本机器路径下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * @param klassLoader
	 *            使用该 ClassLoader进行查找
	 * 
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path, ClassLoader klassLoader) {
		return findFile(path, klassLoader, Constant.CHARSET);
	}

	/**
	 * 从 CLASSPATH 下或从指定的本机器路径下寻找一个文件
	 * 
	 * @param path
	 *            文件路径
	 * 
	 * @return 文件对象，如果不存在，则为 null
	 */
	public static File findFile(String path) {
		return findFile(path, FileHelper.class.getClassLoader(),Constant.CHARSET);
	}
	/**
	 * 文件对象是否是目录，可接受 null
	 */
	public static boolean isDirectory(File f) {
		if (null == f)
			return false;
		if (!f.exists())
			return false;
		if (!f.isDirectory())
			return false;
		return true;
	}
	/**
	 * 文件对象是否是文件，可接受 null
	 */
	public static boolean isFile(File f) {
		return null != f && f.exists() && f.isFile();
	}
	
	/**
	 * 删除一个文件
	 * 
	 * @param f
	 *            文件
	 * @return 是否删除成功
	 * @throws IOException
	 */
	public static boolean deleteFile(File f) {
		if (null == f)
			return false;
		return f.delete();
	}

	/**
	 * 清除一个目录里所有的内容
	 * 
	 * @param dir
	 *            目录
	 * @return 是否清除成功
	 */
	public static boolean clearDir(File dir) {
		if (null == dir)
			return false;
		if (!dir.exists())
			return false;
		File[] fs = dir.listFiles();
		for (File f : fs) {
			if (f.isFile())
				deleteFile(f);
			else if (f.isDirectory())
				deleteDir(f);
		}
		return false;
	}
	/**
	 * 强行删除一个目录，包括这个目录下所有的子目录和文件
	 * 
	 * @param dir
	 *            目录
	 * @return 是否删除成功
	 */
	public static boolean deleteDir(File dir) {
		if (null == dir || !dir.exists())
			return false;
		if (!dir.isDirectory())
			throw new RuntimeException("\"" + dir.getAbsolutePath() + "\" should be a directory!");
		File[] files = dir.listFiles();
		boolean re = false;
		if (null != files) {
			if (files.length == 0)
				return dir.delete();
			for (File f : files) {
				if (f.isDirectory())
					re |= deleteDir(f);
				else
					re |= deleteFile(f);
			}
			re |= dir.delete();
		}
		return re;
	}
	
	/**
	 * @param path
	 *            路径
	 * @return 父路径
	 */
	public static String getParent(String path) {
		if (StringHelper.isBlank(path))
			return path;
		int pos = path.replace('\\', '/').lastIndexOf('/');
		if (pos > 0)
			return path.substring(0, pos);
		return "/";
	}
	
	/**
	 * 将一个目录下的特殊名称的目录彻底删除，比如 '.svn' 或者 '.cvs'
	 * 
	 * @param dir
	 *            目录
	 * @param name
	 *            要清除的目录名
	 * @throws IOException
	 */
	public static void cleanAllFolderInSubFolderes(File dir, String name) throws IOException {
		File[] files = dir.listFiles();
		for (File d : files) {
			if (d.isDirectory())
				if (d.getName().equalsIgnoreCase(name))
					deleteDir(d);
				else
					cleanAllFolderInSubFolderes(d, name);
		}
	}
	
	/**
	 * 递归查找获取一个目录下所有子目录(及子目录的子目录)。子目录如果以 '.' 开头，将被忽略
	 * <p/>
	 * <b>包含传入的目录</b>
	 * 
	 * @param dir
	 *            目录
	 * @return 子目录数组
	 */
	public static File[] scanDirs(File dir) {
		ArrayList<File> list = new ArrayList<File>();
		list.add(dir);
		scanDirs(dir, list);
		return list.toArray(new File[list.size()]);
	}

	private static void scanDirs(File rootDir, List<File> list) {
		File[] dirs = rootDir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return !f.isHidden() && f.isDirectory() && !f.getName().startsWith(".");
			}
		});
		if (dirs != null) {
			for (File dir : dirs) {
				scanDirs(dir, list);
				list.add(dir);
			}
		}
	}
	/**
	 * 获取一个目录下所有的文件(不递归，仅仅一层)。隐藏文件会被忽略。
	 * 
	 * @param dir
	 *            目录
	 * @param suffix
	 *            文件后缀名。如果为 null，则获取全部文件
	 * @return 文件数组
	 */
	public static File[] files(File dir, final String suffix) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return !f.isHidden()
						&& f.isFile()
						&& (null == suffix || f.getName().endsWith(suffix));
			}
		});
	}
	
	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param klass
	 *            参考的类， -- 会用这个类的 ClassLoader
	 * @param enc
	 *            文件路径编码
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path, Class<?> klass, String enc) {
		File f = new File(path);
		if (f.exists())
			try {
				return new FileInputStream(f);
			}
			catch (FileNotFoundException e1) {
				return null;
			}
		if (null != klass) {
			InputStream ins = klass.getClassLoader().getResourceAsStream(path);
			if (null == ins)
				ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			if (null != ins)
				return ins;
		}
		return ClassLoader.getSystemResourceAsStream(path);
	}

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param enc
	 *            文件路径编码
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path, String enc) {
		return findFileAsStream(path, FileHelper.class, enc);
	}

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param klass
	 *            参考的类， -- 会用这个类的 ClassLoader
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path, Class<?> klass) {
		return findFileAsStream(path, klass, Constant.CHARSET);
	}

	/**
	 * 获取输出流
	 * 
	 * @param path
	 *            文件路径
	 * 
	 * @return 输出流
	 */
	public static InputStream findFileAsStream(String path) {
		return findFileAsStream(path, FileHelper.class, Constant.CHARSET);
	}
	/**
	 * 试图生成一个文件对象，如果文件不存在则创建它。 如果给出的 PATH 是相对路径 则会在 CLASSPATH
	 * 中寻找，如果未找到，则会在用户主目录中创建这个文件
	 * 
	 * @param path
	 *            文件路径，可以以 ~ 开头，也可以是 CLASSPATH 下面的路径
	 * @return 文件对象
	 * @throws IOException
	 *             创建失败
	 */
	public static File createFileIfNoExists(String path) throws IOException {
		String thePath = absolute(path);
		if (null == thePath)
			thePath = normalize(path);
		File f = new File(thePath);
		if (!f.exists())
			createNewFile(f);
		if (!f.isFile())
			System.out.printf("'%s' should be a file!", path);
		return f;
	}
	
	/**
	 * 将内容写到一个文件内，内容对象可以是：
	 * <ul>
	 * <li>InputStream - 按二进制方式写入
	 * <li>byte[] - 按二进制方式写入
	 * <li>Reader - 按 UTF-8 方式写入
	 * <li>其他对象被 toString() 后按照 UTF-8 方式写入
	 * </ul>
	 * 
	 * @param path
	 *            文件路径，如果不存在，则创建
	 * @param obj
	 *            内容对象
	 */
	public static void write(String path, Object obj) {
		if (null == path || null == obj)
			return;
		try {
			write(FileHelper.createFileIfNoExists(path), obj);
		}
		catch (IOException e) {
		}
	}

	/**
	 * 将内容写到一个文件内，内容对象可以是：
	 * 
	 * <ul>
	 * <li>InputStream - 按二进制方式写入
	 * <li>byte[] - 按二进制方式写入
	 * <li>Reader - 按 UTF-8 方式写入
	 * <li>其他对象被 toString() 后按照 UTF-8 方式写入
	 * </ul>
	 * 
	 * @param f
	 *            文件
	 * @param obj
	 *            内容
	 */
	public static void write(File f, Object obj) {
		if (null == f || null == obj)
			return;
		if (f.isDirectory())
			System.out.printf("Directory '%s' can not be write as File", f);

		try {
			// 保证文件存在
			if (!f.exists())
				FileHelper.createNewFile(f);
			// 输入流
			if (obj instanceof InputStream) {
				StreamUtil.writeAndClose(StreamUtil.fileOut(f), (InputStream) obj);
			}
			// 字节数组
			else if (obj instanceof byte[]) {
				StreamUtil.writeAndClose(StreamUtil.fileOut(f), (byte[]) obj);
			}
			// 文本输入流
			else if (obj instanceof Reader) {
				StreamUtil.writeAndClose(StreamUtil.fileOutw(f), (Reader) obj);
			}
			// 其他对象
			else {
				StreamUtil.writeAndClose(StreamUtil.fileOutw(f), obj.toString());
			}
		}
		catch (IOException e) {
		}
	}
}
