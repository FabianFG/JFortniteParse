package omega

import omega.fetchers.ChallengeFetcher

val fetching = mapOf("challenges" to true)

val challengeArray = arrayOf("QuestBundle_S17_Milestone")

fun main() {

    if(fetching["challenges"] == true)
        ChallengeFetcher(*challengeArray)

}