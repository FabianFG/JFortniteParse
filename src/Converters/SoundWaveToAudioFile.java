/**
 * 
 */
package Converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import UE4_Assets.FByteBulkData;
import UE4_Assets.FSoundFormatData;
import UE4_Assets.FStreamedAudioChunk;
import UE4_Assets.Package;
import UE4_Assets.ReadException;
import UE4_Assets.exports.USoundWave;

/**
 * @author FunGames
 *
 */
public class SoundWaveToAudioFile {

	private static String format;
	public static void main(String[] args) throws ReadException, IOException {
		File uasset = new File("D:\\Fabian\\Documents\\PakTest\\testFiles\\Emote_Music_Cheerleader.uasset");
		File uexp = new File("D:\\Fabian\\Documents\\PakTest\\testFiles\\Emote_Music_Cheerleader.uexp");
		File ubulk = null;
		Package uePackage = Package.fromFiles(uasset, uexp, ubulk);
		if (uePackage.getExports().size() > 0) {
			Object export = uePackage.getExports().get(0);
			if (export instanceof USoundWave) {
				USoundWave sound = (USoundWave) export;
				byte[] result = readSound(sound);
				File out = new File(uasset.getName().substring(0, uasset.getName().length() - 7) + "."
						+ format);
				FileOutputStream fos = new FileOutputStream(out);
				fos.write(result);
				fos.close();
			}
		}

	}
	
	public static File readSoundToFile(USoundWave sound) throws IOException {
		if (!sound.isBStreaming()) {
			//Not Streamed
			if (sound.isBCooked()) {
				//Cooked
				if (sound.getCompressedFormatData().size() > 0) {
					FSoundFormatData data = ((FSoundFormatData) sound.getCompressedFormatData().get(0));
					format = data.getFormatName();
					byte[] res = data.getData().getData();
					File temp = File.createTempFile("SoundWave", "." + format);
					temp.deleteOnExit();
					FileOutputStream fos = new FileOutputStream(temp);
					fos.write(res);
					fos.close();
					return temp;
				} else {
					System.err.println("No Sound Data is part of the cooked USoundWave");
					return null;
				}
			} else {
				//Uncooked
				if(sound.getRawData() != null) {
					FByteBulkData soundData = sound.getRawData();
					format = "ogg";
					byte[] res = soundData.getData();
					File temp = File.createTempFile("SoundWave", "." + format);
					temp.deleteOnExit();
					FileOutputStream fos = new FileOutputStream(temp);
					fos.write(res);
					fos.close();
					return temp;
				} else {
					System.err.println("No Sound Data is part of the uncooked USoundWave");
					return null;
				}
			}
		} else {
			//Streamed
			if(sound.getStreamedAudioChunks() !=null && sound.getFormat() != null) {
				format = sound.getFormat();
				List<Byte> bytes = new ArrayList<>();
				for(FStreamedAudioChunk chunk : sound.getStreamedAudioChunks()) {
					for(int i=0;i<chunk.getAudioDataSize(); i++) {
						bytes.add(chunk.getData().getData()[i]);
					}
				}
				byte[] res = listToArray(bytes);
				File temp = File.createTempFile("SoundWave", "." + format);
				temp.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(temp);
				fos.write(res);
				fos.close();
				return temp;
			} else {
				System.err.println("No Sound Data is part of the streamed USoundWave");
				return null;
			}
			
		}
	}

	public static byte[] readSound(USoundWave sound) throws ReadException {
		if (!sound.isBStreaming()) {
			//Not Streamed
			if (sound.isBCooked()) {
				//Cooked
				if (sound.getCompressedFormatData().size() > 0) {
					FSoundFormatData data = ((FSoundFormatData) sound.getCompressedFormatData().get(0));
					format = data.getFormatName();
					byte[] res = data.getData().getData();
					return res;
				} else {
					System.err.println("No Sound Data is part of the cooked USoundWave");
					return null;
				}
			} else {
				//Uncooked
				if(sound.getRawData() != null) {
					FByteBulkData soundData = sound.getRawData();
					format = "ogg";
					byte[] res = soundData.getData();
					return res;
				} else {
					System.err.println("No Sound Data is part of the uncooked USoundWave");
					return null;
				}
			}
		} else {
			//Streamed
			if(sound.getStreamedAudioChunks() !=null && sound.getFormat() != null) {
				format = sound.getFormat();
				List<Byte> bytes = new ArrayList<>();
				for(FStreamedAudioChunk chunk : sound.getStreamedAudioChunks()) {
					for(int i=0;i<chunk.getAudioDataSize(); i++) {
						bytes.add(chunk.getData().getData()[i]);
					}
				}
				byte[] res = listToArray(bytes);
				return res;
			} else {
				System.err.println("No Sound Data is part of the streamed USoundWave");
				return null;
			}
			
		}

	}

	private static byte[] listToArray(List<Byte> data) {
		byte[] res = new byte[data.size()];
		for(int i=0;i<data.size();i++) {
			res[i] = data.get(i);
		}
		return res;
	}
}
