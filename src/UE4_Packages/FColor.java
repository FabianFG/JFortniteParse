/**
 * 
 */
package UE4_Packages;

import java.util.Arrays;

/**
 * @author FunGames
 *
 */
public class FColor {
	private byte r;
	private byte g;
	private byte b;
	private byte a;

	public FColor(FArchive Ar) throws ReadException {
		r = Ar.readUInt8();
		g = Ar.readUInt8();
		b = Ar.readUInt8();
		a = Ar.readUInt8();
	}

	public byte getR() {
		return r;
	}

	public byte getG() {
		return g;
	}

	public byte getB() {
		return b;
	}

	public byte getA() {
		return a;
	}

}
