/**
 * 
 */
package UE4_Assets.exports;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.FStripDataFlags;
import UE4_Assets.FTexture2DMipMap;
import UE4_Assets.FTexturePlatformData;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class UTexture2D {
	private UObject baseObject;
	private FStripDataFlags flag1;
	private FStripDataFlags flag2;
	private boolean cooked;
	
	private List<String> pixelFormats;
	private List<FTexturePlatformData> textures;
	
	public UTexture2D(FArchive Ar) throws ReadException, DeserializationException {
		baseObject = Ar.read(UObject.class, "Texture2D");
		flag1 = Ar.read(FStripDataFlags.class);
		flag2 = Ar.read(FStripDataFlags.class);

		cooked = Ar.readBoolean();
		textures = new ArrayList<>();
		pixelFormats = new ArrayList<>();
		if (cooked) {
			String pixelFormat = Ar.readFName();
			while (!pixelFormat.equals("None")) {
				long skipOffset = Ar.readInt64();
				FTexturePlatformData texture = Ar.read(FTexturePlatformData.class);
				if (Ar.Tell() + Ar.uassetSize != skipOffset) {
					throw new ReadException(String.format("Texture read incorrectly, Offset: %d Expected: %d",
							Ar.Tell() + Ar.uassetSize, skipOffset));
				}
				textures.add(texture);
				pixelFormats.add(pixelFormat);
				pixelFormat = Ar.readFName();
			}
		} else {
			throw new ReadException("Cannot read uncooked textures", Ar.Tell() - 4);
		}
	}

	public String getPixelFormat() throws ReadException {
		if (!textures.isEmpty()) {
			return textures.get(0).getPixelFormat();
		} else {
			throw new ReadException("No textures part of export", -1);
		}

	}

	public FTexture2DMipMap getTexture() throws ReadException {
		if (!textures.isEmpty()) {
			if (textures.get(0).getMips().size() > 0) {
				return textures.get(0).getMips().get(0);
			} else {
				throw new ReadException("No mipmaps part of texture", -1);
			}
		} else {
			throw new ReadException("No textures part of export", -1);
		}
	}

}
