package me.fungames.jfortniteparse.fort.objects

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

enum class EFortBuildingPersistentState {
    Default,
    New,
    Constructed,
    Destroyed,
    Searched,
    None
}

class FFortActorRecord(Ar: FAssetArchive) {
    @UProperty("ActorGuid")
    val actorGuid = FGuid(Ar)
    @UProperty("ActorState")
    val actorState = EFortBuildingPersistentState.values()[Ar.read()]
    @UProperty("ActorClass")
    val actorClass = Ar.readString() // actually UClass*
    @UProperty("ActorTransform")
    val actorTransform = FTransform(Ar)
    @UProperty("bSpawnedActor")
    val spawnedActor = Ar.readBoolean()
    @UProperty("ActorData")
    val actorData: FStructFallback?

    init {
        val actorDataNum = Ar.readInt32()
        actorData = if (actorDataNum > 0) FStructFallback(Ar, FName.NAME_None) else null
    }
}