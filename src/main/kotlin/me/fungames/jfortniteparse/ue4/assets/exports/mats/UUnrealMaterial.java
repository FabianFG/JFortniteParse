package me.fungames.jfortniteparse.ue4.assets.exports.mats;

import me.fungames.jfortniteparse.ue4.converters.CMaterialParams;

import java.util.List;

public interface UUnrealMaterial {
    void getParams(CMaterialParams params);

    default boolean isTextureCube() {
        return false;
    }

    default void appendReferencedTextures(List<UUnrealMaterial> outTextures, boolean onlyRendered) {
        CMaterialParams params = new CMaterialParams();
        getParams(params);
        params.appendAllTextures(outTextures);
    }

    String name();
}
