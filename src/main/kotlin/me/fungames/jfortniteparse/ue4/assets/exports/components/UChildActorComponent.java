package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.exports.UClassReal;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;

public class UChildActorComponent extends USceneComponent {
    public Lazy<UClassReal> ChildActorClass;
    public Lazy<AActor> ChildActor;
    public Lazy<AActor> ChildActorTemplate;
}
