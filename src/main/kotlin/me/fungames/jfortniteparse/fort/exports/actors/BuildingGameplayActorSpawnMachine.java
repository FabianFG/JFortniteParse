package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

public class BuildingGameplayActorSpawnMachine extends BuildingGameplayActor {
    public FPackageIndex /*FortPlayerStart*/ ResurrectLocation;
    //public List<FUniqueNetIdRepl> PlayerIdsForResurrection;
    @UProperty(skipPrevious = 1)
    public FPackageIndex /*WeakObjectProperty FortPlayerControllerAthena*/ InstigatorPC;
    public UByte SquadId;
    public UByte ActiveTeam;
    public ESpawnMachineState SpawnMachineState;
    public FScalableFloat ResurrectionStartDelay;
    public FScalableFloat ResurrectionNextPlayerDelay;
    public FPackageIndex /*SoundCue*/ InteractSoundCueLoop;
    public FScalableFloat CooldownLengthRow;
    public FText InteractNoCardsSubText;
    public FText InteractNoVanLockSubText;
    public FLinearColor InteractSubTextColor;
    public ESpawnMachineSubTextState SpawnMachineSubTextState;
    public Integer HandleIntoGameState;

    public enum ESpawnMachineState {
        Default,
        WaitingForUse,
        Active,
        Complete,
        OnCooldown
    }

    public enum ESpawnMachineSubTextState {
        NoCards,
        VanInUse,
        None
    }
}
