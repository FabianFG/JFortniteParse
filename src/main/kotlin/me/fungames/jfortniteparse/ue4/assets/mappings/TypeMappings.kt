package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.ue4.assets.objects.Struct

class TypeMappings(var types: MutableMap<String, Struct> = mutableMapOf(),
                   var enums: MutableMap<String, List<String>> = mutableMapOf())