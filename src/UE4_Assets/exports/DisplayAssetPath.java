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
public class DisplayAssetPath implements NewableWithUObject {

	private UObject baseObject;
	private String detailsImage;
	private String tileImage;

	@Override
	public void init(UObject uObject, Gson gson, ImportMap importMap) {
		JsonObject ob = gson.toJsonTree(uObject).getAsJsonObject();
		Optional.ofNullable(ob.get("DetailsImage")).ifPresent(e -> {
			detailsImage = importMap.getPackageImportByName(e.getAsJsonObject().get("ResourceObject").getAsString());
		});
		Optional.ofNullable(ob.get("TileImage")).ifPresent(e -> {
			tileImage = importMap.getPackageImportByName(e.getAsJsonObject().get("ResourceObject").getAsString());
		});
	}
	
}
