package me.fungames.jfortniteparse.ue4.objects.uobject

/**
 * Flags associated with each property in a class, overriding the
 * property's default behavior.
 * @warning When adding one here, please update ParsePropertyFlags()
 */
enum class EPropertyFlags(val value: ULong) {
    CPF_None(0u),

    CPF_Edit							(0x0000000000000001u),	///< Property is user-settable in the editor.
    CPF_ConstParm						(0x0000000000000002u),	///< This is a constant function parameter
    CPF_BlueprintVisible				(0x0000000000000004u),	///< This property can be read by blueprint code
    CPF_ExportObject					(0x0000000000000008u),	///< Object can be exported with actor.
    CPF_BlueprintReadOnly				(0x0000000000000010u),	///< This property cannot be modified by blueprint code
    CPF_Net								(0x0000000000000020u),	///< Property is relevant to network replication.
    CPF_EditFixedSize					(0x0000000000000040u),	///< Indicates that elements of an array can be modified, but its size cannot be changed.
    CPF_Parm							(0x0000000000000080u),	///< Function/When call parameter.
    CPF_OutParm							(0x0000000000000100u),	///< Value is copied out after function call.
    CPF_ZeroConstructor					(0x0000000000000200u),	///< memset is fine for construction
    CPF_ReturnParm						(0x0000000000000400u),	///< Return value.
    CPF_DisableEditOnTemplate			(0x0000000000000800u),	///< Disable editing of this property on an archetype/sub-blueprint
    //CPF_      						(0x0000000000001000u),	///<
    CPF_Transient						(0x0000000000002000u),	///< Property is transient: shouldn't be saved or loaded, except for Blueprint CDOs.
    CPF_Config							(0x0000000000004000u),	///< Property should be loaded/saved as permanent profile.
    //CPF_								(0x0000000000008000u),	///<
    CPF_DisableEditOnInstance			(0x0000000000010000u),	///< Disable editing on an instance of this class
    CPF_EditConst						(0x0000000000020000u),	///< Property is uneditable in the editor.
    CPF_GlobalConfig					(0x0000000000040000u),	///< Load config from base class, not subclass.
    CPF_InstancedReference				(0x0000000000080000u),	///< Property is a component references.
    //CPF_								(0x0000000000100000u),	///<
    CPF_DuplicateTransient				(0x0000000000200000u),	///< Property should always be reset to the default value during any type of duplication (copy/paste, binary duplication, etc.)
    //CPF_								(0x0000000000400000u),	///<
    //CPF_    							(0x0000000000800000u),	///<
    CPF_SaveGame						(0x0000000001000000u),	///< Property should be serialized for save games, this is only checked for game-specific archives with ArIsSaveGame
    CPF_NoClear							(0x0000000002000000u),	///< Hide clear (and browse) button.
    //CPF_  							(0x0000000004000000u),	///<
    CPF_ReferenceParm					(0x0000000008000000u),	///< Value is passed by reference; CPF_OutParam and CPF_Param should also be set.
    CPF_BlueprintAssignable				(0x0000000010000000u),	///< MC Delegates only.  Property should be exposed for assigning in blueprint code
    CPF_Deprecated						(0x0000000020000000u),	///< Property is deprecated.  Read it from an archive, but don't save it.
    CPF_IsPlainOldData					(0x0000000040000000u),	///< If this is set, then the property can be memcopied instead of CopyCompleteValue / CopySingleValue
    CPF_RepSkip							(0x0000000080000000u),	///< Not replicated. For non replicated properties in replicated structs
    CPF_RepNotify						(0x0000000100000000u),	///< Notify actors when a property is replicated
    CPF_Interp							(0x0000000200000000u),	///< interpolatable property for use with matinee
    CPF_NonTransactional				(0x0000000400000000u),	///< Property isn't transacted
    CPF_EditorOnly						(0x0000000800000000u),	///< Property should only be loaded in the editor
    CPF_NoDestructor					(0x0000001000000000u),	///< No destructor
    //CPF_								(0x0000002000000000u),	///<
    CPF_AutoWeak						(0x0000004000000000u),	///< Only used for weak pointers, means the export type is autoweak
    CPF_ContainsInstancedReference		(0x0000008000000000u),	///< Property contains component references.
    CPF_AssetRegistrySearchable			(0x0000010000000000u),	///< asset instances will add properties with this flag to the asset registry automatically
    CPF_SimpleDisplay					(0x0000020000000000u),	///< The property is visible by default in the editor details view
    CPF_AdvancedDisplay					(0x0000040000000000u),	///< The property is advanced and not visible by default in the editor details view
    CPF_Protected						(0x0000080000000000u),	///< property is protected from the perspective of script
    CPF_BlueprintCallable				(0x0000100000000000u),	///< MC Delegates only.  Property should be exposed for calling in blueprint code
    CPF_BlueprintAuthorityOnly			(0x0000200000000000u),	///< MC Delegates only.  This delegate accepts (only in blueprint) only events with BlueprintAuthorityOnly.
    CPF_TextExportTransient				(0x0000400000000000u),	///< Property shouldn't be exported to text format (e.g. copy/paste)
    CPF_NonPIEDuplicateTransient		(0x0000800000000000u),	///< Property should only be copied in PIE
    CPF_ExposeOnSpawn					(0x0001000000000000u),	///< Property is exposed on spawn
    CPF_PersistentInstance				(0x0002000000000000u),	///< A object referenced by the property is duplicated like a component. (Each actor should have an own instance.)
    CPF_UObjectWrapper					(0x0004000000000000u),	///< Property was parsed as a wrapper class like TSubclassOf<T>, FScriptInterface etc., rather than a USomething*
    CPF_HasGetValueTypeHash				(0x0008000000000000u),	///< This property can generate a meaningful hash value.
    CPF_NativeAccessSpecifierPublic		(0x0010000000000000u),	///< Public native access specifier
    CPF_NativeAccessSpecifierProtected	(0x0020000000000000u),	///< Protected native access specifier
    CPF_NativeAccessSpecifierPrivate	(0x0040000000000000u),	///< Private native access specifier
    CPF_SkipSerialization				(0x0080000000000000u),	///< Property shouldn't be serialized, can still be exported to text
}