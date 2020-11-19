package me.fungames.jfortniteparse.fort.objects.variants;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class LoadoutVariantDef extends PartVariantDef {
    public FSoftObjectPath LoadoutItem;
    public ELoadoutVariantInsertType LocationToInsert;
    public Boolean bItemExpectedInLoadout;
    public Boolean bRequireItemToBeCurrent;
    public Boolean bIgnoreRequireItemToBeCurrentInFrontEnd;
    public List<EmoteMontageVariantDef> VariantEmoteMontages;

    public enum ELoadoutVariantInsertType {
        StartOfArray,
        EndOfArray
    }

    @UStruct
    public static class EmoteMontageVariantDef extends BaseVariantDef {
        public List<EmoteMontageSwap> MontageSwaps;
        public CosmeticMetaTagContainer MetaTags;
    }

    @UStruct
    public static class EmoteMontageSwap {
        public FSoftObjectPath ToSwapFrom;
        public FSoftObjectPath ToSwapTo;
    }
}
