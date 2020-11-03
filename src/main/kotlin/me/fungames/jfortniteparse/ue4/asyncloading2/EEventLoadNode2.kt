package me.fungames.jfortniteparse.ue4.asyncloading2

enum class EEventLoadNode2(val value: UInt) {
    Package_ProcessSummary(0u),
    Package_ExportsSerialized(1u),
    Package_NumPhases(2u),

    ExportBundle_Process(0u),
    ExportBundle_PostLoad(1u),
    ExportBundle_DeferredPostLoad(2u),
    ExportBundle_NumPhases(3u)
}