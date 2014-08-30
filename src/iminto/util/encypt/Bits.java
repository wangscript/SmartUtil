package iminto.util.encypt;

public class Bits {
	static boolean getBoolean(byte[] b, int off) {
        return b[off] != 0;
    }

    static char getChar(byte[] b, int off) {
        return (char) ((b[off + 1] & 0xFF) +
                       (b[off] << 8));
    }

    static short getShort(byte[] b, int off) {
        return (short) ((b[off + 1] & 0xFF) +
                        (b[off] << 8));
    }

    static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)      ) +
               ((b[off + 2] & 0xFF) <<  8) +
               ((b[off + 1] & 0xFF) << 16) +
               ((b[off    ]       ) << 24);
    }

    static float getFloat(byte[] b, int off) {
        return Float.intBitsToFloat(getInt(b, off));
    }

    static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL)      ) +
               ((b[off + 6] & 0xFFL) <<  8) +
               ((b[off + 5] & 0xFFL) << 16) +
               ((b[off + 4] & 0xFFL) << 24) +
               ((b[off + 3] & 0xFFL) << 32) +
               ((b[off + 2] & 0xFFL) << 40) +
               ((b[off + 1] & 0xFFL) << 48) +
               (((long) b[off])      << 56);
    }

    static double getDouble(byte[] b, int off) {
        return Double.longBitsToDouble(getLong(b, off));
    }

    /*
     * Methods for packing primitive values into byte arrays starting at given
     * offsets.
     */

    static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }

    static void putChar(byte[] b, int off, char val) {
        b[off + 1] = (byte) (val      );
        b[off    ] = (byte) (val >>> 8);
    }

    static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val      );
        b[off    ] = (byte) (val >>> 8);
    }

    static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val       );
        b[off + 2] = (byte) (val >>>  8);
        b[off + 1] = (byte) (val >>> 16);
        b[off    ] = (byte) (val >>> 24);
    }

    static void putFloat(byte[] b, int off, float val) {
        putInt(b, off,  Float.floatToIntBits(val));
    }

    static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val       );
        b[off + 6] = (byte) (val >>>  8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off    ] = (byte) (val >>> 56);
    }

    static void putDouble(byte[] b, int off, double val) {
        putLong(b, off, Double.doubleToLongBits(val));
    }
    
    public static String Bytes2HexString(byte[] b) {
	    StringBuilder sb=new StringBuilder();
	    String stemp;
	    for (int i = 0; i < b.length; i++) {
	    	stemp = Integer.toHexString(b[i] & 0xFF);
		      if (stemp.length() == 1) {
		        sb.append(0);
		      }
		      sb.append(stemp);stemp=null;
	    }
	    return sb.toString();
	  }
	/**
	   * 将两个ASCII字符合成一个字节；
	   * 如："EF"–> 0xEF
	   * @param src0 byte
	   * @param src1 byte
	   * @return byte
	   */
	  public static byte uniteBytes(byte src0, byte src1) {
	    byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
	    _b0 = (byte)(_b0 << 4);
	    byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
	    byte ret = (byte)(_b0 ^ _b1);
	    return ret;
	  }

	  /**
	   * 将指定字符串src，以每两个字符分割转换为16进制形式
	   * 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF, 0xD9}
	   * @param src String
	   * @return byte[]
	   */
	  public static byte[] HexString2Bytes(String src){
	    byte[] ret = new byte[8];
	    byte[] tmp = src.getBytes();
	    for(int i=0; i<8; i++){
	      ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
	    }
	    return ret;
	  }
}
