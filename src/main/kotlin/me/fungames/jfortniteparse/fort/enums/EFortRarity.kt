package me.fungames.jfortniteparse.fort.enums

import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText

@ExperimentalUnsignedTypes
enum class EFortRarity(val rarityName: FText) {
    Common(FText("Fort.Rarity", "Common", "Common")),
    Uncommon(FText("Fort.Rarity", "Uncommon", "Uncommon")), // Default
    Rare(FText("Fort.Rarity", "Rare", "Rare")),
    Epic(FText("Fort.Rarity", "Epic", "Epic")),
    Legendary(FText("Fort.Rarity", "Legendary", "Legendary")),
    Mythic(FText("Fort.Rarity", "Mythic", "Mythic")),
    Transcendent(FText("Fort.Rarity", "Transcendent", "Transcendent")),
    Unattainable(FText("Fort.Rarity", "Unattainable", "Unattainable"))
}