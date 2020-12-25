package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortTrapItemDefinition extends FortDecoItemDefinition {
    public FRotator KnockbackDirOverride;
    public Boolean bKnockBackUsingPawnDir;
    public FSoftObjectPath EquipSound;
    public FGameplayTagContainer OverrideAutoEquipTags;
}
