package iminto.util.common;

import iminto.util.Config.Constant;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * URL 辅助类
 * 注意本类需要 {@see RuntimeConstant#charset} 字符集的支持。
 * 否则会产生一个异常记录。
 * </pre>
 */
public class UrlHelper {

    /**
     * 锁定创建
     */
    private UrlHelper() {
    }

    /**
     * URL转义
     * @see #enURL(java.lang.String, java.lang.String)
     * @param urlArgVal
     * @return urlArgVal 为 null时返回null
     */
    public static String encode(String urlArgVal) {
        return encode(urlArgVal, Constant.CHARSET);
    }

    /**
     * URL转义
     * @param urlArgVal
     * @param charsetName
     * @return urlArgVal 为 null时返回null
     */
    public static String encode(String urlArgVal, String charsetName) {
        String result = null;
        if (urlArgVal != null) {
            try {
                result = URLEncoder.encode(urlArgVal, charsetName);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(UrlHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    /**
     * URL 解码
     * @see #unURL(java.lang.String, java.lang.String)
     * @param urlArgVal
     * @return  urlArgVal 为 null时返回null
     */
    public static String decode(String urlArgVal) {
        return decode(urlArgVal, Constant.CHARSET);
    }

    /**
     * URL 解码
     * @param urlArgVal 
     * @param charsetName
     * @return  urlArgVal 为 null时返回null
     */
    public static String decode(String urlArgVal, String charsetName) {
        String result = null;
        if (urlArgVal != null) {
            try {
                if (charsetName == null) {
                    charsetName = Constant.CHARSET;
                }
                result = URLDecoder.decode(urlArgVal, charsetName);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(UrlHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
}
