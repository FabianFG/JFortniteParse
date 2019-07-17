/**
 * 
 */
package Converters;

/**
 * @author FunGames
 *
 */
public enum IconType {
	FEATURED_IMAGE, NORMAL_ICON, GENERATED_WITH_VARIANTS, GENERATED_NO_VARIANTS;

	public String getName() {
		switch (this) {
		case FEATURED_IMAGE:
			return "Featured Image";
		case NORMAL_ICON:
			return "Icon";
		case GENERATED_WITH_VARIANTS:
			return "Generated with variants";
		case GENERATED_NO_VARIANTS:
			return "Generated";
		default:
			return "";
		}
	}

}
