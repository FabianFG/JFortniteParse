/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class EBulkData {
	public static int
	
	BULKDATA_PayloadAtEndOfFile =			0x0001,		// bulk data stored at the end of this file, data offset added to global data offset in package
	BULKDATA_CompressedZlib	=				0x0002,		// the same value as for UE3
	BULKDATA_Unused	=						0x0020,		// the same value as for UE3
	BULKDATA_ForceInlinePayload	=			0x0040,		// bulk data stored immediately after header
	BULKDATA_PayloadInSeperateFile =		0x0100,		// data stored in .ubulk file near the asset (UE4.12+)
	BULKDATA_SerializeCompressedBitWindow = 0x0200, 		// use platform-specific compression
	BULKDATA_OptionalPayload =				0x0800;		// same as BULKDATA_PayloadInSeperateFile, but stored with .uptnl extension (UE4.20+)
	
	public static boolean check(int bulkDataFlags, int bulkDataFlags2) {
		return (bulkDataFlags & bulkDataFlags2) != 0 ? true : false;
	}
}
