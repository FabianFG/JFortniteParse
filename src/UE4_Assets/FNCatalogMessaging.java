/**
 * 
 */
package UE4_Assets;

import java.util.HashMap;
import java.util.Map;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.MapProperty;
import UE4_Assets.FPropertyTagType.StrProperty;
import UE4_Assets.FPropertyTagType.TextProperty;

/**
 * @author FunGames
 *
 */
public class FNCatalogMessaging {
	private Map<String, String> messages = new HashMap<>();
	public UObject getBaseObject() {
		return baseObject;
	}

	private UObject baseObject;
	
	public FNCatalogMessaging(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "CatalogMessaging");
		FPropertyTagType.MapProperty tag = (MapProperty) baseObject.getPropertyByName("Banners");
		UScriptMap messageMap = tag.getNumber();
		Map<FPropertyTagType, FPropertyTagType> mapData = messageMap.getMapData();
		for(FPropertyTagType t : mapData.keySet()) {
			String key = null;
			String value = null;
			if(t instanceof FPropertyTagType.StrProperty) {
				FPropertyTagType.StrProperty name = (StrProperty) t;
				key = name.getText();
				if(mapData.get(name) instanceof FPropertyTagType.TextProperty) {
					FPropertyTagType.TextProperty valueP = (TextProperty) mapData.get(name);
					value = valueP.getText().getSourceString();
				}
			}
			if(key != null && value != null) {
				messages.put(key, value);
			}
		}
	}
	
	public Map<String, String> getMessages() {
		return messages;
	}
}
