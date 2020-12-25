package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortConditionalResourceItemDefinition extends FortPersistentResourceItemDefinition {
    public EFortConditionalResourceItemTest Condition;
    public FSoftObjectPath PassedConditionItem;
    public FSoftObjectPath FailedConditionItem;

    public enum EFortConditionalResourceItemTest {
        CanEarnMtx
    }
}
