/**
 * 
 */
package UE4_Packages;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FunGames
 *
 */
public class FSkeletalMeshRenderData {
	

	private List<FSkelMeshRenderSection> sections;
	private FMultisizeIndexContainer indices;
	private List<Short> activeBoneIndices;
	private List<Short> requiredBones;
	private FPositionVertexBuffer positionVertexBuffer;
	private FStaticMeshVertexBuffer staticMeshVertexBuffer;
	private FSkinWeightVertexBuffer skinWeightVertexBuffer;

	public List<FSkelMeshRenderSection> getSections() {
		return sections;
	}

	public FMultisizeIndexContainer getIndices() {
		return indices;
	}

	public List<Short> getActiveBoneIndices() {
		return activeBoneIndices;
	}

	public List<Short> getRequiredBones() {
		return requiredBones;
	}

	public FPositionVertexBuffer getPositionVertexBuffer() {
		return positionVertexBuffer;
	}

	public FStaticMeshVertexBuffer getStaticMeshVertexBuffer() {
		return staticMeshVertexBuffer;
	}

	public FSkinWeightVertexBuffer getSkinWeightVertexBuffer() {
		return skinWeightVertexBuffer;
	}

	public FSkeletalMeshRenderData(FArchive Ar, boolean hasVertexColors) throws ReadException {
		FStripDataFlags flags = new FStripDataFlags(Ar);
		sections = new ArrayList<>();
		int sectionCount = Ar.readInt32();
		for(int i=0;i<sectionCount;i++) {
			sections.add(new FSkelMeshRenderSection(Ar));
		}
		indices = new FMultisizeIndexContainer(Ar);
		activeBoneIndices = Ar.readTArrayOfInt16();
		requiredBones = Ar.readTArrayOfInt16();
		if(flags.isDataStrippedForServer() && flags.isClassDataStripped(2)) {
			throw new ReadException("Could not read FSkelMesh, no renderable data", Ar.Tell());
		}
		positionVertexBuffer = new FPositionVertexBuffer(Ar);
		staticMeshVertexBuffer = new FStaticMeshVertexBuffer(Ar);
		skinWeightVertexBuffer = new FSkinWeightVertexBuffer(Ar);
	}

}
