/**
 * 
 */
package Gltf;

import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Material {
	private int diffuseTexture;
	private int normalTexture;
	
	
	public Material(int diffuseTexture, int normalTexture) {
		this.diffuseTexture = diffuseTexture;
		this.normalTexture = normalTexture;
	}
	
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		
		JSONObject pbr = new JSONObject();
		JSONObject baseColorTexture = new JSONObject();
		baseColorTexture.put("index", diffuseTexture);
		pbr.put("baseColorTexture", baseColorTexture);
		j.put("pbrMetallicRoughness", pbr);
		
		JSONObject normalTexture = new JSONObject();
		normalTexture.put("index", this.normalTexture);
		j.put("normalTexture", normalTexture);
		
		return j;
		
	}
	
	
}
