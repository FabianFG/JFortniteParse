/**
 * 
 */
package UE4_Localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import UE4.FArchive;
import UE4_Assets.ReadException;

/**
 * @author FunGames
 *
 */
public class Locres {
	private String name;
	private FTextLocalizationResource locresData;
	
	public static void main(String[] args) throws ReadException {
		long time = System.currentTimeMillis();
		Locres lOLD = Locres.fromFile(new File("D:\\Fabian\\Desktop\\Game_BR(1).locres"));
		Locres lNEW = Locres.fromFile(new File("D:\\Fabian\\WORKSPACE\\PakBrowserAES\\Output\\FortniteGame\\Content\\Localization\\Game_BR\\en\\Game_BR.locres"));
		System.out.println(lNEW.find("Bao Bros"));
		lNEW.compareTo(lOLD);
		long timediff = System.currentTimeMillis() - time;
		System.out.println("Time to load locres file : " + timediff + "ms");
	}

	public Locres(byte[] locres, String filename) throws ReadException {
		this.name = filename;
		FArchive locresAr = new FArchive(locres);
		this.locresData = new FTextLocalizationResource(locresAr);
		
		System.out.println("Successfully parsed locres package: " + name);
	}
	
	public static Locres fromFile(File locresFile) throws ReadException {
		try {
			FileInputStream fin = new FileInputStream(locresFile);
			return new Locres(IOUtils.toByteArray(fin), locresFile.getName());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public FTextLocalizationResource getLocresData() {
		return locresData;
	}
	
	public List<String> compareTo(Locres old) {
		List<String> res = new ArrayList<>();
		LocaleNamespace oldNS = findDefault(old);
		LocaleNamespace newNS = findDefault(this);
		
		Set<String> oldSet = new HashSet<>();
		oldNS.data.forEach(entry -> oldSet.add(entry.key));
		
		newNS.data.forEach(entry -> {
			if(!oldSet.contains(entry.key)) {
				res.add(entry.data);
			}
		});
		
		return res;
	}
	
	public boolean find(String value) {
		return findDefault(this).data.stream().anyMatch(entry -> entry.data.equals(value));
	}
	
	public LocaleNamespace findDefault(Locres locres) {
		Optional<LocaleNamespace> lns = locres.getLocresData().getStringData().stream().filter(s -> {
			return s.namespace.equals("");
		}).findFirst();
		if(lns.isPresent()) {
			LocaleNamespace ns = lns.get();
			return ns;
		}
		return null;
	}
}

