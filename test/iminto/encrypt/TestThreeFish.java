package iminto.encrypt;
import iminto.util.encypt.Base64;
import iminto.util.encypt.Threefish;

public class TestThreeFish {
	public static void main(String[] args) {
	Threefish ENCRYPTOR = new Threefish(Threefish.BLOCK_SIZE_BITS_1024);
	ENCRYPTOR.init("imi#to9fdHSVXZ00c*ss@ap", 0x134298db8abf9415L, 0x603b5500abL);
	byte[] encrypted = ENCRYPTOR.encryptString("hapublic static 白菜void main(String[] args)ha");
	String result=Base64.encodeToString(encrypted);
	System.out.println(result);
	byte[] decoded = Base64.decode(result);
	System.out.println(ENCRYPTOR.decryptString(decoded));
}
}
