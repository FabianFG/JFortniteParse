/**
 * 
 */
package UE4_Assets;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPakArchive extends FArchive {

	private RandomAccessFile file;
	private String fileName;

	protected long ArPos;
	protected long ArStopper;
	
	@Override
	public FPakArchive clone() {
		FPakArchive c = new FPakArchive();
		c.file = file;
		c.ArPos = ArPos;
		c.ArStopper = ArStopper;
		return c;
	}

	public FPakArchive(RandomAccessFile file) throws IOException, ReadException {
		super();
		this.file = file;
		
		this.SetStopper64(file.length());
		this.Seek64(0);
	}
	public FPakArchive() {
		super();
		this.ArPos = 0;
		this.ArStopper = 0;
	}
	
	public long Tell64() {
		return this.ArPos;
	}
	
	public long GetStopper64() {
		return this.ArStopper;
	}

	public void SetStopper64(long stopper) {
		this.ArStopper = stopper;
	}

	public void Seek64(long Pos) throws ReadException {
		if (Pos <= this.ArStopper) {
			this.ArPos = Pos;
			try {
				this.file.seek(Pos);
			} catch (IOException e) {
				throw new ReadException(String.format("Seeking to %d failed", Pos));
			}
		} else {
			throw new ReadException(String.format("Seeking behind stopper (%d > %d", Pos, this.ArStopper));
		}
	}

	@Override
	public byte[] serialize(int size) throws ReadException {
		byte[] res = new byte[size];
		if ((this.ArPos + size) <= this.ArStopper) {
			try {
				this.file.read(res);
			} catch (IOException e) {
				throw new ReadException(
						String.format("Serializing of file failed (Couldn't read %d bytes at %d)", size, this.ArPos));
			}
			this.ArPos += size;
			return res;
		} else {
			throw new ReadException(
					String.format("Serializing behind stopper (%d + %d > %d)", this.ArPos, size, this.ArStopper));
		}
	}

	@Override
	public boolean IsA(String type) {
		return "FPakArchive".equals(type);
	}

	@Override
	public boolean IsStopper() {
		int stopper = GetStopper();
		return (stopper != 0) && (Tell() == stopper);
	}

}
