package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.fort.enums.EFXType;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;
import java.util.Map;

@UStruct
public class MarshalledVFXAuthoredData {
    public List<MarshalledVFXData> NiagaraVFX;
    public List<MarshalledVFXData> CascadeVFX;
    public Map<FName, ParameterNameMapping> NameReplacements;

    @UStruct
    public static class MarshalledVFXData {
        public FGameplayTagContainer ParameterGroups;
        public EFXType Type;
        public FSoftObjectPath Asset;
        public FName AttachAtBone;
        public FTransform RelativeOffset;
        public FGameplayTag EffectIdTag;
        public Boolean bAutoActivate;
    }

    @UStruct
    public static class ParameterNameMapping {
        public FName CascadeName;
        public FName NiagaraName;
    }
}
