package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortAttributeInitializationKey;
import me.fungames.jfortniteparse.fort.objects.GameplayEffectApplicationInfo;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftClassPath;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortHeroType extends FortWorkerType {
    public Boolean bForceShowHeadAccessory;
    public Boolean bForceShowBackpack;
    public List<FSoftObjectPath> Specializations;
    public FSoftObjectPath DefaultMontageLookupTable;
    public FSoftObjectPath OverrideMontageLookupTable;
    public List<GameplayEffectApplicationInfo> CombinedStatGEs;
    public FGameplayTagContainer RequiredGPTags;
    public FSoftObjectPath MaleOverrideFeedback;
    public FSoftObjectPath FemaleOverrideFeedback;
    public FSoftClassPath OverridePawnClass;
    public FPackageIndex /*FortHeroGameplayDefinition*/ HeroGameplayDefinition;
    public FPackageIndex /*AthenaCharacterItemDefinition*/ HeroCosmeticOutfitDefinition;
    public FPackageIndex /*AthenaBackpackItemDefinition*/ HeroCosmeticBackblingDefinition;
    public FSoftClassPath FrontendAnimClass;
    public FSoftClassPath ItemPreviewAnimClass;
    public FSoftObjectPath FrontendAnimMontageIdleOverride;
    public Float FrontEndBackPreviewRotationOffset;
    public FText Subtype;
    public FortAttributeInitializationKey AttributeInitKey;
    public FDataTableRowHandle LegacyStatHandle;
    public FSoftObjectPath ItemPreviewMontage_Male;
    public FSoftObjectPath ItemPreviewMontage_Female;
}
