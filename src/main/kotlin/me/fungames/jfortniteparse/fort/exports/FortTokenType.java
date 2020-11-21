package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortTokenType extends FortAccountItemDefinition {
    public FSoftObjectPath /*SoftClassPath*/ ScriptedAction;
    public FSlateColor NodeTintColour;
    public Boolean bPercentageRepresentation;
    public EItemProfileType ProfileType;
    public FSoftObjectPath /*SoftClassPath*/ ItemPreviewActorClass;

    public enum EItemProfileType {
        Common,
        Campaign,
        Athena
    }
}
