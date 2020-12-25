package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;

public class FortBadgeScoringData extends FTableRowBase {
    public int ScoreAwarded;
    public int MissionPoints;
    public EStatCategory ScoreCategory;
    public int ScoreThreshold;

    public enum EStatCategory {
        Combat,
        Building,
        Utility,
        Max_None
    }
}
