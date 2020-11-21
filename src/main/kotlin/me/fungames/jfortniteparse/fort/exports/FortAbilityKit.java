package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortAbilityKit extends FortAbilitySet {
    public FText DisplayName;
    public FText Description;
    public FText TooltipDescription;
    public FSlateBrush IconBrush;
    public FSlateBrush PrimaryTraitIconBrushOverride;
    public List<FSoftObjectPath> Gadgets;
    public List<AbilityKitItem> Items;
    public List<FPackageIndex /*FortSchematicItemDefinition*/> AddedSchematics;
    public List<FPackageIndex /*FortSchematicItemDefinition*/> RemovedSchamatics;
    public FPackageIndex /*FortTooltip*/ Tooltip;
    public FPackageIndex /*FortTooltip*/ SummaryTooltip;
    public FPackageIndex /*FortTooltipDisplayStatsList*/ StatList;

    @UStruct
    public static class AbilityKitItem {
        public FPackageIndex /*FortItemDefinition*/ Item;
        public Integer Quantity;
        public EFortReplenishmentType Replenishment;
    }

    public enum EFortReplenishmentType {
        Restricted,
        ClampMin,
        Add,
        Ability
    }
}
