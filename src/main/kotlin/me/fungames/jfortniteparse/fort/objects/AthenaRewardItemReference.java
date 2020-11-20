package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

@UStruct
public class AthenaRewardItemReference {
    public FSoftObjectPath ItemDefinition;
    public String TemplateId;
    public Integer Quantity;
    public ChallengeGiftBoxData RewardGiftBox;
    public Boolean IsChaseReward;
    public EAthenaRewardItemType RewardType;
    public EAthenaRewardVisualImportanceType RewardVisualImportanceType;

    public enum EAthenaRewardItemType {
        Normal, HiddenReward, GiftboxHiddenReward, NonExportedFakeReward
    }

    public enum EAthenaRewardVisualImportanceType {
        Normal, Hot, CrazyHot, Crazy
    }
}
