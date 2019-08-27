/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;
import annotation.FixedArraySize;
import annotation.BooleanZ;
import annotation.Float32;
import annotation.Serializable;
import annotation.UeExclude;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FMeshUVChannelInfo {

	@UeExclude public static final int TEXSTREAM_MAX_NUM_UVCHANNELS = 4;

	@BooleanZ private boolean bInitialized;
	@BooleanZ private boolean bOverrideDensities;
	@FixedArraySize(TEXSTREAM_MAX_NUM_UVCHANNELS) @Float32 private float[] localUVDensities;
	
	public FMeshUVChannelInfo(FArchive Ar) throws ReadException {
		bInitialized = Ar.readBoolean();
		bOverrideDensities = Ar.readBoolean();
		localUVDensities = new float[TEXSTREAM_MAX_NUM_UVCHANNELS];
		for(int i=0;i<TEXSTREAM_MAX_NUM_UVCHANNELS; i++) {
			localUVDensities[i] = Ar.readFloat32();
		}
	}

}
