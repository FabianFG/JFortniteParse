package omega.fetchers

import com.github.salomonbrys.kotson.put
import com.google.gson.JsonObject
import me.fungames.jfortniteparse.fileprovider.DefaultFileProvider
import omega.provider.OmegaFileProvider
import omega.utils.globalVariables.Companion.pathToFileNames
import omega.utils.globalVariables.Companion.pathToRes
import java.io.File

class ChallengeFetcher(vararg challengeString: String, var challengeName: String = "") {

    init {

        val provider = OmegaFileProvider().provider

        val challengeObject = JsonObject()
        val challengesObject = JsonObject()
        challengeObject.put(Pair("Name", challengeName))
        challengeObject.put(Pair("Image", ""))
        challengeObject.put(Pair("Number", ""))
        challengeObject.put(Pair("Challenges", challengesObject))

        var number = 0
        challengeString.forEach { s ->
            val file = File(pathToFileNames)
            file.forEachLine { fileName ->

                if(!fileName.contains(s))
                    return@forEachLine

                val pkg = provider.loadGameFile(fileName)
                val challengeBundle = pkg!!.toJson();
                val challenges = challengeBundle.get("export_properties").asJsonArray
                        .get(0).asJsonObject
                        .get("CareerQuestBitShifts").asJsonArray

                for (challenge in challenges) {
                    val challengePath = challenge.asJsonObject.get("assetPath").asString.split(".")[0]
                    //println(challengePath)
                    val challengeFile = provider.loadGameFile(challengePath)
                    //println(challengeFile!!.toJson())

                    val challengeJson = challengeFile?.toJson()
                    val challengeExportPath = challengeJson?.get("export_properties")?.asJsonArray?.get(0)?.asJsonObject

//                    val challengeName = challengeExportPath?.get("DisplayName")
//                            ?.asJsonObject?.get("finalText")?.asString
//                            .plus(" (${challengeExportPath?.get("Objectives")?.asJsonArray?.get(0)?.asJsonObject?.get("Count")?.asString})")

                    val challengeName = challengeExportPath?.get("DisplayName")?.asJsonObject?.get("finalText")?.asString
                    val challengeWithCount = challengeName.plus(" (${ if(challengeExportPath?.has("ObjectiveCompletionCount") == true) 
                                                                                challengeExportPath.get("ObjectiveCompletionCount").asString 
                                                                            else 
                                                                                challengeExportPath?.get("Objectives")?.asJsonArray?.get(0)?.asJsonObject?.get("Count")?.asString})")

                    //challengeArray.add(challenge(challengeName,"", arraypin("","")))

                    val challengeDetails = JsonObject()
                    challengesObject.put(Pair("Challenge ${if(number < 10) "0${number}" else number}", challengeDetails))
                    challengeDetails.put(Pair("Name", challengeWithCount))
                    challengeDetails.put(Pair("Detail", challengeName))
                    number++

                    //println(challengeName)
                }
            }
        }

        print(challengeObject)
    }

}