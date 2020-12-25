package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortCardPackItemDefinition extends FortAccountItemDefinition {
    public boolean bIsLlama = false;
    public boolean bIsChoicePack = false;
    public boolean bAutoOpenAsReward = true;
    public Integer LootTier;
    public String LootTierGroup;
    public Integer DisplayRarityLevel;
    public FSoftObjectPath PackImage;
    public FSoftObjectPath XRayTexture;
    public FLinearColor PackColor;
    public FSoftObjectPath PackPersonality;
}
