/**
 * 
 */
package Converters;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.tomgibara.bits.BitReader;
import com.tomgibara.bits.Bits;

import ASTC.ASTC;
import UE4_Assets.FTexture2DMipMap;
import UE4_Assets.Float8;
import UE4_Assets.Package;
import UE4_Assets.ReadException;
import UE4_Assets.exports.UTexture2D;
import ddsutil.DDSUtil;
import gr.zdimensions.jsquish.Squish;

/**
 * @author FunGames
 *
 */
public class Texture2DToBufferedImage {
	
	private static Map<String, PixelFormatInfo> formats = new HashMap<>();
	static {
		
		formats.put("PF_ASTC_4x4", new PixelFormatInfo(0, (byte)4, (byte)4, (byte)16, 0, 0, (byte)0, "ATC_4x4"));
		formats.put("PF_ASTC_6x6", new PixelFormatInfo(0, (byte)6, (byte)6, (byte)16, 0, 0, (byte)0, "ATC_6x6"));
		formats.put("PF_ASTC_8x8", new PixelFormatInfo(0, (byte)8, (byte)8, (byte)16, 0, 0, (byte)0, "ATC_8x8"));
		formats.put("PF_ASTC_10x10", new PixelFormatInfo(0, (byte)10, (byte)10, (byte)16, 0, 0, (byte)0, "ATC_10x10"));
		formats.put("PF_ASTC_12x12", new PixelFormatInfo(0, (byte)12, (byte)12, (byte)16, 0, 0, (byte)0, "ATC_12x12"));
		formats.put("PF_B8G8R8A8", new PixelFormatInfo(0, (byte)1, (byte)1, (byte)4, 32, 32, (byte)0, "BGRA8"));
		formats.put("PF_R8G8B8A8", new PixelFormatInfo(0, (byte)1, (byte)1, (byte)4, 32, 32, (byte)0, "RGBA8"));
		//TODO Implement more formats
	}

	public static void main(String[] args) throws ReadException, IOException {
		File uasset = new File(
				"D:\\Fabian\\Programme\\Fortnite\\UmodelSaved\\FortniteGame\\Content\\Characters\\Player\\Male\\Medium\\Bodies\\M_MED_Assassin\\Meshes\\textures\\M_MED_Soldier_Assassin_Body_N.uasset");
		File uexp = new File(
				"D:\\Fabian\\Programme\\Fortnite\\UmodelSaved\\FortniteGame\\Content\\Characters\\Player\\Male\\Medium\\Bodies\\M_MED_Assassin\\Meshes\\textures\\M_MED_Soldier_Assassin_Body_N.uexp");
		File ubulk = new File(
				"D:\\Fabian\\Programme\\Fortnite\\UmodelSaved\\FortniteGame\\Content\\Characters\\Player\\Male\\Medium\\Bodies\\M_MED_Assassin\\Meshes\\textures\\M_MED_Soldier_Assassin_Body_N.ubulk");
		;
		Package uePackage = Package.fromFiles(uasset, uexp, ubulk);
		if (uePackage.getExports().size() > 0) {
			Object export = uePackage.getExports().get(0);
			if (export instanceof UTexture2D) {
				UTexture2D texture = (UTexture2D) export;
				BufferedImage result = readTexture(texture);
				File out = new File(uasset.getName().substring(0, uasset.getName().length() - 7) + ".png");
				FileOutputStream fos = new FileOutputStream(out);
				ImageIO.write(result, "png", fos);
				fos.close();
			}
		}

	}

	public static synchronized BufferedImage readTexture(UTexture2D texture) throws ReadException, IOException {
		String pixelFormat = texture.getPixelFormat();
		FTexture2DMipMap textureMipMap = texture.getTexture();

		int width = textureMipMap.getSizeX();
		int height = textureMipMap.getSizeY();
		byte[] data = textureMipMap.getData().getData();

		switch (pixelFormat) {
		case "PF_DXT5":
			Squish.CompressionType pfFormat = Squish.CompressionType.DXT5;
			BufferedImage res = DDSUtil.decompressTexture(ByteBuffer.wrap(data), width, height, pfFormat);
			return res;
		case "PF_DXT1":
			Squish.CompressionType pfFormat1 = Squish.CompressionType.DXT1;
			BufferedImage res1 = DDSUtil.decompressTexture(ByteBuffer.wrap(data), width, height, pfFormat1);
			return res1;
		case "PF_BC5":
			BufferedImage res2 = readBC5(data, width, height);
			return res2;
		case "PF_B8G8R8A8":
			PixelFormatInfo info = formats.get(pixelFormat);
			int pixelSize = info.Float != 0 ? 16 : 4;
			int size = width * height * pixelSize;
			BufferedImage res3 = bgraBufferToImage(data, width, height, size);
			return res3;
		case "PF_R8G8B8A8":
			BufferedImage res4 = rgbaBufferToImage(data, width, height);
			return res4;
		case "PF_ASTC_4x4":
		case "PF_ASTC_6x6":
		case "PF_ASTC_8x8":
		case "PF_ASTC_10x10":
		case "PF_ASTC_12x12":
		{
			BufferedImage res5 = readASCT(pixelFormat, data, width, height);
			return res5;
		}
		default:
			System.err.println("Unknown Pixelformat: " + pixelFormat);
		}
		return null;

	}
	
	private static BufferedImage bgraBufferToImage(byte[] data, int width, int height, int dst_size) {
		int s = 0;
		for(int i=0; i<width * height; i++) {
			//BGRA to RGBA
			byte b = data[s];
			byte r = data[s+2];
			data[s] = r;
			data[s+2] = b;
			s+=4;
		}
		return rgbaBufferToImage(data, width, height);
	}
	
	private static BufferedImage readASCT(String format, byte[] data, int width, int height) throws IOException {
		PixelFormatInfo formatInfo = formats.get(format);
		int USize = width;
		int VSize = height;
		
		int blockSizeX = formatInfo.BlockSizeX;
		int blockSizeY = formatInfo.BlockSizeY;
		
		int pixelSize = formatInfo.Float != 0 ? 16 : 4;
		int size = USize * VSize * pixelSize;
		byte[] dst = new byte[size];
		
		ASTC.decompressASTC(data, dst, USize, VSize, blockSizeX, blockSizeY, false/*isNormalMap*/);
		return rgbaBufferToImage(dst, width, height);
	}

	/**
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 * @throws AssertionException
	 */
	private static BufferedImage readBC5(byte[] data, int width, int height) throws IOException {
		byte[] res = new byte[width * height * 3];
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// image.setRGB(x, y, rgb);
		ByteArrayInputStream bin = new ByteArrayInputStream(data);

		for (int yBlock = 0; yBlock < height / 4; yBlock++) {
			for (int xBlock = 0; xBlock < width / 4; xBlock++) {
				byte[] rBytes = decodeBC3Block(bin);
				byte[] gBytes = decodeBC3Block(bin);
				for (int r = 0; r < 16; r++) {
					int xOff = r % 4;
					int yOff = r / 4;
					res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 0)] = rBytes[r];
				}
				for (int g = 0; g < 16; g++) {
					int xOff = g % 4;
					int yOff = g / 4;
					res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 1)] = rBytes[g];
				}
				for (int b = 0; b < 16; b++) {
					int xOff = b % 4;
					int yOff = b / 4;
					byte bVal = getZNormal(rBytes[b], gBytes[b]);
					res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 2)] = bVal;
				}
			}
		} 
		image = rgbBufferToImage(res, width, height);

		return image;
	}
	
	private static BufferedImage rgbBufferToImage(byte[] rgb, int width, int height) {
		DataBuffer buffer = new DataBufferByte(rgb, rgb.length);

		//3 bytes per pixel: red, green, blue
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[] {0, 1, 2}, (Point)null);
		ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE); 
		BufferedImage image = new BufferedImage(cm, raster, true, null);

		return image;
	}
	
	private static BufferedImage rgbaBufferToImage(byte[] rgba, int width, int height) {
		
		DataBuffer buffer = new DataBufferByte(rgba, rgba.length);

		//3 bytes per pixel: red, green, blue
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 4 * width, 4, new int[] {0, 1, 2, 3}, (Point)null);
		ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), true, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE); 
		BufferedImage image = new BufferedImage(cm, raster, true, null);

		return image;
	}

	@SuppressWarnings("unused")
	private static int getColorValue(byte[] data) {
		int sum = 0;
		for (byte d : data) {
			sum += d & 0xFF;
		}
		return sum / data.length;
	}

	private static int getPixelLoc(int width, int x, int y, int off) {
		return (y * width + x) * 3 + off;
	}

	private static byte getZNormal(byte x, byte y) {
		float xf = (float) (((x & 0xFF) / 127.5) - 1);
		float yf = (float) (((y & 0xFF) / 127.5) - 1);
		float zval = (float) Math.min(Math.sqrt(Math.max((1.0f - xf * xf - yf * yf), 0.0f)), 1.0f);
		Float f = (float) ((zval * 127.0) + 128.0);
		return f.byteValue();
	}

	/**
	 * @param bin
	 * @return
	 * @throws AssertionException
	 * @throws IOException
	 */
	private static byte[] decodeBC3Block(ByteArrayInputStream bin) throws IOException {
		byte[] ref0A = new byte[1];
		bin.read(ref0A);
		Float ref0 = new Float(Float8.readFloat(ref0A, true));
		byte[] ref1A = new byte[1];
		bin.read(ref1A);
		float ref1 = new Float(Float8.readFloat(ref1A, true));

		Float[] ref_sl = new Float[8];
		ref_sl[0] = ref0;
		ref_sl[1] = ref1;

		if (ref0 > ref1) {
			ref_sl[2] = (6.0f * ref0 + 1.0f * ref1) / 7.0f;
			ref_sl[3] = (5.0f * ref0 + 2.0f * ref1) / 7.0f;
			ref_sl[4] = (4.0f * ref0 + 3.0f * ref1) / 7.0f;
			ref_sl[5] = (3.0f * ref0 + 4.0f * ref1) / 7.0f;
			ref_sl[6] = (2.0f * ref0 + 5.0f * ref1) / 7.0f;
			ref_sl[7] = (1.0f * ref0 + 6.0f * ref1) / 7.0f;
		} else {
			ref_sl[2] = (4.0f * ref0 + 1.0f * ref1) / 5.0f;
			ref_sl[3] = (3.0f * ref0 + 2.0f * ref1) / 5.0f;
			ref_sl[4] = (2.0f * ref0 + 3.0f * ref1) / 5.0f;
			ref_sl[5] = (1.0f * ref0 + 4.0f * ref1) / 5.0f;
			ref_sl[6] = 0.0f;
			ref_sl[7] = 255.0f;
		}

		byte[] indexBlock1 = new byte[3];
		bin.read(indexBlock1);
		indexBlock1 = getBC3Indices(indexBlock1);
		byte[] indexBlock2 = new byte[3];
		bin.read(indexBlock2);
		indexBlock2 = getBC3Indices(indexBlock2);

		byte[] bytes = new byte[16];
		for (int i = 0; i < 8; i++) {
			byte blockValue = indexBlock1[i];
			Float c = ref_sl[(int) blockValue];
			bytes[7 - i] = Float8.floatToLittleEndian(c)[0];
		}
		for (int i = 0; i < 8; i++) {
			byte blockValue = indexBlock2[i];
			Float c = ref_sl[(int) blockValue];
			bytes[15 - i] = Float8.floatToLittleEndian(c)[0];
		}
		return bytes;
	}

	public static byte[] getBC3Indices(byte[] bufBlock) throws IOException {
		byte[] bufTest = new byte[3];
		bufTest[0] = bufBlock[2];
		bufTest[1] = bufBlock[1];
		bufTest[2] = bufBlock[0];

		byte[] indices = new byte[8];
		BitReader reader = Bits.readerFrom(bufTest);
		for (int i = 0; i < 8; i++) {
			indices[i] = (byte) reader.read(3);
		}
		return indices;
	}
}
