package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.fort.enums.EFortCustomPartType;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;
import java.util.Map;

public class AthenaCharacterItemDefinition extends AthenaCosmeticItemDefinition {
    public Map<FName, Lazy<UObject>> RequestedDataStores;
    @Deprecated
    public Map<EFortCustomPartType, Lazy<MarshalledVFX_AuthoredDataConfig>> AuthoredVFXData_ByPart;
    public Lazy<FortHeroType> HeroDefinition;
    public Lazy<AthenaBackpackItemDefinition> DefaultBackpack;
    public List<Lazy<AthenaCosmeticItemDefinition>> RequiredCosmeticItems;
    public EFortCustomGender Gender;
    @UProperty(skipNext = 1)
    public FSoftObjectPath FeedbackBank;
    //public Map<GameplayTag, AthenaCharacterTaggedPartsList> TaggedPartsOverride;
}
