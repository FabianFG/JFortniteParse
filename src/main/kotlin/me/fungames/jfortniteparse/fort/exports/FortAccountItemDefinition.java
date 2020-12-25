package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;

import java.util.List;

public class FortAccountItemDefinition extends FortPersistableItemDefinition {
    public FCurveTableRowHandle LevelToXpHandle;
    public FCurveTableRowHandle LevelToSacrificeXpHandle;
    public FDataTableRowHandle SacrificeRecipe;
    public FDataTableRowHandle TransmogSacrificeRow;
    public List<FDataTableRowHandle> ConversionRecipes;
    public FDataTableRowHandle UpgradeRarityRecipeHandle;
    public Integer MinLevel;
    public Integer MaxLevel;
    public String GrantToProfileType;
}
