package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortCampaignHeroLoadoutItemDefinition extends FortProfileItemDefinition {
    public List<FortCrewSlotInformation> CrewSlots;
    public FGameplayTagQuery CommanderSlotQuery;
    public FGameplayTagQuery SupportHeroSlotQuery;
    public Integer GadgetSlotsAllowed;
    public FName SlotUnlockSquadName;
    public FSoftObjectPath TeamPerkUnlockNode;

    @UStruct
    public static class FortCrewSlotInformation {
        public FText DisplayName;
        public FName SlotName;
        public FGameplayTagContainer SlotTags;
        public Float SlotStatContribution;
    }
}
