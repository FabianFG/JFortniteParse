/**
 * 
 */
package UE4_Localization;

import java.util.List;

/**
 * @author FunGames
 *
 */
public class LocaleNamespace {
	public String namespace;
	public List<FEntry> data;
	
	public LocaleNamespace(String namespace, List<FEntry> data) {
		this.namespace = namespace;
		this.data = data;
	}
}
