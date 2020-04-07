package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FStripDataFlags
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_23
import me.fungames.jfortniteparse.ue4.versions.GAME_VALORANT

@ExperimentalUnsignedTypes
class UTexture2D : UExport {

    override var baseObject: UObject
    var flag1 : FStripDataFlags
    var flag2 : FStripDataFlags
    var cooked : Boolean
    var textures : MutableMap<FTexturePlatformData, FName>

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        flag1 = FStripDataFlags(Ar)
        flag2 = FStripDataFlags(Ar)

        cooked = Ar.readBoolean()
        textures = mutableMapOf()
        if (cooked) {
            var pixelFormat = Ar.readFName()
            while (pixelFormat.text != "None") {
                val skipOffset = Ar.readInt64()
                textures[FTexturePlatformData(Ar)] = pixelFormat
                if (Ar.relativePos().toLong() != skipOffset) {
                    logger.warn("Texture read incorrectly ${Ar.relativePos()}, skip offset $skipOffset")
                    Ar.seekRelative(skipOffset.toInt())
                }
                pixelFormat = Ar.readFName()
            }
        }
        super.complete(Ar)
    }

    fun getFirstMip() = getFirstTexture().getFirstMip()
    fun getFirstTexture() = if (textures.isNotEmpty()) textures.keys.first() else throw IllegalStateException("No Textures found in this UTexture2D")

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        flag1.serialize(Ar)
        flag2.serialize(Ar)
        Ar.writeBoolean(cooked)
        textures.forEach { (texture, pixelFormat) ->
            Ar.writeFName(pixelFormat)
            val tempAr = Ar.setupByteArrayWriter()
            texture.serialize(tempAr)
            val textureData = tempAr.toByteArray()
            Ar.writeInt64(tempAr.relativePos().toLong() + 8) //new skip offset
            Ar.write(textureData)
        }
        Ar.writeFName(FName.getByNameMap("None", Ar.nameMap) ?: throw ParserException("NameMap must contain \"None\""))
        super.completeWrite(Ar)
    }

    constructor(
        exportType: String,
        baseObject: UObject,
        flag1: FStripDataFlags,
        flag2: FStripDataFlags,
        cooked: Boolean,
        textures: MutableMap<FTexturePlatformData, FName>
    ) : super(exportType) {
        this.baseObject = baseObject
        this.flag1 = flag1
        this.flag2 = flag2
        this.cooked = cooked
        this.textures = textures
    }
}

@ExperimentalUnsignedTypes
class FTexturePlatformData : UClass {
    var sizeX : Int
    var sizeY : Int
    var numSlices : Int
    var pixelFormat : String
    var firstMip : Int
    var mipCount : Int
    var mips : MutableList<FTexture2DMipMap>
    var isVirtual : Boolean = false

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        logger.debug("Reading Texture2D...")
        sizeX = Ar.readInt32()
        logger.debug("Width: $sizeX")
        sizeY = Ar.readInt32()
        logger.debug("Height: $sizeY")
        numSlices = Ar.readInt32()
        pixelFormat = Ar.readString()
        firstMip = Ar.readInt32()
        mipCount = Ar.readInt32()
        mips = mutableListOf()
        for (i in 0 until mipCount) {
            mips.add(FTexture2DMipMap(Ar))
        }

        if (Ar.game >= GAME_UE4_23) {
            isVirtual = Ar.readBoolean()
            if(isVirtual) {
                throw ParserException("Texture is virtual, not implemented", Ar)
            }
        }
        super.complete(Ar)
    }

    fun getFirstMip() = mips[firstMip]

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(sizeX)
        Ar.writeInt32(sizeY)
        Ar.writeInt32(numSlices)
        Ar.writeString(pixelFormat)
        Ar.writeInt32(firstMip)
        Ar.writeInt32(mipCount)
        mips.forEach {it.serialize(Ar)}
        if (Ar.game >= GAME_UE4_23) {
            Ar.writeBoolean(isVirtual)
            if(isVirtual)
                throw ParserException("Texture is virtual, not implemented", Ar)
        }
        super.completeWrite(Ar)
    }

    constructor(sizeX: Int, sizeY: Int, numSlices : Int, pixelFormat : String, firstMip : Int, mipCount : Int, mips : MutableList<FTexture2DMipMap>, isVirtual : Boolean) {
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.numSlices = numSlices
        this.pixelFormat = pixelFormat
        this.firstMip = firstMip
        this.mipCount = mipCount
        this.mips = mips
        this.isVirtual = isVirtual
    }
}

@ExperimentalUnsignedTypes
class FTexture2DMipMap : UClass {
    var cooked : Boolean
    var data : FByteBulkData
    var sizeX : Int
    var sizeY : Int
    var sizeZ : Int
    var u : String? = null

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        cooked = Ar.readBoolean()
        data = FByteBulkData(Ar)
        sizeX = Ar.readInt32()
        sizeY = Ar.readInt32()
        sizeZ = Ar.readInt32()
        if (!cooked)
            u = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeBoolean(cooked)
        data.serialize(Ar)
        Ar.writeInt32(sizeX)
        Ar.writeInt32(sizeY)
        Ar.writeInt32(sizeZ)
        super.completeWrite(Ar)
    }

    constructor(cooked : Boolean, data: FByteBulkData, sizeX : Int, sizeY : Int, sizeZ : Int, u : String?) {
        this.cooked = cooked
        this.data = data
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.sizeZ = sizeZ
    }
}