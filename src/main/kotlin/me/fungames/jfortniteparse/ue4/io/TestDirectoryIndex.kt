package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import java.io.File

fun main() {
    val hhh = mutableMapOf<String, Int>()
    val paksDir = File("C:\\Program Files\\Epic Games\\Fortnite\\FortniteGame\\Content\\Paks")
    paksDir.list()?.forEach {
        if (it.endsWith(".utoc")) {
            val reader = FIoStoreReaderImpl()
            try {
                reader.initialize(File(paksDir, it.substringBeforeLast('.')).path, mapOf(FGuid.mainGuid to Aes.parseKey("0xab32bab083f7d923a33aa768bc64b64bf62488948bd49fe61d95343492252558")))
            } catch (e: FIoStatusException) {
                if (e.errorCode != EIoErrorCode.FileOpenFailed) {
                    throw e
                }
            }
            if (reader.directoryIndexReader != null) {
                iterateDirectoryIndex(FIoDirectoryIndexHandle.rootDirectory(), "", reader.directoryIndexReader!!) { filePath, tocEntryIndex ->
                    hhh[filePath] = tocEntryIndex.toInt()
                    true
                }
            }
        }
    }
    File("bruh.txt").writeText(hhh.keys.sorted().joinToString("\n"))
}