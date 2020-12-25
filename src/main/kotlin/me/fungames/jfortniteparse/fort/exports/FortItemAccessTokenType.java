package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EItemProfileType;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class FortItemAccessTokenType extends FortAccountItemDefinition {
    public EItemProfileType ProfileType;
    public FPackageIndex /*FortItemDefinition*/ access_item;
    public FText UnlockDescription;
}
