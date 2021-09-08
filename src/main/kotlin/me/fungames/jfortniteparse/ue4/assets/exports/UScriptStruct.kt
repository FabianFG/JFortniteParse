package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.unprefix
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@OnlyAnnotated
open class UScriptStruct : UStruct {
    var structClass: Class<*>? = null
        set(value) {
            field = value
            if (superStruct != null) {
                return
            }
            val superclass = value?.superclass
            if (superclass != null && superclass != UObject::class.java) {
                superStruct = lazy { UScriptStruct(superclass) }
            }
        }
    var useClassProperties = false

    @JvmOverloads
    constructor(name: FName = FName.NAME_None) : super() {
        this.name = name.text
    }

    @JvmOverloads
    constructor(clazz: Class<*>?, name: FName = clazz?.let { FName(it.simpleName.unprefix()) } ?: FName.NAME_None) : this(name) {
        structClass = clazz
        useClassProperties = true
    }
}