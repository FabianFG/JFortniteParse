package me.fungames.jfortniteparse.ue4.assets.mappings

import com.google.gson.JsonParser
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.util.await
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class RepoTypeMappingsProvider : JsonTypeMappingsProvider() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger("RepoTypeMappingsProvider")
    }

    override fun reload(): Boolean = reloadAsync().await()

    fun reloadAsync(): CompletableFuture<Boolean> = CompletableFuture.supplyAsync {
        val zip = URL(System.getProperty("mappingsRepoUrl") ?: throw ParserException("Property mappingsRepoUrl is not defined")).readBytes()
        val zipAr = ZipInputStream(ByteArrayInputStream(zip))
        var zipEntry: ZipEntry?
        while (zipAr.nextEntry.also { zipEntry = it } != null) {
            val zipEntry = zipEntry!!
            try {
                when {
                    zipEntry.name.endsWith("_ClassMappings.json") -> addStructs(JsonParser.parseReader(InputStreamReader(zipAr)))
                    zipEntry.name.endsWith("_StructMappings.json") -> addStructs(JsonParser.parseReader(InputStreamReader(zipAr)))
                    zipEntry.name.endsWith("_EnumMappings.json") -> addEnums(JsonParser.parseReader(InputStreamReader(zipAr)))
                }
            } catch (e: Throwable) {
                LOGGER.warn("Failed to load {}", zipEntry.name, e)
            }
        }
        zipAr.close()
        true
    }
}