package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.versions.FInstancedStructCustomVersion

class FInstancedStruct {
	companion object {
		private val NAME_StructProperty = FName("StructProperty")
	}

	val struct: UScriptStruct?

	constructor(Ar: FAssetArchive) {
		if (FInstancedStructCustomVersion.get(Ar) < FInstancedStructCustomVersion.CustomVersionAdded) {
			val version = Ar.readUInt8()
		}

		val structType = FPackageIndex(Ar)
		val serialSize = Ar.readInt32()

		struct = if (structType.isNull() && serialSize > 0) {
			Ar.skip(serialSize.toLong())
			null
		} else {
			UScriptStruct(Ar, PropertyType(NAME_StructProperty).apply {
				structName = structType.name
				structClass = Ar.provider?.mappingsProvider?.let { lazy { it.getStruct(structName) } }
			})
		}
	}

	constructor(struct: UScriptStruct?) {
		this.struct = struct
	}
}