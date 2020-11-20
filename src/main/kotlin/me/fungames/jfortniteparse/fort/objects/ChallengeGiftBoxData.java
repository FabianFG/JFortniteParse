package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class ChallengeGiftBoxData {
    public FSoftObjectPath GiftBoxToUse;
    public List<FortGiftBoxFortmatData> GiftBoxFormatData;

    @UStruct
    public static class FortGiftBoxFortmatData {
        public String StringAssetType;
        public String StringData;
    }
}
