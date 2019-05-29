
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import UE4_Packages.FArchive;
import UE4_Packages.ReadException;


/**
 * @author FunGames
 *
 */
public class OodleKraken implements Oodle.OodleKrakenInterface {
	
	// native method implemented in ooz.dll
	public native int KrakenDecompress(byte[] inputBuffer, byte[] outputBuffer);
	
	
	@Override
	public int oodleDecompress(byte[] src, byte[] dst) throws IOException {
		//Load oodle dll
				InputStream in = OodleKraken.class.getResourceAsStream("ooz.dll");
			    byte[] buffer = new byte[1024];
			    int read = -1;
			    File temp = File.createTempFile("o.dll", "");
			    temp.deleteOnExit();
			    FileOutputStream fos = new FileOutputStream(temp);

			    while((read = in.read(buffer)) != -1) {
			        fos.write(buffer, 0, read);
			    }
			    fos.close();
			    in.close();

			    System.load(temp.getAbsolutePath());
			    String runDir = System.getProperty("user.dir");
			    File oo2core = new File(runDir + "/oo2core_7_win64.dll");
			    if(!oo2core.exists()) {
			    	in = OodleKraken.class.getResourceAsStream("oo2core_7_win64.dll");
			    	fos = new FileOutputStream(oo2core);

				    while((read = in.read(buffer)) != -1) {
				        fos.write(buffer, 0, read);
				    }
				    fos.close();
				    in.close();
			    }
			    
			    int result = KrakenDecompress(src, dst);
			    
			    
				
				return result;
	}
	public byte[] oodleDecompress(byte[] compressedData) throws IOException, ReadException {
		
		//Load oodle dll
		InputStream in = OodleKraken.class.getResourceAsStream("ooz.dll");
	    byte[] buffer = new byte[1024];
	    int read = -1;
	    File temp = File.createTempFile("o.dll", "");
	    temp.deleteOnExit();
	    FileOutputStream fos = new FileOutputStream(temp);

	    while((read = in.read(buffer)) != -1) {
	        fos.write(buffer, 0, read);
	    }
	    fos.close();
	    in.close();
	    
	    String runDir = System.getProperty("user.dir");
	    File oo2core = new File(runDir + "/oo2core_7_win64.dll");
	    if(!oo2core.exists()) {
	    	in = OodleKraken.class.getResourceAsStream("oo2core_7_win64.dll");
	    	fos = new FileOutputStream(oo2core);

		    while((read = in.read(buffer)) != -1) {
		        fos.write(buffer, 0, read);
		    }
		    fos.close();
		    in.close();
	    }

	    System.load(temp.getAbsolutePath());
	    
	    FArchive tempAr = new FArchive(compressedData);
	    
	    int outputSize;
		int hdrsize = 4;//readUnsignedInt64(Arrays.copyOfRange(src, 0, 8), true) >= 0x10000000000L ? 4 : 8;
		if(hdrsize == 4) {
			outputSize = tempAr.readInt32();
		}
		else {
			outputSize = (int) tempAr.readInt64();
		}
		byte[] dst = new byte[outputSize];
		byte[] srcWithoutHeader = Arrays.copyOfRange(compressedData, hdrsize, compressedData.length);
		
		int result = KrakenDecompress(srcWithoutHeader, dst);
	    
	    System.out.println(result);
		
		return dst;
	    
	}
}

