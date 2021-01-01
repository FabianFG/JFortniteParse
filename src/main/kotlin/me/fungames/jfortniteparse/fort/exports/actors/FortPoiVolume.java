package me.fungames.jfortniteparse.fort.exports.actors;

import me.fungames.jfortniteparse.ue4.assets.exports.actors.AVolume;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class FortPoiVolume extends AVolume {
    public Boolean bIsLargeGameVolume;
    public Float CurrentFortPoiVolumeSize;
    public Float VolumeThresholdForLargeGameVolume;
    public Float LargeGameVolume;
    public List<String> EventsEnabledDuring;
    public FGameplayTagContainer LocationTags;
    public FPackageIndex /*FortPOIAmbientAudioBank*/ AudioBank;
    public FPackageIndex /*FortPoiCollisionComponent*/ PoiCollisionComp;
}
