package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.MarshalledVFXAuthoredData;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.UObject;

public class MarshalledVFX_AuthoredDataConfig extends UObject {
    @UProperty(skipPrevious = 1)
    // TODO this is used as a superclass for BlueprintGeneratedClass, find a way to read variables of a serialized UClass
    public MarshalledVFXAuthoredData Data;
}
