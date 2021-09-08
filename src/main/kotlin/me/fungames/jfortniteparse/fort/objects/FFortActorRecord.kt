package me.fungames.jfortniteparse.fort.objects

import me.fungames.jfortniteparse.LOG_JFP
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
    var actorGuid = FGuid(Ar)
    @UProperty("ActorState")
    var actorState = EFortBuildingPersistentState.values()[Ar.read()]
    @UProperty("ActorClass")
    var actorClass = Ar.readString() // actually UClass*
    @UProperty("ActorTransform")
    var actorTransform = FTransform(Ar)
    @UProperty("bSpawnedActor")
    var spawnedActor = Ar.readBoolean()
    @UProperty("ActorData")
    var actorData: FStructFallback? = null

    init {
        val actorDataNum = Ar.readInt32()
        if (actorDataNum > 0) {
            val pos = Ar.pos()
            val validPos = pos + actorDataNum
            actorData = FStructFallback(Ar, FName.NAME_None)
            if (Ar.pos() != validPos) {
                LOG_JFP.debug { "Did not read FortActorRecord.ActorData correctly, ${validPos - Ar.pos()} bytes remaining" }
                Ar.seek(pos + actorDataNum)
            }
        }
    }
}