/**
 * 
 */
package Converters;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import UE4_Assets.AthenaItemDefinition;
import UE4_Assets.FText;
import UE4_Assets.FortCosmeticVariant;
import UE4_Assets.CosmeticVariantContainer;
import UE4_Localization.Locres;
import json.tools.JSONTools;
import util.StaticFiles;

/**
 * @author FunGames
 *
 */
public class ItemDefinitionContainer implements Cloneable {
	private BufferedImage icon;
	private Optional<BufferedImage> featuredIcon = Optional.empty();
	private AthenaItemDefinition itemDefinition;
	private Optional<FText> setNameFText = Optional.empty();
	private String setName;
	
	private List<BufferedImage> additionalIcons;
	
	public ItemDefinitionContainer withFeaturedIcon() {
		ItemDefinitionContainer c = this.clone();
		featuredIcon.ifPresent(fI -> c.setIcon(fI));
		return c;
	}
	
	@Override
	public ItemDefinitionContainer clone() {
		ItemDefinitionContainer c = new ItemDefinitionContainer();
		c.setIcon(getIcon());
		c.setFeaturedIcon(getFeaturedIcon());
		c.setItemDefinition(getItemDefinition());
		c.setSetNameFText(getSetNameFText());
		c.setSetName(getSetName());
		return c;
	}
	
	public ItemDefinitionContainer(JSONObject apiEntry) {
		AthenaItemDefinition fakeDef = new AthenaItemDefinition();
		fakeDef.setDisplayName(apiEntry.optString("displayName"));
		fakeDef.setDescription(apiEntry.optString("description"));
		fakeDef.setShortDescription(apiEntry.optString("type"));
		fakeDef.setRarity(apiEntry.optString("backendRarity", null));
		fakeDef.setDisplayName(apiEntry.optString("displayName"));
		fakeDef.setGameplayTags(JSONTools.jsonArrayToList(apiEntry.optJSONArray("gameplay_tags")));
		
		if(apiEntry.has("icon")) {
			try {
				this.icon = StaticFiles.loadStaticImage(new URL(apiEntry.getString("icon")));
			} catch (MalformedURLException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(apiEntry.has("featured_icon")) {
			try {
				this.featuredIcon = Optional.ofNullable(StaticFiles.loadStaticImage(new URL(apiEntry.getString("featured_icon"))));
			} catch (MalformedURLException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		List<FortCosmeticVariant> variants = new ArrayList<>();
		apiEntry.optJSONObject("variants").keys().forEachRemaining(name -> {
			JSONArray variantArray = apiEntry.optJSONObject("variants").getJSONArray(name);
			FortCosmeticVariant var = new FortCosmeticVariant();
			var.setVariantChannelName(new FText(name));
			var.setVariantChannelTag(name);
			List<CosmeticVariantContainer> containers = new ArrayList<>();
			variantArray.forEach(ob -> {
				JSONObject jsonVariant = (JSONObject) ob;
				CosmeticVariantContainer c = new CosmeticVariantContainer();
				c.setVariantName(new FText(jsonVariant.optString("name", null)));
				try {
					c.setPreviewIcon(StaticFiles.loadStaticImage(new URL(jsonVariant.getString("icon"))));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				containers.add(c);
			});
			var.setVariants(containers);
			variants.add(var);
		});
		fakeDef.setVariants(variants);
		fakeDef.setVariantsLoaded(variants.size() > 0);
		this.itemDefinition = fakeDef;
	}
	
	public ItemDefinitionContainer(BufferedImage icon, AthenaItemDefinition itemDefinition) {
		this(icon, itemDefinition, new HashMap<>());
	}
	public ItemDefinitionContainer(BufferedImage icon, AthenaItemDefinition itemDefinition, Map<String, FText> sets) {
		this.icon = icon;
		this.itemDefinition = itemDefinition;
		if(itemDefinition.getSetTag().isPresent()) {
			Map<String, FText> setMap = sets;
			setNameFText = Optional.ofNullable(setMap.get(itemDefinition.getSetTag().get()));
			setNameFText.ifPresent(text -> {
				setName = text.getString();
			});
		} else {
			setNameFText = Optional.empty();
		}

		this.additionalIcons = new ArrayList<>();
	}
	
	/**
	 * 
	 */
	public ItemDefinitionContainer() {
		// TODO Auto-generated constructor stub
	}

	public Optional<BufferedImage> getFeaturedIcon() {
		return featuredIcon;
	}

	public void setFeaturedIcon(Optional<BufferedImage> featuredIcon) {
		this.featuredIcon = featuredIcon;
	}

	public void setSetNameFText(Optional<FText> setNameFText) {
		this.setNameFText = setNameFText;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public Optional<FText> getSetNameFText() {
		return setNameFText;
	}
	public String getSetName() {
		return setName;
	}
	public ItemDefinitionContainer scaleIcon(int newX, int newY) {
		this.icon = ItemDefinitionToBufferedImage.scaleImage(icon, newX, newY);
		return this;
	}
	
	public ItemDefinitionContainer forLocres(Optional<Locres> language) {
		itemDefinition = itemDefinition.forLanguage(language);
		setNameFText.ifPresent(text -> {
			setName = text.forLocres(language);
		});
		return this;
	}
	
	public void addAdditionalIcon(BufferedImage addIcon) {
		this.additionalIcons.add(addIcon);
	}

	public List<BufferedImage> getAdditionalIcons() {
		return additionalIcons;
	}

	public void setAdditionalIcons(List<BufferedImage> additionalIcons) {
		this.additionalIcons = additionalIcons;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}

	public AthenaItemDefinition getItemDefinition() {
		return itemDefinition;
	}

	public void setItemDefinition(AthenaItemDefinition itemDefinition) {
		this.itemDefinition = itemDefinition;
	}
	
}
