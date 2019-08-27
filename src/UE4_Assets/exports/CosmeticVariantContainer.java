/**
 * 
 */
package UE4_Assets.exports;

import java.awt.image.BufferedImage;

import UE4_Assets.FText;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
public class CosmeticVariantContainer {
	private boolean bStartUnlocked;
	private boolean bIsDefault;
	private boolean bHideIfNotOwned;
	private String customizationVariantTag;
	private FText variantName;
	private String previewImage;
	
	private BufferedImage previewIcon;

}
