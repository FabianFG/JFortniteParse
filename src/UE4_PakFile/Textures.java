package UE4_PakFile;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import ddsutil.DDSUtil;

public class Textures {
	static String watermark = "PakBrowser by FunGamesLeaks";
	private static int fontSize = 15;
	private static String tempDir = System.getProperty("java.io.tmpdir");

	public static String extractTextureWithUbulk(byte[] buffer, byte[] textureData, String filename, String type,
			boolean png, boolean exportMode) {
		RandomAccessFile file;

		try {
			System.out.print("-----Converting World Texture to .png-----\n");
			int beginIndex = seekBegin(buffer);
			if (beginIndex != -1) {
				boolean dxt1 = false;
				if ((buffer[beginIndex + 6] & 0xFF) == 0x31) {
					dxt1 = true;
				}
				int textureBeginIndex = beginIndex + 40;
				int width = readLength(buffer, beginIndex + 40);
				int height = readLength(buffer, beginIndex + 44);
				System.out.print("\nHeight of the Image: " + height);
				System.out.print("\nWidth of the Image: " + width);
				System.out.print("\nGenerating DDS header... ");
				byte[] ddsHeader = generateDDSHeaderWithUbulk(beginIndex, buffer, dxt1);
				System.out.print("DONE");
				String decodedPath = System.getProperty("user.dir");
				String filen = filename.substring(0, filename.length() - 5);
				String ddsFileName;
				if (exportMode) {
					if (png == true) {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".dds";
					}
				} else {
					if (png == true) {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".dds";
					}
				}
				new File(new File(ddsFileName).getParent()).mkdirs();
				System.out.print("\nOpen Output file on: " + filen);
				File outfile = new File(ddsFileName);
				if (png == true) {
					ByteArrayOutputStream fos = new ByteArrayOutputStream();
					System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					System.out.print("DONE");
					System.out.print("\nWriting texture data... ");
					fos.write(textureData);
					System.out.print("DONE");
					fos.close();
					System.out.print("\nConverting dds to png...");
					byte[] dds = fos.toByteArray();

					BufferedImage image;
					if (dxt1 == false) {
						image = DDSUtil.decompressTexture(dds, width, height,
								gr.zdimensions.jsquish.Squish.CompressionType.DXT5);
					} else {
						image = DDSUtil.decompressTexture(dds, width, height,
								gr.zdimensions.jsquish.Squish.CompressionType.DXT1);
					}
					Graphics2D g2 = image.createGraphics();
					Font font;
					g2.setColor(Color.LIGHT_GRAY);
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("font.ttf"));
						font = font.deriveFont(Font.BOLD, fontSize);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						font = new Font("SansSerif", Font.BOLD, fontSize);
						e.printStackTrace();
					}
					g2.setFont(font);
					FontMetrics fm = g2.getFontMetrics();
					int x = (image.getHeight() / 2 - fm.stringWidth(watermark) / 2);
					int y = image.getWidth() / 2;
					g2.drawString(watermark, x, y);
					try {
						ImageIO.write(image, "png", outfile);
						System.out.print("DONE");
						System.out.print("\n\nCONVERSION COMPLETED\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
					return outfile.getAbsolutePath();
				} else {
					FileOutputStream fos = new FileOutputStream(outfile);
					System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					System.out.print("DONE");
					System.out.print("\nWriting texture data... ");
					fos.write(textureData);
					System.out.print("DONE");
					fos.close();
					System.out.print("\n\nCONVERSION COMPLETED\n");
					return outfile.getAbsolutePath();
				}

			} else {
				System.out.print("\n\nFile contains no Texture! Process is stopping!");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String extractTexture(byte[] buffer, String filename, String type, boolean png, boolean exportMode) {
		RandomAccessFile file;
		try {
			System.out.println("-----Converting UI Texture to .png-----\n");
			int beginIndex = seekBegin(buffer);
			if (beginIndex != -1) {
				boolean dxt1 = false;
				if ((buffer[beginIndex + 6] & 0xFF) == 0x31) {
					dxt1 = true;
				}
				int textureBeginIndex = beginIndex + 40;
				int width = readLength(buffer, beginIndex - 16);
				int height = readLength(buffer, beginIndex - 12);
				System.out.print("\nHeight of the Image: " + height);
				System.out.print("\nWidth of the Image: " + width);
				System.out.print("\nGenerating DDS header... ");
				byte[] ddsHeader = generateDDSHeader(beginIndex, buffer, dxt1);
				System.out.print("DONE");
				String decodedPath = System.getProperty("user.dir");
				String filen = filename.substring(0, filename.length() - 5);

				String ddsFileName;
				if (exportMode) {
					if (png == true) {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".dds";
					}
				} else {
					if (png == true) {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".dds";
					}
				}
				new File(new File(ddsFileName).getParent()).mkdirs();
				System.out.print("\nOpen Output file on: " + ddsFileName);
				byte[] textureData = new byte[buffer.length - textureBeginIndex];
				for (int i = textureBeginIndex; i < buffer.length; i++) {
					textureData[i - textureBeginIndex] = buffer[i];
				}
				File outfile = new File(ddsFileName);
				if (png == true) {
					ByteArrayOutputStream fos = new ByteArrayOutputStream();
					System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					System.out.print("DONE");
					System.out.print("\nWriting texture data... ");
					fos.write(buffer, textureBeginIndex, textureData.length - 24);
					System.out.print("DONE");
					System.out.print("\nConverting dds to png...");
					byte[] dds = fos.toByteArray();
					fos.close();

					// TODO Switch to creating temp files
					try {
						FileOutputStream test = new FileOutputStream(new File(tempDir + "\\.Temp\\" + filen + ".dds"));
						test.write(dds);
						test.close();
					} catch (FileNotFoundException e) {

					}
					BufferedImage image;
					if (dxt1 == false) {
						// image = DDSUtil.decompressTexture(dds, width, height,
						// gr.zdimensions.jsquish.Squish.CompressionType.DXT5);
						image = DDSUtil.decompressTexture(new File(tempDir + "\\.Temp\\" + filen + ".dds"));
						// FileUtils.forceDelete(new File(ddsFileName.replace(".png", ".dds")));
					} else {
						// image = DDSUtil.decompressTexture(dds, width, height,
						// gr.zdimensions.jsquish.Squish.CompressionType.DXT1);
						image = DDSUtil.decompressTexture(new File(tempDir + "\\.Temp\\" + filen + ".dds"));
						// FileUtils.forceDelete(new File(ddsFileName));
					}
					Graphics2D g2 = image.createGraphics();
					Font font;
					g2.setColor(Color.LIGHT_GRAY);
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("font.ttf"));
						font = font.deriveFont(Font.BOLD, fontSize);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						font = new Font("SansSerif", Font.BOLD, fontSize);
						e.printStackTrace();
					}
					g2.setFont(font);
					FontMetrics fm = g2.getFontMetrics();
					int x = (image.getHeight() / 2 - fm.stringWidth(watermark) / 2);
					int y = image.getWidth() / 2;
					g2.drawString(watermark, x, y);
					try {

						ImageIO.write(image, "png", outfile);
						System.out.print("DONE");
						System.out.print("\n\nCONVERSION COMPLETED\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
					return outfile.getAbsolutePath();
				} else {
					FileOutputStream fos = new FileOutputStream(outfile);
					System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					System.out.print("DONE");
					System.out.print("\nWriting texture data... ");
					fos.write(buffer, textureBeginIndex, textureData.length - 24);
					System.out.print("DONE");
					System.out.print("\n\nCONVERSION COMPLETED\n");
					fos.close();
					return outfile.getAbsolutePath();
				}
			} else {
				System.out.print("\n\nFile contains no UI Texture! Process is stopping!");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage extractTextureWithUbulkToBufferedImage(byte[] buffer, byte[] textureData,
			String filename, String type, boolean png, boolean exportMode) {
		RandomAccessFile file;
		try {
			System.out.print("-----Converting World Texture to .png-----\n");
			int beginIndex = seekBegin(buffer);
			if (beginIndex != -1) {
				boolean dxt1 = false;
				if ((buffer[beginIndex + 6] & 0xFF) == 0x31) {
					dxt1 = true;
				}
				int textureBeginIndex = beginIndex + 40;
				int width = readLength(buffer, beginIndex + 40);
				int height = readLength(buffer, beginIndex + 44);
				System.out.print("\nHeight of the Image: " + height);
				System.out.print("\nWidth of the Image: " + width);
				System.out.print("\nGenerating DDS header... ");
				byte[] ddsHeader = generateDDSHeaderWithUbulk(beginIndex, buffer, dxt1);
				System.out.print("DONE");
				String decodedPath = System.getProperty("user.dir");
				String filen = filename.substring(0, filename.length() - 5);
				String ddsFileName;
				if (exportMode) {
					if (png == true) {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".dds";
					}
				} else {
					if (png == true) {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".dds";
					}
				}
				new File(new File(ddsFileName).getParent()).mkdirs();
				System.out.print("\nOpen Output file on: " + filen);
				File outfile = new File(ddsFileName);
				if (png == true) {
					ByteArrayOutputStream fos = new ByteArrayOutputStream();
					System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					System.out.print("DONE");
					System.out.print("\nWriting texture data... ");
					fos.write(textureData);
					System.out.print("DONE");
					fos.close();
					System.out.print("\nConverting dds to png...");
					byte[] dds = fos.toByteArray();

					BufferedImage image;
					if (dxt1 == false) {
						image = DDSUtil.decompressTexture(dds, width, height,
								gr.zdimensions.jsquish.Squish.CompressionType.DXT5);
					} else {
						image = DDSUtil.decompressTexture(dds, width, height,
								gr.zdimensions.jsquish.Squish.CompressionType.DXT1);

					}
					Graphics2D g2 = image.createGraphics();
					Font font;
					g2.setColor(Color.LIGHT_GRAY);
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("font.ttf"));
						font = font.deriveFont(Font.BOLD, fontSize);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						font = new Font("SansSerif", Font.BOLD, fontSize);
						e.printStackTrace();
					}
					g2.setFont(font);
					FontMetrics fm = g2.getFontMetrics();
					int x = (image.getHeight() / 2 - fm.stringWidth(watermark) / 2);
					int y = image.getWidth() / 2;
					g2.drawString(watermark, x, y);
					return image;
				} else {
					FileOutputStream fos = new FileOutputStream(outfile);
					System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					System.out.print("DONE");
					System.out.print("\nWriting texture data... ");
					fos.write(textureData);
					System.out.print("DONE");
					fos.close();
					System.out.print("\n\nCONVERSION COMPLETED\n");
					return null;
				}

			} else {
				System.out.print("\n\nFile contains no Texture! Process is stopping!");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage extractTextureToBufferedImage(byte[] buffer, String filename, String type, boolean png,
			boolean exportMode) {
		RandomAccessFile file;
		try {
			System.out.println("-----Loading UI Texture-----");
			int beginIndex = seekBegin(buffer);
			if (beginIndex != -1) {
				boolean dxt1 = false;
				if ((buffer[beginIndex + 6] & 0xFF) == 0x31) {
					dxt1 = true;
				}
				int textureBeginIndex = beginIndex + 40;
				int width = readLength(buffer, beginIndex - 16);
				int height = readLength(buffer, beginIndex - 12);
				System.out.print("\nHeight of the Image: " + height);
				System.out.print("\nWidth of the Image: " + width);
				//System.out.print("\nGenerating DDS header... ");
				byte[] ddsHeader = generateDDSHeader(beginIndex, buffer, dxt1);
				//System.out.print("DONE");
				String decodedPath = System.getProperty("user.dir");
				String filen = filename.substring(0, filename.length() - 5);

				String ddsFileName;
				if (exportMode) {
					if (png == true) {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = decodedPath + "\\Exported\\" + type + "\\" + filen + ".dds";
					}
				} else {
					if (png == true) {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".png";
					} else {
						ddsFileName = tempDir + "\\.Temp\\" + type + "\\" + filen + ".dds";
					}
				}
				new File(new File(ddsFileName).getParent()).mkdirs();
				//System.out.print("\nOpen Output file on: " + ddsFileName);
				byte[] textureData = new byte[buffer.length - textureBeginIndex];
				for (int i = textureBeginIndex; i < buffer.length; i++) {
					textureData[i - textureBeginIndex] = buffer[i];
				}
				File outfile = new File(ddsFileName);
				if (png == true) {
					ByteArrayOutputStream fos = new ByteArrayOutputStream();
					//System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					//System.out.print("DONE");
					//System.out.print("\nWriting texture data... ");
					fos.write(buffer, textureBeginIndex, textureData.length - 24);
					//System.out.print("DONE");
					//System.out.print("\nConverting dds to png...");
					byte[] dds = fos.toByteArray();

					try {
						FileOutputStream test = new FileOutputStream(new File(tempDir + "\\.Temp\\" + filen + ".dds"));
						test.write(dds);
						test.close();
					} catch (FileNotFoundException e) {

					}
					BufferedImage image;
					if (dxt1 == false) {
						// image = DDSUtil.decompressTexture(dds, width, height,
						// gr.zdimensions.jsquish.Squish.CompressionType.DXT5);
						image = DDSUtil.decompressTexture(new File(tempDir + "\\.Temp\\" + filen + ".dds"));
						// FileUtils.forceDelete(new File(ddsFileName.replace(".png", ".dds")));
					} else {
						// image = DDSUtil.decompressTexture(dds, width, height,
						// gr.zdimensions.jsquish.Squish.CompressionType.DXT1);
						image = DDSUtil.decompressTexture(new File(tempDir + "\\.Temp\\" + filen + ".dds"));
						// FileUtils.forceDelete(new File(ddsFileName));
					}

					Graphics2D g2 = image.createGraphics();
					Font font;
					g2.setColor(Color.LIGHT_GRAY);
					try {
						font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("font.ttf"));
						font = font.deriveFont(Font.BOLD, fontSize);
					} catch (FontFormatException | IOException e) {
						// TODO Auto-generated catch block
						font = new Font("SansSerif", Font.BOLD, fontSize);
						e.printStackTrace();
					}
					g2.setFont(font);
					FontMetrics fm = g2.getFontMetrics();
					int x = (image.getHeight() / 2 - fm.stringWidth(watermark) / 2);
					int y = image.getWidth() / 2;
					g2.drawString(watermark, x, y);

					return image;
				} else {
					FileOutputStream fos = new FileOutputStream(outfile);
					//System.out.print("\nWriting DDS header... ");
					fos.write(ddsHeader, 0, 128);
					//System.out.print("DONE");
					//System.out.print("\nWriting texture data... ");
					fos.write(buffer, textureBeginIndex, textureData.length - 24);
					//System.out.print("DONE");
					//System.out.print("\n\nCONVERSION COMPLETED\n");
					fos.close();
					return null;
				}
			} else {
				System.out.print("\n\nFile contains no UI Texture! Process is stopping!");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] generateDDSHeaderWithUbulk(int beginIndex, byte[] buffer, boolean dxt1) {
		byte[] header = new byte[128];
		header[0] = 0x44;
		header[1] = 0x44;
		header[2] = 0x53;
		header[3] = 0x20;
		header[4] = 0x7C;
		header[8] = 0x07;
		header[9] = 0x10;
		header[10] = 0x08;

		header[16] = buffer[beginIndex + 40];
		header[17] = buffer[beginIndex + 41];
		header[18] = buffer[beginIndex + 42];
		header[19] = buffer[beginIndex + 43];
		header[12] = buffer[beginIndex + 44];
		header[13] = buffer[beginIndex + 45];
		header[14] = buffer[beginIndex + 46];
		header[15] = buffer[beginIndex + 47];

		header[68] = 0x4E;
		header[69] = 0x56;
		header[70] = 0x54;
		header[71] = 0x54;
		header[72] = 0x06;
		header[74] = 0x02;
		header[76] = 0x20;
		header[80] = 0x04;
		header[84] = 0x44;
		header[85] = 0x58;
		header[86] = 0x54;
		if (dxt1 == true) {
			header[87] = 0x31;
		} else {
			header[87] = 0x35;
		}
		header[109] = 0x10;
		return header;
	}

	private static byte[] generateDDSHeader(int beginIndex, byte[] buffer, boolean dxt1) {
		byte[] header = new byte[128];
		header[0] = 0x44;
		header[1] = 0x44;
		header[2] = 0x53;
		header[3] = 0x20;
		header[4] = 0x7C;
		header[8] = 0x07;
		header[9] = 0x10;
		header[10] = 0x08;

		header[16] = buffer[beginIndex - 16];
		header[17] = buffer[beginIndex - 15];
		header[18] = buffer[beginIndex - 14];
		header[19] = buffer[beginIndex - 13];
		header[12] = buffer[beginIndex - 12];
		header[13] = buffer[beginIndex - 11];
		header[14] = buffer[beginIndex - 10];
		header[15] = buffer[beginIndex - 9];

		header[68] = 0x4E;
		header[69] = 0x56;
		header[70] = 0x54;
		header[71] = 0x54;
		header[72] = 0x06;
		header[74] = 0x02;
		header[76] = 0x20;
		header[80] = 0x04;
		header[84] = 0x44;
		header[85] = 0x58;
		header[86] = 0x54;
		if (dxt1 == true) {
			header[87] = 0x31;
		} else {
			header[87] = 0x35;
		}
		header[109] = 0x10;
		return header;
	}

	private static int seekBegin(byte[] buffer) {
		for (int i = 0; i < buffer.length - 8; i++) {
			if ((buffer[i] & 0xFF) == 0x50 && (buffer[i + 1] & 0xFF) == 0x46 && (buffer[i + 2] & 0xFF) == 0x5F
					&& (buffer[i + 3] & 0xFF) == 0x44 && (buffer[i + 4] & 0xFF) == 0x58
					&& (buffer[i + 5] & 0xFF) == 0x54
					&& (((buffer[i + 6] & 0xFF) == 0x35) || ((buffer[i + 6] & 0xFF) == 0x31))) {
				return i;
			}
		}
		return -1;
	}

	static int readLength(byte[] buffer, int index) {
		return (buffer[index] & 0xFF) + 0x100 * (buffer[index + 1] & 0xFF) + 0x10000 * (buffer[index + 2] & 0xFF)
				+ 0x1000000 * (buffer[index + 3] & 0xFF);
	}

	public static BufferedImage putToOneImage(BufferedImage[] upcomingIcons, String[] upcomingIconTexts, int maxPerLine,
			int maxImageHeight, int maxImageWidth) {
		int textSpaceY = 60;
		int imageHeight = maxImageHeight;
		int imageWidth = maxImageWidth;
		int itemcount = upcomingIcons.length;
		int totalX = 0;
		int totalY = 0;
		int lines = 1;
		int lineItem = 1;
		if (itemcount % maxPerLine == 0) {
			lines = itemcount / maxPerLine;
		} else {
			lines = itemcount / maxPerLine + 1;
		}

		if (itemcount <= maxPerLine) {
			totalX = itemcount * imageWidth;
		} else {
			totalX = maxPerLine * imageWidth;
		}

		totalY = lines * imageHeight + lines * textSpaceY;
		BufferedImage out_Image = new BufferedImage(totalX, totalY, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = out_Image.createGraphics();
		BufferedImage image = null;
		int processed = 0;
		for (int l = 1; l <= lines; l++) {
			int processedThisLine = 0;
			for (int i = 1; i <= maxPerLine; i++) {
				if (processed >= itemcount) {
					break;
				}
				image = upcomingIcons[processed];
				g2.drawImage(image, processedThisLine * imageHeight, (l - 1) * imageWidth + (l - 1) * textSpaceY, null);
				g2.setColor(Color.LIGHT_GRAY);
				Font font;
				try {
					font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("font.ttf"));
					font = font.deriveFont(Font.BOLD, 50);
				} catch (FontFormatException | IOException e) {
					// TODO Auto-generated catch block
					font = new Font("SansSerif", Font.BOLD, 50);
					e.printStackTrace();
				}
				g2.setFont(font);
				FontMetrics fm = g2.getFontMetrics();
				int x = (processedThisLine * imageHeight + imageHeight / 2)
						- fm.stringWidth(upcomingIconTexts[processed]) / 2;
				int y = (l - 1) * imageWidth + imageWidth + (textSpaceY * (l - 1) + fm.getHeight() - 10);
				g2.drawString(upcomingIconTexts[processed], x, y);
				processedThisLine++;
				processed++;
			}
		}
		return out_Image;
	}
}
