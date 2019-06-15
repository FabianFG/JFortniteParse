/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FMeshUVChannelInfo {

	public static final int TEXSTREAM_MAX_NUM_UVCHANNELS = 4;

	private boolean bInitialized;
	private boolean bOverrideDensities;
	private float[] localUVDensities;

	

	public boolean getbInitialized() {
		return bInitialized;
	}

	public boolean getbOverrideDensities() {
		return bOverrideDensities;
	}

	public float[] getLocalUVDensities() {
		return localUVDensities;
	}
	
	public FMeshUVChannelInfo(FArchive Ar) throws ReadException {
		bInitialized = Ar.readBoolean();
		bOverrideDensities = Ar.readBoolean();
		localUVDensities = new float[TEXSTREAM_MAX_NUM_UVCHANNELS];
		for(int i=0;i<TEXSTREAM_MAX_NUM_UVCHANNELS; i++) {
			localUVDensities[i] = Ar.readFloat32();
		}
	}

}
