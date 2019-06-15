/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FByteBulkData {
	private FByteBulkDataHeader header;
	private byte[] data;

	public FByteBulkData(FArchive Ar) throws ReadException {
		header = new FByteBulkDataHeader(Ar);
		int bulkDataFlags = header.getBulkDataFlags();
		data = new byte[header.getElementCount()];
		if(EBulkData.check(EBulkData.BULKDATA_Unused, bulkDataFlags)) {
			System.out.println("WARNING: Bulk with no data");
		} 
		//UE4
		else if(EBulkData.check((EBulkData.BULKDATA_OptionalPayload | EBulkData.BULKDATA_PayloadInSeperateFile), bulkDataFlags)) {
			//System.out.println(String.format("bulk data in %s file (flags=0x%X, pos=%d, size=%d)", EBulkData.check(EBulkData.BULKDATA_OptionalPayload, bulkDataFlags) ? ".uptnl" : ".ubulk", bulkDataFlags, header.getOffsetInFile() + Ar.getUbulkOffset(), header.getSizeOnDisk()));
			
			//Ubulk
			if(EBulkData.check(EBulkData.BULKDATA_PayloadInSeperateFile, bulkDataFlags)) {
				FArchive Ar_ubulk = Ar.getPayload("UBULK");
				if(Ar_ubulk != null) {
					int offset = (int) (header.getOffsetInFile() + Ar.getUbulkOffset());
					Ar_ubulk.Seek(offset);
					for(int i=0;i<header.getElementCount();i++) {
						data[i] = Ar_ubulk.readUInt8();
					}
				} else {
					throw new ReadException("No ubulk specified for this package but needed for loading", Ar.Tell());
				}
			}
			//TODO UPTNL
		}
		else if(EBulkData.check(EBulkData.BULKDATA_PayloadAtEndOfFile, bulkDataFlags)) {
			// stored in the same file, but at different position
			// save archive position
			int savePos = Ar.Tell();
			int saveStopper = Ar.GetStopper();
			// seek to data block and read data
			if(header.getOffsetInFile() - Ar.getUexpOffset() + header.getElementCount() <= Ar.data.length) {
				Ar.SetStopper((int) (header.getOffsetInFile() + header.getElementCount()));
				Ar.Seek((int) header.getOffsetInFile());
				//System.out.println(String.format("bulk data in %s file (Payload at end of file) (flags=0x%X, pos=%d, size=%d)", ".uexp", bulkDataFlags, header.getOffsetInFile() - Ar.getUexpOffset(), header.getSizeOnDisk()));
				for(int i=0;i<header.getElementCount(); i++) {
					data[i] = Ar.readUInt8();
				}
			} else {
				throw new ReadException(String.format("Can't read %d bytes at offset %d, max index: %d", header.getElementCount(), header.getOffsetInFile() - Ar.getUexpOffset(), Ar.data.length), Ar.Tell());
			}
			// restore archive position
			Ar.Seek(savePos);
			Ar.SetStopper(saveStopper);	
		}
		else if(EBulkData.check(EBulkData.BULKDATA_ForceInlinePayload, bulkDataFlags)) {
			//System.out.println(String.format("bulk data in %s file (Force payload inline) (flags=0x%X, pos=%d, size=%d)", ".uexp", bulkDataFlags, Ar.Tell(), header.getSizeOnDisk()));
			for(int i=0; i<header.getElementCount(); i++) {
				data[i] = Ar.readUInt8();
			}
		}
	}

	public FByteBulkDataHeader getHeader() {
		return header;
	}

	public byte[] getData() {
		return data;
	}
	
	
}
