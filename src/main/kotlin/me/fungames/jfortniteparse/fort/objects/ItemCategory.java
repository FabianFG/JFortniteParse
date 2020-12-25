package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

@UStruct
public class ItemCategory {
    public FGameplayTagContainer TagContainer;
    public FText CategoryName;
    public FortMultiSizeBrush CategoryBrush;
}
