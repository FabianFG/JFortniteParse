package omega.provider

import me.fungames.jfortniteparse.fileprovider.DefaultFileProvider
import me.fungames.jfortniteparse.ue4.assets.mappings.UsmapTypeMappingsProvider
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import omega.utils.globalVariables
import omega.utils.globalVariables.Companion.pathToFileNames
import omega.utils.globalVariables.Companion.pathToMappings
import omega.utils.globalVariables.Companion.pathToRes
import java.io.File

class OmegaFileProvider(val fetchFile: Boolean = false, val fetchMapping: Boolean = false) {

    var provider: DefaultFileProvider
    var requiredKeys: List<FGuid>

    init {
        provider = DefaultFileProvider(File(globalVariables.gameFolderPath))
        requiredKeys = provider.requiredKeys()
        provider.submitKey(FGuid.mainGuid, globalVariables.mainAes);

        if(fetchFile) fetchFiles()
        if(fetchMapping) fetchMappings()

        provider.mappingsProvider = UsmapTypeMappingsProvider(File(pathToMappings)).apply { reload() }
    }

    private fun fetchFiles() {
        val fileNameFile = File(pathToFileNames)
        if (fileNameFile.exists()) {
            if (!fileNameFile.delete()) {
                throw error("\"filenames\" file cannot be delete. Please check if it is open.")
            }
        }

        fileNameFile.createNewFile()

        var files = provider.files()
        files = files.toSortedMap()

        for (file in files) {
            fileNameFile.appendText(file.value.toString() + "\n")
        }
    }

    private fun fetchMappings(){

    }

}