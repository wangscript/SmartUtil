package iminto.encrypt;
import java.util.Calendar;
import iminto.util.encypt.MD5;

public class AuthCodes {
	public enum AuthcodeMode {
		Encode, Decode
	};
	
	public String authEncode(String str,AuthcodeMode mode,String key,int expire){
		int ckey_length = 4;
		if(key==null){
			key="d我w10c20我m05w18";
		}
		key = MD5.md5(key);
		String keya=MD5.md5(CutString(key, 0, 16));
		String keyb=MD5.md5(CutString(key,16, 16));
		String keyc="";
		if(ckey_length>0){
			if(mode==AuthcodeMode.Decode){
				keyc=CutString(str, 0, ckey_length);
			}else{
				keyc=CutString(MD5.md5(String.valueOf(getUnixTimestamp())),28,32);
			}
		}
		String cryptkey=keya+MD5.md5(keya+keyc);
		
		return null;
		
	}
	
	public long getUnixTimestamp() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis() / 1000;
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

	public static String CutString(String str, int startIndex) {
		return CutString(str, startIndex, str.length());
	}
	
	public static void main(String[] args) {
		String key=MD5.md5("d我w10c20我m05w18");
		System.out.println(key);
		String keya=MD5.md5(CutString(key, 0, 16));
		System.out.println(CutString(key,16, 16));
		String keyb=MD5.md5(CutString(key,16, 16));
		System.out.println(keya);
		System.out.println(keyb);
		System.out.println("---------------");
		String keyc=CutString("fe7596448848fadd42a9ad09313cd9ce", 28,32);
		System.out.println(keyc);
	}
}

