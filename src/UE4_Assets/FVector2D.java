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
public class FVector2D {
	@Float32 private float x;
	@Float32 private float y;
	
	public FVector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public List<Float> getTuple() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		return f;
	}
}
