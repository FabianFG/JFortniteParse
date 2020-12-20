package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FDateTime;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortDailyRewardScheduleDefinitions extends UDataAsset {
    public List<FortDailyRewardScheduleDefinition> Schedules;

    @UStruct
    public static class FortDailyRewardScheduleDefinition {
        public FName ScheduleName;
        public FSoftObjectPath EnablingToken;
        public Lazy<UDataTable> Rewards;
        public FortDailyRewardScheduleDisplayData DisplayData;
        public FDateTime BeginDate;
        public FDateTime EndDate;
    }

    @UStruct
    public static class FortDailyRewardScheduleDisplayData {
        public FText Title;
        public FText Description;
        public FText ItemDescription;
        public FText EpicItemDescription;
    }
}
