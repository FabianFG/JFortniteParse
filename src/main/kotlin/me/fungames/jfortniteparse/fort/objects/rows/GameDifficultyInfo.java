package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;

public class GameDifficultyInfo extends FTableRowBase {
	public boolean bIsOnboarding;
	public float Difficulty;
	public float DifficultyMatchmakingMinOverride;
	public float DifficultyMatchmakingMaxOverride;
	public int LootLevel;
	public ERatingsEnforcementType RatingsEnforcement;
	public int RequiredRating;
	public int MaximumRating;
	public int PvPRating;
	public int RecommendedRating;
	public float ScoreBonus;
	public String LootTierGroup;
	public String BonusLootTierGroup;
	public String DifficultyIncreaseLootTierGroup;
	public int NumDifficultyIncreases;
	public FText ThreatDisplayName;
	public FName ColorParamName;
	public int DefaultPlayerLives;
	public FName PlayerStatClampRowName;

	public enum ERatingsEnforcementType {
		Default, IgnoreMaximums, IgnoreParty, IgnorePartyMaximum
	}
}
