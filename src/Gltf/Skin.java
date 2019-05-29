/**
 * 
 */
package Gltf;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Skin {
	private int skeleton;
	private List<Node> joints;
	private int accessor;
	
	public Skin(int skeleton, List<Node> joints, int accessor) {
		this.skeleton = skeleton;
		this.joints = joints;
		this.accessor = accessor;
	}
	
	public JSONObject jsonify() {
		JSONObject j = new JSONObject();
		j.put("skeleton", skeleton);
		JSONArray jo = new JSONArray();
		for(int i=1;i<=joints.size();i++) {
			jo.add(i);
		}
		j.put("joints", jo);
		j.put("inverseBindMatrices", accessor);
		return j;
	}
	
	
}
