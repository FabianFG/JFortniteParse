package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;

public class FortWeaponDurabilityByRarityStats extends FTableRowBase {
    public int Common;
    public int Uncommon;
    public int Rare;
    public int Epic;
    public int Legendary;
    public int Mythic;
    public int Transcendent;
    public int Unattainable;

    public int get(EFortRarity rarity) {
        switch (rarity) {
            case Common:
                return Common;
            case Uncommon:
                return Uncommon;
            case Rare:
                return Rare;
            case Epic:
                return Epic;
            case Legendary:
                return Legendary;
            case Mythic:
                return Mythic;
            case Transcendent:
                return Transcendent;
            case Unattainable:
                return Unattainable;
            default:
                return -1;
        }
    }
}
