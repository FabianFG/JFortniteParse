/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import annotation.Float32;
import annotation.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
@AllArgsConstructor
public class FVector4 {
	@Float32 private float x;
	@Float32 private float y;
	@Float32 private float z;
	@Float32 private float w;
	
	public List<Float> getTuple() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		f.add(z);
		f.add(w);
		return f;
	}
	
	public List<Float> getTuple3() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		f.add(z);
		return f;
	}
	public FVector4 getNormal() {
		float length = (float) Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
		if(length == 0.0f) {
			return new FVector4(0.0f, 0.0f, 0.0f , 0.0f);
		} else {
			float x = this.x / length;
			float y = this.y / length;
			float z = this.z / length;
			float w;
			if(this.w > 0.0f) {
				w = -1.0f;
			} else {
				w = 1.0f;
			}
			return new FVector4(x, y, z, w);
		}
	}
}
