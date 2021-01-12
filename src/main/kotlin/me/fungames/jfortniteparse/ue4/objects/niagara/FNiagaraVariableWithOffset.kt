package me.fungames.jfortniteparse.ue4.objects.niagara

import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class FNiagaraVariableWithOffset : FNiagaraVariableBase {
    var offset: Int

    constructor(Ar: FAssetArchive) : super(Ar) {
        offset = Ar.readInt32()
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        Ar.write(offset)
    }

    constructor(name: FName, typeDef: FStructFallback, offset: Int) : super(name, typeDef) {
        this.offset = offset
    }
}