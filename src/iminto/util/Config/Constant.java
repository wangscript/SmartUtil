package iminto.util.Config;
import iminto.util.common.EnvInfo;

public interface Constant {
	public static final String CHARSET = "UTF-8";
	/**
     * 并发容器默认容量
     */
    public static final int CONCURRENT_CAPACITY_SIZE = 500;
    public static final int LOCKER_SLEEP_UNIT_TIME = 20;
    /**
     * 文本读取缓冲大小 byte
     */
    public static final int BUFFER_BYTE_SIZE = 8192;
    /**
     * 文本读取缓冲大小 char
     */
    public static final int BUFFER_CHAR_SIZE = BUFFER_BYTE_SIZE;
    /**
     * 默认临时文件目录
     */
    public static final String DEFAULT_TEMP_DIR = EnvInfo.getJavaIoTmpdir();
    public static final String DOT=".";
    public static final String DOTDOT = "..";
    public static final String tempFilePrefix = "iminto-";
    public static final String EMPTY = "";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String NULL = "null";
    public static final String SPACE = " ";
    public static final String QUOTE = "\"";
    public static int ioBufferSize = 16384;
    
}
