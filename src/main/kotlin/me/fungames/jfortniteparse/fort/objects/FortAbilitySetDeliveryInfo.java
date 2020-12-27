package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

@UStruct
public class FortAbilitySetDeliveryInfo {
    public FortDeliveryInfoRequirementsFilter DeliveryRequirements;
    public List<FSoftObjectPath> AbilitySets;
}
