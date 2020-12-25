package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

import java.util.List;

public class AlterationSlotsLoadout extends FTableRowBase {
    public List<AlterationSlot> AlterationSlots;

    @UStruct
    public static class AlterationSlot {
        public Integer UnlockLevel;
        public EFortRarity UnlockRarity;
        public FName SlotDefinitionRow;
        public Boolean bRespeccable;
        public FName SlotRarityInitRow;
        public EFortRarity SlotInitMin;
        public EFortRarity SlotInitMax;
        public Integer SlotInitIndex;
    }
}
