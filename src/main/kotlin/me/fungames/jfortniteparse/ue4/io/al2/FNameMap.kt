package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
class FNameMap {
    private val nameEntries = mutableListOf<FNameEntryId>()
    private var nameMapType = FMappedName.EType.Global

    fun loadGlobal(ioDispatcher: FIoDispatcher) {
        check(nameEntries.isEmpty())

        val namesId = createIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)
        val hashesId = createIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)

        val batch = ioDispatcher.newBatch()
        val nameRequest = batch.read(namesId, FIoReadOptions())
        val hashRequest = batch.read(hashesId, FIoReadOptions())
        batch.issue(EIoDispatcherPriority.IoDispatcherPriority_High)

        /*reserveNameBatch(
            ioDispatcher.getSizeForChunk(namesId),
            ioDispatcher.getSizeForChunk(hashesId))*/

        batch.waitRequests()

        val nameBuffer = nameRequest.getResultOrThrow()
        val hashBuffer = hashRequest.getResultOrThrow()

        load(nameBuffer.asArray(), hashBuffer.asArray(), FMappedName.EType.Global)

        ioDispatcher.freeBatch(batch)
    }

    fun size() = nameEntries.size

    fun load(nameBuffer: ByteArray, hashBuffer: ByteArray, nameMapType: FMappedName.EType) {
        loadNameBatch(nameEntries, nameBuffer, hashBuffer)
        this.nameMapType = nameMapType
    }

    fun getName(mappedName: FMappedName): FName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        val nameEntry = nameEntries[mappedName.getIndex().toInt()]
        // return FName.createFromDisplayId(nameEntry, mappedName.getNumber())
        return FName.dummy("TODO")
    }

    fun tryGetName(mappedName: FMappedName): FName? {
        check(mappedName.getType() == nameMapType)
        val index = mappedName.getIndex()
        if (index < nameEntries.size.toUInt()) {
            val nameEntry = nameEntries[mappedName.getIndex().toInt()]
            // return FName.createFromDisplayId(nameEntry, mappedName.getNumber())
            return FName.dummy("TODO")
        }
        return null
    }

    fun getMinimalName(mappedName: FMappedName): FMinimalName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        val nameEntry = nameEntries[mappedName.getIndex().toInt()]
        return FMinimalName(nameEntry, mappedName.number.toInt())
    }
}

// TODO move to UnrealNames
fun loadNameBatch(OutNames: MutableList<FNameEntryId>, NameData: ByteArray, HashData: ByteArray) {
    /*check(IsAligned(NameData.GetData(), sizeof(uint64)))
    check(IsAligned(HashData.GetData(), sizeof(uint64)))
    check(IsAligned(HashData.Num(), sizeof(uint64)))
    check(HashData.size > 0)

    const uint8* NameIt = NameData.GetData()
    const uint8* NameEnd = NameData.GetData() + NameData.Num()

    const uint64* HashDataIt = reinterpret_cast<const uint64*>(HashData.GetData())
    uint64 HashVersion = INTEL_ORDER64(HashDataIt[0])
    TArrayView<const uint64> Hashes = MakeArrayView(HashDataIt + 1, HashData.Num() / sizeof(uint64) - 1)

    OutNames.Empty(Hashes.Num())

//    GetNamePoolPostInit().BatchLock()

    if (HashVersion == FNameHash::AlgorithmId)
    {
        for (uint64 Hash : Hashes)
        {
            check(NameIt < NameEnd)
            FNameSerializedView Name = LoadNameHeader(*//* in-out *//* NameIt)
            OutNames.Add(BatchLoadNameWithHash(Name, INTEL_ORDER64(Hash)))
        }
    }
    else
    {
        while (NameIt < NameEnd)
        {
            FNameSerializedView Name = LoadNameHeader(*//* in-out *//* NameIt)
            OutNames.Add(BatchLoadNameWithoutHash(Name))
        }

    }

//    GetNamePoolPostInit().BatchUnlock()

    check(NameIt == NameEnd)*/
}