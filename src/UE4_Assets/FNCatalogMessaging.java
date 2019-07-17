/**
 * 
 */
package UE4_Assets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.MapProperty;
import UE4_Assets.FPropertyTagType.StrProperty;
import UE4_Assets.FPropertyTagType.TextProperty;
import UE4_Localization.Locres;

/**
 * @author FunGames
 *
 */
public class FNCatalogMessaging {
	public UObject getBaseObject() {
		return baseObject;
	}

	private UObject baseObject;
	private UScriptMap messageMap;
	
	public FNCatalogMessaging(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "CatalogMessaging");
		FPropertyTagType.MapProperty tag = (MapProperty) baseObject.getPropertyByName("Banners");
		messageMap = tag.getNumber();
	}
	
	public Map<String, String> getMessagesForLocres(Optional<Locres> locres) {
		Map<String, String> messages = new HashMap<>();
		Map<FPropertyTagType, FPropertyTagType> mapData = messageMap.getMapData();
		for(FPropertyTagType t : mapData.keySet()) {
			String key = null;
			String value = null;
			if(t instanceof FPropertyTagType.StrProperty) {
				FPropertyTagType.StrProperty name = (StrProperty) t;
				key = name.getText();
				if(mapData.get(name) instanceof FPropertyTagType.TextProperty) {
					FPropertyTagType.TextProperty valueP = (TextProperty) mapData.get(name);
					value = valueP.getText().forLocres(locres);
				}
			}
			if(key != null && value != null) {
				messages.put(key, value);
			}
		}
		return messages;
	}
}
