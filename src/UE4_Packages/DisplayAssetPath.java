/**
 * 
 */
package UE4_Packages;

import java.util.List;

import UE4_Packages.FPropertyTagType.ObjectProperty;
import UE4_Packages.FPropertyTagType.StructProperty;

/**
 * @author FunGames
 *
 */
public class DisplayAssetPath {

	private UObject baseObject;
	private String detailsImage;
	private String tileImage;


	public String getDetailsImage() {
		return detailsImage;
	}


	public UObject getBaseObject() {
		return baseObject;
	}

	public DisplayAssetPath(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "FortMtxOfferData");
		String detailsImageObjectName = null;
		StructProperty detailsImage = (StructProperty) baseObject.getPropertyByName("DetailsImage");
		FStructFallback f = detailsImage != null ? ((FStructFallback)detailsImage.getStruct().getStructType()) : null;
		List<FPropertyTag> list = f!= null ? f.getProperties() : null;
		for(FPropertyTag tag : list) {
			if(tag.getName().equals("ResourceObject") && tag.getPropertyType().equals("ObjectProperty")) {
				ObjectProperty o = (ObjectProperty) tag.getTag();
				detailsImageObjectName = o.getStruct().getImportName();
				
			}
		}
		
		this.detailsImage = null;
		for(FObjectImport importObject : importMap.getEntrys()) {
			if(importObject.getClassName().equals("Package")) {
				if(detailsImageObjectName != null) {
					if(importObject.getObjectName().endsWith(detailsImageObjectName)) {
						this.detailsImage = importObject.getObjectName();
						break;
					}
				}
			}
		}
		
		
		String tileImageObjectName = null;
		StructProperty tileImage = (StructProperty) baseObject.getPropertyByName("TileImage");
		FStructFallback f2 = tileImage != null ? ((FStructFallback)tileImage.getStruct().getStructType()) : null;
		List<FPropertyTag> list2 = f2!= null ? f2.getProperties() : null;
		for(FPropertyTag tag : list2) {
			if(tag.getName().equals("ResourceObject") && tag.getPropertyType().equals("ObjectProperty")) {
				ObjectProperty o = (ObjectProperty) tag.getTag();
				tileImageObjectName = o.getStruct().getImportName();
				
			}
		}
		
		this.tileImage = null;
		for(FObjectImport importObject : importMap.getEntrys()) {
			if(importObject.getClassName().equals("Package")) {
				if(tileImageObjectName != null) {
					if(importObject.getObjectName().endsWith(tileImageObjectName)) {
						this.tileImage = importObject.getObjectName();
						break;
					}
				}
			}
		}
	}
	
}
