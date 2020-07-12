package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.pak.PakFileReader

@Suppress("EXPERIMENTAL_API_USAGE")
abstract class PakFileProvider : AbstractFileProvider() {

    abstract fun requiredKeys() : List<FGuid>
    fun submitKey(guid : FGuid, key : String) = submitKeys(mapOf(guid to key))
    abstract fun submitKeys(keys : Map<FGuid, String>) : Int
    abstract fun unloadedPaks() : List<PakFileReader>
    abstract fun mountedPaks() : List<PakFileReader>
}