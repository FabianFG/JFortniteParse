package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FDateTime;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortRepeatableDailiesCardItemDefinition extends FortAccountItemDefinition {
    public String GranterQuestPack;
    public Integer FillCount;
    public FScalableFloat ReplacedRestedXpValue;
    public FScalableFloat ReducedXPReward;
    public FScalableFloat ReplacedRestedXpValueScalarForMissedDays;
    public FSoftObjectPath RequiredItemDef;
    public List<RepeatableDailiesCardDateOverride> DateOverrides;

    @UStruct
    public static class RepeatableDailiesCardDateOverride {
        public FSoftObjectPath Quest;
        public FDateTime Start;
        public FDateTime End;
    }
}
