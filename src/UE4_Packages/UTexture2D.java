/**
 * 
 */
package UE4_Packages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author FunGames
 *
 */
public class UTexture2D {
	private UObject baseObject;
	private boolean cooked;
	private List<FTexturePlatformData> textures;
	
	public UObject getBaseObject() {
		return baseObject;
	}

	public boolean isCooked() {
		return cooked;
	}

	public List<FTexturePlatformData> getTextures() {
		return textures;
	}

	public UTexture2D(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "Texture2D");
		textures = new ArrayList<>();
		
		FStripDataFlags flag1 = new FStripDataFlags(Ar);
		FStripDataFlags flag2 = new FStripDataFlags(Ar);
		
		boolean cooked = Ar.readBoolean();
		if(cooked) {
			String pixelFormat = Ar.readFName(nameMap);
			while(!pixelFormat.equals("None")) {
				long skipOffset = Ar.readInt64();
				FTexturePlatformData texture = new FTexturePlatformData(Ar, nameMap, importMap);
				if(Ar.Tell() + Ar.uassetSize != skipOffset) {
					throw new ReadException(String.format("Texture read incorrectly, Offset: %d Expected: %d", Ar.Tell() + Ar.uassetSize, skipOffset));
				}
				textures.add(texture);
				pixelFormat = Ar.readFName(nameMap);
			}
		} else {
			throw new ReadException("Cannot read uncooked textures", Ar.Tell() - 4);
		}
		
	}

	public String getPixelFormat() throws ReadException {
		if(!textures.isEmpty()) {
			return textures.get(0).getPixelFormat();
		} else {
			throw new ReadException("No textures part of export",-1);
		}
		
	}
	public FTexture2DMipMap getTexture() throws ReadException {
		if(!textures.isEmpty()) {
			if(textures.get(0).getMips().size() >0) {
				return textures.get(0).getMips().get(0);
			} else {
				throw new ReadException("No mipmaps part of texture",-1);
			}
		} else {
			throw new ReadException("No textures part of export",-1);
		}
	}
	
	
}
