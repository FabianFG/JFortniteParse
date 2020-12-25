package me.fungames.jfortniteparse.ue4.objects.slatecore.styling;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.core.math.FBox2D;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.slatecore.layout.FMargin;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

/**
 * An brush which contains information about how to draw a Slate element
 */
@UStruct
public class FSlateBrush {
    public FVector2D ImageSize;
    public FMargin Margin;
    public FSlateColor TintColor;
    public Lazy<UObject> ResourceObject;
    public FName ResourceName;
    public FBox2D UVRegion;
    public ESlateBrushDrawType DrawAs;
    public ESlateBrushTileType Tiling;
    public ESlateBrushMirrorType Mirroring;
    public ESlateBrushImageType ImageType;
    public Boolean bIsDynamicallyLoaded;
    public Boolean bHasUObject;

    /**
     * Enumerates ways in which an image can be drawn.
     */
    public enum ESlateBrushDrawType {
        /** Don't do anything */
        NoDrawType,

        /** Draw a 3x3 box, where the sides and the middle stretch based on the Margin */
        Box,

        /** Draw a 3x3 border where the sides tile and the middle is empty */
        Border,

        /** Draw an image; margin is ignored */
        Image
    }

    /**
     * Enumerates tiling options for image drawing.
     */
    public enum ESlateBrushTileType {
        /** Just stretch */
        NoTile,

        /** Tile the image horizontally */
        Horizontal,

        /** Tile the image vertically */
        Vertical,

        /** Tile in both directions */
        Both
    }

    /**
     * Possible options for mirroring the brush image
     */
    public enum ESlateBrushMirrorType {
        /** Don't mirror anything, just draw the texture as it is. */
        NoMirror,

        /** Mirror the image horizontally. */
        Horizontal,

        /** Mirror the image vertically. */
        Vertical,

        /** Mirror in both directions. */
        Both,

        ESlateBrushMirrorType_MAX
    }

    /**
     * Enumerates brush image types.
     */
    public enum ESlateBrushImageType {
        /** No image is loaded.  Color only brushes, transparent brushes etc. */
        NoImage,

        /** The image to be loaded is in full color. */
        FullColor,

        /** The image is a special texture in linear space (usually a rendering resource such as a lookup table). */
        Linear,
    }
}
