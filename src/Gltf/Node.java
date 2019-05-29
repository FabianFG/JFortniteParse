/**
 * 
 */
package Gltf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

/**
 * @author FunGames
 *
 */
public class Node {
	private Map<Object, Object> properties;
	private List<Integer> children;

	public Node() {
		this.properties = new HashMap<>();
	}
	public void addProperty(Object key, Object value) {
		this.properties.put(key, value);
	}
	public void addChildren(Integer children) {
		if(this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(children);
	}
	
	public Vec3 getTranslation() {
		JSONArray values = (JSONArray) this.properties.get("translation");
		Vec3 res = new Vec3(((float)values.get(0)),((float)values.get(1)),((float)values.get(2)));
		return res;
		
	}
	public Vec4 getRotation() {
		JSONArray values = (JSONArray) this.properties.get("rotation");
		Vec4 res = new Vec4(((float)values.get(0)),((float)values.get(1)),((float)values.get(2)), ((float)values.get(3)));
		return res;
	}
	
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		Iterator it = this.properties.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			j.put(key, this.properties.get(key));
		}
		if(children != null) {
			JSONArray childArray = new JSONArray();
			for(Integer c : children) {
				childArray.add(c);
			}
			j.put("children", childArray);
		}
		return j;
	}
}
