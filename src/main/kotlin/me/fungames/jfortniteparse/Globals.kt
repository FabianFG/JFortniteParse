@file:JvmName("Globals")

package me.fungames.jfortniteparse

import mu.KotlinLogging
import kotlin.jvm.JvmField as F

@F var GDebugProperties = false
@F var GFatalObjectSerializationErrors = false
@F var GReadScriptData = false

@F val LOG_DATA_TABLE = KotlinLogging.logger("DataTable")
@F val LOG_JFP = KotlinLogging.logger("JFortniteParse")
@F val LOG_STREAMING = KotlinLogging.logger("Streaming")