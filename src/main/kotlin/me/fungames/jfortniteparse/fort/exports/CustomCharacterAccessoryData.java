package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class CustomCharacterAccessoryData extends CustomCharacterPartData {
    public FName AttachSocketName;
    public FVector AttachOffset;
    public FPackageIndex /*CustomAccessoryAttachmentData*/ AttachmentOverrideData;
    public Boolean bUseClothCollisionFromOtherParts;
    public Boolean bCollideWithOtherPartsCloth;
    public FSoftObjectPath /*SoftClassPath*/ AnimClass;
    public FSoftObjectPath /*SoftClassPath*/ FrontEndAnimClass;
    public FSoftObjectPath /*SoftClassPath*/ MannequinAnimClass;
    public FSoftObjectPath AccessoryColors;
}
