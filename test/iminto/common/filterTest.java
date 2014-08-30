package iminto.common;
import java.util.ArrayList;
import java.util.List;
import iminto.format.FILTER;
import iminto.format.ValidatorHelper;
import static java.lang.System.out;

public class filterTest {
	
	public static void main(String[] args) {
		out.println(ValidatorHelper.filter_var("ppmm@qq.com", FILTER.EMAIL));
		out.println(ValidatorHelper.filter_var("202.255.15.238", FILTER.IPV4));
		out.println(ValidatorHelper.filter_var("978-7-115-29031-6",
				FILTER.ISBN13));
		out.println(ValidatorHelper.filter_var("360722198808290528",
				FILTER.IDCARD));
		out.println(ValidatorHelper.filter_var("360722198808290527",
				FILTER.IDCARD));
		String emailS = "admin@qq.com,ma_hua_teng2012@163.com,mayun@alibaba.com.cn";
		String emailReg = "(?i)[_a-z0-9\\-]+([._a-z0-9\\-]+)*@[a-z0-9\\-]+([.a-z0-9\\-]+)*(\\.[a-z]{2,4})";
		List<String> list = new ArrayList<String>();
		list = ValidatorHelper.preg_match_all(emailS, emailReg);
		for (String string : list) {
			out.println(string);
		}
	}

}
