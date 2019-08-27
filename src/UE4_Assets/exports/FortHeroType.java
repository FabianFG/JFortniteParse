/**
 * 
 */
package UE4_Assets.exports;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import UE4.deserialize.NewableWithUObject;
import UE4_Assets.ImportMap;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
public class FortHeroType implements NewableWithUObject {

	private UObject baseObject;
	private String iconName;

	@Override
	public void init(UObject uObject, Gson gson, ImportMap importMap) {
		JsonObject ob = (JsonObject) gson.toJsonTree(uObject);
		Optional.ofNullable(ob.get("LargePreviewImage")).ifPresent(e -> {
			iconName = e.getAsJsonObject().get("asset_path").getAsString();
		});
		//TODO has more properties, I just need the image for now
	}
	
}
