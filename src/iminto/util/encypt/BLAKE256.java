package iminto.util.encypt;

public class BLAKE256 extends BLAKESmallCore {

	/**
	 * Create the engine.
	 */
	public BLAKE256()
	{
		super();
	}

	/** The initial value for BLAKE-256. */
	private static final int[] initVal = {
		0x6A09E667, 0xBB67AE85, 0x3C6EF372, 0xA54FF53A,
		0x510E527F, 0x9B05688C, 0x1F83D9AB, 0x5BE0CD19
	};

	/** @see BLAKESmallCore */
	int[] getInitVal()
	{
		return initVal;
	}

	/** @see Digest */
	public int getDigestLength()
	{
		return 32;
	}

	/** @see Digest */
	public Digest copy()
	{
		return copyState(new BLAKE256());
	}
}
