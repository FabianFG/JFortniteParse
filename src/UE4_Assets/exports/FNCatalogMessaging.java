/**
 * 
 */
package UE4_Assets.exports;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;

import UE4.deserialize.NewableWithUObject;
import UE4_Assets.FPropertyTagType;
import UE4_Assets.FPropertyTagType.MapProperty;
import UE4_Assets.FPropertyTagType.StrProperty;
import UE4_Assets.FPropertyTagType.TextProperty;
import UE4_Assets.ImportMap;
import UE4_Assets.UScriptMap;
import UE4_Localization.Locres;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
public class FNCatalogMessaging implements NewableWithUObject {

	private UObject baseObject;
	private UScriptMap messageMap;
	
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

	@Override
	public void init(UObject uObject, Gson gson, ImportMap importMap) {
		FPropertyTagType.MapProperty tag = (MapProperty) uObject.getPropertyByName("Banners");
		messageMap = tag.getMap();
	}
}
