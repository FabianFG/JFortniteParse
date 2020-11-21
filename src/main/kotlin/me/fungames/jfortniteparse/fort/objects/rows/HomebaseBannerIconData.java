package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

/**
 * @deprecated in v14.20, this was replaced with individual items
 */
@Deprecated
public class HomebaseBannerIconData extends FTableRowBase {
    public FSoftObjectPath SmallImage;
    public FSoftObjectPath LargeImage;
    public FName CategoryRowName;
    public FText DisplayName;
    public FText DisplayDescription;
    public boolean bFullUsageRights;
}
