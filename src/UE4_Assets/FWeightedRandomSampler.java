/**
 * 
 */
package UE4_Assets;

import java.util.List;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FWeightedRandomSampler {

	private List<Float> prob; //Of Float32
	private List<Integer> alias; //Of Int32
	private float totalWeight;
	
	public FWeightedRandomSampler(FArchive Ar) throws ReadException {
		prob = Ar.readTArrayOfFloat32();
		alias = Ar.readTArrayOfInt32();
		totalWeight = Ar.readFloat32();
	}

	public List<Float> getProb() {
		return prob;
	}

	public List<Integer> getAlias() {
		return alias;
	}

	public float getTotalWeight() {
		return totalWeight;
	}
}
