/**
 * 
 */
package Gltf;

import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Image {
	private String uri;
	
	public Image(String uri) {
		this.uri = uri;
	}
	
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		j.put("uri", uri);
		return j;
	}
	
}
