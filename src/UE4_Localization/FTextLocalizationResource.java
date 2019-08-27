/**
 * 
 */
package UE4_Localization;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.FGUID;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class FTextLocalizationResource {
	private byte version;
	private List<LocaleNamespace> stringData;
	private long strArrayOffset;

	public static final FGUID LOCRES_MAGIC = new FGUID("1970541582-4228074087-2643465546-461322179");
	private static final int INDEX_NONE = -1;

	@SuppressWarnings("unused")
	public FTextLocalizationResource(FArchive Ar) throws ReadException, DeserializationException {
		FGUID magic = Ar.read(FGUID.class);

		if (!magic.getString().equals(LOCRES_MAGIC.getString())) {
			throw new ReadException("Wrong locres guid");
		}

		version = Ar.readUInt8();
		strArrayOffset = Ar.readInt64();
		if (strArrayOffset == INDEX_NONE) {
			throw new ReadException("No offset found");
		}

		// Only works for version: optimized
		int currentOffset = Ar.Tell();

		Ar.Seek((int) strArrayOffset);
		List<FTextLocalizationResourceString> localizedStrings = new ArrayList<>();
		long localizedStringCount = Ar.readUInt32();
		for (int i = 0; i < localizedStringCount; i++) {
			localizedStrings.add(Ar.read(FTextLocalizationResourceString.class));
		}

		Ar.Seek(currentOffset);

		long entryCount = Ar.readUInt32();
		long namespaceCount = Ar.readUInt32();
		this.stringData = new ArrayList<>();
		for (int i = 0; i < namespaceCount; i++) {
			FTextKey namespace = Ar.read(FTextKey.class);
			long keyCount = Ar.readUInt32();

			List<FEntry> strings = new ArrayList<>();
			
			for(int j = 0; j < keyCount; j++) {
				FTextKey textKey = Ar.read(FTextKey.class);
				long _sourceHash = Ar.readUInt32();
				int stringIndex = Ar.readInt32();
				if(stringIndex > 0 && stringIndex < localizedStrings.size()) {
					strings.add(new FEntry(textKey.getText(), localizedStrings.get(stringIndex).getData()));
				}
			}
			
			stringData.add(new LocaleNamespace(namespace.getText(), strings));
		}
	}
}
