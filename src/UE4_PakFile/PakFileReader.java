package UE4_PakFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;

import EncryptionHandler.Aes;
import UE4.FArchive;
import UE4_Assets.FPakArchive;
import UE4_Assets.ReadException;
import oodle.Oodle;

public class PakFileReader {
	private String pakFilePath;
	private List<GameFile> fileList = new ArrayList<>();
	//private Map<String, UE4Package> packages = new HashMap<String, UE4Package>();
	private File pakFile;
	private FPakArchive Ar;
	private long fileLength;
	private long indexOffset;
	private long indexLength;
	private int fileCount;
	private String key;
	private String outputPath = System.getProperty("user.dir") + "/Output/";
	
	public boolean isIndexChecksumValid() throws ReadException, NoSuchAlgorithmException {
		byte[] storedHash = getStoredIndexHash();
		Ar.Seek64(pakInfo.indexOffset);
		byte[] index = Ar.serialize((int) pakInfo.indexSize);
		byte[] calculatedHash = calculateIndexHash();
		return Arrays.equals(storedHash, calculatedHash);
	}
	public byte[] getStoredIndexHash() {
		return pakInfo.indexHash;
	}
	
	public static void main(String[] args) {
		
	}
	
	public byte[] calculateIndexHash() throws NoSuchAlgorithmException, ReadException {
		Ar.Seek64(pakInfo.indexOffset);
		byte[] index = Ar.serialize((int) pakInfo.indexSize);
		byte[] calculatedHash = SHA1.SHA1.SHAsumToByteArray(index);
		return calculatedHash;
	}

	public String getPakFilePath() {
		return pakFilePath;
	}

	public String getKey() {
		return key;
	}

	public List<GameFile> getFileList() {
		return fileList;
	}
	
	public String getFilename() {
		return pakFile.getName();
	}
	
	private String mountPrefix = "";
	private boolean bIndexEncrypted;
	public FPakInfo pakInfo;
	private int encryptedFileCount;

	public PakFileReader(String pakFilePath) throws IOException, ReadException {
		this.pakFilePath = pakFilePath;
		this.pakFile = new File(pakFilePath);
		this.Ar = new FPakArchive(new RandomAccessFile(pakFile, "r"));
		this.fileLength = Ar.GetStopper64();

		this.pakInfo = FPakInfo.readPakInfo(Ar);
		if (this.pakInfo == null) {
			throw new ReadException(String.format("File '%s' has an unknown format", pakFile.getName()), -1);
		}
		this.indexOffset = pakInfo.indexOffset;
		this.indexLength = pakInfo.indexSize;

		this.bIndexEncrypted = pakInfo.bEncryptedIndex;

		if (pakInfo.version > PAK.PakVersion.PAK_LATEST.v) {
			System.err.println(String.format("WARNING: Pak file \"%s\" has unsupported version %d",
					new File(pakFilePath).getName(), pakInfo.version));
		}
	}
	public PakFileReader(FPakArchive Ar, File pakFile) throws ReadException {
		this.pakFile = pakFile;
		this.pakFilePath = pakFile.getAbsolutePath();
		this.Ar = Ar;
		this.fileLength = Ar.GetStopper64();
		this.pakInfo = FPakInfo.readPakInfo(Ar);
		if (this.pakInfo == null) {
			throw new ReadException(String.format("File '%s' has an unknown format", pakFile.getName()), -1);
		}
		this.indexOffset = pakInfo.indexOffset;
		this.indexLength = pakInfo.indexSize;

		this.bIndexEncrypted = pakInfo.bEncryptedIndex;

		if (pakInfo.version > PAK.PakVersion.PAK_LATEST.v) {
			System.err.println(String.format("WARNING: Pak file \"%s\" has unsupported version %d",
					new File(pakFilePath).getName(), pakInfo.version));
		}
		
	}

	public FPakInfo getPakInfo() {
		return pakInfo;
	}

	// Public Methods

	public String getMountPrefix() {
		return mountPrefix;
	}

	public void setPakFilePath(String pakFilePath) {
		this.pakFilePath = pakFilePath;
	}

	public String extractSelected(GameFile gameFile) throws ReadException, IOException {
		File outFile = new File(outputPath + gameFile.getName());
		outFile.getParentFile().mkdirs();
		byte[] data = extractSelectedToBuffer(gameFile);
		FileOutputStream fos = new FileOutputStream(outFile);
		fos.write(data);
		fos.close();
		System.out.println("Successfully extracted to " + outFile.getAbsolutePath());
		return outFile.getAbsolutePath();
	}
	
	private class Extractor implements Callable<byte[]> {
		
		private GameFile gameFile;
		private boolean async;

		public Extractor(GameFile gameFile, boolean async) {
			this.gameFile = gameFile;
			this.async = async;
		}

		@Override
		public byte[] call() throws Exception {
			try {
				FPakArchive cAr = async ? Ar.clone() : Ar;
				cAr.Seek64(gameFile.getOffset());
				FPakEntry entry = new FPakEntry(cAr, pakInfo, false);
				GameFile tempFile = new GameFile(entry, null, 0, null);
				ByteArrayOutputStream fos = new ByteArrayOutputStream();
				if (gameFile.isEncrypted()) {
					//System.out.println("Selected File is encrypted. Attempting to decrypt with given key...");
					int fixedFileLength = gameFile.getLength();
					while (fixedFileLength % 16 != 0) {
						fixedFileLength++;
					}
					byte[] encryptedData = cAr.serialize(fixedFileLength);
					try {
						byte[] decryptedData = Aes.decryptToByteArray(encryptedData,
								DatatypeConverter.parseHexBinary(key.substring(2)));
						//System.out.println("Decryption finished successfully");
						decryptedData = Arrays.copyOfRange(decryptedData, 0, gameFile.getLength());
						fos.write(decryptedData);
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					byte[] data = cAr.serialize(gameFile.getLength());
					fos.write(data);
				}
				if (gameFile.isCompressed()) {

					if (gameFile.getCompressionMethod() == PAK.CompressionMethod.COMPRESS_OODLE.m) {

						// Compressed with oodle
						ByteArrayOutputStream fos2 = new ByteArrayOutputStream();
						int blockIndex = 0;
						System.out.println("Attempting to decompress " + gameFile.getCompressionBlocks().size()
								+ " oodle compressed block(s)");
						for (FPakCompressedBlock block : gameFile.getCompressionBlocks()) {
							cAr.Seek64(block.compressedStart);
							byte[] src = cAr.serialize((int) (block.compressedEnd - block.compressedStart));
							assert (src.length == gameFile.getLength());
							System.out.print("Block " + blockIndex + ": ");
							byte[] dst = null;
							if(gameFile.getCompressionBlocks().size() - 1 == blockIndex) {
								int decompressedSize = gameFile.getUncompressedlength() -(blockIndex) * gameFile.getCompressionBlockSize();
								dst = Oodle.oodleDecompress(src, decompressedSize);
							} else {
								 dst = Oodle.oodleDecompress(src, gameFile.getCompressionBlockSize());
							}
							

							if (dst != null) {
								fos2.write(dst);
							} else {
								throw new ReadException("Oodle Decompression failed, Expected Bytes: " + gameFile.getCompressionBlockSize(), -1);
							}
							blockIndex++;

						}
						byte[] resTemp = fos2.toByteArray();
						if (resTemp.length >= gameFile.getUncompressedlength()) {
							System.out.println("Successfully decompressed " + gameFile.getActualName());
							return Arrays.copyOfRange(resTemp, 0, gameFile.getUncompressedlength()); // Delete empty data to
																										// prevent parse
																										// errors
						} else {
							throw new ReadException("Oodle Decompression failed: Total decompressed bytes: "
									+ resTemp.length + ", must be atleast " + gameFile.getUncompressedlength() + " bytes",
									-1);
						}
					}

					else {
						throw new ReadException(
								"File is compressed with an unknown compression method: " + gameFile.getCompressionMethod(),
								-1);
					}
				}
				return fos.toByteArray();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	public Future<byte[]> extractSelectedToBufferAsync(GameFile gameFile) {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		Future<byte[]> result = ex.submit(new Extractor(gameFile, true));
		ex.shutdown();
		return result;
	}

	public byte[] extractSelectedToBuffer(GameFile gameFile) throws ReadException {
		try {
			return new Extractor(gameFile, true).call();
		} catch (Exception e) {
			throw new ReadException(e.getMessage());
		}
	}
	/*
	 * @Deprecated public void replaceSelected(GameFile gameFile, byte[] newData)
	 * throws UnsupportedOperationException, IOException, NoSuchAlgorithmException {
	 * 
	 * try { RandomAccessFile fileRW = new RandomAccessFile(this.pakFilePath, "rw");
	 * fileRW.seek(gameFile.getOffset() + PAK.ASSET_INDEX);
	 * 
	 * System.out.println("\n---Replacing " + gameFile.getActualName() + "---"); //
	 * Case 1: Length of new File is same as before if (newData.length ==
	 * gameFile.getLength()) {
	 * System.out.println("New File has the same length as the old file");
	 * System.out.print("Overwriting data..."); if (gameFile.isEncrypted()) {
	 * newData = bringByteArrayToMultipleOf16Bytes(newData); newData =
	 * Aes.encryptToByteArray(newData,
	 * DatatypeConverter.parseHexBinary(this.key.substring(2))); }
	 * fileRW.write(newData); // Write new data System.out.print("DONE\n");
	 * System.out.print("Fixing checksum in the file header...");
	 * fileRW.seek(gameFile.getOffset() + 28); // Fix checksum of the file in header
	 * fileRW.write(SHA1.SHAsumToByteArray(newData)); System.out.print("DONE\n");
	 * 
	 * // If index is encrypted decrypt it before overwriting data boolean decrypted
	 * = this.isEncrypted(); if (decrypted) {
	 * System.out.print("Decrypting index..."); fileRW.seek(indexOffset); byte[]
	 * index = new byte[(int) indexLength]; fileRW.read(index); index =
	 * Aes.decryptToByteArray(index,
	 * DatatypeConverter.parseHexBinary(this.key.substring(2)));
	 * fileRW.seek(indexOffset); fileRW.write(index); System.out.print("DONE\n"); }
	 * 
	 * System.out.print("Fixing checksum in the index...");
	 * fileRW.seek(gameFile.getEncryptionBooleanOffset() - 20); // Fix checksum
	 * ofthe file in the index fileRW.write(SHA1.SHAsumToByteArray(newData));
	 * System.out.print("DONE\n"); // Fix index checksum
	 * System.out.print("Fixing index checksum..."); fileRW.seek(this.fileLength -
	 * PAK.INDEX_CHECKSUM_OFFSET); fileRW.seek(indexOffset); byte[] index = new
	 * byte[(int) indexLength]; fileRW.read(index);
	 * fileRW.write(SHA1.SHAsumToByteArray(index)); System.out.print("DONE\n");
	 * 
	 * // If index got decrypted because of encrypt it again if (decrypted) {
	 * System.out.print("Reencrypting index..."); fileRW.seek(indexOffset);
	 * fileRW.read(index); index = Aes.encryptToByteArray(index,
	 * DatatypeConverter.parseHexBinary(this.key.substring(2)));
	 * fileRW.seek(indexOffset); fileRW.write(index); System.out.print("DONE\n"); }
	 * 
	 * System.out.println("\nSuccessfully replaced " + gameFile.getActualName()); }
	 * 
	 * // Case 2: Length of new File is shorter as before else if (newData.length <
	 * gameFile.getLength()) {
	 * 
	 * System.out.println("New File is shorter as the old file");
	 * System.out.print("Overwriting data..."); // Write new data
	 * fileRW.write(newData); System.out.print("DONE\n");
	 * System.out.print("Removing unused data..."); // File is shorter than
	 * original, remove unused data int difference = gameFile.getLength() -
	 * newData.length; fileRW.write(new byte[difference]);
	 * System.out.print("DONE\n"); // Fix file length and checksum in header
	 * System.out.print("Fixing file length and checksum in header...");
	 * fileRW.seek(gameFile.getOffset() + 8); byte[] newLength =
	 * longToLittleEndian(newData.length); fileRW.write(newLength);
	 * fileRW.write(newLength); fileRW.skipBytes(4); byte[] checksum =
	 * SHA1.SHAsumToByteArray(newData); fileRW.write(checksum);
	 * System.out.print("DONE\n");
	 * 
	 * // Fix index entry System.out.print("Fixing index entry..."); long
	 * cIndexOffset = indexOffset + (gameFile.getIndexOffset() - indexOffset);
	 * fileRW.seek(indexOffset); byte[] index = new byte[(int) indexLength];
	 * fileRW.read(index); cIndexOffset += 4; String assetName = readString(index,
	 * (int) cIndexOffset); cIndexOffset += assetName.length() + 1; cIndexOffset +=
	 * 8; for (long i = 0; i < newLength.length; i++) { index[(int) (cIndexOffset +
	 * i)] = newLength[(int) i]; } cIndexOffset += 8; for (long i = 0; i <
	 * newLength.length; i++) { index[(int) (cIndexOffset + i)] = newLength[(int)
	 * i]; } cIndexOffset += 8; cIndexOffset += 4; // Empty Data before Checksum for
	 * (long i = 0; i < 20; i++) { index[(int) (cIndexOffset + i)] = checksum[(int)
	 * i]; } cIndexOffset += 20; cIndexOffset += 1; // Encryption Boolean
	 * cIndexOffset += 4; // Empty Data fileRW.seek(indexOffset);
	 * fileRW.write(index); System.out.print("DONE\n");
	 * 
	 * // Fix index checksum System.out.print("Fixing index checksum...");
	 * fileRW.seek(this.fileLength - PAK.INDEX_CHECKSUM_OFFSET);
	 * 
	 * fileRW.write(SHA1.SHAsumToByteArray(index)); System.out.print("DONE\n");
	 * 
	 * System.out.println("\nSuccessfully replaced " + gameFile.getActualName());
	 * 
	 * }
	 * 
	 * // Case 3: Length of new File is longer as before else if (newData.length >
	 * gameFile.getLength()) { int difference = Math.subtractExact(newData.length,
	 * gameFile.getLength());
	 * System.out.println("New file is longer as the old file");
	 * System.out.print("Inserting " + difference + " bytes to the file..."); // //
	 * insert byte difference at end of the asset
	 * FileUtil.insertBytes(this.pakFilePath, gameFile.getOffset() + PAK.ASSET_INDEX
	 * + gameFile.getLength(), new byte[difference]);
	 * 
	 * System.out.print("DONE\n"); // fix file header
	 * 
	 * System.out.print("Fixing file header..."); fileRW.seek(gameFile.getOffset() +
	 * 8); fileRW.write(longToLittleEndian(newData.length));
	 * fileRW.write(longToLittleEndian(newData.length)); fileRW.skipBytes(4);
	 * fileRW.write(SHA1.SHAsumToByteArray(newData)); System.out.print("DONE\n");
	 * 
	 * // write new data System.out.print("Overwriting data...");
	 * fileRW.seek(gameFile.getOffset() + PAK.ASSET_INDEX); fileRW.write(newData);
	 * System.out.print("DONE\n");
	 * 
	 * // fix offsets for all files after the changed file in the file header
	 * because // offsets changed
	 * System.out.print("Fixing offsets of all files in their headers..."); for (int
	 * i = gameFile.getIndexCount() + 1; i < this.fileCount; i++) {
	 * fileRW.seek(this.fileList.get(i).getOffset() + difference);
	 * fileRW.write(longToLittleEndian(this.fileList.get(i).getOffset() +
	 * difference));
	 * 
	 * } System.out.print("DONE\n"); // fix offsets for all files after the changed
	 * // file in the index because offsets changed
	 * System.out.print("Fixing offsets of all files in the index..."); for (int i =
	 * gameFile.getIndexCount() + 1; i < this.fileCount; i++) {
	 * fileRW.seek(this.fileList.get(i).getEncryptionBooleanOffset() - 48 +
	 * difference); fileRW.write(longToLittleEndian(this.fileList.get(i).getOffset()
	 * + difference));
	 * 
	 * } System.out.print("DONE\n");
	 * 
	 * // fix file length and checksum in index
	 * System.out.print("Fixing index entry...");
	 * fileRW.seek(gameFile.getEncryptionBooleanOffset() + difference - 40);
	 * 
	 * fileRW.write(longToLittleEndian(newData.length));
	 * fileRW.write(longToLittleEndian(newData.length));
	 * fileRW.seek(gameFile.getEncryptionBooleanOffset() + difference - 20);
	 * fileRW.write(SHA1.SHAsumToByteArray(newData)); System.out.print("DONE\n");
	 * 
	 * // fix index offset System.out.print("Fixing index offset...");
	 * fileRW.seek(this.fileLength + difference - 0x24);
	 * fileRW.write(longToLittleEndian(this.indexOffset + difference));
	 * System.out.print("DONE\n");
	 * 
	 * // fix index checksum System.out.print("Fixing index checksum...");
	 * fileRW.seek(indexOffset + difference); byte[] index = new byte[(int)
	 * indexLength]; fileRW.read(index); fileRW.seek(this.fileLength + difference -
	 * PAK.INDEX_CHECKSUM_OFFSET); fileRW.write(SHA1.SHAsumToByteArray(index));
	 * System.out.print("DONE\n");
	 * 
	 * System.out.println("\nSuccessfully replaced " + gameFile.getActualName());
	 * 
	 * } fileRW.close();
	 * 
	 * } catch (FileNotFoundException e) { // TODO Auto-generated catch block throw
	 * new UnsupportedOperationException("Pak File not accessable for writing"); }
	 * catch (InvalidKeyException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (NoSuchPaddingException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch
	 * (IllegalBlockSizeException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (BadPaddingException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } catch (InvalidAlgorithmParameterException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (KeyNotSetException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * @Deprecated public void encryptPak(int[] PAKCode, String newKey, String
	 * outFilePath) throws Exception { File outFileF = new File(outFilePath); new
	 * File(outFileF.getParent()).mkdirs(); System.out.print("Creating copy of " +
	 * this.pakFile.getName() + "..."); FileUtils.copyFile(this.pakFile, outFileF);
	 * System.out.print("DONE\n"); RandomAccessFile fileRW = new
	 * RandomAccessFile(outFileF, "rw"); if (!bIndexEncrypted) { if (PAKCode[1] ==
	 * PAK.ASSETS) { for (GameFile c : fileList) { if
	 * (!c.getActualName().endsWith(PAK.INI)) { if (!c.isEncrypted()) { String name
	 * = c.getActualName(); System.out.print("Encrypting " + name + "..."); long
	 * offset = c.getOffset(); int length = c.getLength(); int fixedLength = length;
	 * while (fixedLength % 16 != 0) { // Bring the length to a multiple of 16
	 * fixedLength++; } fileRW.seek(offset + PAK.ASSET_INDEX); byte[]
	 * fixedAssetBuffer = new byte[fixedLength]; fileRW.read(fixedAssetBuffer); try
	 * { byte[] encryptedAsset = Aes.encryptToByteArray(fixedAssetBuffer,
	 * DatatypeConverter.parseHexBinary(newKey.substring(2))); fileRW.seek(offset +
	 * PAK.ASSET_INDEX); fileRW.write(encryptedAsset);
	 * fileRW.seek(c.getEncryptionBooleanOffset()); fileRW.writeByte(0x01);
	 * System.out.print("DONE\n"); } catch (InvalidKeyException |
	 * NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
	 * | BadPaddingException | InvalidAlgorithmParameterException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); new ErrorDialog(shell,
	 * SWT.TITLE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL) .open(e.getMessage()); }
	 * } } } } if (PAKCode[2] == PAK.INIS) { for (GameFile c : fileList) { if
	 * (c.getActualName().endsWith(PAK.INI)) { if (!c.isEncrypted()) { String name =
	 * c.getActualName(); System.out.print("Encrypting " + name + "..."); long
	 * offset = c.getOffset(); int length = c.getLength(); int fixedLength = length;
	 * while (fixedLength % 16 != 0) { // Bring the length to a multiple of 16
	 * fixedLength++; } fileRW.seek(offset + PAK.ASSET_INDEX); byte[]
	 * fixedAssetBuffer = new byte[fixedLength]; fileRW.read(fixedAssetBuffer); try
	 * { byte[] encryptedAsset = Aes.encryptToByteArray(fixedAssetBuffer,
	 * DatatypeConverter.parseHexBinary(newKey.substring(2))); fileRW.seek(offset +
	 * PAK.ASSET_INDEX); fileRW.write(encryptedAsset);
	 * fileRW.seek(c.getEncryptionBooleanOffset()); fileRW.writeByte(0x01);
	 * System.out.print("DONE\n"); } catch (InvalidKeyException |
	 * NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
	 * | BadPaddingException | InvalidAlgorithmParameterException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); new ErrorDialog(shell,
	 * SWT.TITLE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL) .open(e.getMessage()); }
	 * } } } }
	 * 
	 * if (PAKCode[0] == PAK.INDEX) { // Read Index and decrypt
	 * System.out.print("Encrypting index..."); byte[] encryptedindexArray = new
	 * byte[(int) indexLength]; byte[] index = new byte[(int) indexLength];
	 * fileRW.seek(indexOffset); fileRW.read(index); try { encryptedindexArray =
	 * Aes.encryptToByteArray(index,
	 * DatatypeConverter.parseHexBinary(newKey.substring(2))); } catch
	 * (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException |
	 * IllegalBlockSizeException | BadPaddingException |
	 * InvalidAlgorithmParameterException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); new ErrorDialog(shell, SWT.TITLE | SWT.DIALOG_TRIM |
	 * SWT.APPLICATION_MODAL).open(e.getMessage()); } fileRW.seek(indexOffset);
	 * fileRW.write(encryptedindexArray); System.out.print("DONE\n");
	 * 
	 * System.out.print("Setting index encryption boolean to 'true'...");
	 * fileRW.seek(this.fileLength - PAK.INDEX_ENCRYPTION_BOOLEAN_OFFSET);
	 * fileRW.writeByte(0x01);
	 * 
	 * for (GameFile c : fileList) { if (c.isEncrypted()) {
	 * fileRW.seek(c.getEncryptionBooleanOffset()); fileRW.writeByte(0x00); } }
	 * System.out.print("DONE\n"); System.out.print("Fixing index checksum...");
	 * byte[] fixedIndex = new byte[(int) indexLength]; fileRW.seek(indexOffset);
	 * fileRW.read(fixedIndex); fileRW.seek(this.fileLength -
	 * PAK.INDEX_CHECKSUM_OFFSET); byte[] checksum =
	 * SHA1.SHAsumToByteArray(fixedIndex); fileRW.write(checksum);
	 * System.out.print("DONE\n");
	 * 
	 * }
	 * 
	 * } else { throw new
	 * UnsupportedOperationException("Pak needs to be decrypted before encrypting it"
	 * ); } fileRW.close(); }
	 * 
	 * @Deprecated public void decryptPak(String outFilePath) throws IOException,
	 * ReadException { File outFileF = new File(outFilePath); new
	 * File(outFileF.getParent()).mkdirs(); System.out.print("Creating copy of " +
	 * this.pakFile.getName() + "..."); FileUtils.copyFile(this.pakFile, outFileF);
	 * System.out.print("DONE\n"); RandomAccessFile fileRW = new
	 * RandomAccessFile(outFileF, "rw"); if (bIndexEncrypted) { // Read Index and
	 * decrypt System.out.print("Decrypting index..."); byte[] indexArray = new
	 * byte[(int) indexLength];
	 * 
	 * if (this.key != null) { try { if (testKey(this.key)) { byte[] encryptedIndex
	 * = new byte[(int) indexLength]; fileRW.seek(indexOffset);
	 * fileRW.read(encryptedIndex); indexArray =
	 * Aes.decryptToByteArray(encryptedIndex,
	 * DatatypeConverter.parseHexBinary(key.substring(2)));
	 * fileRW.seek(indexOffset); fileRW.write(indexArray);
	 * System.out.print("DONE\n");
	 * 
	 * System.out.print("Setting encryption booleans to 'false'...");
	 * fileRW.seek(this.fileLength - PAK.INDEX_ENCRYPTION_BOOLEAN_OFFSET);
	 * fileRW.writeByte(0x00); for (GameFile c : fileList) { if (c.isEncrypted()) {
	 * fileRW.seek(c.getEncryptionBooleanOffset()); fileRW.writeByte(0x00); } }
	 * System.out.print("DONE\n"); System.out.print("Fixing index checksum...");
	 * byte[] fixedIndex = new byte[(int) indexLength]; fileRW.seek(indexOffset);
	 * fileRW.read(fixedIndex); fileRW.seek(this.fileLength -
	 * PAK.INDEX_CHECKSUM_OFFSET); byte[] checksum =
	 * SHA1.SHAsumToByteArray(fixedIndex); fileRW.write(checksum);
	 * System.out.print("DONE\n");
	 * 
	 * } else { throw new InvalidKeyException(); } } catch (InvalidKeyException |
	 * NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
	 * | BadPaddingException | InvalidAlgorithmParameterException | IOException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); new
	 * ErrorDialog(shell, SWT.TITLE | SWT.DIALOG_TRIM |
	 * SWT.APPLICATION_MODAL).open(e.getMessage()); } } else { throw new
	 * NullPointerException(); }
	 * 
	 * for (GameFile c : fileList) { if (c.isEncrypted()) { String name =
	 * c.getActualName(); System.out.print("Decrypting " + name + "..."); long
	 * offset = c.getOffset(); int length = c.getLength(); int fixedLength = length;
	 * while (fixedLength % 16 != 0) { // Bring the length to a multiple of 16
	 * fixedLength++; } fileRW.seek(offset + PAK.ASSET_INDEX); byte[]
	 * fixedAssetBuffer = new byte[fixedLength]; fileRW.read(fixedAssetBuffer); try
	 * { byte[] decryptedAsset = Aes.decryptToByteArray(fixedAssetBuffer,
	 * DatatypeConverter.parseHexBinary(key.substring(2))); fileRW.seek(offset +
	 * PAK.ASSET_INDEX); fileRW.write(decryptedAsset); System.out.print("DONE\n"); }
	 * catch (InvalidKeyException | NoSuchAlgorithmException |
	 * NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
	 * InvalidAlgorithmParameterException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); new ErrorDialog(shell, SWT.TITLE | SWT.DIALOG_TRIM |
	 * SWT.APPLICATION_MODAL) .open(e.getMessage()); } } }
	 * 
	 * } System.out.println("\nFinished decrypting pak"); fileRW.close(); }
	 */

	public boolean isEncrypted() {
		return bIndexEncrypted;
	}

	public boolean testKey(String key) throws IOException, ReadException {
		Ar.Seek64(indexOffset);
		byte[] keyTestBytes = Ar.serialize(128);
		byte[] keyBytes = DatatypeConverter.parseHexBinary(key.substring(2)); // Substring because key has 0x prefix
		try {
			byte[] decrypted = Aes.decryptToByteArray(keyTestBytes, keyBytes);
			FArchive testAr = new FArchive(decrypted);
			int stringLength = testAr.readInt32();
			if (stringLength > 512 || stringLength < -512) {
				return false;
			}

			if (stringLength < 0) {
				int position = 4 - (stringLength - 1) * 2;
				testAr.Seek(position);
				int c = testAr.readUInt16();
				return c == 0 ? true : false;
			}
			if (stringLength > 0) {
				int position = 4 + stringLength - 1;
				byte c = decrypted[position];
				return c == 0x00 ? true : false;
			}

		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<GameFile> readIndex() {
		try {
			// Prepare index and decrypt if necessary

			byte[] indexArray = new byte[(int) indexLength];
			if (bIndexEncrypted) {
				if (this.key != null) {
					if (testKey(this.key)) {
						Ar.Seek64(indexOffset);
						byte[] encryptedIndex = Ar.serialize((int) indexLength);
						indexArray = Aes.decryptToByteArray(encryptedIndex,
								DatatypeConverter.parseHexBinary(key.substring(2)));

					} else {
						throw new InvalidKeyException();
					}
				} else {
					throw new NullPointerException();
				}
			} else {
				Ar.Seek64(indexOffset);
				indexArray = Ar.serialize((int) indexLength);

			}

			FArchive indexAr = new FArchive(indexArray);

			// Read index
			String mountPoint = indexAr.readString();
			this.mountPrefix = mountPoint;
			boolean badMountPoint = false;
			if (!mountPrefix.startsWith("../../..")) {
				badMountPoint = true;
			} else {
				mountPrefix = mountPrefix.replaceFirst("../../..", "");
			}
			if (!mountPrefix.substring(0, 1).equals("/")
					|| ((mountPrefix.length() > 1) && (mountPrefix.substring(1, 2).equals(".")))) {
				badMountPoint = true;
			}
			if (badMountPoint) {
				System.err.println(String.format("WARNING: Pak \"%s\" has strange mount point \"%s\", mounting to root",
						new File(pakFilePath).getName(), mountPrefix));
				mountPrefix = "/";
			}
			mountPrefix = mountPrefix.substring(1); // Don't need the first '/'

			this.fileCount = indexAr.readInt32();
			this.encryptedFileCount = 0;

			// Begin of files
			Map<String, GameFile> tempFileList = new HashMap<>();
			for (int indexCount = 0; indexCount < fileCount; indexCount++) {
				long cIndexOffset = indexOffset;
				FPakEntry entry = new FPakEntry(indexAr, this.pakInfo, true);

				long encryptionBoolOffset = this.indexOffset + indexOffset;
				GameFile c = new GameFile(entry, this.mountPrefix, indexCount, this.pakFilePath);
				c.setEncryptionBooleanOffset(encryptionBoolOffset);
				c.setIndexOffset(cIndexOffset);
				if (c.isEncrypted()) {
					this.encryptedFileCount++;
				}
				tempFileList.put(c.getName(), c);
				indexOffset += entry.binaryLength;

			}
			
			this.fileList = new ArrayList<>();
			for (GameFile file : tempFileList.values()) {
				String assetName = file.getName();
				if(file.isUE4Package()) {
					GameFile uexp = tempFileList.get(assetName.substring(0, assetName.length() - ".uasset".length()) + ".uexp");
					if(uexp != null) {
						file.addUexp(uexp);
					}
					GameFile ubulk = tempFileList.get(assetName.substring(0, assetName.length() - ".uasset".length()) + ".ubulk");
					if(ubulk != null) {
						file.addUbulk(ubulk);
					}
					this.fileList.add(file);
				}
				else {
					if(!assetName.endsWith(".uexp") && !assetName.endsWith(".ubulk")) {
						this.fileList.add(file);
					}
				}
				
			}
			System.out.println(String.format("Pak %s: %d files (%d encrypted), mount point: \"%s\", version %d",
					new File(this.pakFilePath).getName(), this.fileCount, this.encryptedFileCount, this.mountPrefix,
					this.pakInfo.version));

		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileList;
	}

	// Private Methods

	public static long readToLong(byte[] buffer, int index) {
		byte[] lengthBytes = Arrays.copyOfRange(buffer, index, index + 8);
		long length = batol(lengthBytes, true);
		return length;

	}

	private int readToInt(byte[] buffer, int index) {
		byte[] lengthBytes = Arrays.copyOfRange(buffer, index, index + 4);
		int length = batol4(lengthBytes, true);
		return length;

	}

	private String readString(byte[] buffer, int index) {
		String res = "";
		for (int i = index; i < buffer.length; i++) {
			if (buffer[i] != 0x00) {
				res += (char) buffer[i];
			} else {
				break;
			}
		}
		return res;

	}

	private static long batol(byte[] buff, boolean littleEndian) {
		assert (buff.length == 8);
		ByteBuffer bb = ByteBuffer.wrap(buff);
		if (littleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong();
	}

	private static int batol4(byte[] buff, boolean littleEndian) {
		assert (buff.length == 4);
		ByteBuffer bb = ByteBuffer.wrap(buff);
		if (littleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private static byte[] longToLittleEndian(long numero) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putLong(numero);
		return bb.array();
	}

	private static byte[] intToLittleEndian(int numero) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(numero);
		return bb.array();
	}

	private byte[] fillByteArrayToLength(byte[] oldData, int newLength) {
		byte[] result = new byte[newLength];
		for (int i = 0; i < oldData.length; i++) {
			result[i] = oldData[i];
		}
		int index = 0;
		for (int i = oldData.length; i < newLength; i++) {
			result[i] = PAK.FIX_BYTES[index];
			index++;
		}
		return result;
	}

	private void insert(String filename, long offset, byte[] content) throws IOException {

		RandomAccessFile r = new RandomAccessFile(filename, "rw");
		RandomAccessFile rtemp = new RandomAccessFile(filename + "Temp", "rw");
		long fileSize = r.length();
		FileChannel sourceChannel = r.getChannel();
		FileChannel targetChannel = rtemp.getChannel();
		sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
		sourceChannel.truncate(offset);
		r.seek(offset);
		r.write(content);
		long newOffset = r.getFilePointer();
		targetChannel.position(0L);
		sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
		sourceChannel.close();
		targetChannel.close();
		rtemp.close();
		r.close();
		FileUtils.forceDelete(new File(filename + "Temp"));
	}

	private byte[] bringByteArrayToMultipleOf16Bytes(byte[] oldData) {
		int fLength = oldData.length;
		while (fLength % 16 != 0) {
			fLength++;
		}
		return fillByteArrayToLength(oldData, fLength);
	}
}
