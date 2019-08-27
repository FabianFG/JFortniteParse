/**
 * 
 */
package UE4_Assets.exports;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.FByteBulkData;
import UE4_Assets.FGUID;
import UE4_Assets.FPropertyTagType;
import UE4_Assets.FPropertyTagType.BoolProperty;
import UE4_Assets.FSoundFormatData;
import UE4_Assets.FStreamedAudioChunk;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
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
	
	public USoundWave(FArchive Ar) throws DeserializationException, ReadException {
		baseObject = Ar.read(UObject.class, "SoundWave");
		bCooked = Ar.readBoolean();
		FPropertyTagType streamingProperty = baseObject.getPropertyByName("bStreaming");
		bStreaming = false;
		if(streamingProperty != null) {
			if(streamingProperty instanceof FPropertyTagType.BoolProperty) {
				BoolProperty streamingPropertyBoolProperty = (FPropertyTagType.BoolProperty) streamingProperty;
				bStreaming = streamingPropertyBoolProperty.isBool();
			}
		}
		if(!bStreaming) {
			if(bCooked) {
				compressedFormatData = new ArrayList<>();
				int elemCount = Ar.readInt32();
				for(int i=0;i<elemCount;i++) {
					compressedFormatData.add(Ar.read(FSoundFormatData.class));
					format = compressedFormatData.get(i).getFormatName();
				}
				compressedDataGUID = Ar.read(FGUID.class);
			} else {
				rawData = Ar.read(FByteBulkData.class);
				compressedDataGUID = Ar.read(FGUID.class);
			}
		} else {
			compressedDataGUID = Ar.read(FGUID.class);
			int numChunks = Ar.readInt32();
			format = Ar.readFName();
			streamedAudioChunks = new ArrayList<>();
			for(int i=0;i<numChunks;i++) {
				streamedAudioChunks.add(Ar.read(FStreamedAudioChunk.class));
			}
		}
	}

}
