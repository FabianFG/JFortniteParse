package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortWorkerType extends FortCharacterType {
    public EFortCustomGender Gender;
    public FSoftObjectPath FixedPortrait;
    public boolean bIsManager = false;
    public FGameplayTagContainer ManagerSynergyTag;
    public FGameplayTagContainer FixedPersonalityTag;
    public FGameplayTagContainer FixedSetBonusTag;
    public Integer MatchingPersonalityBonus;
    public Integer MismatchingPersonalityPenalty;
}
