package me.fungames.jfortniteparse.ue4.assets.exports.components;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.UClass;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;

public class UChildActorComponent extends USceneComponent {
    public Lazy<UClass> ChildActorClass;
    public Lazy<AActor> ChildActor;
    public Lazy<AActor> ChildActorTemplate;
}
