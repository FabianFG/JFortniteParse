package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class AthenaCharacterItemDefinition extends AthenaCosmeticItemDefinition {
    //public Map<FName, PackageIndex /*UObject**/> RequestedDataStores;
    //public Map<EFortCustomPartType, PackageIndex /*UMarshalledVFX_AuthoredDataConfig*/> RequestedDataStores;
    public FPackageIndex /*FortHeroType*/ HeroDefinition;
    public FPackageIndex /*AthenaBackpackItemDefinition*/ DefaultBackpack;
    public FPackageIndex[] /*AthenaCosmeticItemDefinition[]*/ RequiredCosmeticItems;
    //public EFortCustomGender Gender;
    public FSoftObjectPath FeedbackBank;
    //public Map<GameplayTag, AthenaCharacterTaggedPartsList> TaggedPartsOverride;
}
