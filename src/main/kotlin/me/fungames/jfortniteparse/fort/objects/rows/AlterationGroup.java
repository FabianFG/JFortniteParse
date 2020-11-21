package me.fungames.jfortniteparse.fort.objects.rows;

import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase;

import java.util.List;
import java.util.Map;

public class AlterationGroup extends FTableRowBase {
    public Map<EFortRarity, AlterationWeightSet> RarityMapping;

    @UStruct
    public static class AlterationWeightSet {
        public List<AlterationWeightData> WeightData;
    }

    @UStruct
    public static class AlterationWeightData {
        public String AID;
        public Integer InitialRollWeight;
        public List<String> ExclusionNames;
    }
}
