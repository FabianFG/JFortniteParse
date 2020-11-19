package me.fungames.jfortniteparse.fort.exports.variants;

import me.fungames.jfortniteparse.fort.objects.variants.BaseVariantDef;
import me.fungames.jfortniteparse.fort.objects.variants.ParticlVariantDef;

import java.util.List;

public class FortCosmeticParticleVariant extends FortCosmeticVariantBackedByArray {
    public List<ParticlVariantDef> ParticleOptions;

    @Override
    public List<? extends BaseVariantDef> getVariants() {
        return ParticleOptions;
    }
}
