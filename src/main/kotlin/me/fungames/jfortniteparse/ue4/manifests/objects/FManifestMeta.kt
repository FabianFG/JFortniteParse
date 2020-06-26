package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FManifestMeta : UClass {

    var isFileDataInt : Boolean
    var appId : UInt
    var appName : String
    var buildVersion : String
    var launchExe : String
    var launchCommand : String
    var prereqIds : Array<String>
    var prereqName : String
    var prereqPath : String
    var prereqArgs : String


    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        /*val featureLevelInt = */Ar.readInt32()
        isFileDataInt = Ar.readFlag()
        appId = Ar.readUInt32()
        appName = Ar.readString()
        buildVersion = Ar.readString()
        launchExe = Ar.readString()
        launchCommand = Ar.readString()
        prereqIds = Ar.readTArray { it.readString() }
        prereqName = Ar.readString()
        prereqPath = Ar.readString()
        prereqArgs = Ar.readString()

        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}