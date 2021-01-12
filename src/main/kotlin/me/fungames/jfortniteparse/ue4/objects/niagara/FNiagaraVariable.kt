package me.fungames.jfortniteparse.ue4.objects.niagara

import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class FNiagaraVariable : FNiagaraVariableBase {
    var varData: ByteArray

    constructor(Ar: FAssetArchive) : super(Ar) {
        varData = Ar.read(Ar.readInt32())
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        Ar.write(varData.size)
        Ar.write(varData)
    }

    constructor(name: FName, typeDef: FStructFallback, varData: ByteArray) : super(name, typeDef) {
        this.varData = varData
    }
}