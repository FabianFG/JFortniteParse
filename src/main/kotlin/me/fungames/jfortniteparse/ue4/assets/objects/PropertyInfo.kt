package me.fungames.jfortniteparse.ue4.assets.objects

import com.google.gson.JsonObject
import me.fungames.jfortniteparse.ue4.assets.UProperty
import java.lang.reflect.Field

class PropertyInfo {
    var index = 0
    var name: String
    var type = PropertyType()
    var arrayDim = 1

    var field: Field? = null

    constructor(name: String, type: PropertyType, arrayDim: Int) {
        this.name = name
        this.type = type
        this.arrayDim = arrayDim
    }

    constructor(json: JsonObject) {
        index = json["index"].asInt
        name = json["name"].asString
        type = PropertyType(json)
        json["arraySize"]?.run { arrayDim = asInt }
    }

    constructor(field: Field, ann: UProperty?) {
        this.field = field
        type = PropertyType()
        var name: String? = null
        if (ann != null) {
            name = ann.name.takeIf { it.isNotEmpty() }
            arrayDim = ann.arrayDim
            type.isEnumAsByte = ann.isEnumAsByte
        }
        this.name = name ?: field.name
        type.setupWithField(field)
    }
}