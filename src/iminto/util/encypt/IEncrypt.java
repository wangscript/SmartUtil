package iminto.util.encypt;

public interface IEncrypt {
	byte encode(byte plaintext, byte secret);
	byte decode(byte cryptograph, byte secret);
}
