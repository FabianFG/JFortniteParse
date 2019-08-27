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
public class FVector {
	@Float32 private float x;
	@Float32 private float y;
	@Float32 private float z;
	
	public List<Float> getTuple() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		f.add(z);
		return f;
	}
}
