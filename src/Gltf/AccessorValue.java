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
public class AccessorValue {
	private int bufferView;
	private int byteOffset;
	private int componentType;
	private int count;
	private String type;
	private float[] min;
	private float[] max;
	private Map<Object, Object> additionalObjects;
	
	public AccessorValue(String type, int count, int componentType, int bufferView, int byteOffset) {
		this.type = type;
		this.count = count;
		this.componentType = componentType;
		this.bufferView = bufferView;
		this.byteOffset = byteOffset;
		this.additionalObjects = new HashMap<>();
	}
	
	public int getBufferView() {
		return bufferView;
	}
	public void setBufferView(int bufferView) {
		this.bufferView = bufferView;
	}
	public int getByteOffset() {
		return byteOffset;
	}
	public void setByteOffset(int byteOffset) {
		this.byteOffset = byteOffset;
	}
	public int getComponentType() {
		return componentType;
	}
	public void setComponentType(int componentType) {
		this.componentType = componentType;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public float[] getMin() {
		return min;
	}
	public void setMin(float[] min) {
		this.min = min;
	}
	public float[] getMax() {
		return max;
	}
	public void setMax(float[] max) {
		this.max = max;
	}
	public void addAdditionalProperty(Object key, Object value) {
		this.additionalObjects.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject jsonify() {
		JSONObject res = new JSONObject();
		res.put("bufferView", this.bufferView);
		res.put("byteOffset", this.byteOffset);
		res.put("componentType", this.componentType);
		res.put("count", this.count);
		res.put("type", this.type);
		Iterator it = this.additionalObjects.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			res.put(key, this.additionalObjects.get(key));
		}
		if(this.max != null) {
			JSONArray m = new JSONArray();
			m.add(0, max[0]);
			m.add(1, max[1]);
			m.add(2, max[2]);
			res.put("max", m);
		}
		if(this.min != null) {
			JSONArray m = new JSONArray();
			m.add(0, min[0]);
			m.add(1, min[1]);
			m.add(2, min[2]);
			res.put("min", m);
		}
		return res;
	}
	
	
}
