/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FSkeletalMaterial {

	private FPackageIndex materialInterface;
	private String materialSlotName;
	private FMeshUVChannelInfo uvChannelData;

	public FPackageIndex getMaterialInterface() {
		return materialInterface;
	}

	public String getMaterialSlotName() {
		return materialSlotName;
	}

	public FMeshUVChannelInfo getUvChannelData() {
		return uvChannelData;
	}
	
	public String getInterface() {
		return this.materialInterface.getImportName();
	}

	public FSkeletalMaterial(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		materialInterface = new FPackageIndex(Ar, importMap);
		boolean serializeSlotName = Ar.readBoolean();
		materialSlotName = serializeSlotName ? Ar.readFName(nameMap) : "";
		uvChannelData = new FMeshUVChannelInfo(Ar);
	}
}
