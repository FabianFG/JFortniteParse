/**
 * 
 */
package Converters;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;

import Gltf.AccessorValue;
import Gltf.BufferView;
import Gltf.Gltf;
import Gltf.GltfConstants;
import Gltf.Image;
import Gltf.Material;
import Gltf.Mesh;
import Gltf.Node;
import Gltf.Sampler;
import Gltf.Scene;
import Gltf.Skin;
import Gltf.Texture;
import UE4.GameFile;
import UE4.PakFileReader;
import UE4_Packages.FBinaryWriter;
import UE4_Packages.FMeshBoneInfo;
import UE4_Packages.FObjectImport;
import UE4_Packages.FPackedNormal;
import UE4_Packages.FPropertyTag;
import UE4_Packages.FPropertyTagType;
import UE4_Packages.FPropertyTagType.ArrayProperty;
import UE4_Packages.FPropertyTagType.NameProperty;
import UE4_Packages.FPropertyTagType.ObjectProperty;
import UE4_Packages.FPropertyTagType.StructProperty;
import UE4_Packages.FReferenceSkeleton;
import UE4_Packages.FSkelMeshRenderSection;
import UE4_Packages.FSkeletalMaterial;
import UE4_Packages.FSkeletalMeshRenderData;
import UE4_Packages.FSkinWeightInfo;
import UE4_Packages.FSkinWeightVertexBuffer;
import UE4_Packages.FStaticMeshVertexDataUV;
import UE4_Packages.FStructFallback;
import UE4_Packages.FTransform;
import UE4_Packages.FVector;
import UE4_Packages.FVector2D;
import UE4_Packages.FVector2DHalf;
import UE4_Packages.Package;
import UE4_Packages.ReadException;
import UE4_Packages.TStaticMeshVertexTangent;
import UE4_Packages.TStaticMeshVertexUV;
import UE4_Packages.UObject;
import UE4_Packages.USkeletalMesh4;
import UE4_Packages.UTexture2D;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

/**
 * @author FunGames
 *
 */
public class SkeletalMeshToGltf {

	private static List<BufferView> bufferViews = new ArrayList<>();
	private static List<AccessorValue> accessors = new ArrayList<>();
	private static List<Node> nodes = new ArrayList<>();
	private static List<Scene> scenes = new ArrayList<>();
	private static List<Mesh> meshes = new ArrayList<>();
	private static List<Skin> skins = new ArrayList<>();
	private static List<Material> materials = new ArrayList<>();
	private static List<Sampler> samplers = new ArrayList<>();
	private static List<Image> images = new ArrayList<>();
	private static List<Texture> textures = new ArrayList<>();

	public static File decodeMesh(Package meshPackage, Map<String, Integer> pakFileReaderIndices,
			PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles, String mountPrefix, String outputFolder)
			throws ReadException, ClassCastException, IOException, URISyntaxException {
		Gltf gltf = new Gltf("PakBrowser", "2.0");
		if (!meshPackage.getExports().isEmpty() && meshPackage.getExports().get(0) instanceof USkeletalMesh4) {
			USkeletalMesh4 mesh = (USkeletalMesh4) meshPackage.getExports().get(0);
			String materialName = ((FSkeletalMaterial) mesh.getMaterials().get(0)).getInterface();
			String materialPath = getImportPathByName(meshPackage, materialName);
			boolean materialLoaded = false;
			materialPath = Package.toGameSpecificName(materialPath, mountPrefix);
			if (materialPath != null) {
				Package materialPackage = Package.loadPackageByName(materialPath, pakFileReaderIndices, loadedPaks,
						gameFiles);
				Material material = loadMaterial(gltf, materialName, materialPackage, pakFileReaderIndices, loadedPaks,
						gameFiles, mountPrefix, outputFolder);
				if(material != null) {
					materialLoaded = true;
					materials.add(material);
				}
				
			} else {
				System.err.println("Unable to find material for " + meshPackage.getName());
			}

			FSkeletalMeshRenderData lod = mesh.getFirstLod();
			if (lod != null) {
				List<FVector> positionVerts = lod.getPositionVertexBuffer().getVerts();
				byte[] vertsBuffer = writeVertsBuffer(positionVerts);
				BufferView positionBufferView = new BufferView(vertsBuffer, 0, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(positionBufferView);
				AccessorValue positionAccessor = new AccessorValue("VEC3", positionVerts.size(), GltfConstants.GL_FLOAT,
						positionBufferView.getBufferViewIndex(), 0);
				float[] min = getVertMinimum(positionVerts);
				positionAccessor.setMin(min);
				float[] max = getVertMaximum(positionVerts);
				positionAccessor.setMax(max);
				accessors.add(positionAccessor);
				int positionSize = positionVerts.size() * 3 * 4;

				List indices = lod.getIndices().getIndices();
				int startPos = positionSize;
				byte[] indexBuffer = null;
				switch (indices.get(0).getClass().getSimpleName()) {
				case "Integer":
					indexBuffer = writeU16Buffer(indices);
					break;
				case "Long":
					indexBuffer = writeU32Buffer(indices);
					break;
				}
				BufferView indexBufferView = new BufferView(indexBuffer, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(indexBufferView);
				AccessorValue indexAccessor = new AccessorValue("SCALAR", indices.size(),
						GltfConstants.GL_UNSIGNED_SHORT, indexBufferView.getBufferViewIndex(), 0);
				accessors.add(indexAccessor);
				List<TStaticMeshVertexTangent> tangents = lod.getStaticMeshVertexBuffer().getTangents().getTangents();
				TStaticMeshVertexTangent d = (tangents.get(0));
				List<FPackedNormal> tangentVector = new ArrayList<>();
				if (d.usesFPackedNormal()) {
					for (TStaticMeshVertexTangent ll : tangents) {
						tangentVector.add(ll.getTangent_FPackedNormal());
					}
				} else {
					System.err.println("FPackedRGBA16N is not supported tangent precision");
				}
				startPos += indexBuffer.length;
				byte[] tangentBuffer = writeVertsBuffer4(tangentVector);
				BufferView tangentBufferView = new BufferView(tangentBuffer, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(tangentBufferView);
				AccessorValue tangentAccessor = new AccessorValue("VEC4", tangentVector.size(), GltfConstants.GL_FLOAT,
						tangentBufferView.getBufferViewIndex(), 0);
				accessors.add(tangentAccessor);
				
				TStaticMeshVertexTangent d2 = (tangents.get(0));
				List<FPackedNormal> normalVector = new ArrayList<>();
				if (d.usesFPackedNormal()) {
					for (TStaticMeshVertexTangent ll : tangents) {
						normalVector.add(ll.getNormal_FPackedNormal());
					}
				} else {
					System.err.println("FPackedRGBA16N is not supported normal precision");
				}

				startPos += tangentBuffer.length;
				byte[] normalBuffer = writeVertsBuffer43(normalVector);
				BufferView normalBufferView = new BufferView(normalBuffer, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(normalBufferView);
				AccessorValue normalAccessor = new AccessorValue("VEC3", tangentVector.size(), GltfConstants.GL_FLOAT,
						normalBufferView.getBufferViewIndex(), 0);
				accessors.add(normalAccessor);

				FStaticMeshVertexDataUV uvs = lod.getStaticMeshVertexBuffer().getUvs();
				TStaticMeshVertexUV f = (TStaticMeshVertexUV) uvs.getVertexDataUV().get(0);
				List<FVector2D> uvValues = new ArrayList<>();
				if (f.usesFVector2D()) {
					for (TStaticMeshVertexUV ff : uvs.getVertexDataUV()) {
						FVector2D fff = ff.getValue_FVector2D();
						uvValues.add(fff);
					}
				} else {
					for (TStaticMeshVertexUV ff : uvs.getVertexDataUV()) {
						FVector2DHalf fff = ff.getValue_FVector2DHalf();
						uvValues.add(fff.getVector());
					}
				}
				startPos += normalBuffer.length;
				byte[] uvBuffer = writeVert2Buffer(uvValues);
				BufferView uvBufferView = null;
				AccessorValue uvAccessor = null;
				if (uvBuffer != null && uvBuffer.length > 0) {
					uvBufferView = new BufferView(uvBuffer, startPos, gltf.nextBufferViewIndex(), 0);
					bufferViews.add(uvBufferView);
					uvAccessor = new AccessorValue("VEC2", uvValues.size(), GltfConstants.GL_FLOAT,
							uvBufferView.getBufferViewIndex(), 0);
					accessors.add(uvAccessor);
					startPos += uvBuffer.length;
				}
				FSkinWeightVertexBuffer weightBuffer = lod.getSkinWeightVertexBuffer();

				byte[] jointsBuffer1 = writeJointsBuffer(weightBuffer, lod.getSections(), 0);
				BufferView jointsBufferView1 = new BufferView(jointsBuffer1, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(jointsBufferView1);
				AccessorValue jointsAccessor1 = new AccessorValue("VEC4", weightBuffer.getWeights().size(),
						GltfConstants.GL_UNSIGNED_SHORT, jointsBufferView1.getBufferViewIndex(), 0);
				accessors.add(jointsAccessor1);
				startPos += jointsBuffer1.length;
				byte[] weightBuffer1 = writeWeightsBuffer(weightBuffer, startPos, 0);
				BufferView weightBufferView1 = new BufferView(weightBuffer1, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(weightBufferView1);
				AccessorValue weightsAccessor1 = new AccessorValue("VEC4", weightBuffer.getWeights().size(),
						GltfConstants.GL_UNSIGNED_BYTE, weightBufferView1.getBufferViewIndex(), 0);
				weightsAccessor1.addAdditionalProperty("normalized", true);
				accessors.add(weightsAccessor1);
				startPos += weightBuffer1.length;

				List<AccessorValue> weightAccs = new ArrayList<>();
				weightAccs.add(jointsAccessor1);
				weightAccs.add(weightsAccessor1);

				byte[] jointsBuffer2 = writeJointsBuffer(weightBuffer, lod.getSections(), 4);
				BufferView jointsBufferView2 = new BufferView(jointsBuffer2, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(jointsBufferView2);
				AccessorValue jointsAccessor2 = new AccessorValue("VEC4", weightBuffer.getWeights().size(),
						GltfConstants.GL_UNSIGNED_SHORT, jointsBufferView2.getBufferViewIndex(), 0);
				accessors.add(jointsAccessor2);
				startPos += jointsBuffer2.length;
				byte[] weightBuffer2 = writeWeightsBuffer(weightBuffer, startPos, 4);
				BufferView weightBufferView2 = new BufferView(weightBuffer2, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(weightBufferView2);
				AccessorValue weightsAccessor2 = new AccessorValue("VEC4", weightBuffer.getWeights().size(),
						GltfConstants.GL_UNSIGNED_BYTE, weightBufferView2.getBufferViewIndex(), 0);
				weightsAccessor2.addAdditionalProperty("normalized", true);
				accessors.add(weightsAccessor2);
				startPos += weightBuffer2.length;

				List<AccessorValue> weightAccs2 = new ArrayList<>();
				weightAccs2.add(jointsAccessor2);
				weightAccs2.add(weightsAccessor2);

				Mesh meshV = new Mesh();
				meshV.addAttribute("POSITION", positionAccessor.getBufferView());
				meshV.addAttribute("TANGENT", tangentAccessor.getBufferView());
				meshV.addAttribute("NORMAL", normalAccessor.getBufferView());
				meshV.addAttribute("TEXCOORD_0", uvAccessor.getBufferView());
				meshV.addAttribute("JOINTS_0", weightAccs.get(0).getBufferView());
				meshV.addAttribute("WEIGHTS_0", weightAccs.get(1).getBufferView());
				meshV.addAttribute("JOINTS_1", weightAccs2.get(0).getBufferView());
				meshV.addAttribute("WEIGHTS_1", weightAccs2.get(1).getBufferView());
				
				meshV.addProperty("indices", 1);
				if(materialLoaded) {
					meshV.addProperty("material", 0);
				}
				meshes.add(meshV);

				Node meshNode = new Node();
				meshNode.addProperty("mesh", 0);
				nodes.add(meshNode);
				Scene scene = new Scene("MainScene", new int[] { 0 });
				scenes.add(scene);

				// Skeleton

				FReferenceSkeleton skeleton = mesh.getRefSkeleton();
				List<FMeshBoneInfo> boneInfo = skeleton.getRefBoneInfo();

				List<FTransform> bonePose = skeleton.getRefBonePose();

				assert (boneInfo.size() == bonePose.size());

				List<Node> boneNodes = new ArrayList<>();
				for (FTransform transform : bonePose) {
					Node boneNode = new Node();

					List<Float> transformTuple = transformTranslationTuple(transform.getTranslation().getTuple());
					JSONArray translationArray = new JSONArray();
					translationArray.add(transformTuple.get(0));
					translationArray.add(transformTuple.get(1));
					translationArray.add(transformTuple.get(2));
					boneNode.addProperty("translation", translationArray);

					List<Float> rotationTuple = transformRotationTuple(transform.getRotation().getTuple());
					JSONArray rotationArray = new JSONArray();
					rotationArray.add(rotationTuple.get(0));
					rotationArray.add(rotationTuple.get(1));
					rotationArray.add(rotationTuple.get(2));
					rotationArray.add(rotationTuple.get(3));
					boneNode.addProperty("rotation", rotationArray);
					boneNodes.add(boneNode);
				}

				for (int i = 0; i < boneInfo.size(); i++) {
					FMeshBoneInfo nodeInfo = (FMeshBoneInfo) boneInfo.get(i);
					Node node = boneNodes.get(i);
					String tName = nodeInfo.getName();
					node.addProperty("name", tName);
					if (nodeInfo.getParentIndex() != -1) {
						Node parentNode = boneNodes.get(nodeInfo.getParentIndex());
						parentNode.addChildren(i + 1);
						boneNodes.set(nodeInfo.getParentIndex(), parentNode);
					}
					boneNodes.set(i, node);
				}
				List<FMeshBoneInfo> boneInfoList = new ArrayList<>();
				for (Object ds : boneInfo) {
					boneInfoList.add((FMeshBoneInfo) ds);
				}
				for (Node n : boneNodes) {
					nodes.add(n);
				}

				FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
				for (int i = 0; i < boneInfo.size(); i++) {
					System.out.println(i);
					Mat4 bindMatrix = calculateBindMatrix(i, boneInfoList, boneNodes);
					writeMatrix(bindMatrix, bos);
				}

				assert (bos.size() == boneInfo.size() * 16 * 4);

				byte[] matrixBuffer = bos.toByteArray();
				BufferView matrixBufferView = new BufferView(matrixBuffer, startPos, gltf.nextBufferViewIndex(), 0);
				bufferViews.add(matrixBufferView);
				AccessorValue matrixAccessor = new AccessorValue("MAT4", boneInfo.size(),
						GltfConstants.GL_FLOAT, matrixBufferView.getBufferViewIndex(), 0);
				accessors.add(matrixAccessor);

				Node rootNode = nodes.get(0);
				// Order in the joints array needs to exactly match the bone indices in the
				// original data
				// so that the joints and weights buffers are correct
				Skin skin = new Skin(1, boneNodes, matrixAccessor.getBufferView());
				skins.add(skin);
				rootNode.addProperty("skin", 0);
				rootNode.addChildren(1);
				nodes.set(0, rootNode);

				gltf.addBuffer(meshPackage.getName() + ".bin", outputFolder, bufferViews);
				gltf.addAccessors(accessors);
				gltf.addNodes(nodes);
				gltf.addScenes(scenes);
				gltf.addMeshes(meshes);
				gltf.addSkins(skins);
				gltf.addMaterials(materials);
				gltf.addSamplers(samplers);
				gltf.addImages(images);
				gltf.addTextures(textures);
				bufferViews.clear();
				accessors.clear();
				nodes.clear();
				scenes.clear();
				meshes.clear();
				skins.clear();
				materials.clear();
				samplers.clear();
				images.clear();
				textures.clear();
				File out = new File(outputFolder + "/" + meshPackage.getName() + ".gltf");
				gltf.writeTo(new FileOutputStream(out));
				return out;
			} else {
				System.err.println("No lod was exported, unable to create gltf");
			}

		}
		return null;
	}
	

	/**
	 * @param bindMatrix
	 * @param bos
	 * @throws IOException
	 */
	private static void writeMatrix(Mat4 mat, FBinaryWriter bos) throws IOException {
		writeGlmVec(mat.get(0), bos);
		writeGlmVec(mat.get(1), bos);
		writeGlmVec(mat.get(2), bos);
		writeGlmVec(mat.get(3), bos);
	}

	/**
	 * @param vec4
	 * @param bos
	 * @throws IOException
	 */
	private static void writeGlmVec(Vec4 vec4, FBinaryWriter bos) throws IOException {
		bos.writeFloat32(vec4.getX());
		bos.writeFloat32(vec4.getY());
		bos.writeFloat32(vec4.getZ());
		bos.writeFloat32(vec4.getW());
	}

	/**
	 * @param weightBuffer
	 * @param gltf
	 * @param sections
	 * @param off
	 * @throws IOException
	 */
	private static List<AccessorValue> makeWeightAccessors(FSkinWeightVertexBuffer weights, int startPos, Gltf gltf,
			List<FSkelMeshRenderSection> sections, int off) throws IOException {
		byte[] jointsBuffer = writeJointsBuffer(weights, sections, off);
		BufferView jointsBufferView = new BufferView(jointsBuffer, startPos, gltf.nextBufferViewIndex(), 0);
		bufferViews.add(jointsBufferView);
		AccessorValue jointsAccessor = new AccessorValue("VEC4", weights.getWeights().size(),
				GltfConstants.GL_UNSIGNED_SHORT, jointsBufferView.getBufferViewIndex(), 0);
		accessors.add(jointsAccessor);
		startPos += jointsBuffer.length;

		byte[] weightBuffer = writeWeightsBuffer(weights, startPos, off);
		BufferView weightBufferView = new BufferView(weightBuffer, startPos, gltf.nextBufferViewIndex(), 0);
		bufferViews.add(weightBufferView);
		AccessorValue weightsAccessor = new AccessorValue("VEC4", weights.getWeights().size(),
				GltfConstants.GL_UNSIGNED_BYTE, weightBufferView.getBufferViewIndex(), 0);
		weightsAccessor.addAdditionalProperty("normalized", true);
		accessors.add(weightsAccessor);
		startPos += weightBuffer.length;

		List<AccessorValue> res = new ArrayList<>();
		res.add(jointsAccessor);
		res.add(weightsAccessor);
		return res;
	}

	/**
	 * @param weights
	 * @param startPos
	 * @param off
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeWeightsBuffer(FSkinWeightVertexBuffer weights, int startPos, int off)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		List<FSkinWeightInfo> weightsList = weights.getWeights();
		for (FSkinWeightInfo weight : weightsList) {
			byte[] index = weight.getBoneWeight();
			bos.write(index[0 + off]);
			bos.write(index[1 + off]);
			bos.write(index[2 + off]);
			bos.write(index[3 + off]);
		}
		return bos.toByteArray();
	}

	/**
	 * @param weights
	 * @param sections
	 * @param off
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeJointsBuffer(FSkinWeightVertexBuffer weights, List<FSkelMeshRenderSection> sections, int off)
			throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());

		List<FSkinWeightInfo> weightsList = weights.getWeights();

		for (FSkelMeshRenderSection section : sections) {
			List<Integer> boneMap = section.getBoneMap();
			for (int i = 0; i < section.getNumVertices(); i++) {
				if (weightsList.size() >= i + section.getBaseIndex()) {
					FSkinWeightInfo weight = weightsList.get(i + section.getBaseIndex());
					byte[] index = weight.getBoneIndex();
					bos.writeUInt16(boneMap.get(index[0 + off]));
					bos.writeUInt16(boneMap.get(index[1 + off]));
					bos.writeUInt16(boneMap.get(index[2 + off]));
					bos.writeUInt16(boneMap.get(index[3 + off]));
				} else {
					bos.write(new byte[] { 0x36, 0x00 });
					bos.write(new byte[] { 0x36, 0x00 });
					bos.write(new byte[] { 0x36, 0x00 });
					bos.write(new byte[] { 0x36, 0x00 });
				}
			}

		}
		return bos.toByteArray();
	}

	/**
	 * @param uvValues
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeVert2Buffer(List<FVector2D> uvValues) throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
		for (int i = 0; i < uvValues.size(); i++) {
			FVector2D v = uvValues.get(i);
			List<Float> vert = v.getTuple();
			// Unreal is left-handed, GLTF is right handed..... or the other way around
			bos.writeFloat32(vert.get(0));
			bos.writeFloat32(vert.get(1));
		}
		return bos.toByteArray();
	}

	/**
	 * @param tangentVector
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeVertsBuffer43(List<FPackedNormal> tangentVector) throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
		for (int i = 0; i < tangentVector.size(); i++) {
			FPackedNormal v = tangentVector.get(i);
			List<Float> vert = v.getVector().getNormal().getTuple3();
			// Unreal is left-handed, GLTF is right handed..... or the other way around
			bos.writeFloat32(vert.get(0));
			bos.writeFloat32(vert.get(2));
			bos.writeFloat32(vert.get(1));
		}
		return bos.toByteArray();
	}

	/**
	 * @param tangentVector
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeVertsBuffer4(List<FPackedNormal> tangentVector) throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
		for (int i = 0; i < tangentVector.size(); i++) {
			FPackedNormal v = tangentVector.get(i);
			List<Float> vert = v.getVector().getNormal().getTuple();
			// Unreal is left-handed, GLTF is right handed..... or the other way around
			bos.writeFloat32(vert.get(0));
			bos.writeFloat32(vert.get(2));
			bos.writeFloat32(vert.get(1));
			bos.writeFloat32(vert.get(3));
		}
		return bos.toByteArray();
	}

	/**
	 * @param indices
	 */
	/**
	 * @param indices
	 * @return
	 * @throws IOException
	 */
	private static byte[] writeU32Buffer(List<Long> indices) throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
		for (long i : indices) {	
			bos.writeUInt32(i);
		}
		return bos.toByteArray();
	}

	private static byte[] writeU16Buffer(List<Integer> indices) throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
		for (int i : indices) {	
			bos.writeUInt16(i);
		}
		return bos.toByteArray();
	}

	private static Material loadMaterial(Gltf gltf, String materialName, Package materialPackage,
			Map<String, Integer> pakFileReaderIndices, PakFileReader[] loadedPaks, Map<String, GameFile> gameFiles,
			String mountPrefix, String outputFolder) throws ReadException, ClassCastException, IOException {
		UObject materialExport = materialPackage.getuObjects().get(0);
		FPropertyTagType tag = materialExport.getPropertyByName("TextureParameterValues");
		if (!(tag instanceof FPropertyTagType.ArrayProperty)) {
			throw new ReadException("TextureParameterValues must be instance of ArrayProperty", -1);
		}
		ArrayProperty textureVals = (ArrayProperty) tag;
		Map<String, String> textureNames = new HashMap<>();
		for (int i = 0; i < textureVals.getNumber().getData().size(); i++) {
			StructProperty valStruct = (StructProperty) textureVals.getNumber().getData().get(i);
			String textureName = null;
			for (FPropertyTag textureNameTag : ((FStructFallback) valStruct.getStruct().getStructType())
					.getProperties()) {
				if (textureNameTag.getName().equals("ParameterValue")) {
					textureName = ((ObjectProperty) textureNameTag.getTag()).getStruct().getImportName();
					break;
				}
			}
			String textureType = null;
			for (FPropertyTag textureTypeTag : ((FStructFallback) valStruct.getStruct().getStructType())
					.getProperties()) {
				if (textureTypeTag.getName().equals("ParameterInfo")) {
					StructProperty t = (StructProperty) textureTypeTag.getTag();
					for (FPropertyTag nameTag : ((FStructFallback) t.getStruct().getStructType()).getProperties()) {
						if (nameTag.getName().equals("Name")) {
							textureType = ((NameProperty) nameTag.getTag()).getText();
						}
					}
					break;
				}
			}
			if (textureName != null && textureType != null) {
				textureNames.put(textureType, textureName);
			}

		}
		System.out.println("");
		if (textureNames.containsKey("Diffuse") && textureNames.containsKey("Normals")) {
			String diffuseName = textureNames.get("Diffuse");
			String diffusePath = getImportPathByName(materialPackage, diffuseName);
			diffusePath = Package.toGameSpecificName(diffusePath, mountPrefix);
			Package diffusePackage = Package.loadPackageByName(diffusePath, pakFileReaderIndices, loadedPaks,
					gameFiles);
			Image diffuseImg = null;
			if (diffusePackage != null && !diffusePackage.getExports().isEmpty()
					&& diffusePackage.getExports().get(0) instanceof UTexture2D) {
				UTexture2D diffuseTexture = (UTexture2D) diffusePackage.getExports().get(0);
				BufferedImage diffuseImage = Texture2DToBufferedImage.readTexture(diffuseTexture);
				File diffuseTemp = new File(outputFolder + "/" + diffuseName + ".png");
				FileOutputStream fos = new FileOutputStream(diffuseTemp);
				ImageIO.write(diffuseImage, "png", fos);
				fos.close();

				diffuseImg = new Image(diffuseName + ".png");

			}
			String normalName = textureNames.get("Normals");
			String normalPath = getImportPathByName(materialPackage, normalName);
			normalPath = Package.toGameSpecificName(normalPath, mountPrefix);
			Package normalPackage = Package.loadPackageByName(normalPath, pakFileReaderIndices, loadedPaks, gameFiles);
			Image normalImg = null;
			if (normalPackage != null && !normalPackage.getExports().isEmpty()
					&& normalPackage.getExports().get(0) instanceof UTexture2D) {
				UTexture2D normalTexture = (UTexture2D) normalPackage.getExports().get(0);
				BufferedImage normalImage = Texture2DToBufferedImage.readTexture(normalTexture);
				if (normalImage != null) {
					File normalTemp = new File(outputFolder + "/" + normalName + ".png");
					FileOutputStream fos = new FileOutputStream(normalTemp);
					ImageIO.write(normalImage, "png", fos);
					fos.close();
					normalImg = new Image(normalName + ".png");

				}
			}
			if (diffuseImg != null && normalImg != null) {

				images.add(diffuseImg);
				images.add(normalImg);
				Sampler gltfSampler = Sampler.DEFAULT_SAMPLER;
				samplers.add(gltfSampler);
				Texture diffuseTexture = new Texture(0, 0);
				textures.add(diffuseTexture);
				Texture normalTexture = new Texture(1, 0);
				textures.add(normalTexture); 
				Material material = new Material(0, 1);
				
				return material;

			}
		}
		return null;

	}

	private static byte[] writeVertsBuffer(List<FVector> verts) throws IOException {
		FBinaryWriter bos = new FBinaryWriter(new ByteArrayOutputStream());
		for (int i = 0; i < verts.size(); i++) {
			FVector v = (FVector) verts.get(i);
			List<Float> vert = transformTranslationTuple(v.getTuple());
			// Unreal is left-handed, GLTF is right handed..... or the other way around
			bos.writeFloat32(vert.get(0));
			bos.writeFloat32(vert.get(1));
			bos.writeFloat32(vert.get(2));
		}
		return bos.toByteArray();
	}

	private static float[] getVertMinimum(List<FVector> verts) throws IOException {
		List<Float> vec = transformTranslationTuple(verts.get(0).getTuple());
		for (int i = 0; i < verts.size(); i++) {
			FVector vert = (FVector) verts.get(i);
			List<Float> comp = transformTranslationTuple(vert.getTuple());
			if (comp.get(0) < vec.get(0)) {
				vec.add(0, comp.get(0));
				vec.remove(1);
			}
			if (comp.get(1) < vec.get(1)) {
				vec.add(1, comp.get(1));
				vec.remove(2);
			}
			if (comp.get(2) < vec.get(2)) {
				vec.add(2, comp.get(2));
				vec.remove(3);
			}
			// Unreal is left-handed, GLTF is right handed..... or the other way around
		}
		return new float[] { vec.get(0), vec.get(1), vec.get(2) };
	}

	private static float[] getVertMaximum(List<FVector> verts) throws IOException {
		List<Float> vec = transformTranslationTuple(verts.get(0).getTuple());
		for (int i = 0; i < verts.size(); i++) {
			FVector vert = (FVector) verts.get(i);
			List<Float> comp = transformTranslationTuple(vert.getTuple());
			if (comp.get(0) > vec.get(0)) {
				vec.add(0, comp.get(0));
				vec.remove(1);
			}
			if (comp.get(1) > vec.get(1)) {
				vec.add(1, comp.get(1));
				vec.remove(2);
			}
			if (comp.get(2) > vec.get(2)) {
				vec.add(2, comp.get(2));
				vec.remove(3);
			}
			// Unreal is left-handed, GLTF is right handed..... or the other way around
		}
		return new float[] { vec.get(0), vec.get(1), vec.get(2) };
	}

	private static List<Float> transformTranslationTuple(List<Float> v) {
		List<Float> f = new ArrayList<>();
		f.add((float) (v.get(0) * 0.01));
		f.add((float) (v.get(2) * 0.01));
		f.add((float) (v.get(1) * 0.01));
		return f;
	}

	private static List<Float> transformRotationTuple(List<Float> v) {
		List<Float> f = new ArrayList<>();
		// f.add(v.get(0));
		// f.add(v.get(2));
		// f.add(v.get(1));
		f.add((float) v.get(0));
		f.add((float) v.get(2));
		f.add((float) v.get(1));
		f.add((float) (v.get(3) * -1.0f));
		return f;
	}

	private static Mat4 calculateBindMatrix(int nodeIndex, List<FMeshBoneInfo> boneList, List<Node> boneNodes) {
		Map<Vec3, Vec4> transforms = new LinkedHashMap<>();
		int activeIndex = nodeIndex;
		while (activeIndex != -1) {
			Node node = boneNodes.get(activeIndex);
			transforms.put(node.getTranslation(), node.getRotation());
			activeIndex = boneList.get(activeIndex).getParentIndex();
		}
		Mat4 finalMat = null;
		Mat4 transformMatrix = new Mat4(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);
		ArrayList<Vec3> keys = new ArrayList<Vec3>(transforms.keySet());
		Mat4 previous = transformMatrix;
		for (int i = keys.size() - 1; i >= 0; i--) {
			Vec3 translation = keys.get(i);
			Vec4 rotation = transforms.get(translation);
			Mat4 translate = getTranslationMatrix(translation);
			Mat4 rotate = getRotationMatrix(rotation);
			finalMat = previous.times(translate).times(rotate);
			previous = finalMat;
		}
		Mat4 inverse = finalMat.inverse();
		// I think glm::inverse has a precision issue - without setting it results in
		// values like 0.999999 and 1.000001, which cause issues.
		// DONT KNOW WHETHER THIS IS TRUE FOR KOTLINGLM
		Vec4 f = inverse.get(3);
		f.set(3, 1.0);
		inverse.set(3, f);
		return inverse;
	}

	/**
	 * @param rotation
	 * @return
	 */
	private static Mat4 getRotationMatrix(Vec4 rotation) {
		List<Float> q = normalizeQuat(rotation);
		float qx = q.get(0);
		float qy = q.get(1);
		float qz = q.get(2);
		float qw = q.get(3);
		float x0 = 1.0f - 2.0f * qy * qy - 2.0f * qz * qz;
		float y0 = 2.0f * qx * qy - 2.0f * qz * qw;
		float z0 = 2.0f * qx * qz + 2.0f * qy * qw;
		float w0 = 0.0f;
		float x1 = 2.0f * qx * qy + 2.0f * qz * qw;
		float y1 = 1.0f - 2.0f * qx * qx - 2.0f * qz * qz;
		float z1 = 2.0f * qy * qz - 2.0f * qx * qw;
		float w1 = 0.0f;
		float x2 = 2.0f * qx * qz - 2.0f * qy * qw;
		float y2 = 2.0f * qy * qz + 2.0f * qx * qw;
		float z2 = 1.0f - 2.0f * qx * qx - 2.0f * qy * qy;
		float w2 = 0.0f;
		float x3 = 0.0f;
		float y3 = 0.0f;
		float z3 = 0.0f;
		float w3 = 1.0f;
		return new Mat4(x0, y0, z0, w0, x1, y1, z1, w1, x2, y2, z2, w2, x3, y3, z3, w3).transpose();
	}

	private static List<Float> normalizeQuat(Vec4 q) {
		float length = (float) Math
				.sqrt((q.get(0) * q.get(0)) + (q.get(1) * q.get(1)) + (q.get(2) * q.get(2)) + (q.get(3) * q.get(3)));
		float n = 1.0f / length;
		List<Float> res = new ArrayList<>();
		res.add(n * q.get(0));
		res.add(n * q.get(1));
		res.add(n * q.get(2));
		res.add(n * q.get(3));
		return res;
	}

	/**
	 * @param translation
	 * @return
	 */
	private static Mat4 getTranslationMatrix(Vec3 translation) {
		return new Mat4(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, translation.getX(),
				translation.getY(), translation.getZ(), 1.0f);
	}

	private static String getImportPathByName(Package upackage, String filename) {
		for (FObjectImport cimport : upackage.getImportMap().getEntrys()) {
			if (cimport.getClassName().equals("Package")) {
				if (filename != null) {
					if (cimport.getObjectName().endsWith(filename)) {
						return cimport.getObjectName();
					}
				}
			}
		}
		return null;
	}
}
