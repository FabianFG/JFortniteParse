package me.fungames.jfortniteparse.ue4.assets.exports.valorant

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FSoftObjectPath

@ExperimentalUnsignedTypes
class CharacterDataAsset : UExport {
    override var baseObject: UObject
    var characterID : UByte?
    var character : FSoftObjectPath
    var uiData : FSoftObjectPath
    var role : FSoftObjectPath
    var characterSelectFXC : FSoftObjectPath
    var developerName : String
    var isPlayableCharacter : Boolean
    var availableForTest : Boolean
    var uuid : FGuid
    var baseContent : Boolean

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        characterID = baseObject.getOrNull("CharacterID")
        character = baseObject.get("Character")
        uiData = baseObject.get("UIData")
        role = baseObject.get("Role")
        characterSelectFXC = baseObject.get("CharacterSelectFXC")
        developerName = baseObject.get<FName>("DeveloperName").text
        isPlayableCharacter = baseObject.getOrNull("bIsPlayableCharacter") ?: false
        availableForTest = baseObject.getOrNull("bAvailableForTest") ?: false
        uuid = baseObject.get("Uuid")
        baseContent = baseObject.getOrNull("bBaseContent") ?: false
        super.complete(Ar)
    }

    constructor(
        exportType: String,
        baseObject: UObject,
        characterID: UByte,
        character: FSoftObjectPath,
        uiData: FSoftObjectPath,
        role: FSoftObjectPath,
        characterSelectFXC: FSoftObjectPath,
        developerName: String,
        isPlayableCharacter: Boolean,
        availableForTest: Boolean,
        uuid: FGuid,
        baseContent: Boolean
    ) : super(exportType) {
        this.baseObject = baseObject
        this.characterID = characterID
        this.character = character
        this.uiData = uiData
        this.role = role
        this.characterSelectFXC = characterSelectFXC
        this.developerName = developerName
        this.isPlayableCharacter = isPlayableCharacter
        this.availableForTest = availableForTest
        this.uuid = uuid
        this.baseContent = baseContent
    }


    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}