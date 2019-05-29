/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FStreamedAudioChunk {
	private FByteBulkData data;
	private int dataSize;
	private int audioDataSize;

	public FByteBulkData getData() {
		return data;
	}

	public int getDataSize() {
		return dataSize;
	}

	public int getAudioDataSize() {
		return audioDataSize;
	}

	public FStreamedAudioChunk(FArchive Ar) throws ReadException {
		boolean bCooked = Ar.readBoolean();
		if(bCooked) {
			data = new FByteBulkData(Ar);
			dataSize = Ar.readInt32();
			audioDataSize = Ar.readInt32();
		} else {
			throw new ReadException("StreamedAudioChunks must be cooked", Ar.Tell() -4);
		}
	}
	
}
