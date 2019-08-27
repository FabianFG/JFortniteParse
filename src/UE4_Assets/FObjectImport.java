/**
 * 
 */
package UE4_Assets;

import annotation.FName;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FObjectImport {
	@FName private String classPackage;
	@FName private String className;
	private FPackageIndex outerIndex;
	@FName private String objectName;

}
