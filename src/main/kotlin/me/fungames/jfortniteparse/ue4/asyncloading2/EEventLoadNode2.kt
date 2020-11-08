package me.fungames.jfortniteparse.ue4.asyncloading2

enum class EEventLoadNode2(val value: Int) {
    Package_ProcessSummary(0),
    Package_ExportsSerialized(1),
    Package_NumPhases(2),

    ExportBundle_Process(0),
    ExportBundle_PostLoad(1),
    ExportBundle_DeferredPostLoad(2),
    ExportBundle_NumPhases(3)
}