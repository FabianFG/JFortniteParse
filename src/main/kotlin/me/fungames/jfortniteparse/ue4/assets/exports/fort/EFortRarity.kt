package me.fungames.jfortniteparse.ue4.assets.exports.fort

enum class EFortRarity(val rarityName : String) {

    Masterwork("Transcendent"),
    Transcendent("Transcendent"),

    Elegant("Mythic"),
    Mythic("Mythic"),

    Fine("Legendary"),
    Legendary("Legendary"),

    Quality("Epic"),
    Epic("Epic"),

    Sturdy("Rare"),
    Rare("Rare"),

    Uncommon("Uncommon"),

    Handmade("Common"),
    Common("Common");

    companion object {
        fun getEnum(rarity: String?) : EFortRarity {
            if (rarity == null)
                return Uncommon
            val name = rarity.substringAfterLast("EFortRarity::")
            values().forEach {
                if (name == it.name)
                    return it
            }
            return Common
        }
    }
}