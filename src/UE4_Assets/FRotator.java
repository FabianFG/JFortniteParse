/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FRotator {
	private float pitch;
	private float yaw;
	private float roll;
	
	public FRotator(FArchive Ar) throws ReadException {
		pitch = Ar.readFloat32();
		yaw = Ar.readFloat32();
		roll = Ar.readFloat32();
	}

	public float getRoll() {
		return roll;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}
}
