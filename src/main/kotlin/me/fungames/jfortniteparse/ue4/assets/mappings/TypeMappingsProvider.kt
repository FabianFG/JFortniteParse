package me.fungames.jfortniteparse.ue4.assets.mappings

abstract class TypeMappingsProvider {
    val mappings = TypeMappings()
    abstract fun reload(): Boolean
}