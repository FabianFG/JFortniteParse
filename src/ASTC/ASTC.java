/**
 * 
 */
package ASTC;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import Converters.Texture2DToBufferedImage;
import UE4_Assets.Package;
import UE4_Assets.ReadException;
import UE4_Assets.exports.UTexture2D;

/**
 * @author FunGames
 *
 */
public class ASTC {
	
	public static void main(String[] args) throws IOException, ReadException {
		File uasset = new File(
				"D:\\Fabian\\WORKSPACE\\PakBrowserAES\\Output\\FortniteGame\\Content\\2dAssets\\Loadingscreens\\Season6\\T_LS_S6_Cumulative_01.uasset");
		File uexp = new File(
				"D:\\Fabian\\WORKSPACE\\PakBrowserAES\\Output\\FortniteGame\\Content\\2dAssets\\Loadingscreens\\Season6\\T_LS_S6_Cumulative_01.uexp");
		File ubulk = null;
		;
		Package uePackage = Package.fromFiles(uasset, uexp, ubulk);
		BufferedImage test = Texture2DToBufferedImage.readTexture((UTexture2D) uePackage.getExports().get(0));
		FileOutputStream fos = new FileOutputStream("testASTC.png");
		ImageIO.write(test, "png", fos);
		fos.close();
	}
	
	
	public native static void decodeASTC(byte[] inputBuffer, byte[] outputBuffer, int USize, int VSize, int size, int blockSizeX, int blockSizeY, boolean isNormalmap);
	
	public static void decompressASTC(byte[] inputBuffer, byte[] outputBuffer, int USize, int VSize, int blockSizeX, int blockSizeY, boolean isNormalmap) throws IOException {
		
		//Load Library
		InputStream in = ASTC.class.getResourceAsStream("ASTC.dll");
	    byte[] buffer = new byte[1024];
	    int read = -1;
	    File temp = File.createTempFile("o.dll", "");
	    temp.deleteOnExit();
	    FileOutputStream fos = new FileOutputStream(temp);

	    while((read = in.read(buffer)) != -1) {
	        fos.write(buffer, 0, read);
	    }
	    fos.close();
	    in.close();

	    System.load(temp.getAbsolutePath());
		
		//Run Native Method
		decodeASTC(inputBuffer, outputBuffer, outputBuffer.length, USize, VSize, blockSizeX, blockSizeY, isNormalmap);
	}
}
