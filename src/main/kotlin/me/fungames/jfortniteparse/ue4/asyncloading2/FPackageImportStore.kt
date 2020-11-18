package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

class FPackageImportStore {
    val globalPackageStore: FPackageStore
    val globalImportStore: FGlobalImportStore
    val desc: FAsyncPackageDesc2
    val importMap = mutableListOf<FPackageObjectIndex>()

    constructor(globalPackageStore: FPackageStore, desc: FAsyncPackageDesc2) {
        this.globalPackageStore = globalPackageStore
        globalImportStore = globalPackageStore.importStore
        this.desc = desc
        addPackageReferences()
    }

    fun destroy() {
        check(importMap.isEmpty())
        releasePackageReferences()
    }

    inline fun isValidLocalImportIndex(localIndex: FPackageIndex): Boolean {
        check(importMap.isNotEmpty())
        return localIndex.isImport() && localIndex.toImport() < importMap.size
    }

    inline fun findOrGetImportObjectFromLocalIndex(localIndex: FPackageIndex): UObject? {
        check(localIndex.isImport())
        check(importMap.isNotEmpty())
        val localImportIndex = localIndex.toImport()
        check(localImportIndex < importMap.size)
        val globalIndex = importMap[localImportIndex]
        var obj: UObject? = null
        if (globalIndex.isImport()) {
            obj = globalImportStore.findOrGetImportObject(globalIndex)
        } else {
            check(globalIndex.isNull())
        }
        return obj
    }

    inline fun findOrGetImportObject(globalIndex: FPackageObjectIndex): UObject? {
        check(globalIndex.isImport())
        return globalImportStore.findOrGetImportObject(globalIndex)
    }

    /*fun GetUnresolvedCDOs(TArray<UClass*, TInlineAllocator<8>>& Classes):Boolean {
        for (const FPackageObjectIndex& Index : importMap)
        {
            if (!Index.IsScriptImport())
            {
                continue;
            }

            UObject* Object = globalImportStore.FindScriptImportObjectFromIndex(Index);
            if (Object)
            {
                continue;
            }

            const FScriptObjectEntry* Entry = globalImportStore.ScriptObjectEntriesMap.FindRef(Index);
            check(Entry);
            const FPackageObjectIndex& CDOClassIndex = Entry->CDOClassIndex;
            if (CDOClassIndex.IsScriptImport())
            {
                UObject* CDOClassObject = globalImportStore.FindScriptImportObjectFromIndex(CDOClassIndex);
                if (CDOClassObject)
                {
                    UClass* CDOClass = static_cast<UClass*>(CDOClassObject);
                    Classes.AddUnique(CDOClass);
                }
            }
        }
        return Classes.Num() > 0;
    }*/

    inline fun storeGlobalObject(packageId: FPackageId, globalIndex: FPackageObjectIndex, obj: UObject) {
        globalImportStore.storeGlobalObject(packageId, globalIndex, obj)
    }

    /*private fun addAsyncFlags(ImportedPackage: UPackage) {
        // const FPackageStoreEntry& ImportEntry = globalPackageStore.GetStoreEntry(InPackageId);
        // UObject* ImportedPackage = StaticFindObjectFast(UPackage::StaticClass(), nullptr, MinimalNameToName(ImportEntry.Name), true);

        if (GUObjectArray.IsDisregardForGC(ImportedPackage))
        {
            // UE_LOG(LogStreaming, Display, TEXT("Skipping AddAsyncFlags for persistent package: %s"), *ImportedPackage->GetPathName());
            return;
        }
        ForEachObjectWithOuter(ImportedPackage, [](UObject* Object)
        {
            if (Object->HasAllFlags(RF_Public | RF_WasLoaded))
            {
                checkf(!Object->HasAnyInternalFlags(EInternalObjectFlags::Async), TEXT("%s"), *Object->GetFullName());
                Object->SetInternalFlags(EInternalObjectFlags::Async);
            }
        }, *//* bIncludeNestedObjects*//* true);
        checkf(!ImportedPackage->HasAnyInternalFlags(EInternalObjectFlags::Async), TEXT("%s"), *ImportedPackage->GetFullName());
        ImportedPackage->SetInternalFlags(EInternalObjectFlags::Async);
    }

    private fun clearAsyncFlags(ImportedPackage: UPackage) {
        // const FPackageStoreEntry& ImportEntry = globalPackageStore.GetStoreEntry(InPackageId);
        // UObject* ImportedPackage = StaticFindObjectFast(UPackage::StaticClass(), nullptr, MinimalNameToName(ImportEntry.Name), true);

        if (GUObjectArray.IsDisregardForGC(ImportedPackage))
        {
            // UE_LOG(LogStreaming, Display, TEXT("Skipping ClearAsyncFlags for persistent package: %s"), *ImportedPackage->GetPathName());
            return;
        }
        ForEachObjectWithOuter(ImportedPackage, [](UObject* Object)
        {
            if (Object->HasAllFlags(RF_Public | RF_WasLoaded))
            {
                checkf(Object->HasAnyInternalFlags(EInternalObjectFlags::Async), TEXT("%s"), *Object->GetFullName());
                Object->AtomicallyClearInternalFlags(EInternalObjectFlags::Async);
            }
        }, *//* bIncludeNestedObjects*//* true);
        checkf(ImportedPackage->HasAnyInternalFlags(EInternalObjectFlags::Async), TEXT("%s"), *ImportedPackage->GetFullName());
        ImportedPackage->AtomicallyClearInternalFlags(EInternalObjectFlags::Async);
    }*/

    private fun addPackageReferences() {
        for (importedPackageId in desc.storeEntry!!.importedPackages) {
            val packageRef = globalPackageStore.loadedPackageStore.getOrPut(importedPackageId) { FLoadedPackageRef() }
            /*if (packageRef.addRef()) {
                addAsyncFlags(packageRef.`package`)
            }*/
        }
        if (desc.canBeImported()) {
            val packageRef = globalPackageStore.loadedPackageStore.getOrPut(desc.diskPackageId) { FLoadedPackageRef() }
            /*if (packageRef.addRef()) {
                addAsyncFlags(packageRef.`package`)
            }*/
        }
    }

    private fun releasePackageReferences() {
        for (importedPackageId in desc.storeEntry!!.importedPackages) {
            val packageRef = globalPackageStore.loadedPackageStore.getOrPut(importedPackageId) { FLoadedPackageRef() }
            /*if (packageRef.releaseRef(desc.diskPackageId, importedPackageId)) {
                clearAsyncFlags(packageRef.`package`)
            }*/
        }
        if (desc.canBeImported()) {
            // clear own reference, and possible all async flags if no remaining ref count
            val packageRef = globalPackageStore.loadedPackageStore.getOrPut(desc.diskPackageId) { FLoadedPackageRef() }
            /*if (packageRef.releaseRef(desc.diskPackageId, desc.diskPackageId)) {
                clearAsyncFlags(packageRef.`package`)
            }*/
        }
    }
}