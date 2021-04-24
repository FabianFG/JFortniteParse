@file:JvmName("Globals")

package me.fungames.jfortniteparse

import mu.KotlinLogging

@JvmField var GDebugProperties = false
@JvmField var GFatalObjectSerializationErrors = false

@JvmField val LOG_STREAMING = KotlinLogging.logger("Streaming")
@JvmField val LOG_JFP = KotlinLogging.logger("JFortniteParse")