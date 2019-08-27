/**
 * 
 */
package Converters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import UE4_Assets.FText;
import UE4_Assets.exports.AthenaItemDefinition;
import UE4_Localization.Locres;

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
