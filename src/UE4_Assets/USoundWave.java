/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.BoolProperty;

/**
 * @author FunGames
 *
 */
public class USoundWave {

	private boolean bStreaming;
	private boolean bCooked;
	private FByteBulkData rawData;
	private FGUID compressedDataGUID;
	private List<FSoundFormatData> compressedFormatData;

	private String format;
	private List<FStreamedAudioChunk> streamedAudioChunks;

	private int sampleRate;
	private int numChannels;

	private UObject baseObject;


	public boolean isCooked() {
		return bCooked;
	}

	public boolean isStreaming() {
		return bStreaming;
	}

	public FByteBulkData getRawData() {
		return rawData;
	}

	public FGUID getCompressedDataGUID() {
		return compressedDataGUID;
	}

	public List<FSoundFormatData> getCompressedFormatData() {
		return compressedFormatData;
	}

	public String getFormat() {
		return format;
	}

	public List<FStreamedAudioChunk> getStreamedAudioChunks() {
		return streamedAudioChunks;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getNumChannels() {
		return numChannels;
	}
	
	public USoundWave(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "SoundWave");
		bCooked = Ar.readBoolean();
		FPropertyTagType streamingProperty = baseObject.getPropertyByName("bStreaming");
		bStreaming = false;
		if(streamingProperty != null) {
			if(streamingProperty instanceof FPropertyTagType.BoolProperty) {
				BoolProperty streamingPropertyBoolProperty = (FPropertyTagType.BoolProperty) streamingProperty;
				bStreaming = streamingPropertyBoolProperty.getBool();
			}
		}
		if(!bStreaming) {
			if(bCooked) {
				compressedFormatData = new ArrayList<>();
				int elemCount = Ar.readInt32();
				for(int i=0;i<elemCount;i++) {
					System.out.print("Reading chunk " + i + "...");
					compressedFormatData.add(new FSoundFormatData(Ar, nameMap));
					format = compressedFormatData.get(i).getFormatName();
				}
				compressedDataGUID = new FGUID(Ar);
			} else {
				System.out.print("Reading raw audio data...");
				rawData = new FByteBulkData(Ar);
				compressedDataGUID = new FGUID(Ar);
			}
		} else {
			compressedDataGUID = new FGUID(Ar);
			int numChunks = Ar.readInt32();
			format = Ar.readFName(nameMap);
			System.out.println(String.format("Found streamed SoundWave with format '%s' and %d chunk(s)", format, numChunks));
			streamedAudioChunks = new ArrayList<>();
			for(int i=0;i<numChunks;i++) {
				System.out.print("Reading streamed chunk " + i + "...");
				streamedAudioChunks.add(new FStreamedAudioChunk(Ar));
			}
		}
	}

	public UObject getBaseObject() {
		return baseObject;
	}
}
