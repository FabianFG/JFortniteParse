/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FSkinWeightInfo {
	
	private byte[] boneIndex; //Size 8
	private byte[] boneWeight; //Size 8


	public byte[] getBoneIndex() {
		return boneIndex;
	}

	public byte[] getBoneWeight() {
		return boneWeight;
	}

	public FSkinWeightInfo(FArchive Ar, int maxInfluences) throws ReadException {
		if(maxInfluences > 8) {
			throw new ReadException("Max influences too high", Ar.Tell());
		}
		boneIndex = new byte[maxInfluences];
		for(int i=0;i<maxInfluences; i++) {
			boneIndex[i] = Ar.readUInt8();
		}
		boneWeight = new byte[maxInfluences];
		for(int i=0;i<maxInfluences; i++) {
			boneWeight[i] = Ar.readUInt8();
		}
	}
}
