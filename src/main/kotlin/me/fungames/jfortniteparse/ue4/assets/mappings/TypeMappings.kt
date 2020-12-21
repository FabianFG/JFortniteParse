package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.ue4.assets.exports.UStruct

class TypeMappings(var types: MutableMap<String, UStruct> = mutableMapOf(),
                   var enums: MutableMap<String, List<String>> = mutableMapOf())