package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortAccoladeItemDefinition extends FortPersistableItemDefinition {
    public EFortAccoladeType AccoladeType;
    public EFortAccoladeSubtype AccoladeSubtype;
    public List<FSoftObjectPath> AccoladeToReplace;
    public FSoftObjectPath PreviouseAccoladeSmallPreviewImage;
    public FSoftObjectPath PreviouseAccoladeLargePreviewImage;
    public EXPEventPriorityType Priority;
    public FScalableFloat XpRewardAmount;
    public Float AccoladeLevel;
    public Boolean bOnlyAllowOncePerDay;
    public Boolean bIgnoreInAntiAddictionReducedStates;
    public FPackageIndex /*SoundCue*/ AwardedSoundCue;
    public FScalableFloat XpRewardScalarByCount;
    public List<AccoladeSecondaryXpType> SecondaryXpValues;

    public enum EFortAccoladeType {
        Acknowledgement,
        Accolade,
        Medal
    }

    public enum EFortAccoladeSubtype {
        NotSet,
        Action,
        Discovery,
        XpToken
    }

    public enum EXPEventPriorityType {
        NearReticle,
        XPBarOnly,
        TopCenter,
        Feed
    }

    @UStruct
    public static class AccoladeSecondaryXpType {
        public FGameplayTag Type;
        public FScalableFloat XpAmount;
    }
}
