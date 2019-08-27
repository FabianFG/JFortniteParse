/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import annotation.Float32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FQuat {
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
}
