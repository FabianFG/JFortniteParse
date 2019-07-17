/**
 * 
 */
package UE4_Assets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import UE4.FArchive;
import UE4.PackageLogger;
import UE4_Localization.Locres;
import UE4_PakFile.GameFile;
import UE4_PakFile.PakFileReader;

/**
 * @author FunGames
 *
 */
public class Package {

	public final int uassetMagic = 0x9E2A83C1;

	private String name;
	private FPackageFileSummary info;
	private NameMap nameMap;
	private ImportMap importMap;
	private String packagePath;
	
	public final Gson gson;
	private JsonObject packageInfo;

	public String getPackagePath() {
		if (packagePath != null) {
			return packagePath;
		} else {
			return name;
		}

	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public FPackageFileSummary getInfo() {
		return info;
	}

	public NameMap getNameMap() {
		return nameMap;
	}

	public ImportMap getImportMap() {
		return importMap;
	}

	public ExportMap getExportMap() {
		return exportMap;
	}

	public List<Object> getExports() {
		return exports;
	}

	private ExportMap exportMap;
	private List<Object> exports;
	private List<UObject> uObjects;

	private byte[] uasset;
	private FArchive uassetAr;

	private byte[] uexp;
	private FArchive uexpAr;

	private byte[] ubulk;

	public static Package fromFiles(File uassetFile, File uexpFile, File ubulkFile) throws ReadException, IOException {
		if (uassetFile.exists()) {
			String name = uassetFile.getName().substring(0, uassetFile.getName().length() - 7);
			FileInputStream uassetIn = new FileInputStream(uassetFile);
			byte[] uasset = new byte[uassetIn.available()];
			uassetIn.read(uasset);
			uassetIn.close();
			FileInputStream uexpIn = new FileInputStream(uexpFile);
			byte[] uexp = new byte[uexpIn.available()];
			uexpIn.read(uexp);
			uexpIn.close();
			byte[] ubulk = null;
			if (ubulkFile != null) {
				FileInputStream ubulkIn = new FileInputStream(ubulkFile);
				ubulk = new byte[ubulkIn.available()];
				ubulkIn.read(ubulk);
				ubulkIn.close();
			}
			return new Package(name, uasset, uexp, ubulk);
		} else {
			throw new ReadException("Uasset File: doesn't exist");
		}
	}

	public Package(String name, byte[] uasset, byte[] uexp, byte[] ubulk, Optional<Locres> locres)
			throws ReadException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Package.class, new PackageSerializer());
		builder.registerTypeAdapter(NameMap.class, new PackageSerializer.NameMapSerializer());
		builder.registerTypeAdapter(ImportMap.class, new PackageSerializer.ImportMapSerializer());
		builder.registerTypeAdapter(ExportMap.class, new PackageSerializer.ExportMapSerializer());
		builder.registerTypeAdapter(UObject.class, new UObject.UObjectSerializer());
		gson = builder.create();
		this.uasset = uasset;
		this.uexp = uexp;
		this.ubulk = ubulk;
		this.name = name;

		// Load uasset
		this.uassetAr = new FArchive();
		this.uassetAr.data = uasset;

		this.uassetAr.SetStopper(uasset.length);
		this.uassetAr.Seek(0);

		this.info = new FPackageFileSummary(this.uassetAr);

		if (this.info.tag == this.uassetMagic) {
			// Read NameMap
			this.uassetAr.Seek(this.info.nameOffset);
			this.nameMap = new NameMap(this.uassetAr, this.info.nameCount);

			// Read ImportMap
			this.uassetAr.Seek(this.info.importOffset);
			this.importMap = new ImportMap(uassetAr, this.info.importCount, nameMap);

			// Read ExportMap
			this.uassetAr.Seek(this.info.exportOffset);
			this.exportMap = new ExportMap(this.uassetAr, this.info.exportCount, nameMap, importMap);
			this.exportMap.setUassetSize(info.totalHeaderSize);

			int assetLength = info.totalHeaderSize;
			exports = new ArrayList<>();
			uObjects = new ArrayList<>();

			int exportSize = 0;
			for (FObjectExport export : exportMap.getEntrys()) {
				exportSize += export.getSerialSize();
			}

			this.uexpAr = new FArchive();
			this.uexpAr.data = uexp;
			this.uexpAr.SetStopper(exportSize);
			this.uexpAr.Seek(0);
			this.uexpAr.uassetSize = assetLength;
			this.uexpAr.uexpSize = exportSize;
			this.uexpAr.locres = locres;

			if (ubulk != null) {
				FArchive ubulkAr = new FArchive();
				ubulkAr.data = ubulk;
				ubulkAr.SetStopper(ubulk.length);
				ubulkAr.Seek(0);
				uexpAr.addPayload("UBULK", ubulkAr);
			}

			AthenaItemDefinition previousItemDef = null;

			for (FObjectExport export : exportMap.getEntrys()) {
				String exportType = export.getClassIndex().getImportName();
				if (exportType.equals("0")) {
					exports.add(null);
					break;
				}
				int position = (int) (export.getSerialOffset() - uexpAr.getUexpOffset());
				this.uexpAr.Seek(position);
				switch (exportType) {
				case "Texture2D":
					UTexture2D t = new UTexture2D(this.uexpAr, nameMap, importMap);
					exports.add(t);
					uObjects.add(t.getBaseObject());
					break;
				case "SoundWave":
					USoundWave s = new USoundWave(this.uexpAr, nameMap, importMap);
					exports.add(s);
					uObjects.add(s.getBaseObject());
					break;
				case "FortMtxOfferData":
					DisplayAssetPath p = new DisplayAssetPath(this.uexpAr, nameMap, importMap);
					exports.add(p);
					uObjects.add(p.getBaseObject());
					break;
				case "FortHeroType":
					FortHeroType ht = new FortHeroType(this.uexpAr, nameMap, importMap);
					exports.add(ht);
					uObjects.add(ht.getBaseObject());
					break;
				case "DataTable":
					UDataTable d = new UDataTable(this.uexpAr, nameMap, importMap);
					exports.add(d);
					uObjects.add(d.getBaseObject());
					break;
				case "FortWeaponMeleeItemDefinition":
					FortWeaponMeleeItemDefinition fD = new FortWeaponMeleeItemDefinition(this.uexpAr, nameMap,
							importMap);
					exports.add(fD);
					uObjects.add(fD.getBaseObject());
					break;
				case "SkeletalMesh":
					USkeletalMesh4 sm = new USkeletalMesh4(this.uexpAr, nameMap, importMap);
					exports.add(sm);
					uObjects.add(sm.getSuperObject());
					break;
				case "CatalogMessaging":
					FNCatalogMessaging cm = new FNCatalogMessaging(this.uexpAr, nameMap, importMap);
					exports.add(cm);
					uObjects.add(cm.getBaseObject());
					break;
				default:
					if (exportType.startsWith("Athena") && exportType.endsWith("Definition")) {
						// Cannot check that in the switch
						AthenaItemDefinition item = new AthenaItemDefinition(this.uexpAr, nameMap, importMap,
								exportType);
						exports.add(item);
						uObjects.add(item.getBaseObject());
						previousItemDef = item;
					} else if (exportType.startsWith("FortCosmetic") && exportType.endsWith("Variant")) {
						// Cannot check that in the switch
						FortCosmeticVariant charVariant = new FortCosmeticVariant(this.uexpAr, nameMap, importMap,
								exportType);
						if (previousItemDef != null)
							previousItemDef.addVariants(charVariant);
						exports.add(charVariant);
						uObjects.add(charVariant.getBaseObject());
					} else {

						UObject baseObject = new UObject(this.uexpAr, nameMap, importMap, exportType);
						int validPos = (int) (position + export.getSerialSize());
						if (validPos != export.getSerialSize()) {
							// System.out.println(String.format(
							// "WARNING: Unknown export has unread data beside the uobject, Read bytes: %d
							// Export Length: %d",
							// this.uexpAr.Tell() - position, export.getSerialSize()));
						} else {
							// System.out.println("INFO: Unknown Export has no more data beside the
							// uobject");
						}
						// System.err.println("Skipped unknown export '" + exportType + "' at " +
						// export.getSerialOffset()
						// + " with length " + export.getSerialSize());
						exports.add(baseObject);
						uObjects.add(baseObject);
					}
				}
				int validPos = (int) (position + export.getSerialSize());
				if (this.uexpAr.Tell() != validPos) {
					// System.out.println(
					// String.format("Did not read '%s' correctly. Current Position: %d, Bytes
					// Remaining: %d",
					// exportType, this.uexpAr.Tell(), validPos - this.uexpAr.Tell()));
				} else {
					// System.out.println(String.format("Successfully read export '%s' at offset %d
					// with length %d",
					// exportType, export.getSerialOffset() - assetLength, export.getSerialSize()));
				}
			}
			PackageLogger.log.println("Successfully parsed package: " + name);

		} else {
			throw new ReadException("Given uasset file is not an uasset. Wrong package tag", 0);
		}
	}

	public <T> T getExportAs(Class<T> exportClass) throws ReadException {
		try {
			Optional<Object> ob = exports.stream().filter(o -> {
				return o.getClass().equals(exportClass);
			}).findFirst();
			@SuppressWarnings("unchecked")
			T res = (T) ob.get();
			return res;
		} catch (Exception e) {
			throw new ReadException("Package does not have any export with class " + exportClass.getSimpleName());
		}

	}

	public Package(String name, byte[] uasset, byte[] uexp, byte[] ubulk) throws ReadException {
		this(name, uasset, uexp, ubulk, Optional.empty());

	}

	public byte[] getUasset() {
		return uasset;
	}

	public byte[] getUexp() {
		return uexp;
	}

	public byte[] getUbulk() {
		return ubulk;
	}

	public String getName() {
		return name;
	}

	public List<UObject> getuObjects() {
		return uObjects;
	}
	
	public static class PackageSerializer implements JsonSerializer<Package> {

		@Override
		public JsonElement serialize(Package src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject ob = new JsonObject();
			
			ob.add("name_map", context.serialize(src.nameMap));
			ob.add("import_map", context.serialize(src.importMap));
			ob.add("export_map", context.serialize(src.exportMap));
			
			JsonArray exportProperties = new JsonArray();
			src.uObjects.forEach(uObject -> exportProperties.add(context.serialize(uObject)));
			ob.add("export_properties", exportProperties);
			
			return ob;
		}
		
		
		public static class NameMapSerializer implements JsonSerializer<NameMap> {

			@Override
			public JsonElement serialize(NameMap src, Type typeOfSrc, JsonSerializationContext context) {
				JsonArray nameMapData = new JsonArray();
				src.getEntrys().forEach(entry -> {
					nameMapData.add(entry.getName());
				});
				return nameMapData;
			}
			
		}
		
		public static class ImportMapSerializer implements JsonSerializer<ImportMap> {

			@Override
			public JsonElement serialize(ImportMap src, Type typeOfSrc, JsonSerializationContext context) {
				JsonArray importMapData = new JsonArray();
				src.getEntrys().forEach(entry -> {
					JsonObject importObject = new JsonObject();
					importObject.addProperty("class_name", entry.getClassName());
					importObject.addProperty("class_package", entry.getClassPackage());
					importObject.addProperty("object_name", entry.getObjectName());
					importMapData.add(importObject);
				});
				return importMapData;
			}
			
		}
		
		public static class ExportMapSerializer implements JsonSerializer<ExportMap> {

			@Override
			public JsonElement serialize(ExportMap src, Type typeOfSrc, JsonSerializationContext context) {
				JsonArray exportMapData = new JsonArray();
				src.getEntrys().forEach(entry -> {
					JsonObject exportObject = new JsonObject();
					exportObject.addProperty("export_type", entry.getClassIndex().getImportName());
					exportObject.addProperty("export_offset", entry.getSerialOffset() - src.getUassetSize());
					exportObject.addProperty("export_length", entry.getSerialSize());
					exportMapData.add(exportObject);
				});
				return exportMapData;
			}
			
		}
		
	}
	
	public JsonObject toJSON() {
		if(this.packageInfo == null)
			this.packageInfo = (JsonObject) this.gson.toJsonTree(this);
		return this.packageInfo;
	}

	public String toJSONString() {
		return this.gson.toJson(toJSON());

	}

	public static Package loadPackageByName(String gameFileName, Map<String, Integer> pakFileReaderIndices,
			PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles) throws ReadException {
		GameFile gameFile = gameFiles.get(gameFileName);
		if (gameFile != null) {
			if (gameFile.isUE4Package()) {
				PakFileReader pFR = loadedPaks[pakFileReaderIndices.get(gameFile.getPakFilePath())];
				if (pFR != null) {
					try {
						Future<byte[]> uasset = pFR.extractSelectedToBufferAsync(gameFile);
						Future<byte[]> uexp = null;
						if (gameFile.hasUexp()) {
							uexp = pFR.extractSelectedToBufferAsync(gameFile.getUexp());
						}
						Future<byte[]> ubulk = null;
						if (gameFile.hasUbulk()) {
							ubulk = pFR.extractSelectedToBufferAsync(gameFile.getUbulk());
						}

						byte[] uassetA = uasset.get();
						byte[] uexpA = null;
						if (gameFile.hasUexp()) {
							uexpA = uexp.get();
						}
						byte[] ubulkA = null;
						if (gameFile.hasUbulk()) {
							ubulkA = ubulk.get();
						}

						Package uePackage = new Package(gameFile.getNameWithoutExtension(), uassetA, uexpA, ubulkA);
						return uePackage;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						System.err.println("Failed to load Package: " + gameFile.getNameWithoutExtension());
						return null;
					}
				} else {
					// Unable to find PakFileReader
					return null;
				}
			} else {
				// Is no UE Package
				return null;
			}

		} else {
			return null;
		}
	}

	public static String toGameSpecificName(String name, String mountPrefix) {
		String res = name;
		if (res != null) {
			if (res.startsWith("/")) {
				res = res.substring(1);
			}
			if (res.startsWith("Game/")) {
				if (!mountPrefix.contains("Content/")) {
					res = res.replaceFirst("Game", mountPrefix + "Content");
				} else {
					res = res.replaceFirst("Game/", mountPrefix);
				}

			}
			if (!res.endsWith(".uasset")) {
				if (res.contains(".")) {
					String[] parts = res.split("\\.");
					if (parts.length == 2) {
						res = parts[0];
					}
				}
				res += ".uasset";
			}
			return res;
		} else {
			return null;
		}
	}

	public Object getExport(FObjectExport export) {
		int exportIndex = -1;
		for (int i = 0; i < exportMap.getEntrys().size(); i++) {
			if (export.equals(exportMap.get(i))) {
				exportIndex = i;
				break;
			}
		}
		if (exportIndex >= 0) {
			Object d = exports.get(exportIndex);
			return d;
		} else {
			return null;
		}
	}
}
