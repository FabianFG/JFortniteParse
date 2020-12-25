package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EAthenaCustomizationCategory;
import me.fungames.jfortniteparse.ue4.assets.UStruct;

import java.util.List;

public class FortCosmeticLockerItemDefinition extends FortProfileItemDefinition {
    public List<FortCosmeticLockerSlotInformation> LockerSlots;

    @UStruct
    public static class FortCosmeticLockerSlotInformation {
        public EAthenaCustomizationCategory CustomizationCategory;
        public Integer NumSlotsOfCategory;
        public Boolean bCanBeBlank;
        public Boolean bMustBeUniqueInArray;
    }
}
