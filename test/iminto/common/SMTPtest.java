package iminto.common;
import java.io.IOException;
import java.net.UnknownHostException;
import iminto.util.common.MailSend;
import iminto.util.common.MailSend.MailMessage;

public class SMTPtest {	
	public static void main(String[] args) throws UnknownHostException, IOException {
		MailSend.MailMessage msg=new MailMessage();
		msg.setFrom("lengfeng1601@163.com");
		msg.setUser("lengfeng1601");
		msg.setPassWord("hen0990123");
		msg.setTo("waitfox@qq.com");
		msg.setSubject("test");
		msg.setContent("hello it is a test");
		msg.setSendName("妖魔舞");
		msg.setReceiveName("肖同学");
		MailSend m=new MailSend("smtp.163.com");
		m.sendMail(msg);
		
	}
	
	
}
