package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

class FIntBulkData : FByteBulkData {
    constructor(Ar: FAssetArchive) : super(Ar)
    constructor(header: FByteBulkDataHeader, data: ByteArray) : super(header, data)
}