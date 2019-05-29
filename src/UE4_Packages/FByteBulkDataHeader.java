/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FByteBulkDataHeader {
	
	private int bulkDataFlags;
	private int elementCount;
	private int sizeOnDisk;
	private long offsetInFile;

	public FByteBulkDataHeader(FArchive Ar) throws ReadException {
		bulkDataFlags = Ar.readInt32();
		elementCount = Ar.readInt32();
		sizeOnDisk = Ar.readInt32();
		offsetInFile = Ar.readInt64();
	}

	public int getBulkDataFlags() {
		return bulkDataFlags;
	}

	public int getElementCount() {
		return elementCount;
	}

	public int getSizeOnDisk() {
		return sizeOnDisk;
	}

	public long getOffsetInFile() {
		return offsetInFile;
	}

}
