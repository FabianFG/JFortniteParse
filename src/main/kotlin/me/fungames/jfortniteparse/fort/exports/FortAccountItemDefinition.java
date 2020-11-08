package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.UProperty;

public class FortAccountItemDefinition extends FortPersistableItemDefinition {
	/*public FCurveTableRowHandle LevelToXpHandle;
	public FCurveTableRowHandle LevelToSacrificeXpHandle;
	public FDataTableRowHandle SacrificeRecipe;
	public FDataTableRowHandle TransmogSacrificeRow;
	public FDataTableRowHandle[] ConversionRecipes;
	public FDataTableRowHandle UpgradeRarityRecipeHandle;*/
	@UProperty(skipPrevious = 6)
	public Integer MinLevel;
	public Integer MaxLevel;
	public String GrantToProfileType;
}
