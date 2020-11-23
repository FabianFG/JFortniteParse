package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaPetItemDefinition extends AthenaCosmeticItemDefinition {
    public EAthenaPetAttachRule PetAttachRule;
    public FVector PetAttachOffset;
    public List<FPackageIndex /*FortPetStimuliBank*/> StimuliBanks;
    public FSoftObjectPath /*SoftClassPath*/ PetPrefabClass;
    public FSoftObjectPath PetSoundBank;

    public enum EAthenaPetAttachRule {
        AttachToBackpack,
        AttachToBody
    }
}
