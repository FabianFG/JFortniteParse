package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

internal const val TEXSTREAM_MAX_NUM_UVCHANNELS = 4

@UStruct
class FMeshUVChannelInfo {
    @JvmField @UProperty("bInitialized")
    var initialized: Boolean
    @JvmField @UProperty("bOverrideDensities")
    var overrideDensities: Boolean
    @JvmField @UProperty("LocalUVDensities", arrayDim = TEXSTREAM_MAX_NUM_UVCHANNELS)
    var localUVDensities: Array<Float>

    constructor(Ar: FArchive) {
        initialized = Ar.readBoolean()
        overrideDensities = Ar.readBoolean()
        localUVDensities = Array(TEXSTREAM_MAX_NUM_UVCHANNELS) { Ar.readFloat32() }
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeBoolean(initialized)
        Ar.writeBoolean(overrideDensities)
        localUVDensities.forEach { Ar.writeFloat32(it) }
    }

    constructor(initialized: Boolean, overrideDensities: Boolean, localUVDensities: Array<Float>) {
        this.initialized = initialized
        this.overrideDensities = overrideDensities
        this.localUVDensities = localUVDensities
    }

    constructor() : this(false, false, emptyArray())
}