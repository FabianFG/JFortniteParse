package me.fungames.jfortniteparse.util

import glm_.vec3.Vec3

fun Vec3.set(x : Float, y : Float, z : Float) : Vec3 {
    this.x = x
    this.y = y
    this.z = z
    return this
}

fun Vec3.set(other : Vec3): Vec3 {
    this.x = other.x
    this.y = other.y
    this.z = other.z
    return this
}