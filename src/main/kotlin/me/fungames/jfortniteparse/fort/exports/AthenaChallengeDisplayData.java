package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.objects.FortChallengeSetStyle;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaChallengeDisplayData extends UDataAsset {
    public Integer SoonDisplayMinuteThreshold;
    public FLinearColor PartyAssistFontColor;
    public FLinearColor DifficultChallengeFontColor;
    public Lazy<UTexture2D> PartyAssistIcon;
    public Lazy<UTexture2D> ExternalPartyAssistIcon;
    public FortChallengeSetStyle DefaultChallengeStyle;
    public FortChallengeSetStyle DailyChallengeStyle;
    public List<FGameplayTagContainer> PunchCardSortOrder;
    public List<FortChallengePunchCardStyles> ChallengePunchCardStyles;

    @UStruct
    public static class FortChallengePunchCardStyles {
        public FGameplayTag StyleType;
        public String EncodedName;
        public FLinearColor BaseColor1;
        public FLinearColor BaseColor2;
        public FLinearColor BaseColor3;
        public FLinearColor BaseColor4;
        public FLinearColor BaseColor5;
        public FLinearColor AccentColor1;
        public FLinearColor AccentColor2;
        public FSoftObjectPath BackgrounTexture;
    }
}
