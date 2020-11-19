package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.EFortCustomGender;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;
import java.util.Map;

public class AthenaCharacterItemDefinition extends AthenaCosmeticItemDefinition {
    public Map<FName, FPackageIndex /*UObject*/> RequestedDataStores;
    //public Map<EFortCustomPartType, PackageIndex /*UMarshalledVFX_AuthoredDataConfig*/> RequestedDataStores;
    @UProperty(skipPrevious = 1)
    public FPackageIndex /*FortHeroType*/ HeroDefinition;
    public FPackageIndex /*AthenaBackpackItemDefinition*/ DefaultBackpack;
    public List<FPackageIndex /*AthenaCosmeticItemDefinition*/> RequiredCosmeticItems;
    public EFortCustomGender Gender;
    @UProperty(skipNext = 1)
    public FSoftObjectPath FeedbackBank;
    //public Map<GameplayTag, AthenaCharacterTaggedPartsList> TaggedPartsOverride;
}
