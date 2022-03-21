package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortPersistentResourceItemDefinition extends FortAccountItemDefinition {
    public FSoftObjectPath ItemPreviewActorClass;
    public boolean bIsEventItem = false;
    public String StatName;
    public String StatTotalName;
    public FText ExclusiveDesciption;
    public FSoftObjectPath ExclusiveIcon;
}
