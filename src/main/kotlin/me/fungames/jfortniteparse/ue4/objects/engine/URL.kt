package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive

/** URL structure. */
@ExperimentalUnsignedTypes
class FURL : UClass {
    /*companion object {
        val urlConfig = FUrlConfig()
    }*/

    /** Protocol, i.e. "unreal" or "http". */
    var protocol: String

    /** Optional hostname, i.e. "204.157.115.40" or "unreal.epicgames.com", blank if local. */
    var host: String

    /** Optional host port. */
    var port: Int

    var valid: Int

    /** Map name, i.e. "SkyCity", default is "Entry". */
    var map: String

    /** Options. */
    var op: Array<String>

    /** Portal to enter through, default is "". */
    var portal: String

    constructor(Ar: FArchive) {
        protocol = Ar.readString()
        host = Ar.readString()
        map = Ar.readString()
        portal = Ar.readString()
        op = Ar.readTArray { Ar.readString() }
        port = Ar.readInt32()
        valid = Ar.readInt32()
    }

    constructor(protocol: String, host: String, map: String, portal: String, op: Array<String>, port: Int, valid: Int) {
        this.protocol = protocol
        this.host = host
        this.map = map
        this.portal = portal
        this.op = op
        this.port = port
        this.valid = valid
    }

    /*override fun toString() = toString(false)

    /**
     * Convert this URL to text.
     */
    fun toString(fullyQualified: Boolean = false): String {
        var result = ""

        // Emit protocol.
        if ((protocol != urlConfig.defaultProtocol) || fullyQualified) {
            result += protocol
            result += ":"

            if (host != urlConfig.defaultHost) {
                result += "//"
            }
        }

        // Emit host and port
        if ((host != urlConfig.defaultHost) || (port != urlConfig.defaultPort)) {
            result += getHostPortString()
            result += "/"
        }

        // Emit map.
        if (map.isNotEmpty()) {
            result += map
        }

        // Emit options.
        op.forEach {
            result += "?"
            result += it
        }

        // Emit portal.
        if (portal.isNotEmpty()) {
            result += "#"
            result += portal
        }

        return result
    }*/

    /**
     * Prepares the Host and Port values into a standards compliant string
     */
    /*fun getHostPortString(): String {
        var result = ""
        val bNotUsingDefaultPort = (port != urlConfig.defaultPort)

        // If this is an IPv6 address (determined if there's more than one colon)
        // and we're going to be adding the port, we need to put in the brackets
        // This is done because there's no sane way to serialize the address otherwise
        val firstColonIndex = host.indexOf(':')
        val lastColonIndex = host.lastIndexOf(':')
        result += if (firstColonIndex != -1 && lastColonIndex != -1 && firstColonIndex != lastColonIndex && bNotUsingDefaultPort) {
            "[%s]".format(host)
        } else {
            // Otherwise print the IPv6/IPv4 address as is
            host
        }

        if (bNotUsingDefaultPort) {
            result += ":$port"
        }

        return result
    }

    /**
     * Helper for obtaining the default Url configuration
     */
    class FUrlConfig : UClass {
        var defaultProtocol: String
        var defaultName: String
        var defaultHost: String
        var defaultPortal: String
        var defaultSaveExt: String
        var defaultPort: int

        /**
         * Initialize with defaults from ini
         */
        fun init()

        /**
         * Reset state
         */
        fun reset()
    }*/
}