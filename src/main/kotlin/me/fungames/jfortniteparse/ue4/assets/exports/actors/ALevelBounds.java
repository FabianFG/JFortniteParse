package me.fungames.jfortniteparse.ue4.assets.exports.actors;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UBoxComponent;

public class ALevelBounds extends AActor {
    public Lazy<UBoxComponent> BoxComponent;
    public Boolean bAutoUpdateBounds;
}
