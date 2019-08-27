/**
 * 
 */
package UE4_Assets;
import annotation.FName;
import annotation.Serializable;
import annotation.UInt8Boolean;
import lombok.Data;


/**
 * @author FunGames
 *
 */
public interface FPropertyTagData {
	
	@Data
	@Serializable
	public static class StructProperty implements FPropertyTagData {
		@FName private String name;
		private FGUID guid;

	}
	
	@Data
	@Serializable
	public static class BoolProperty implements FPropertyTagData {
		@UInt8Boolean private boolean bool;
	}
	
	@Data
	@Serializable
	public static class ByteProperty implements FPropertyTagData {
		@FName private String name;
	}
	
	@Data
	@Serializable
	public static class EnumProperty implements FPropertyTagData {
		@FName private String property;
	}
	
	@Data
	@Serializable
	public static class ArrayProperty implements FPropertyTagData {
		@FName private String property;
	}
	
	@Data
	@Serializable
	public static class MapProperty implements FPropertyTagData {
		@FName private String key;
		@FName private String property;
	}
	
	@Data
	@Serializable
	public static class SetProperty implements FPropertyTagData {
		@FName private String property;
	}
}
