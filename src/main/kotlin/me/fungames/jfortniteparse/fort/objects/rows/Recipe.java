package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.objects.FortItemQuantityPair;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;

import java.util.List;

public class Recipe extends FTableRowBase {
    public List<FortItemQuantityPair> RecipeResults;
    public boolean bIsConsumed;
    public List<FortItemQuantityPair> RecipeCosts;
    public FGameplayTagContainer RequiredCatalysts;
    public int Score;
}
