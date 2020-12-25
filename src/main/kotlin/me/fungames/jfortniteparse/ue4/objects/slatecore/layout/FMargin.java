package me.fungames.jfortniteparse.ue4.objects.slatecore.layout;

import me.fungames.jfortniteparse.ue4.assets.UStruct;

/**
 * Describes the space around a Widget.
 */
@UStruct
public class FMargin {
    /** Holds the margin to the left. */
    public float Left;

    /** Holds the margin to the top. */
    public float Top;

    /** Holds the margin to the right. */
    public float Right;

    /** Holds the margin to the bottom. */
    public float Bottom;
}
