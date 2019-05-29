package UE4;

public class PAK {
	public static final int INDEX = 1;
	public static final int ASSETS = 2;
	public static final int INIS = 3;
	public static final byte INDEX_CHECK_BYTE = 0x00;
	public static final byte INI_CHECK_BYTE = 0x5B;
	public static final int ASSET_INDEX = 53;

	/**
	 * 
	 * @param namelength (With null termination byte);
	 * @return length of the index entry
	 */
	public static int INDEX_ENTRY_LENGTH(int namelength) {
		return 4 + namelength + 53;
	};

	public static final int INDEX_ENCRYPTION_BOOLEAN_OFFSET = 45;
	public static final int INDEX_CHECKSUM_OFFSET = 20;
	public static final String UASSET = ".uasset";
	public static final String UEXP = ".uexp";
	public static final String UBULK = ".ubulk";
	public static final String INI = ".ini";
	public static final byte[] UASSET_HEADER = { (byte) 0xC1, (byte) 0x83, (byte) 0x2A, (byte) 0x9E, (byte) 0xF9,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
	public static final byte[] FIX_BYTES = { (byte) 0xC1, (byte) 0x83, (byte) 0x2A, (byte) 0x9E, (byte) 0xF9,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
	public static final String EXAMPLE_KEY = "0xbe2db196eb94c3ea458ffb6aa9fbe1edc4bd427afc8103c4197d081f28d9569e";
	public static final String Main_GUID = "0-0-0-0";
	
	public static final int PAK_FILE_MAGIC = 0x5A6F12E1;
	
	public static enum PakVersion {
		PAK_INITIAL( 1 ),
		PAK_NO_TIMESTAMPS(2),
		PAK_COMPRESSION_ENCRYPTION(3),			// UE4.3+
		PAK_INDEX_ENCRYPTION(4),				// UE4.17+ - encrypts only pak file index data leaving file content as is
		PAK_RELATIVE_CHUNK_OFFSETS(5),			// UE4.20+
		PAK_DELETE_RECORDS(6),					// UE4.21+ - this constant is not used in UE4 code
		PAK_ENCRYPTION_KEY_GUID(7),			// ... allows to use multiple encryption keys over the single project
		PAK_FNAME_BASED_COMPRESSION_METHOD(8), // UE4.22+ - use string instead of enum for compression method
		
		PAK_LATEST(8);
		
		public int v;
		
		PakVersion(int v) {
			this.v = v;
		}
		
		
	}
	public static enum CompressionMethod {
		COMPRESS_ZLIB ( 1 ),
		COMPRESS_LZO ( 2 ),
		/*COMPRESS_LZX ( 4 ), Seems to be not used in UE4 anymore*/
		COMPRESS_OODLE ( 4 ); //Is used instead of LZX in UE4
		
		public int m;
		
		CompressionMethod(int m) {
			this.m = m;
		}
	}

}
