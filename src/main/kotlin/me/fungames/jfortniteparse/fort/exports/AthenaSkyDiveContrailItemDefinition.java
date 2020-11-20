package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class AthenaSkyDiveContrailItemDefinition extends AthenaCosmeticItemDefinition {
    public FSoftObjectPath ContrailEffect;
    public FSoftObjectPath FrontEndContrailEffect;
    public FSoftObjectPath NiagaraContrailEffect;
    public FVector DefaultVelocityVector;
    public FName VelocityVectorParameterName;
    public FName ParaGlideLeanParameterName;
    public List<VectorParticleParameter> VectorParameters;
    public List<FloatParticleParameter> FloatParameters;

    @UStruct
    public static class VectorParticleParameter {
        public FVector Value;
        public FName ParameterName;
    }

    @UStruct
    public static class FloatParticleParameter {
        public Float Value;
        public FName ParameterName;
    }
}
