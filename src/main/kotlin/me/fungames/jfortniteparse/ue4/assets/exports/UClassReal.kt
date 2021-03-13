package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@OnlyAnnotated
open class UClassReal : UStruct() {
    /** Used to check if the class was cooked or not */
    var bCooked = false

    /** Class flags; See EClassFlags for more information */
    var classFlags = 0u

    /** The required type for the outer of instances of this class */
    lateinit var classWithin: FPackageIndex

    /** This is the blueprint that caused the generation of this class, or null if it is a native compiled-in class */
    lateinit var classGeneratedBy: FPackageIndex

    /** Which Name.ini file to load Config variables out of */
    lateinit var classConfigName: FName

    /** The class default object; used for delta serialization and object initialization */
    lateinit var classDefaultObject: FPackageIndex

    /** Map of all functions by name contained in this class */
    lateinit var funcMap: MutableMap<FName, Lazy<UFunction>?>

    /**
     * The list of interfaces which this class implements, along with the pointer property that is located at the offset of the interface's vtable.
     * If the interface class isn't native, the property will be null.
     */
    lateinit var interfaces: Array<FImplementedInterface>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)

        // serialize the function map
        funcMap = Ar.readTMap { Ar.readFName() to Ar.readObject() }

        // Class flags first.
        classFlags = Ar.readUInt32()

        // Variables.
        classWithin = FPackageIndex(Ar)
        classConfigName = Ar.readFName()

        classGeneratedBy = FPackageIndex(Ar)

        // Load serialized interface classes
        interfaces = Ar.readTArray { FImplementedInterface(Ar) }

        val bDeprecatedScriptOrder = Ar.readBoolean()
        val dummy = Ar.readFName()

        if (Ar.ver >= 241 /*VER_UE4_ADD_COOKED_TO_UCLASS*/) {
            bCooked = Ar.readBoolean()
        }

        // Defaults.
        classDefaultObject = FPackageIndex(Ar)
    }

    class FImplementedInterface(
        /** the interface class */
        var clazz: FPackageIndex,
        /** the pointer offset of the interface's vtable */
        var pointerOffset: Int,
        /** whether or not this interface has been implemented via K2 */
        var bImplementedByK2: Boolean
    ) {
        constructor(Ar: FAssetArchive) : this(FPackageIndex(Ar), Ar.readInt32(), Ar.readBoolean())
    }
}