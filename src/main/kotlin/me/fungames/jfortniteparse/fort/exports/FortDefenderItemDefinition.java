package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortAttributeInitializationKey;
import me.fungames.jfortniteparse.fort.objects.GameplayEffectApplicationInfo;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortDefenderItemDefinition extends FortCharacterType {
    public List<GameplayEffectApplicationInfo> CombinedStatGEs;
    public FName AppearanceOverrideName;
    public FortAttributeInitializationKey AttributeInitKey;
    public FSoftObjectPath /*SoftClassPath*/ PawnClass;
}
