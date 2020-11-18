package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.fort.enums.EFortItemType;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;

@UStruct
public class ItemCategoryMappingData {
    public EFortItemType CategoryType;
    public FText CategoryName;
}
