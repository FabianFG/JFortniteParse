package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.FDataTableCategoryHandle;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortSchematicItemDefinition extends FortAlterableItemDefinition {
    public FDataTableRowHandle CraftingRecipe;
    public FSoftObjectPath CraftingSuccessSound;
    public FPackageIndex /*FortWorldItemDefinition*/ CachedResultWorldItemDefinition;
    public FDataTableCategoryHandle LootLevelData;
    public FName CraftingTimeRowName;
    public Boolean bUseSchematicDisplayName;
}
