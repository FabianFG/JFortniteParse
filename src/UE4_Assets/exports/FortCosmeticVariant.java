/**
 * 
 */
package UE4_Assets.exports;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import UE4.deserialize.NewableWithUObject;
import UE4_Assets.FText;
import UE4_Assets.ImportMap;
import UE4_Localization.Locres;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FunGames
 *
 */
@Data
@NoArgsConstructor
public class FortCosmeticVariant implements NewableWithUObject {
	private FText variantChannelName;
	private String variantChannelTag;
	private List<CosmeticVariantContainer> variants;
	private UObject baseObject;

	@Override
	public void init(UObject uObject, Gson gson, ImportMap importMap) {
		JsonObject ob = gson.toJsonTree(uObject).getAsJsonObject();
		Optional.ofNullable(ob.get("VariantChannelTag")).ifPresent(e -> {
			variantChannelTag = e.getAsJsonObject().get("TagName").getAsString();
		});
		Optional.ofNullable(ob.get("VariantChannelName")).ifPresent(e -> {
			JsonObject textProps = e.getAsJsonObject();
			variantChannelName = new FText(textProps.get("namespace").getAsString(),
					textProps.get("key").getAsString(), textProps.get("text").getAsString());
		});

		String searchTag = null;
		switch (uObject.getExportType()) {
		case "FortCosmeticCharacterPartVariant":
			searchTag = "PartOptions";
			break;
		case "FortCosmeticMaterialVariant":
			searchTag = "MaterialOptions";
			break;
		case "FortCosmeticParticleVariant":
			searchTag = "ParticleOptions";
			break;
		default:
			searchTag = uObject.getExportType().replace("FortCosmetic", "").replace("Variant", "");
			break;
		}
		
		Optional.ofNullable(ob.get(searchTag)).ifPresent(e -> {
			JsonArray vars = e.getAsJsonArray();
			variants = new ArrayList<>();
			vars.forEach(e1 -> {
				JsonObject o = e1.getAsJsonObject();
				CosmeticVariantContainer v = new CosmeticVariantContainer();
				v.setBHideIfNotOwned(o.get("bHideIfNotOwned").getAsBoolean());
				v.setBIsDefault(o.get("bIsDefault").getAsBoolean());
				v.setBStartUnlocked(o.get("bStartUnlocked").getAsBoolean());
				v.setCustomizationVariantTag(o.get("CustomizationVariantTag").getAsJsonObject().get("TagName").getAsString());
				v.setPreviewImage(o.get("PreviewImage").getAsJsonObject().get("asset_path").getAsString());
				JsonObject nameProps = o.get("VariantName").getAsJsonObject();
				v.setVariantName(new FText(nameProps.get("namespace").getAsString(),
						nameProps.get("key").getAsString(), nameProps.get("text").getAsString()));
				variants.add(v);
			});
		});
	}
	/**
	 * @param locres
	 * @return
	 */
	public FortCosmeticVariant forLanguage(Optional<Locres> locres) {
		if (variantChannelName != null)
			this.variantChannelName = this.variantChannelName.cloneForLocres(locres);
		variants.forEach(varContainer -> {
			varContainer.setVariantName(varContainer.getVariantName().cloneForLocres(locres));
		});
		return this;
	}

}
