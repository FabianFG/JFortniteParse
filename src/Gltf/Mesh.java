/**
 * 
 */
package Gltf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Mesh {
	private Map<String, Integer> attributes = new HashMap<>();
	private Map<Object, Object> properties = new HashMap<>();
	
	public Mesh() {
		
	}
	
	public void addAttribute(String name, int value) {
		attributes.put(name, value);
	}
	public void addProperty(Object name, Object value) {
		properties.put(name, value);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject jsonify() {
		JSONObject r = new JSONObject();
		
		JSONArray primitives = new JSONArray();
		JSONObject primitive = new JSONObject();
		JSONObject attributes = new JSONObject();
		Iterator it = this.attributes.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			attributes.put(key, this.attributes.get(key));
		}
		primitive.put("attributes", attributes);
		Iterator<Object> it2 = this.properties.keySet().iterator();
		while(it2.hasNext()) {
			Object key = it2.next();
			primitive.put(key, this.properties.get(key));
		}
		primitives.add(primitive);
		r.put("primitives", primitives);
		return r;
	}
}
