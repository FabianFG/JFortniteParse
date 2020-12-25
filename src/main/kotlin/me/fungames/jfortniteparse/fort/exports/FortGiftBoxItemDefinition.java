package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.ESubGame;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortGiftBoxItemDefinition extends FortAccountItemDefinition {
    public ESubGame RestrictToSubgame;
    public EFortGiftWrapType GiftWrapType;
    public FText ViolatorText;
    public FText DefaultHeaderText;
    public FText SubHeaderText;
    public FText DefaultBodyText;
    public FSoftObjectPath ItemDisplayAsset;
    public Integer SortPriority;
    public Boolean bReuseExistingBoxIfPossible;
    public Boolean bOverrideBackgroundMessage;
    public FText /*SoftClassPath*/ BackgroundMessageText;
    public FSoftObjectPath /*SoftClassPath*/ GiftBoxPreMessageWidgetRef;
    public FSoftObjectPath /*SoftClassPath*/ GiftBoxHeaderSubWidgetRef;
    public FSoftObjectPath /*SoftClassPath*/ CustomLeftsideContent;

    public enum EFortGiftWrapType {
        System,
        UserFree,
        UserUnlock,
        UserConsumable,
        Message,
        Ungift
    }
}
