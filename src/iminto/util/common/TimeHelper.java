package iminto.util.common;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * 常用时间点获取
 * 每日时间起始为当日 00:00 时。
 * 每日时间结束是为次日 00:00 时。
 * </pre>
 */
public class TimeHelper {

	//基准时间
    private long millisTime = 0L;
    private static final long oneDay = 24 * 3600 * 1000;
    
    /**
     * <pre>
     * DATE_ATOM（string）
     * 原子钟格式（如：2005-08-15T15:52:01+00:00）
     * DATE_COOKIE（string）
     * HTTP Cookies 格式（如：Mon, 15 Aug 2005 15:52:01 UTC）
     * DATE_ISO8601（string）
     * ISO-8601（如：2005-08-15T15:52:01+0000）
     * DATE_RFC822（string）
     * RFC 822（如：Mon, 15 Aug 2005 15:52:01 UTC）
     * DATE_RFC850（string）
     * RFC 850（如：Monday, 15-Aug-05 15:52:01 UTC）
     * DATE_RFC1036（string）
     * RFC 1036（如：Monday, 15-Aug-05 15:52:01 UTC）
     * DATE_RFC1123（string）
     * RFC 1123（如：Mon, 15 Aug 2005 15:52:01 UTC）
     * DATE_RFC2822（string）
     * RFC 2822（如：Mon, 15 Aug 2005 15:52:01 +0000）
     * DATE_RSS（string）
     * RSS（如：Mon, 15 Aug 2005 15:52:01 UTC）
     * DATE_W3C（string）
     * World Wide Web Consortium（如：2005-08-15T15:52:01+00:00）
     * DATE_MYSQL
     * MySQL （如：2008-08-15 15：22）
    </pre>
     */
    public static final String DATE_MYSQL = "yyyy-MM-dd HH:mm:ss";
    /**
	 * 将一个秒数（天中），转换成一个如下格式的数组:
	 * <pre>
	 * [0-23][0-59[-059]
	 * </pre>
	 */
    public static int[] T(int sec) {
		int[] re = new int[3];
		re[0] = Math.min(23, sec / 3600);
		re[1] = Math.min(59, (sec - (re[0] * 3600)) / 60);
		re[2] = Math.min(59, sec - (re[0] * 3600) - (re[1] * 60));
		return re;
	}

    /**
     * 时间辅助对象
     */
    public TimeHelper( ) {
        this.millisTime = System.currentTimeMillis();
    }
    /**
     * 时间辅助对象
     * @param millisTime 毫秒级相对时间
     */
    public TimeHelper(long millisTime) {
        this.millisTime = millisTime;
    }

    /**
     * 获取被清零到 天 的Calendar
     * @return
     */
    private Calendar getDayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisTime);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar;
    }

    /**
     * 获取当月起始时间
     * @return
     */
    public long monthStart() {
        Calendar calendar = getDayCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月结束时间（也是下月的开始时间）
     * @return
     */
    public long monthEnd() {
        Calendar calendar = getDayCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTimeInMillis() + oneDay;
    }

    /**
     * 获取当周起始时间
     * @return
     */
    public long weekStart() {
        Calendar calendar = getDayCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当周结束时间（也是下周的开始时间）
     * @return
     */
    public long weekEnd() {
        Calendar calendar = getDayCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        return calendar.getTimeInMillis() + oneDay;
    }

    /**
     * 获取当天的起始时间
     * @return
     */
    public long today() {
        return millisTime - millisTime % oneDay;
    }

    /**
     * 获取明天的起始时间
     * @return
     */
    public long tomorrow() {
        return today() + oneDay;
    }

    /**
     * 获取昨天的起始时间
     * @return
     */
    public long yesterday() {
        return today() - oneDay;
    }

    /**
     * 精确到微秒的时间
     * @return
     */
    public static long time() {
        return (long) Math.floor(System.currentTimeMillis() / 1000);
    }
    
    public static boolean isLeapYear(long year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }
    /**
     * 格式化long类型时间（当前 Unix 时间戳和毫秒数）
     * @return 格式化的时间
     * @since 0.2
     */
    public static String format() {
        Date date = new Date();
        return format(date, DATE_MYSQL);
    }
    /**
     * 格式化long类型时间（当前 Unix 时间戳和毫秒数）
     * @param pattern 格式描述。
     * @return 格式化的时间
     * @since 0.2
     */
    public static String format(String pattern) {
        Date date = new Date();
        return format(date, pattern);
    }
    /**
     * 格式化long类型时间（当前 Unix 时间戳和毫秒数）
     *
     * @param time Unix 时间戳和毫秒数（毫秒级Unix时间戳）。
     * @param pattern 格式描述。
     * @return 格式化的时间
     */
    public static String format(long time, String pattern) {
        Date date = new Date();
        date.setTime(time);
        return format(date, pattern);
    }
    /**
     * 格式化long类型时间（当前 Unix 时间戳和毫秒数）
     * @param time time Unix 时间戳和毫秒数（毫秒级Unix时间戳）。
     * @return
     */
    public static String format(long time) {
        Date date = new Date();
        date.setTime(time);
        return format(date, DATE_MYSQL);
    }
    /**
     * 格式化Date 
     * @param date
     * @param pattern 格式描述。
     * @return 格式化的时间
     */
    public static String format(Date date, String pattern) {
        DateFormat dateFormator = new SimpleDateFormat(pattern);
        return dateFormator.format(date);
    }
    
    /**
    *
    * 默认只可以转换 {@see #DATE_MYSQL},使用当前默认时区
    * @param timeStr 需要转化的时间字符串
    * @return
    */
   public static long strToTime(String timeStr) {
       return strToTime(timeStr, DATE_MYSQL);
   }

   /**
    * 使用自定义格式转换时间到long，使用当前默认时区
    * @param timeStr 时间字符串
    * @param format 格式化信息
    * @return
    */
   public static long strToTime(String timeStr, String format) {
       return strToTime(timeStr, format, TimeZone.getDefault());
   }

   /**
    * 使用自定义格式转换时间到long，使用当前自定义时区
    * @param timeStr 时间字符串
    * @param format 格式化信息
    * @param timeZone 时区
    * @return
    */
   public static long strToTime(String timeStr, String format, TimeZone timeZone) {
       Date date = null;
       try {
           DateFormat dateFormat = new SimpleDateFormat(format);
           dateFormat.setTimeZone(timeZone);
           date = dateFormat.parse(timeStr);
           return date.getTime();
       } catch (ParseException ex) {
           Logger.getLogger(TimeHelper.class.getName()).log(Level.SEVERE, null, ex);
       }
       return -1L;
   }
   

}
