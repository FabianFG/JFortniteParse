package me.fungames.jfortniteparse.ue4.objects.slatecore.styling;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;

/**
 * A Slate color can be a directly specified value, or the color can be pulled from a WidgetStyle.
 */
@UStruct
public class FSlateColor {
    /** The current specified color; only meaningful when ColorToUse == UseColor_Specified. */
    public FLinearColor SpecifiedColor;

    /** The rule for which color to pick. */
    public ESlateColorStylingMode ColorUseRule;

    /**
     * Enumerates types of color values that can be held by Slate color.
     *
     * Should we use the specified color? If not, then which color from the style should we use.
     */
    public enum ESlateColorStylingMode {
        /** Color value is stored in this Slate color. */
        UseColor_Specified,

        /** Color value is stored in the linked color. */
        UseColor_Specified_Link,

        /** Use the widget's foreground color. */
        UseColor_Foreground,

        /** Use the widget's subdued color. */
        UseColor_Foreground_Subdued
    }
}
