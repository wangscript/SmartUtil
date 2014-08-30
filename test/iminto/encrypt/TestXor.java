package iminto.encrypt;
import iminto.util.encypt.XorEncrypt;

public class TestXor {
	public static void main(String[] args){
		String key="1大dg";
		XorEncrypt xor=new XorEncrypt("1大dg");
		byte[] keys = key.getBytes();
	       for (byte _key : keys) {
	           System.out.println(_key);;
	       }
		System.out.println(xor.encrypt("haha"));
		System.out.println(xor.decrypt(xor.encrypt("haha")));
		
	}

}
