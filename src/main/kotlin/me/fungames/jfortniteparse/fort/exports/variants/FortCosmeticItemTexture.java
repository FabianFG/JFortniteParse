package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.ItemTextureVariantDef;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class FortCosmeticItemTexture extends FortCosmeticVariantBackedByArray {
    public ItemTextureVariantDef ItemTextureVar;

    @Nullable
    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return Collections.singletonList(ItemTextureVar);
    }
}
