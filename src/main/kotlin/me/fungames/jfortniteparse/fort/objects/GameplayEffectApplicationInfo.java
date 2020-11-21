package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

@UStruct
public class GameplayEffectApplicationInfo {
    public FSoftObjectPath GameplayEffect;
    public Float Level;
}
