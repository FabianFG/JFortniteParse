package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortConversation extends UDataAsset {
    public List<FortConversationSentence> Sentences;

    @UStruct
    public static class FortConversationSentence {
        public FortSentenceAudio SpeechAudio;
        public FText SpeechText;
        public FSoftObjectPath TalkingHeadTexture;
        public FText TalkingHeadTitle;
        public FSoftObjectPath AnimMontage;
        public Float PostSentenceDelay;
        public Float DisplayDuration;
    }

    @UStruct
    public static class FortSentenceAudio {
        public FSoftObjectPath Audio;
        public FortFeedbackHandle Handle;
    }

    @UStruct
    public static class FortFeedbackHandle {
        public FPackageIndex /*FortFeedbackBank*/ FeedbackBank;
        public FName EventName;
        public Boolean bReadOnly;
        public Boolean bBankDefined;
        public EFortFeedbackBroadcastFilter BroadcastFilterOverride;
    }

    public enum EFortFeedbackBroadcastFilter {
        FFBF_Speaker,
        FFBF_SpeakerTeam,
        FFBF_SpeakerAdressee,
        FFBF_HumanPvP_Team1,
        FFBF_HumanPvP_Team2,
        FFBF_None_Max
    }
}
