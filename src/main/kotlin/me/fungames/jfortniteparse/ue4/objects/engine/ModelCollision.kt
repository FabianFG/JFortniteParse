package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.square
import me.fungames.jfortniteparse.util.INDEX_NONE
import kotlin.jvm.internal.Ref.*
import kotlin.math.sqrt

/**
 * Find closest vertex to a point at or below a node in the Bsp.  If no vertices
 * are closer than MinRadius, returns -1.
 */
fun findNearestVertex(
    model: UModel,
    sourcePoint: FVector,
    destPoint: ObjectRef<FVector>,
    minRadius: FloatRef,
    iNode: Int,
    pVertex: IntRef,
): Float {
    var iNode = iNode
    var resultRadius = -1f
    while (iNode != INDEX_NONE) {
        var node = model.nodes[iNode]
        val iBack = node.iBack
        val planeDist = node.plane.planeDot(sourcePoint)
        if (planeDist >= -minRadius.element && node.iFront != INDEX_NONE) {
            // Check front.
            val tempRadius = findNearestVertex(model, sourcePoint, destPoint, minRadius, node.iFront, pVertex)
            if (tempRadius >= 0f) resultRadius = tempRadius; minRadius.element = tempRadius
        }
        if (planeDist > -minRadius.element && planeDist <= minRadius.element) {
            // Check this node's poly's vertices.
            while (iNode != INDEX_NONE) {
                // Loop through all coplanars.
                node = model.nodes[iNode]
                val surf = model.surfs[node.iSurf]
                val base = model.points[surf.pBase]
                val tempRadiusSquared = sourcePoint.distSquared(base)

                if (tempRadiusSquared < square(minRadius.element)) {
                    pVertex.element = surf.pBase
                    minRadius.element = sqrt(tempRadiusSquared)
                    resultRadius = minRadius.element
                    destPoint.element = base
                }

                var vertPoolIdx = node.iVertPool
                repeat(node.numVertices.toInt()) {
                    val vertPool = model.verts[vertPoolIdx]
                    val vertex = model.points[vertPool.pVertex]
                    val tempRadiusSquared2 = sourcePoint.distSquared(vertex)
                    if (tempRadiusSquared2 < square(minRadius.element)) {
                        pVertex.element = vertPool.pVertex
                        minRadius.element = sqrt(tempRadiusSquared2)
                        resultRadius = minRadius.element
                        destPoint.element = vertex
                    }
                    vertPoolIdx++
                }
                iNode = node.iPlane
            }
        }
        if (planeDist > minRadius.element)
            break
        iNode = iBack
    }
    return resultRadius
}