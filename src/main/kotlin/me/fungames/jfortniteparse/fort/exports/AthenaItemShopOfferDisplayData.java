package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.ECosmeticCompatibleMode;
import me.fungames.jfortniteparse.ue4.assets.exports.UPrimaryDataAsset;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaItemShopOfferDisplayData extends UPrimaryDataAsset {
    public List<ContextualPresentation> ContextualPresentations;

    public static class ContextualPresentation {
        public ECosmeticCompatibleMode PrimaryMode;
        public FSoftObjectPath Material;
    }
}
