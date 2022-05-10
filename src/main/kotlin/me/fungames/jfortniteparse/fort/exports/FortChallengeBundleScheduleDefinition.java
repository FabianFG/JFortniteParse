package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.ChallengeGiftBoxData;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortChallengeBundleScheduleDefinition extends FortAccountItemDefinition {
    public List<FortChallengeBundleScheduleEntry> ScheduleEntries;
    public String CalendarEventTag;
    public String CalendarEventName;
    public FGameplayTag RequiredMcpContextTags;
    public ChallengeGiftBoxData GrantedBundleGiftBox;
    public Boolean CleanUpOnBundleCompletion;
    public FGameplayTag CategoryTag;
    public FGameplayTag ChallengeDetailsTag;
    public Boolean bHideInLegacyAllChallengesEscapeMenu;
    public FGameplayTag SourceTag;
    public Boolean bSeperateEachBundleForDisplay;
    public Integer SortPriority;
    public FText UnlockTextOverride;
    public List<FSoftObjectPath> CustomChallengeHeaderExtensions;
    public Boolean bHideCountdownFromMapChallenges;

    @UStruct
    public static class FortChallengeBundleScheduleEntry {
        public FSoftObjectPath ChallengeBundle;
        public EChallengeScheduleUnlockType UnlockType;
        public Integer UnlockValue;
        public ChallengeGiftBoxData GiftBox;
    }

    public enum EChallengeScheduleUnlockType {
        Manually,
        OnScheduleGranted,
        DaysSinceEventStart
    }
}
