/**
 * 
 */
package UE4_Assets;

import java.awt.image.BufferedImage;

/**
 * @author FunGames
 *
 */
public class CosmeticVariantContainer {
	private boolean bStartUnlocked;
	private boolean bIsDefault;
	private boolean bHideIfNotOwned;
	private String customizationVariantTag;
	private FText variantName;
	private String previewImage;
	
	private BufferedImage previewIcon;
	public boolean isbStartUnlocked() {
		return bStartUnlocked;
	}
	public void setbStartUnlocked(boolean bStartUnlocked) {
		this.bStartUnlocked = bStartUnlocked;
	}
	public boolean isbIsDefault() {
		return bIsDefault;
	}
	public void setbIsDefault(boolean bIsDefault) {
		this.bIsDefault = bIsDefault;
	}
	public boolean isbHideIfNotOwned() {
		return bHideIfNotOwned;
	}
	public void setbHideIfNotOwned(boolean bHideIfNotOwned) {
		this.bHideIfNotOwned = bHideIfNotOwned;
	}
	public String getCustomizationVariantTag() {
		return customizationVariantTag;
	}
	public void setCustomizationVariantTag(String customizationVariantTag) {
		this.customizationVariantTag = customizationVariantTag;
	}
	public FText getVariantName() {
		return variantName;
	}
	public void setVariantName(FText variantName) {
		this.variantName = variantName;
	}
	public String getPreviewImage() {
		return previewImage;
	}
	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}
	public BufferedImage getPreviewIcon() {
		return previewIcon;
	}
	public void setPreviewIcon(BufferedImage previewIcon) {
		this.previewIcon = previewIcon;
	}
}
