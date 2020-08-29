package me.fungames.jfortniteparse.ue4.pak.enums

const val PakVersion_Initial = 1
const val PakVersion_NoTimestamps = 2
const val PakVersion_CompressionEncryption = 3          // UE4.3+
const val PakVersion_IndexEncryption = 4                // UE4.17+ - encrypts only pak file index data leaving file content as is
const val PakVersion_RelativeChunkOffsets = 5           // UE4.20+
const val PakVersion_DeleteRecords = 6                  // UE4.21+ - this constant is not used in UE4 code
const val PakVersion_EncryptionKeyGuid = 7              // ... allows to use multiple encryption keys over the single project
const val PakVersion_FNameBasedCompressionMethod = 8    // UE4.22+ - use string instead of enum for compression method
const val PakVersion_FrozenIndex = 9
const val PakVersion_PathHashIndex = 10
const val PakVersion_Fnv64BugFix = 11
const val PakVersion_Last = 12
const val PakVersion_Latest = PakVersion_Last - 1