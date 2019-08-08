/**
 * 
 */
package UE4_Localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import UE4.FArchive;
import UE4_Assets.ReadException;
import lombok.extern.log4j.Log4j;

/**
 * @author FunGames
 *
 */
@Log4j(topic = "PackageParser")
public class Locres {
	private String name;
	private Map<String, Map<String, String>> texts;
	
	public static void main(String[] args) throws ReadException {
		long time = System.currentTimeMillis();
		Locres lNEW = Locres.fromFile(new File("D:\\Fabian\\WORKSPACE\\PakBrowserAES\\Output\\FortniteGame\\Content\\Localization\\Game_BR\\en\\Game_BR.locres"));
		System.out.println(lNEW.getKey("Part of the <SetName>{0}</> set"));
		long timediff = System.currentTimeMillis() - time;
		System.out.println("Time to load locres file : " + timediff + "ms");
	}

	public Locres(byte[] locres, String filename) throws ReadException {
		this.name = filename;
		FArchive locresAr = new FArchive(locres);
		FTextLocalizationResource locresData = new FTextLocalizationResource(locresAr);
		this.texts = new HashMap<>();
		locresData.getStringData().forEach(nameSpace -> {
			Map<String, String> text = new HashMap<>();
			nameSpace.data.forEach(entry -> {
				text.put(entry.key, entry.data);
			});
			texts.put(nameSpace.namespace, text);
		});
		
		log.info("Successfully parsed locres package: " + name);
	}
	
	public Map<String, String> getTexts(String nameSpace) {
		return texts.get(nameSpace);
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
	
	public List<String> compareTo(Locres old) {
		List<String> res = new ArrayList<>();
		
		Set<String> oldSet = new HashSet<>();
		old.texts.get("").keySet().forEach(entry -> oldSet.add(entry));
		
		this.texts.keySet().forEach(entry -> {
			if(!oldSet.contains(entry)) {
				res.add(texts.get("").get(entry));
			}
		});
		
		return res;
	}
	
	private String searchTemp = null;
	
	public String getNamespaceKey(String search) {
		searchTemp = null;
		this.texts.forEach((key, value) -> {
			value.forEach((innerKey, innerValue) -> {
				if(org.apache.commons.lang3.StringUtils.containsIgnoreCase(innerValue, search)) {
					searchTemp = key;
					return;
				}
			});
		});
		return searchTemp;
	}
	
	public String getKeyInNameSpace(String search, Map<String,String> nameSpace) {
		Optional<Entry<String,String>> res = nameSpace.entrySet().stream().filter(entry -> {
			
			return org.apache.commons.lang3.StringUtils.containsIgnoreCase(entry.getValue(), search);
		}).findFirst();
		return res.isPresent() ? res.get().getKey() : null;
	}
	
	public String getKey(String search) {
		String nameSpace = getNamespaceKey(search);
		if(nameSpace == null) 
			return null;
		return getKeyInNameSpace(search, this.texts.get(nameSpace));
	}
	
}

