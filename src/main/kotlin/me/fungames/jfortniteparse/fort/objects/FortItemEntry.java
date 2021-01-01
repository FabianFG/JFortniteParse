package me.fungames.jfortniteparse.fort.objects;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.exports.FortAlterationItemDefinition;
import me.fungames.jfortniteparse.fort.exports.FortHeroType;
import me.fungames.jfortniteparse.fort.exports.FortItemDefinition;
import me.fungames.jfortniteparse.fort.exports.actors.BuildingGameplayActor;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortItemEntry {
    public Integer Count;
    public Integer PreviousCount;
    public Lazy<FortItemDefinition> ItemDefinition;
    public Short OrderIndex;
    public Float Durability;
    public Integer Level;
    public Integer LoadedAmmo;
    public Integer PhantomReserveAmmo;
    public List<String> AlterationDefinitions;
    public List<FortSavedWeaponModSlot> SavedWeaponModSlots;
    public String ItemSource;
    public FGuid ItemGuid;
    public Integer ControlOverride;
    public Boolean inventory_overflow_date;
    public Boolean bWasGifted;
    public Boolean bIsReplicatedCopy;
    public Boolean bIsDirty;
    public Boolean bUpdateStatsOnCollection;
    public FortGiftingInfo GiftingInfo;
    //public List<FortItemEntryStateValue> StateValues; // Transient
    @UProperty(skipPrevious = 1)
    public FPackageIndex /*WeakObjectProperty FortInventory*/ ParentInventory;
    public BuildingGameplayActor.FGameplayAbilitySpecHandle GameplayAbilitySpecHandle;
    public List<Lazy<FortAlterationItemDefinition>> AlterationInstances;
    //public List<FFortWeaponModSlot> WeaponModSlots; // Transient
    @UProperty(skipPrevious = 1)
    public FSoftObjectPath WrapOverride;
    public List<Float> GenericAttributeValues;
    public Integer PickupVariantIndex;
    public Integer ItemVariantDataMappingIndex;

    @UStruct
    public static class FortSavedWeaponModSlot {
        public String WeaponModTemplateID;
        public Boolean bIsDynamic;
    }

    @UStruct
    public static class FortGiftingInfo {
        public String PlayerName;
        public Lazy<FortHeroType> HeroType;
    }
}
