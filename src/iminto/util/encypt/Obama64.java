package iminto.util.encypt;
import java.util.Arrays;
import java.util.Random;
/**
 * 奥巴码编码/解码 1.3最终版
 * @author zhongqiu
 * http://code.google.com/p/aobama/
 * 
 */
public class Obama64 {
	/** 码表坐标掩码 */
	private static final int MASK_64 = 0x3F;	
	/** 默认扰乱码 */
	private static byte DEF_BLUFF_CODE = 0x43;	
	/** 扰乱开关 */
	private boolean bluff = false;	
	/** 扰乱码 */
	private byte bluff_code = DEF_BLUFF_CODE;	
	private Random rand;
	
	/** 编码基表 */
	private byte base_encode[] = {
		// 默认序列
	    'P', 'e', 'r', 'Q', 'f', 'w', '7', 'g', 'i', 'p', 	/*  0- 9 */
	    '8', '9', 'B', 'd', 'O', 'v', '6', 'S', 'D', 'M', 	/* 10-19 */
	    'b', 's', 'R', 'C', 'N', 'c', 'm', '5', 'l', 'z', 	/* 20-29 */
	    'I', 'X', 'o', 'j', 'H', '2', 'x', 'W', '1', 'J', 	/* 30-39 */
	    'V', 'h', 'G', '0', 'Y', 'q', 'E', 'T', 'k', '3', 	/* 40-49 */
	    'a', 'L', 'y', 'n', 't', 'U', 'u', 'Z', '4', 'K', 	/* 50-59 */
	    'F', 'A', '_', '-'									/* 60-63 */ //95,45, 
	};
	
	/** 解码参照表 */
	private int base_decode[] = {
		// 参照编码表默认序列的值序
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  			/*  0- 9 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  			/*  10- 19 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  			/*  20- 29 */
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  			/*  30- 39 */
		-1, -1, -1, -1, -1, 63, -1, -1, 43, 38,  			/*  40- 49 */
		35, 49, 58, 27, 16, 6, 10, 11, -1, -1,  			/*  50- 59 */
		-1, -1, -1, -1, -1, 61, 12, 23, 18, 46,  			/*  60- 69 */
		60, 42, 34, 30, 39, 59, 51, 19, 24, 14,  			/*  70- 79 */
		0, 3, 22, 17, 47, 55, 40, 37, 31, 44,  				/*  80- 89 */
		57, -1, -1, -1, -1, 62, 0, 50, 20, 25,  			/*  90- 99 */
		13, 1, 4, 7, 41, 8, 33, 48, 28, 26,  				/*  100- 109 */
		53, 32, 9, 45, 2, 21, 54, 56, 15, 5,  				/*  110- 119 */
		36, 52, 29, -1, -1, -1, -1, -1, -1		 			/*  120- 128 */	
	};
	
	/** 
	 * 加密钩子, 默认实现 .
	 * 
	 * 用户可以通过实现自己的Encrypt，覆盖encode()、decode()方法，
	 * 并通过setEncrypt传递给Obama64来实现简单的自定义加解密过程
	 * 
	 * 实现Encrypt接口应遵循如下规则：
	 * 1.返回值范围在0-127之间
	 * 2.IF b = encode(a, secret); THEN a = decode(b, secret);
	 */
	private IEncrypt encrypt = new IEncrypt(){
		@Override
		public byte encode(byte plaintext, byte secret) {
			return (byte)(plaintext ^ secret);
		}
		
		@Override
		public byte decode(byte cryptograph, byte secret) {
			return (byte)(cryptograph ^ secret);
		}
	};
	
	public Obama64(){
		this.rand = new Random();
	}
	
	/**
	 * 检查Encrypt的有效性
	 * @param encrypt 用户自定义Encrypt
	 * @return Encrypt合法则返回true，否则返回false
	 */
	private boolean checkEncrypt(IEncrypt encrypt){
		byte en = 0;
		for(byte b = 127; b>=0; b--){
			en = encrypt.encode(b, DEF_BLUFF_CODE);
			if(b != encrypt.decode(en, DEF_BLUFF_CODE))
				return false;
		}
		return true;
	}

	/**
	 * 是否绕乱编码结果
	 * @return 返回true表示对编码结果进行扰乱操作，false表示不进行扰乱
	 */
	public boolean isBluff() {
		return bluff;
	}

	/**
	 * 设置扰乱开关
	 * @param bluff true打开，false关闭
	 */
	public void setBluff(boolean bluff) {
		this.bluff = bluff;
	}
	
	/**
	 * 检查扰乱码有效性，作为编码结果的一部分，限制其值在编码表base_encode值范围内
	 * @param bluffCode 扰乱码
	 * @return 如果扰乱码不符合规定则返回false，否则返回true
	 */
	private boolean checkBluffCode(byte bluffCode){
		for(byte b : base_encode){
			if(b == bluffCode)
				return true;
		}
		return false;
	}

	/**
	 * 获取扰乱码
	 * @return bluff_code
	 */
	public byte getBluff_code() {
		return bluff_code;
	}

	/**
	 * 设置扰乱码
	 * @param bluffCode
	 */
	public void setBluff_code(byte bluffCode) {
		if(checkBluffCode(bluffCode))
			bluff_code = bluffCode;
		else
			throw new IllegalArgumentException("Invalid Bluff Code:" + bluffCode);
	}

	/**
	 * 设置自定义的简单加/接密过程
	 * @param encrypt
	 */
	public void setEncrypt(IEncrypt encrypt) {
		boolean check = checkEncrypt(encrypt);
		if(check)
			this.encrypt = encrypt;
		else
			throw new IllegalArgumentException("Invalid Encrypt!");
	}

	/**
	 * 重排序编码基表和对应的解码表
	 */
	public void init(){
		
		Random rand = new Random();
		// 重排序编码基表
		for (int i=base_encode.length; i>1; i--)
            swap(base_encode, i-1, rand.nextInt(i));
		
		// 根据重排序后的编码基表初始化解码表
		initBase_decode();
	}
	
	/**
	 * 交换byte数组的指定两个元素
	 * @param arr
	 * @param i
	 * @param j
	 */
	private void swap(byte[] arr, int i, int j) {
        byte tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
	
	/**
	 * 根据编码基表，初始化解码参照表
	 */
	private void initBase_decode(){
		Arrays.fill(base_decode, -1);
		for(int i=0; i<base_encode.length; i++){
			base_decode[base_encode[i]] = i;
		}
	}
	
	/**
	 * 根据解码参照表，初始化编码基表
	 */
	private void initBase_encode(){
		for(int i=0; i<base_decode.length; i++){
			if(base_decode[i] < 0)
				continue;
			else
				base_encode[base_decode[i]] = (byte)i;
		}
	}
	
	/**
	 * 设置解码参照表，同时会根据设置的解码参照表初始化编码基表
	 * @param decode 解码参照表
	 */
	public void setBase_decode(int[] decode){
		base_decode = decode;
		initBase_encode();
	}
	
	/**
	 * 设置编码基表，同时会根据设置的编码基表初始化解码参照表
	 * @param encode 编码基表
	 */
	public void setBase_encode(byte[] encode){
		base_encode = encode;
		initBase_decode();
	}
	
	private byte bluffCode(){
		return base_encode[rand.nextInt(64)];
	}
	
	/**
	 * 基本的编码方法
	 * @param content 待编码的字节序列
	 * @return 编码后的字节序列
	 */
	public byte[] encode(byte[] content){
		if(content==null || content.length==0)
			return content;
		
		int len = content.length;
		
		byte[] cArray = new byte[len + (int)Math.ceil(len/3.0d) + 1];
		
		if(bluff){
//			int s = 0;
//			int r = new Random().nextInt(26);
//			boolean u = ((r ^ len) & 1) == 1;
//			s = u ? 65 : 97;
//			cArray[0] = (byte)(r + s);
			cArray[0] = bluffCode();
		}else{
			cArray[0] = bluff_code;
		}
		
		byte c = 0;
		int n = 0;
		int mark = 0;
		int pos = 0;
		int segs = 0;
		for(int i=0; i<len; i+=3){
			mark = 1+i+segs;
			for(int k=0; (k<3) && (pos<len); k++){
				c = content[pos];
				
				if(c<0){
					c = (byte)~c;
					cArray[mark] |= (2<<(k<<1));
				}
				
				n = encrypt.encode(c, cArray[0]);
				n ^= k;
				
				cArray[mark] |= ((n>>>6)<<(k<<1)); 
				
				cArray[mark+k+1] = (byte)base_encode[n & MASK_64];
				pos++;
			}
			segs++;
			cArray[mark] = (byte)base_encode[cArray[mark]];
		}
		
		return cArray;
	}
	
	/**
	 * 基本的解码方法，用于解码encode()生成的编码
	 * @param content encode()生成的编码字节序列
	 * @return 解码后的字节序列
	 */
	public byte[] decode(byte[] content){
		
		if(content==null || content.length==0)
			return content;
		
		int len = content.length;
		
		byte[] cArray = new byte[len - 1 - (int)Math.ceil((len-1)/4.0)];
		
		byte secret = content[0];
		byte c = 0;
		int mark = 0;
		int index = 0;
		int tmp = 0;
		for(int i=1; i<len; i+=4){
			mark = base_decode[content[i]];
			for(int k=0, pos=i + k + 1; k<3 && pos<len; k++, pos++){
				c = content[pos];
				tmp = mark>>>(k<<1); 
				cArray[index] = (byte)encrypt.decode((byte)((base_decode[c] + ((tmp&1)<<6))^ k), secret);
				if((tmp & 2) > 0)
					cArray[index] = (byte)~cArray[index];
				index++;
			}
		}
		
		return cArray;
	}
	
	
	/** 
	 * 编码纯ASCII字节序列，使用此方法产生的编码相对encode()短。
	 * <ul>
	 * <li>encode()产生的编码长度为 len + len/3 + 1，len代表待编码字节序列长度
	 * <li>encodeAscii()产生的编码长度为 len + len/6 + 1, len代表待编码字节序列长度
	 * </ul>
	 * @param content 待编码的ASCII字节序列
	 * @return 编码后的字节序列
	 * 
	 * 注意：使用该方法编码非ASCII字符将抛出ArrayIndexOutOfBoundsException。
	 * 如果不清楚待编码的字节序列是否是纯ASCII字符，那么应该选用encode()和decode()方法进行编/解码。
	 */
	public byte[] encodeAscii(byte[] content){
		
		if(content==null || content.length==0)
			return null;
		
		int len = content.length;
		
		byte[] cArray = new byte[len + (int)Math.ceil(len/6.0d) + 1];
		
		if(bluff){
//			int s = 0;
//			int r = new Random().nextInt(26);
//			boolean u = ((r ^ len) & 1) == 1;
//			s = u ? 65 : 97;
//			cArray[0] = (byte)(r + s);
			cArray[0] = bluffCode();
		}else{
			cArray[0] = bluff_code;
		}
		
		
		byte c = 0;
		int n = 0;
		int mark = 0;
		int pos = 0;
		int segs = 0;
		for(int i=0; i<len; i+=6){
			mark = 1+i+segs;
			for(int k=0; (k<6) && (pos<len); k++){
				c = content[pos];
				n = encrypt.encode(c, cArray[0]);
//				n = c ^ cArray[0]; 
				n ^= k;
				cArray[mark] |= ((n>>>6)<<k); 
				
				cArray[mark+k+1] = (byte)base_encode[n & MASK_64];
				pos++;
			}
			segs++;
			cArray[mark] = (byte)base_encode[cArray[mark]];
		}
		
		return cArray;
		
	}
	
	/**
	 * 解码由encodeAscii生成的字节序列
	 * 
	 * @param content 待解码的字节序列
	 * @return 解码后的字节序列
	 * 
	 * 
	 */
	public byte[] decodeAscii(byte[] content){
		
		if(content==null || content.length==0)
			return null;
		
		int len = content.length;
		
		byte[] cArray = new byte[len - 1 - (int)Math.ceil((len-1)/7.0)];
		
		byte secret = content[0];
		byte c = 0;
		int mark = 0;
		int index = 0;
		for(int i=1; i<len; i+=7){
			mark = base_decode[content[i]];
			for(int k=0, pos=i + k + 1; k<6 && pos<len; k++, pos++){
				c = content[pos];
				cArray[index] = (byte)encrypt.decode((byte)((base_decode[c] + (((mark>>>k)&1)<<6))^ k), secret);
				index++;
			}
		}
		
		return cArray;
	}
	
	/**
	 * 打印编码基表
	 */
	public void printBaseEncode(){
		int loop =0;
		for(byte n : base_encode){
			System.out.print("'"+(char)n+"', ");
			loop++;
			if(loop%10==0)
				System.out.println("\t/* " + (loop-10) + " - " + (loop-1) + " */" );
		}
		System.out.println();
	}
	
	/**
	 * 打印解码参照表
	 */
	public void printBaseDecode(){
		int loop =0;
		for(int n : base_decode){
			System.out.print((n<10&&n>-1?" ":"") + n + ", ");
			loop++;
			if(loop%10==0)
				System.out.println("\t/* " + (loop-10) + " - " + (loop-1) + " */");
		}
		System.out.println();
	}
	
}
