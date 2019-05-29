/**
 * 
 */
package Gltf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class Gltf {
	private JSONObject root;
	private JSONObject asset;
	private RandomAccessFile bufferOut;
	private File bufferFile;
	private int bufferViewIndex = 0;
	
	public static void main(String[] args) throws IOException {
		Gltf test = new Gltf("PakBrowser", "2.0");
		FileOutputStream fos = new FileOutputStream("t.json");
		
		
		FileInputStream fin = new FileInputStream("D:\\Fabian\\Programme\\Fortnite\\UmodelSaved\\FortniteGame\\Content\\Characters\\Player\\Male\\Medium\\Bodies\\M_MED_Assassin\\Meshes\\vbuffer654950086222418592.bin");
		byte[] buff = new byte[fin.available()];
		fin.read(buff);
		fin.close();
		BufferView vbuf = new BufferView(buff, 0, 0, 0);
		List<BufferView> bufferViews = new ArrayList<>();
		bufferViews.add(vbuf);
		test.addBuffer("t.bin","", bufferViews);
		
		List<AccessorValue> accessors = new ArrayList<>();
		AccessorValue testAccessor = new AccessorValue("VEC3", 5854, GltfConstants.GL_FLOAT, 0, 0);
		testAccessor.setMax(new float[] {36.7142f, 150.138f, 17.6073f});
		testAccessor.setMin(new float[] {-36.7142f, -0.0000017801232f, -14.7683f});
		accessors.add(testAccessor);
		test.addAccessors(accessors);
		
		test.writeTo(fos);
		fos.close();
	}
	
	@SuppressWarnings("unchecked")
	public Gltf(String generator, String version) {
		this.root = new JSONObject();
		this.asset = new JSONObject();
		this.asset.put("generator", generator);
		this.asset.put("version", version);
		this.root.put("asset", this.asset);	
	}
	@SuppressWarnings("unchecked")
	public int addBuffer(String name, String outputFolder, List<BufferView> bufferViews) throws IOException {
		JSONArray bufferViewss = new JSONArray();
		bufferFile = new File(outputFolder + "/" + name);
		bufferOut = new RandomAccessFile(bufferFile, "rw");
		bufferViews = insertionSort(bufferViews);
		for(BufferView view : bufferViews) {
			bufferOut.write(view.getData());
			bufferViewss.add(view.jsonify());
		}
		root.put("bufferViews", bufferViewss);
		JSONArray buffers = new JSONArray();
		JSONObject b = new JSONObject();
		b.put("byteLength", bufferOut.length());
		b.put("uri", name);
		buffers.add(b);
		root.put("buffers", buffers);
		int length = (int) bufferOut.length();
		bufferOut.close();
		return length;
	}
	public void addAccessors(List<AccessorValue> accessors) {
		JSONArray accessorss = new JSONArray();
		for(AccessorValue v : accessors) {
			accessorss.add(v.jsonify());
		}
		root.put("accessors", accessorss);
	}
	public void addScenes(List<Scene> scenes) {
		JSONArray scene = new JSONArray();
		for(Scene v : scenes) {
			scene.add(v.jsonify());
		}
		root.put("scenes", scene);
		root.put("scene", 0);
	}
	public void addNodes(List<Node> nodes) {
		JSONArray nodess = new JSONArray();
		for(Node v : nodes) {
			nodess.add(v.jsonify());
		}
		root.put("nodes", nodess);
	}
	public void addSkins(List<Skin> skins) {
		JSONArray skinss = new JSONArray();
		for(Skin v : skins) {
			skinss.add(v.jsonify());
		}
		root.put("skins", skinss);
	}
	public void addMaterials(List<Material> materials) {
		JSONArray materialss = new JSONArray();
		for(Material v : materials) {
			materialss.add(v.jsonify());
		}
		root.put("materials", materialss);
	}
	public void addSamplers(List<Sampler> samplers) {
		JSONArray samplerss = new JSONArray();
		for(Sampler v : samplers) {
			samplerss.add(v.jsonify());
		}
		root.put("samplers", samplerss);
	}
	public void addTextures(List<Texture> textures) {
		JSONArray texturess = new JSONArray();
		for(Texture v : textures) {
			texturess.add(v.jsonify());
		}
		root.put("textures", texturess);
	}
	public void addImages(List<Image> images) {
		JSONArray imagess = new JSONArray();
		for(Image v : images) {
			imagess.add(v.jsonify());
		}
		root.put("images", imagess);
	}
	public void addMeshes(List<Mesh> meshes) {
		JSONArray meshess = new JSONArray();
		for(Mesh v : meshes) {
			meshess.add(v.jsonify());
		}
		root.put("meshes", meshess);
	}
	public int nextBufferViewIndex() {
		this.bufferViewIndex++;
		return this.bufferViewIndex-1;
	}
	
	public void writeTo(OutputStream os) throws IOException {
		os.write(root.toJSONString().getBytes());
		os.close();
	}
	
	private static List<BufferView> insertionSort(List<BufferView> a) {
	    for (int i=1;i<a.size() ; i++) {
	      BufferView merke = a.get(i);
	      int j=i;
	      while (j>0 && a.get(j-1).getByteOffset()>merke.getByteOffset()) {
	    	 a.set(j, a.get(j-1));
	         j=j-1;
	      } // end of while
	      a.set(j, merke);
	    } // end of for
	    return a;
	  }
}
