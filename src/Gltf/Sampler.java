/**
 * 
 */
package Gltf;

import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Sampler {
	private int magFilter;
	private int minFilter;
	private int wrapS;
	private int wrapT;
	
	
	public Sampler(int magFilter, int minFilter, int wrapS, int wrapT) {
		this.magFilter = magFilter;
		this.minFilter = minFilter;
		this.wrapS = wrapS;
		this.wrapT = wrapT;
	}
	
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		j.put("magFilter", magFilter);
		j.put("minFilter", minFilter);
		j.put("wrapS", wrapS);
		j.put("wrapT", wrapT);
		return j;
	}
	
	public static final Sampler DEFAULT_SAMPLER = new Sampler(GltfConstants.GL_LINEAR, GltfConstants.GL_LINEAR, GltfConstants.GL_CLAMP_TO_EDGE, GltfConstants.GL_CLAMP_TO_EDGE);
	
	
}
