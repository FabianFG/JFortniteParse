package me.fungames.jfortniteparse.ue4.assets

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class OnlyAnnotated

@Target(AnnotationTarget.FIELD)
annotation class UProperty(
	val name: String = "",
	val skipPrevious: Int = 0,
	val skipNext: Int = 0,
	val arrayDim: Int = 1,
	val isEnumAsByte: Boolean = true,
	val innerType: KClass<*> = Object::class,
	val valueType: KClass<*> = Object::class
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class UStruct