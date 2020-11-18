package me.fungames.jfortniteparse.ue4.assets.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector4;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class UPaperSprite extends UObject {
    public List<FPackageIndex> AdditionalSourceTextures;
    public FVector2D BakedSourceUV;
    public FVector2D BakedSourceDimension;
    public FPackageIndex BakedSourceTexture;
    public FPackageIndex DefaultMaterial;
    public FPackageIndex AlternateMaterial;
    public List<FPaperSpriteSocket> Sockets;
    public ESpriteCollisionMode SpriteCollisionDomain;
    public float PixelsPerUnrealUnit;
    public FPackageIndex BodySetup;
    public int AlternateMaterialSplitIndex;
    public List<FVector4> BakedRenderData;

    @UStruct
    public static class FPaperSpriteSocket {
        public FTransform LocalTransform;
        public FName SocketName;
    }

    public enum ESpriteCollisionMode {
        None,
        Use2DPhysics,
        Use3DPhysics
    }
}
