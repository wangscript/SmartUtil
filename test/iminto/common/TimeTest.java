package iminto.common;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {
	public static void main(String[] args) {
		Date date = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = dateFormat.parse("2099-01-01 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(date.getTime());
		
	}

}
