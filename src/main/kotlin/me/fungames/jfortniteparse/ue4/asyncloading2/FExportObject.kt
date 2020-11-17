package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.exports.UObject

class FExportObject {
    var exportObject: UObject? = null
    var templateObject: UObject? = null
    var superObject: UObject? = null
    var bFiltered = false
    var bExportLoadFailed = false
    // custom
    lateinit var exportMapEntry: FExportMapEntry
}