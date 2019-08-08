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
public class FTexturePlatformData {
	
	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getNumSlices() {
		return numSlices;
	}

	public String getPixelFormat() {
		return pixelFormat;
	}

	public int getFirstMip() {
		return firstMip;
	}

	public List<FTexture2DMipMap> getMips() {
		return mips;
	}

	private int sizeX;
	private int sizeY;
	private int numSlices;
	private String pixelFormat;
	private int firstMip;
	private List<FTexture2DMipMap> mips;
	
	public FTexturePlatformData(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		//System.out.println("Reading Texture2D...");
		sizeX = Ar.readInt32();
		//System.out.println("Size X: " + sizeX);
		sizeY = Ar.readInt32();
		//System.out.println("Size Y: " + sizeY);
		numSlices = Ar.readInt32();
		pixelFormat = Ar.readString();
		firstMip = Ar.readInt32();
		int mipCount = Ar.readInt32();
		//System.out.println("MipMap Count:" + mipCount);
		mips = new ArrayList<>();
		for(int i=0; i<mipCount; i++) {
			//System.out.print("Reading MipMap " + i + "...");
			mips.add(new FTexture2DMipMap(Ar));
		}
		
	}
	
	

	
}
