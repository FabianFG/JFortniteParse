package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.MeshVariantDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FortCosmeticMeshVariant extends FortCosmeticVariantBackedByArray {
    public List<MeshVariantDef> MeshOptions;

    @Nullable
    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return MeshOptions;
    }
}
