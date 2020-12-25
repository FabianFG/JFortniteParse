package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortCreativeTagsHelper;
import me.fungames.jfortniteparse.fort.objects.FortMultiSizeBrush;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortAmmoItemDefinition extends FortWorldItemDefinition {
    public FSlateBrush ClipIconBrush;
    public FScalableFloat bIsConsumed;
    public Boolean bTriggersFeedbackLines;
    public FScalableFloat RegenCooldown;
    public FortMultiSizeBrush AmmoIconBrush;
    public FSoftObjectPath HUDAmmoSmallPreviewImage;
    public FPackageIndex /*Class*/ WorldItemClassOverride;
    public FPackageIndex /*PlaylistUserOptions*/ ItemOptions;
    public FortCreativeTagsHelper CreativeTagsHelper;
}
