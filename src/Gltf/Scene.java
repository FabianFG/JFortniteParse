/**
 * 
 */
package Gltf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Scene {
	private String name;
	private int[] nodes;
	
	public Scene(String name, int[] nodes) {
		this.name = name;
		this.nodes = nodes;
	}

	public String getName() {
		return name;
	}

	public int[] getNodes() {
		return nodes;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		j.put("name", this.name);
		JSONArray a = new JSONArray();
		for(int i : this.nodes) {
			a.add(i);
		}
		j.put("nodes", a);
		return j;
	}
}
