/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FSkelMeshRenderSection {
	

	private short materialIndex;
	private int baseIndex;
	private int numTriangles;
	private long baseVertexIndex;
	private List<FMeshToMeshVertData> clothMappingData;
	private List<Integer> boneMap;
	private int numVertices;
	private int maxBoneInfluences;
	private FClothingSectionData clothingData;
	private boolean disabled;
	
	public short getMaterialIndex() {
		return materialIndex;
	}

	public int getBaseIndex() {
		return baseIndex;
	}

	public int getNumTriangles() {
		return numTriangles;
	}

	public long getBaseVertexIndex() {
		return baseVertexIndex;
	}

	public List<FMeshToMeshVertData> getClothMappingData() {
		return clothMappingData;
	}

	public List<Integer> getBoneMap() {
		return boneMap;
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getMaxBoneInfluences() {
		return maxBoneInfluences;
	}

	public FClothingSectionData getClothingData() {
		return clothingData;
	}

	public boolean getDisabled() {
		return disabled;
	}
	
	@SuppressWarnings("unused")
	public FSkelMeshRenderSection(FArchive Ar) throws ReadException {
		FStripDataFlags flags = new FStripDataFlags(Ar);
		materialIndex = Ar.readInt16();
		baseIndex = Ar.readInt32();
		numTriangles = Ar.readInt32();
		boolean _recomputeTangent = Ar.readBoolean();
		boolean _castShadow = Ar.readBoolean();
		baseVertexIndex = 0;
		if(!flags.isDataStrippedForServer()) {
			baseVertexIndex = Ar.readUInt32();
		}
		clothMappingData = new ArrayList<>();
		int clothMappingDataCount = Ar.readInt32();
		for(int i=0;i<clothMappingDataCount;i++) {
			clothMappingData.add(new FMeshToMeshVertData(Ar));
		}
		boneMap = Ar.readTArrayOfUInt16();
		
		numVertices = Ar.readInt32();
		maxBoneInfluences = Ar.readInt32();
		short _correspondClothAssetIndex = Ar.readInt16();
		clothingData = new FClothingSectionData(Ar);
		List<Integer> _vertexBuffer = Ar.readTArrayOfInt32();
		List<FIndexLengthPair> _indexPairs = new ArrayList<>();
		int indexPairCount = Ar.readInt32();
		for(int i=0;i<indexPairCount;i++) {
			_indexPairs.add(new FIndexLengthPair(Ar));
		}
		disabled = Ar.readBoolean();
	}
}
