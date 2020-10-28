package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.io.al2.FAsyncLoadingThread2
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

val LOG_PAK_FILE = LoggerFactory.getLogger("PakFile")

fun main() {
    try {
        val gameDir = File("C:\\Program Files\\Epic Games\\Fortnite\\FortniteGame")
        val contentDir = File(gameDir, "Content")
        val paksDir = File(contentDir, "Paks")
        val ioStoreGlobalEnvironment = FIoStoreEnvironment(File(paksDir, "global").path)
        if (FIoDispatcher.isValidEnvironment(ioStoreGlobalEnvironment)) {
            try {
                FIoDispatcher.initialize()
                val ioDispatcher = FIoDispatcher.get()
                try {
                    ioDispatcher.mount(ioStoreGlobalEnvironment)
                    LOG_PAK_FILE.info("Initialized I/O dispatcher")
                } catch (e: FIoStatusException) {
                    LOG_PAK_FILE.error("Failed to mount I/O dispatcher global environment: '%s'".format(e.message))
                }
            } catch (e: FIoStatusException) {
                LOG_PAK_FILE.error("Failed to initialize I/O dispatcher: '%s'".format(e.message))
            }
        }
        if (FIoDispatcher.isInitialized()) {
            FIoDispatcher.initializePostSettings()
            paksDir.list()?.forEach {
                if (false && it.endsWith(".utoc") && FIoDispatcher.isInitialized()) {
                    val ioStoreEnvironment = FIoStoreEnvironment(File(paksDir, it.substringBeforeLast('.')).path)
                    try {
                        FIoDispatcher.get().mount(ioStoreEnvironment)
                        LOG_PAK_FILE.info("Mounted IoStore environment \"%s\"".format(ioStoreEnvironment.path))
                    } catch (e: FIoStatusException) {
                        LOG_PAK_FILE.warn("Failed to mount IoStore environment \"%s\" [%s]".format(ioStoreEnvironment.path, e.message))
                    }
                }
            }
            val t = FAsyncLoadingThread2()
            t.lazyInitializeFromLoadPackage()
            print("kek")
            exitProcess(0)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        exitProcess(1)
    }
}