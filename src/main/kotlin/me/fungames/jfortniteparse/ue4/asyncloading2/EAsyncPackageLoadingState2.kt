package me.fungames.jfortniteparse.ue4.asyncloading2

enum class EAsyncPackageLoadingState2 {
    NewPackage,
    ImportPackages,
    ImportPackagesDone,
    WaitingForIo,
    ProcessPackageSummary,
    ProcessExportBundles,
    WaitingForExternalReads,
    ExportsDone,
    PostLoad,
    DeferredPostLoad,
    DeferredPostLoadDone,
    Finalize,
    CreateClusters,
    Complete,
    DeferredDelete
}