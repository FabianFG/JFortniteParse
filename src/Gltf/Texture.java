/**
 * 
 */
package Gltf;

import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Texture {
	private int sourceImage;
	private int sampler;
	
	public Texture(int sourceImage, int sampler) {
		this.sourceImage = sourceImage;
		this.sampler = sampler;
	}
	
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		j.put("source", sourceImage);
		j.put("sampler", sampler);
		return j;
	}
	
}
