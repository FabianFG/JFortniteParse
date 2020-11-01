package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.io.al2.*
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger


val LOG_IO_STORE = LoggerFactory.getLogger("IoStore")

fun createIoStoreReader(path: String, decryptionKeys: Map<FGuid, ByteArray>): FIoStoreReaderImpl? {
    val ioEnvironment = FIoStoreEnvironment(path.substringBeforeLast('.'))
    val ioStoreReader = FIoStoreReaderImpl()

    return try {
        ioStoreReader.initialize(ioEnvironment, decryptionKeys)
        ioStoreReader
    } catch (e: FIoStatusException) {
        LOG_IO_STORE.warn("Failed creating IoStore reader '$path' [${e.message}]")
        null
    }
}

fun iterateDirectoryIndex(directory: FIoDirectoryIndexHandle, path: String, reader: FIoDirectoryIndexReader, visit: (filePath: String, tocEntryIndex: UInt) -> Boolean): Boolean {
    var childDirectory = reader.getChildDirectory(directory)
    while (childDirectory.isValid()) {
        val directoryName = reader.getDirectoryName(childDirectory)
        val childDirectoryPath = path / directoryName

        var file = reader.getFile(childDirectory)
        while (file.isValid()) {
            val tocEntryIndex = reader.getFileData(file)
            val fileName = reader.getFileName(file)
            val filePath = reader.getMountPoint() / childDirectoryPath / fileName

            if (!visit(filePath, tocEntryIndex)) {
                return false
            }

            file = reader.getNextFile(file)
        }

        if (!iterateDirectoryIndex(childDirectory, childDirectoryPath, reader, visit)) {
            return false
        }

        childDirectory = reader.getNextDirectory(childDirectory)
    }

    return true
}

fun describe(
    globalContainerPath: String,
    decryptionKeys: Map<FGuid, ByteArray>,
    packageFilter: String,
    outPath: String,
    bIncludeExportHashes: Boolean
): Int {
    if (!File(globalContainerPath).exists()) {
        LOG_IO_STORE.error("Global container '$globalContainerPath' doesn't exist.")
        return -1
    }
    val globalReader = createIoStoreReader(globalContainerPath, decryptionKeys)
    if (globalReader == null) {
        LOG_IO_STORE.warn("Failed reading global container '$globalContainerPath'")
        return -1
    }

    LOG_IO_STORE.info("Loading global name map...")

    val globalNamesIoBuffer = runCatching { globalReader.read(FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)) }
        .getOrElse {
            LOG_IO_STORE.warn("Failed reading names chunk from global container '$globalContainerPath'")
            return -1
        }

    val globalNameHashesIoBuffer = runCatching { globalReader.read(FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)) }
        .getOrElse {
            LOG_IO_STORE.warn("Failed reading name hashes chunk from global container '$globalContainerPath'")
            return -1
        }

    val globalNameMap = mutableListOf<FNameEntryId>()
    loadNameBatch(globalNameMap, globalNamesIoBuffer, globalNamesIoBuffer)

    LOG_IO_STORE.info("Loading script imports...")

    val initialLoadIoBuffer = runCatching { globalReader.read(FIoChunkId(0u, 0u, EIoChunkType.LoaderInitialLoadMeta)) }
        .getOrElse {
            LOG_IO_STORE.warn("Failed reading initial load meta chunk from global container '$globalContainerPath'")
            return -1
        }

    val scriptObjectByGlobalIdMap = mutableMapOf<FPackageObjectIndex, FScriptObjectDesc>()
    val initialLoadArchive = FByteArchive(initialLoadIoBuffer)
    val numScriptObjects = initialLoadArchive.readInt32()
    for (scriptObjectIndex in 0 until numScriptObjects) {
        val scriptObjectEntry = FScriptObjectEntry(initialLoadArchive)
        val mappedName = FMappedName.fromMinimalName(scriptObjectEntry.objectName)
        check(mappedName.isGlobal())
        scriptObjectByGlobalIdMap[scriptObjectEntry.globalIndex] = FScriptObjectDesc(
            name = FName.createFromDisplayId(globalNameMap[mappedName.getIndex().toInt()], mappedName.number.toInt()),
            globalImportIndex = scriptObjectEntry.globalIndex,
            outerIndex = scriptObjectEntry.outerIndex
        )
    }
    for (scriptObjectDesc in scriptObjectByGlobalIdMap.values) {
        if (scriptObjectDesc.fullName.isNone()) {
            val scriptObjectStack = mutableListOf<FScriptObjectDesc>()
            var current: FScriptObjectDesc? = scriptObjectDesc
            var fullName = ""
            while (current != null) {
                if (!current.fullName.isNone()) {
                    fullName = current.fullName.toString()
                    break
                }
                scriptObjectStack.add(current)
                current = scriptObjectByGlobalIdMap[current.outerIndex]
            }
            while (scriptObjectStack.isNotEmpty()) {
                current = scriptObjectStack.removeLast()
                fullName /= current.name.toString()
                current.fullName = FName.dummy(fullName)
            }
        }
    }

    val foundContainerFiles = File(globalContainerPath).parentFile.listFiles().filter { it.name.endsWith(".utoc") }

    LOG_IO_STORE.info("Loading containers...")

    val readers = mutableListOf<FIoStoreReaderImpl>()

    val loadContainerHeaderJobs = mutableListOf<FLoadContainerHeaderJob>()

    for (containerFile in foundContainerFiles) {
        val reader = createIoStoreReader(containerFile.path, decryptionKeys)
        if (reader == null) {
            LOG_IO_STORE.warn("Failed to read container '$containerFile'")
            continue
        }

        loadContainerHeaderJobs.add(FLoadContainerHeaderJob().also {
            it.reader = reader
            it.containerName = FName.dummy(containerFile.name.substringBeforeLast('.'))
        })

        readers.add(reader)
    }

    val totalPackageCount = AtomicInteger(0)
    CompletableFuture.allOf(*Array(loadContainerHeaderJobs.size) {
        CompletableFuture.supplyAsync {
            val job = loadContainerHeaderJobs[it]

            job.containerDesc = FContainerDesc().apply {
                name = job.containerName
                id = job.reader.containerId
                encryptionKeyGuid = job.reader.encryptionKeyGuid
                val flags = job.reader.containerFlags
                bCompressed = flags and IO_CONTAINER_FLAG_COMPRESSED != 0
                bEncrypted = flags and IO_CONTAINER_FLAG_ENCRYPTED != 0
                bSigned = flags and IO_CONTAINER_FLAG_SIGNED != 0
                bIndexed = flags and IO_CONTAINER_FLAG_INDEXED != 0
            }

            val ioBuffer = job.reader.read(FIoChunkId(job.reader.containerId.value(), 0u, EIoChunkType.ContainerHeader))
            val Ar = FByteArchive(ioBuffer)
            val containerHeader = FContainerHeader(Ar)

            job.rawCulturePackageMap = containerHeader.culturePackageMap
            job.rawPackageRedirects = containerHeader.packageRedirects

            val storeEntries = FByteArchive(containerHeader.storeEntries).readTArray(containerHeader.packageCount.toInt()) { FPackageStoreEntry(Ar) }
            storeEntries.forEachIndexed { packageIndex, containerEntry ->
                val packageId = containerHeader.packageIds[packageIndex]
                job.packages.add(FPackageDesc().also {
                    it.packageId = packageId
                    it.size = containerEntry.exportBundlesSize
                    it.exportCount = containerEntry.exportCount
                    it.exportBundleCount = containerEntry.exportBundleCount
                    it.loadOrder = containerEntry.loadOrder
                })
                totalPackageCount.incrementAndGet()
            }
        }
    }).await()

    class FLoadPackageSummaryJob {
        lateinit var packageDesc: FPackageDesc
        lateinit var chunkId: FIoChunkId
        val containers = mutableListOf<FLoadContainerHeaderJob>()
    }

    val loadPackageSummaryJobs = mutableListOf<FLoadPackageSummaryJob>()

    val containers = mutableListOf<FContainerDesc>()
    val packages = mutableListOf<FPackageDesc>()
    val packageByIdMap = mutableMapOf<FPackageId, FPackageDesc>()
    val packageJobByIdMap = mutableMapOf<FPackageId, FLoadPackageSummaryJob>()
    for (loadContainerHeaderJob in loadContainerHeaderJobs) {
        containers.add(loadContainerHeaderJob.containerDesc)
        for (packageDesc in loadContainerHeaderJob.packages) {
            packageJobByIdMap.getOrPut(packageDesc.packageId) {
                packages.add(packageDesc)
                packageByIdMap[packageDesc.packageId] = packageDesc
                val loadPackageSummaryJob = FLoadPackageSummaryJob().also {
                    it.packageDesc = packageDesc
                    it.chunkId = FIoChunkId(packageDesc.packageId.value(), 0u, EIoChunkType.ExportBundleData)
                }
                loadPackageSummaryJobs.add(loadPackageSummaryJob)
                loadPackageSummaryJob
            }.containers.add(loadContainerHeaderJob)
        }
    }
    for (loadContainerHeaderJob in loadContainerHeaderJobs) {
        for (redirectPair in loadContainerHeaderJob.rawPackageRedirects) {
            loadContainerHeaderJob.containerDesc.packageRedirects.add(FPackageRedirect(
                source = packageByIdMap[redirectPair.first]!!,
                target = packageByIdMap[redirectPair.second]!!
            ))
        }
        for (cultureRedirectsPair in loadContainerHeaderJob.rawCulturePackageMap) {
            val culture = FName.dummy(cultureRedirectsPair.key)
            for (redirectPair in cultureRedirectsPair.value) {
                loadContainerHeaderJob.containerDesc.packageRedirects.add(FPackageRedirect(
                    source = packageByIdMap[redirectPair.first]!!,
                    target = packageByIdMap[redirectPair.second]!!,
                    culture = culture
                ))
            }
        }
    }

    CompletableFuture.allOf(*Array(loadPackageSummaryJobs.size) {
        CompletableFuture.supplyAsync {
            val job = loadPackageSummaryJobs[it]
            for (loadContainerHeaderJob in job.containers) {
                val chunkInfo = loadContainerHeaderJob.reader.getChunkInfo(job.chunkId)
                job.packageDesc.locations.add(FPackageLocation(
                    container = loadContainerHeaderJob.containerDesc,
                    offset = chunkInfo.offset
                ))
            }

            val reader = job.containers.first().reader
            val readOptions = FIoReadOptions()
            if (!bIncludeExportHashes) {
                readOptions.setRange(0uL, 16uL shl 10)
            }
            var packageSummaryData = reader.read(job.chunkId, readOptions)
            var packageSummaryAr = FByteArchive(packageSummaryData)
            var packageSummary = FPackageSummary(packageSummaryAr)
            val packageSummarySize = packageSummary.graphDataOffset + packageSummary.graphDataSize
            if (packageSummarySize > packageSummaryAr.size()) {
                readOptions.setRange(0u, packageSummarySize.toULong())
                packageSummaryData = reader.read(job.chunkId, readOptions)
                packageSummaryAr = FByteArchive(packageSummaryData)
                packageSummary = FPackageSummary(packageSummaryAr)
            }

            val packageNameMap = mutableListOf<FNameEntryId>()
            if (packageSummary.nameMapNamesSize > 0) {
                val nameMapNamesData = FByteArchive(ByteBuffer.wrap(packageSummaryData, packageSummary.nameMapNamesOffset, packageSummary.nameMapNamesSize))
                val nameMapHashesData = FByteArchive(ByteBuffer.wrap(packageSummaryData, packageSummary.nameMapHashesOffset, packageSummary.nameMapHashesSize))
                loadNameBatch(packageNameMap, nameMapNamesData, nameMapHashesData)
            }

            job.packageDesc.apply {
                packageName = FName.createFromDisplayId(packageNameMap[packageSummary.name.getIndex().toInt()], packageSummary.name.number.toInt())
                packageFlags = packageSummary.packageFlags
                nameCount = packageNameMap.size
            }

            packageSummaryAr.seek(packageSummary.importMapOffset)
            job.packageDesc.imports = MutableList((packageSummary.exportMapOffset - packageSummary.importMapOffset) / 8 /*sizeof(FPackageObjectIndex)*/) {
                FImportDesc().apply {
                    globalImportIndex = FPackageObjectIndex(packageSummaryAr)
                }
            }

            packageSummaryAr.seek(packageSummary.exportMapOffset)
            job.packageDesc.exports = MutableList(job.packageDesc.exportCount) {
                val exportMapEntry = FExportMapEntry(packageSummaryAr)
                FExportDesc().apply {
                    `package` = job.packageDesc
                    name = FName.createFromDisplayId(packageNameMap[exportMapEntry.objectName.getIndex().toInt()], exportMapEntry.objectName.number.toInt())
                    outerIndex = exportMapEntry.outerIndex
                    classIndex = exportMapEntry.classIndex
                    superIndex = exportMapEntry.superIndex
                    templateIndex = exportMapEntry.templateIndex
                    globalImportIndex = exportMapEntry.globalImportIndex
                    serialSize = exportMapEntry.cookedSerialSize
                }
            }

            var currentExportOffset = packageSummarySize
            for (exportBundleIndex in 0 until job.packageDesc.exportBundleCount) {
                val exportBundleDesc = mutableListOf<FExportBundleEntryDesc>()
                job.packageDesc.exportBundles.add(exportBundleDesc)
            }
        }
    }).await()
    return 0
}

class FPackageRedirect(
    val source: FPackageDesc,
    val target: FPackageDesc,
    val culture: FName? = null
)

class FContainerDesc {
    var name: FName? = null
    var id: FIoContainerId? = null
    var encryptionKeyGuid: FGuid? = null
    var packageRedirects = mutableListOf<FPackageRedirect>()
    var bCompressed = false
    var bSigned = false
    var bEncrypted = false
    var bIndexed = false
}

class FPackageLocation(
    val container: FContainerDesc,
    val offset: ULong = (-1).toULong()
)

class FExportDesc {
    lateinit var `package`: FPackageDesc
    lateinit var name: FName
    lateinit var fullName: FName
    lateinit var outerIndex: FPackageObjectIndex
    lateinit var classIndex: FPackageObjectIndex
    lateinit var superIndex: FPackageObjectIndex
    lateinit var templateIndex: FPackageObjectIndex
    lateinit var globalImportIndex: FPackageObjectIndex
    var serialOffset = 0uL
    var serialSize = 0uL
    lateinit var hash: ByteArray /*FSHAHash*/
}

class FExportBundleEntryDesc {
    //var commandType = FExportBundleEntry.EExportCommandType.ExportCommandType_Count
    val localExportIndex = -1
    lateinit var export: FExportDesc
}

class FImportDesc {
    lateinit var name: FName
    lateinit var globalImportIndex: FPackageObjectIndex
    lateinit var export: FExportDesc
}

class FScriptObjectDesc(
    val name: FName,
    val globalImportIndex: FPackageObjectIndex,
    val outerIndex: FPackageObjectIndex) {
    lateinit var fullName: FName
}

class FPackageDesc {
    lateinit var packageId: FPackageId
    lateinit var packageName: FName
    var size = 0uL
    var loadOrder = (-1).toUInt()
    var packageFlags = 0u
    var nameCount = -1
    var exportCount = -1 // custom
    var exportBundleCount = -1
    val locations = mutableListOf<FPackageLocation>()
    lateinit var imports: MutableList<FImportDesc>
    lateinit var exports: MutableList<FExportDesc>
    val exportBundles = mutableListOf<List<FExportBundleEntryDesc>>()
}

class FLoadContainerHeaderJob {
    lateinit var containerName: FName
    lateinit var containerDesc: FContainerDesc
    val packages = mutableListOf<FPackageDesc>()
    lateinit var reader: FIoStoreReaderImpl
    lateinit var rawCulturePackageMap: FCulturePackageMap
    lateinit var rawPackageRedirects: Array<Pair<FPackageId, FPackageId>>
}