package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class WeaponUpgradeItemRow extends FTableRowBase {
    public FPackageIndex /*FortWeaponRangedItemDefinition*/ CurrentWeaponDef;
    public FPackageIndex /*FortWeaponRangedItemDefinition*/ UpgradedWeaponDef;
    public EFortWeaponUpgradeCosts WoodCost;
    public EFortWeaponUpgradeCosts MetalCost;
    public EFortWeaponUpgradeCosts BrickCost;
    public EFortWeaponUpgradeDirection Direction;

    public enum EFortWeaponUpgradeCosts {
        NotSet,
        WoodUncommon,
        WoodRare,
        WoodVeryRare,
        WoodSuperRare,
        MetalUncommon,
        MetalRare,
        MetalVeryRare,
        MetalSuperRare,
        BrickUncommon,
        BrickRare,
        BrickVeryRare,
        BrickSuperRare,
        HorizontalWoodCommon,
        HorizontalWoodUncommon,
        HorizontalWoodRare,
        HorizontalWoodVeryRare,
        HorizontalWoodSuperRare,
        HorizontalMetalCommon,
        HorizontalMetalUncommon,
        HorizontalMetalRare,
        HorizontalMetalVeryRare,
        HorizontalMetalSuperRare,
        HorizontalBrickCommon,
        HorizontalBrickUncommon,
        HorizontalBrickRare,
        HorizontalBrickVeryRare,
        HorizontalBrickSuperRare
    }

    public enum EFortWeaponUpgradeDirection {
        NotSet,
        Vertical,
        Horizontal
    }
}
