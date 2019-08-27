/**
 * 
 */
package UE4.deserialize;

import com.google.gson.Gson;

import UE4_Assets.ImportMap;
import UE4_Assets.exports.UObject;

/**
 * @author FunGames
 *
 */
public interface NewableWithUObject {
	
	public void init(UObject uObject, Gson gson, ImportMap importMap);
	
	public UObject getBaseObject();
	public void setBaseObject(UObject uObject);
}
