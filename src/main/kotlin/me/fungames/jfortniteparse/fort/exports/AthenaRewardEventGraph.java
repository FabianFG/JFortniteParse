package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.AthenaRewardItemReference;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaRewardEventGraph extends FortPersistableItemDefinition {
	public String CalendarEventName;
	public String CalendarEventType;
	public FSoftObjectPath PurchaseToken;
	public List<RewardKeyData> RewardKey;
	public Boolean bRewardKeysInternally;
	public List<RewardNode> Rewards;
	public List<FSoftObjectPath> ItemsToCleanUpUponRemoval;
	public FPackageIndex /*AthenaRewardEventGraphCosmeticItemDefinition*/ CosmeticRandomnes;

	@UStruct
	public static class RewardKeyData {
		public FSoftObjectPath Key;
		public FGameplayTag NodeTagMatchReq;
		public Integer RewardKeyMaxCount;
		public Integer RewardKeyInitialCount;
		public FSoftObjectPath UnlockingItemDef;
		public Boolean bUseUnlockingItemDisplayName;
	}

	@UStruct
	public static class RewardNode {
		public FSoftObjectPath RequiredKey;
		public Integer KeyCount;
		public Integer MinKeyCountToUnlock;
		public Integer DaysFromEventStartToUnlock;
		public FGameplayTagContainer ChildNodes;
		public FGameplayTagContainer ParentNodes;
		public FGameplayTag NodeTag;
		public Boolean bGrantedAtGraphDestruction;
		public Boolean bRequiredOwnership;
		public List<AthenaRewardItemReference> Rewards;
		public String RewardOperation;
		public FSoftObjectPath RewardContextItem;
		public List<CosmeticVariantInfo> HardDefinedVisuals;
	}

	@UStruct
	public static class CosmeticVariantInfo {
		public FGameplayTag VariantChannelTag;
		public FGameplayTag ActiveVariantTag;
	}
}
