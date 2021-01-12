package me.fungames.jfortniteparse.ue4.objects.niagara

import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

open class FNiagaraVariableBase {
    var name: FName
    var typeDef: FStructFallback // originally FNiagaraTypeDefinitionHandle typeDefHandle

    constructor(Ar: FAssetArchive) {
        name = Ar.readFName()
        typeDef = FStructFallback(Ar, FName.dummy("NiagaraTypeDefinition"))
    }

    open fun serialize(Ar: FAssetArchiveWriter) {
        Ar.writeFName(name)
        typeDef.serialize(Ar)
    }

    constructor(name: FName, typeDef: FStructFallback) {
        this.name = name
        this.typeDef = typeDef
    }
}