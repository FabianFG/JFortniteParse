package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortHomebaseNodeItemDefinition extends FortAccountItemDefinition {
    public List<HomebaseNodeLevel> LevelData;
    public EHomebaseNodeType DisplayType;

    @UStruct
    public static class HomebaseNodeLevel {
        public FName DisplayDataId;
        public Integer MinCommanderLevel;
        public List<FortItemQuantityPair> Cost;
        public List<FName> GameplayEffectRowNames;
        public FSoftObjectPath AbilityKit;
        public List<HomebaseSquadSlotId> UnlockedSquadSlots;
    }

    @UStruct
    public static class HomebaseSquadSlotId {
        public FName SquadId;
        public Integer SquadSlotIndex;
    }

    public enum EHomebaseNodeType {
        Gadget, Utility, Hidden
    }
}
