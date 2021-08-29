package test

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import omega.provider.OmegaFileProvider
import omega.utils.globalVariables
import omega.utils.globalVariables.Companion.pathToRes
import java.awt.Font
import java.io.File
import javax.imageio.ImageIO


fun main() {

    val bufferedImage = ImageIO.read(File(pathToRes.plus("default_map.jpg")))
    val graphics2D = bufferedImage.createGraphics()
    graphics2D.font = Font(Font.SANS_SERIF, Font.BOLD, 10)

    var provider = OmegaFileProvider().provider

    val file = File(globalVariables.pathToFileNames)
    file.forEachLine { fileName ->

        if(!fileName.contains(".umap"))
            return@forEachLine

        val pkg = provider.loadGameFile(fileName)

        for(export in pkg!!.exports){
            for(prop in export.properties){
                if(prop.toString().contains("SpyProbe"))
                    print(fileName)
            }
        }

    }

//    val pkg = provider.loadGameFile("FortniteGame/Content/Athena/Apollo/Maps/Apollo_Terrain_HLOD.umap")

    //print(pkg!!.toJson())

//    for (export in pkg!!.exports) {
//        //println(export)
//        var name: FName? = null
//        var vector: FVector? = null
//        for (prop in export.properties) {
//            println("\t\t" + prop)
//            if (prop.name.text.equals("ActorFName") || prop.name.text.equals("UniqueWorldLocation")) {
//                if (prop.getTagTypeValueLegacy() is FName) {
//                    println(prop.getTagTypeValueLegacy())
//                    name = prop.getTagTypeValueLegacy() as FName
//                }
//                else if (prop.getTagTypeValueLegacy() is FVector) {
//                    vector = prop.getTagTypeValueLegacy() as FVector
//                    println(vector)
//                }
//            }
//        }
//        if(name != null && vector != null){
//            val location = normalize(vector.y, vector.x)
//            graphics2D.drawString(name.text, location!![0], location[1])
//        }
//    }
//
//    graphics2D.dispose()
//    ImageIO.write(bufferedImage, "jpg", File(pathToRes.plus("marked.jpg")))
}

fun normalize(xA: Float, yA: Float): FloatArray? {

    val div_var = 67.3422344f;
    var xA = xA
    var yA = yA
    yA = if (yA < 0) 2000 + Math.abs(yA / div_var) else 2000 - Math.abs(yA / div_var)
    xA = if (xA > 0) 2000 + Math.abs(xA / div_var) else 2000 - Math.abs(xA / div_var)
    return floatArrayOf(xA, yA)
}



