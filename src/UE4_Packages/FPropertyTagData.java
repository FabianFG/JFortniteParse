/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FPropertyTagData {
	
	public static class StructProperty extends FPropertyTagData {
		private String name;
		private FGUID guid;
		
		public StructProperty(String name, FGUID guid) {
			this.name = name;
			this.guid = guid;
		}

		public String getName() {
			return name;
		}

		public FGUID getGuid() {
			return guid;
		}
	}
	public static class BoolProperty extends FPropertyTagData {
		private boolean bool;
		
		public BoolProperty(boolean bool) {
			this.bool = bool;
		}

		public boolean getBool() {
			return bool;
		}

	}
	public static class ByteProperty extends FPropertyTagData {
		private String name;
		
		public ByteProperty(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}


	}
	public static class EnumProperty extends FPropertyTagData {
		private String property;
		
		public EnumProperty(String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}


	}
	public static class ArrayProperty extends FPropertyTagData {
		private String property;
		
		public ArrayProperty(String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}


	}
	public static class MapProperty extends FPropertyTagData {
		private String key;
		private String property;
		
		public MapProperty(String key,String property) {
			this.key = key;
			this.property = property;
		}

		public String getProperty() {
			return property;
		}

		public String getKey() {
			return key;
		}


	}
	public static class SetProperty extends FPropertyTagData {
		private String property;
		
		public SetProperty(String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}


	}
}
