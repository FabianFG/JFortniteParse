package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

@UStruct
public class FortChallengeSetStyle {
    public FLinearColor PrimaryColor;
    public FLinearColor SecondaryColor;
    public FLinearColor AccentColor;
    public FLinearColor Context_LimitedTimeColor;
    public FLinearColor Context_BaseColor;
    public FSoftObjectPath DisplayImage;
    public FSoftObjectPath CustomBackground;
}
