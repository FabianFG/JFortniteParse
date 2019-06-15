/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.BoolProperty;

/**
 * @author FunGames
 *
 */
public class USkeletalMesh4 {
	
	private UObject superObject;
	private FBoxSphereBounds importedBounds;
	private List<FSkeletalMaterial> materials;
	private FReferenceSkeleton refSkeleton;
	private List<FSkeletalMeshRenderData> lodModels;

	public UObject getSuperObject() {
		return superObject;
	}

	public FBoxSphereBounds getImportedBounds() {
		return importedBounds;
	}

	public List<FSkeletalMaterial> getMaterials() {
		return materials;
	}

	public FReferenceSkeleton getRefSkeleton() {
		return refSkeleton;
	}

	public List<FSkeletalMeshRenderData> getLodModels() {
		return lodModels;
	}
	public FSkeletalMeshRenderData getFirstLod() {
		return lodModels.get(0);
	}
	
	public USkeletalMesh4(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		superObject = new UObject(Ar, nameMap, importMap, "SkeletalMesh");
		
		boolean hasVertexColors = false;
		FPropertyTagType.BoolProperty propertyObject = (BoolProperty) superObject.getPropertyByName("bHasVertexColors");
		hasVertexColors = propertyObject != null ? propertyObject.getBool() : false;
		
		FStripDataFlags stripFlags = new FStripDataFlags(Ar);
		importedBounds = new FBoxSphereBounds(Ar);
		materials = new ArrayList<>();
		int elemCount = Ar.readInt32();
		for(int i=0;i<elemCount; i++) {
			materials.add(new FSkeletalMaterial(Ar, nameMap, importMap));
		}
		refSkeleton = new FReferenceSkeleton(Ar, nameMap, importMap);
		if(!stripFlags.isEditorDataStripped()) {
			System.out.println("editor data still present");
		}
		
		boolean cooked = Ar.readBoolean();
		if(!cooked) {
			throw new ReadException("Asset does not contain cooked data.", Ar.Tell() - 4);
		}
		long numModels = Ar.readUInt32();
		lodModels = new ArrayList<>();
		for(int i=0;i<numModels;i++) {
			lodModels.add(new FSkeletalMeshRenderData(Ar, hasVertexColors));
		}
		
		if(lodModels.get(lodModels.size()-1) != null) {
			long _serializeGUID = Ar.readUInt32();
		}
		
	}
	
}
