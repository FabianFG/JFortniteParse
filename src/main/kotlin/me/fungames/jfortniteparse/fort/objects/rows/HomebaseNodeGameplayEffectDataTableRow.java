package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FGameplayAttribute;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

public class HomebaseNodeGameplayEffectDataTableRow extends FTableRowBase {
    public FGameplayAttribute Attribute;
    public EGameplayModOp Operation;
    public float Magnitude;
    public FGameplayTagContainer ApplicationRequiredTagsContainer;
    public FGameplayTagContainer RequiredSourceTagsContainer;
    public FGameplayTagContainer RequiredTargetTagsContainer;
    public FGameplayTagContainer GrantedTagsContainer;
    public FGameplayTagContainer IgnoreSourceTagsContainer;
    public FGameplayTagContainer AssetTagsContainer;
    public int AssociatedGEIdx;
    public int AssociatedModifierIdx;

    public enum EGameplayModOp {
        Additive,
        Multiplicitive,
        Division,
        Override,
        Max
    }
}
