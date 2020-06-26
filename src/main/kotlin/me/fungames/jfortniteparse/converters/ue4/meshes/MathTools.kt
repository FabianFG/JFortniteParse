@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.converters.ue4.meshes

import glm_.vec3.Vec3
import me.fungames.jfortniteparse.util.INDEX_NONE
import me.fungames.jfortniteparse.util.set
import me.fungames.kotlinPointers.asPointer
import kotlin.math.floor

// structure which helps to share vertices between wedges
class CVertexShare {

    val points = mutableListOf<Vec3>()
    val normals = mutableListOf<CPackedNormal>()
    val extraInfos  = mutableListOf<UInt>()
    var wedgeToVert = mutableListOf<Int>()
    lateinit var vertToWedge : IntArray
    var wedgeIndex = 0

    // hashing
    var mins = Vec3()
    var maxs = Vec3()
    lateinit var extents : Vec3
    val hash = IntArray(16384) { -1 }
    lateinit var hashNext : IntArray

    fun prepare(verts : Array<CMeshVertex>) {
        vertToWedge = IntArray(verts.size)

        // compute bounds for better hashing
        computeBounds(verts)
        extents = maxs - mins
        extents[0] += 1; extents[1] += 1; extents[2] += 1; // avoid zero divide
        // initialize Hash and HashNext with -1
        hashNext = IntArray(verts.size) { -1 }
    }

    fun computeBounds(verts: Array<CMeshVertex>, updateBounds : Boolean = false) {
        if (verts.isEmpty()) {
            if (!updateBounds) {
                mins.set(0f, 0f, 0f)
                maxs.set(0f, 0f, 0f)
            }
            return
        }

        var data = verts.asPointer()

        var numVerts = verts.size
        if (!updateBounds) {
            mins.set(maxs.set(data[0].position))
            data++
            numVerts--
        }

        while (numVerts-- != 0) {
            val v = data[0].position
            data++
            if (v[0] < mins[0]) mins[0] = v[0]
            if (v[0] > maxs[0]) maxs[0] = v[0]
            if (v[1] < mins[1]) mins[1] = v[1]
            if (v[1] > maxs[1]) maxs[1] = v[1]
            if (v[2] < mins[2]) mins[2] = v[2]
            if (v[2] > maxs[2]) maxs[2] = v[2]
        }
    }

    fun addVertex(pos : Vec3, normal: CPackedNormal, extraInfo : UInt = 0u): Int {
        var pointIndex: Int

        normal.data = normal.data and 0xFFFFFFu         // clear W component which is used for binormal computation

        // compute hash
        val h = floor(
            ( (pos[0] - mins[0]) / extents[0] + (pos[1] - mins[1]) / extents[1] + (pos[2] - mins[2]) / extents[2] ) // 0..3
            * (hash.size / 3.0f * 16)   // multiply to 16 for better spreading inside Hash array
        ).toInt() % hash.size
        // find point with the same position and normal
        pointIndex = hash[h]
        while (pointIndex >= 0) {
            if (points[pointIndex] == pos && normals[pointIndex] == normal && extraInfos[pointIndex] == extraInfo)
                break // found it
            pointIndex = hashNext[pointIndex]
        }
        if (pointIndex == INDEX_NONE) {
            // point was not found - create it
            points.add(pos)
            pointIndex = points.lastIndex
            normals.add(normal)
            extraInfos.add(extraInfo)
            // add to Hash
            hashNext[pointIndex] = hash[h]
            hash[h] = pointIndex
        }

        // remember vertex <-> wedge map
        wedgeToVert.add(pointIndex)
        vertToWedge[pointIndex] = wedgeIndex++

        return pointIndex
    }
}