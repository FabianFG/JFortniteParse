package me.fungames.jfortniteparse.ue4.manifests.objects

@ExperimentalUnsignedTypes
data class FFileManifest(
    var fileName : String = "",
    var symlinkTarget : String = "",
    var fileHash : ByteArray = ByteArray(0),
    var fileMetaFlags : UByte = 0.toUByte(),
    var installTags : Array<String> = emptyArray(),
    var chunkParts : Array<FChunkPart> = emptyArray(),
    var fileSize : ULong = 0.toULong()
)