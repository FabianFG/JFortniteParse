/**
 * 
 */
package UE4_Localization;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@AllArgsConstructor
public class LocaleNamespace {
	private String namespace;
	private List<FEntry> data;
}
