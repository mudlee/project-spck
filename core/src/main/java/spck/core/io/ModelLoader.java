package spck.core.io;

import org.joml.Vector3f;
import org.lwjgl.assimp.*;
import org.lwjgl.system.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.Disposable;
import spck.core.render.Mesh;
import spck.core.render.model.Model;
import spck.core.render.model.ModelJarUtil;
import spck.core.render.model.ModelPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader implements Disposable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ModelLoader.class);
    private final static int IMPORT_FLAGS = Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_OptimizeMeshes;
    private final static Map<String, AIScene> modelCache = new HashMap<>();

    @Override
    public void dispose() {
        // TODO
    }

    public static Model load(String resourcePath) {
        LOGGER.debug("Trying to load model {}...", resourcePath);

        AIScene scene;
        boolean fromCache = false;
        String extension = resourcePath.substring(resourcePath.lastIndexOf("."));

        if (modelCache.containsKey(resourcePath)) {
            LOGGER.debug("- Loading from cache");
            scene = modelCache.get(resourcePath);
            fromCache = true;
        } else {
            LOGGER.debug("- Extension: {}", extension);

            final var res = ModelLoader.class.getResource(resourcePath);
            if (res == null) {
                throw new RuntimeException(String.format("Could not find model: %s", resourcePath));
            }
            String modelPath = getModelPath(res.getPath());

            LOGGER.debug("- Loading model from Path->'{}', Resource->'{}'", modelPath, res);

            if (res.toString().startsWith("jar:") || res.toString().startsWith("jrt:")) {
                modelPath = ModelJarUtil.unpackFromJar(resourcePath, extension);
            }

            scene = Assimp.aiImportFile(
                modelPath,
                IMPORT_FLAGS
            );

            if (scene == null) {
                final var error = Assimp.aiGetErrorString();
                throw new RuntimeException(String.format("Could not load model from %s. Error: %s", modelPath, error));
            }

            modelCache.put(resourcePath, scene);
        }

        final var parts = loadModelParts(scene, resourcePath, extension, fromCache);
        LOGGER.debug("- Model {} has been loaded. Contains {} meshes", resourcePath, parts.size());
        return new Model(parts);
    }

    private static void cleanUp() {
        modelCache.values().forEach(Assimp::aiReleaseImport);
    }

    private static String getModelPath(String path) {
        // TODO: do I need this?
        if (Platform.get() == Platform.WINDOWS) {
            return path.substring(1);
        }

        return path;
    }

    private static List<ModelPart> loadModelParts(AIScene scene, String resourcePath, String extension, boolean fromCache) {
        final var parts = new ArrayList<ModelPart>();
        final var numMeshes = scene.mNumMeshes();
        final var numMaterials = scene.mNumMaterials();

        if (!fromCache) {
            LOGGER.debug("- Found {} meshes...", numMeshes);
            LOGGER.debug("- Found {} materials...", numMaterials);
        }

        final var aiMeshes = scene.mMeshes();

        if (aiMeshes == null) {
            throw new RuntimeException("aiMeshes PointBuffer was null for model " + resourcePath);
        }

        if (numMeshes == 0) {
            throw new RuntimeException("Could not find any mesh for model " + resourcePath);
        }

        /*List<Material> materials = new ArrayList<>();
        PointerBuffer aiMaterials = scene.mMaterials();

        if (aiMaterials == null) {
            throw new RuntimeException("aiMaterials PointBuffer was null for model " + resourcePath);
        }

        for (int i = 0; i < numMaterials; i++) {
            if (!fromCache) LOGGER.debug("- Processing material at index {}...", i);
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            materials.add(processMaterial(aiMaterial, resourcePath, extension, fromCache));
            if (!fromCache) LOGGER.debug("- Material loaded");
        }*/

        for (int i = 0; i < numMeshes; i++) {
            if (!fromCache) {
                LOGGER.debug("- Processing mesh at index {}...", i);
            }

            final var aiMesh = AIMesh.create(aiMeshes.get(i));
            final var materialIndex = aiMesh.mMaterialIndex();
            final var mesh = new Mesh(
                getVerticesFromMesh(aiMesh),
                getIndicesFromMesh(aiMesh),
                getNormalsFromMesh(aiMesh),
                getUVCoordsFromMesh(aiMesh)
            );

            /*final var mesh = new Mesh(
                    getVerticesFromMesh(aiMesh),
                    getIndicesFromMesh(aiMesh),
                    getNormalsFromMesh(aiMesh),
                    getUVCoordsFromMesh(aiMesh),
                    calculateAABB(aiMesh)
                    normals, uvCoords);*/

            if (!fromCache) {
                LOGGER.debug("- Mesh has been loaded. Verts: {}, normals: {}, mat idx: {}", mesh.getIndices().length, mesh.getNormals().length / 3, materialIndex);
            }
            /*Material material = materials.isEmpty() ? new DefaultMaterial() : materials.get(materialIndex);
            parts.add(new ModelPart(mesh, material));*/
            parts.add(new ModelPart(mesh));
        }

        return parts;
    }

    /*private static AABBf calculateAABB(AIMesh aiMesh) {
        boolean initiated = false;
        AABBf aabb = new AABBf();

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();

            if (!initiated) {
                aabb.setMin(new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()));
                aabb.setMax(new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()));
                initiated = true;
                continue;
            }

            if (aiVertex.x() < aabb.minX) {
                aabb.minX = aiVertex.x();
            } else if (aiVertex.x() > aabb.maxX) {
                aabb.maxX = aiVertex.x();
            }

            if (aiVertex.y() < aabb.minY) {
                aabb.minY = aiVertex.y();
            } else if (aiVertex.y() > aabb.maxY) {
                aabb.maxY = aiVertex.y();
            }

            if (aiVertex.z() < aabb.minZ) {
                aabb.minZ = aiVertex.z();
            } else if (aiVertex.z() > aabb.maxZ) {
                aabb.maxZ = aiVertex.z();
            }
        }

        LOGGER.debug(
                "- AABB: min(x:{}, y:{}, z:{}) max(x:{}, y:{}, z:{})",
                aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ
        );
        return aabb;
    }*/

    /*private static Material processMaterial(AIMaterial aiMaterial, String modelPath, String extension, boolean fromCache) {
        DefaultMaterial material = new DefaultMaterial();
        material.setDiffuseColor(getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, fromCache));
        material.setSpecularColor(getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, fromCache));
        material.setAmbientColor(getMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, fromCache));
        material.setDiffuseTexture(getMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, modelPath, extension));

        return material;
    }

    // NOTE: it only loads textures with the same name
    private static Texture2D getMaterialTexture(AIMaterial aiMaterial, int textureType, String modelPath, String extension) {
        AIString path = AIString.calloc();
        int result = Assimp.aiGetMaterialTexture(aiMaterial, textureType, 0, path, (IntBuffer) null, null, null, null, null, null);
        if (result != Assimp.aiReturn_SUCCESS) {
            return null;
        }

        String textPath = modelPath.replace(extension, ".png");

        LOGGER.debug("- Trying to load texture from {}", textPath);
        try {
            ByteBuffer buffer = ResourceLoader.loadToByteBuffer(textPath);
            Texture2D texture = (Texture2D) TextureRegistry.register(TextureStorage.loadFromTextureData(
                    TextureLoader.loadFromByteBuffer(buffer),
                    ShaderUniform.Material.DIFFUSE_TEXTURE_SAMPLER.getUniformName(),
                    textPath
            ));
            LOGGER.debug("- Material {} texture is {}, - {}", textureType, textPath, texture);
            return texture;
        } catch (Exception e) {
            LOGGER.error("- Texture was not found at {}", textPath);
            throw new RuntimeException(e);
        }
    }*/

    private static Vector3f getMaterialColor(AIMaterial aiMaterial, String colorType, boolean fromCache) {
        AIColor4D color = AIColor4D.create();
        Assimp.aiGetMaterialColor(aiMaterial, colorType, Assimp.aiTextureType_NONE, 0, color);
        Vector3f matColor = new Vector3f(color.r(), color.g(), color.b());
        if (!fromCache) LOGGER.debug("- Material {} color is {}", colorType, matColor);
        return matColor;
    }

    private static float[] getVerticesFromMesh(AIMesh aiMesh) {
        final var aiVertices = aiMesh.mVertices();
        final var result = new float[aiVertices.capacity() * 3];

        int i = 0;
        while (aiVertices.remaining() > 0) {
            final var aiVertex = aiVertices.get();
            result[i++] = aiVertex.x();
            result[i++] = aiVertex.y();
            result[i++] = aiVertex.z();
        }

        return result;
    }

    private static float[] getNormalsFromMesh(AIMesh aiMesh) {
        final var aiNormals = aiMesh.mNormals();

        if (aiNormals == null) {
            return null;
        }

        final var result = new float[aiNormals.capacity() * 3];

        int i = 0;
        while (aiNormals.remaining() > 0) {
            final var aiNormal = aiNormals.get();
            result[i++] = aiNormal.x();
            result[i++] = aiNormal.y();
            result[i++] = aiNormal.z();
        }

        return result;
    }

    private static int[] getIndicesFromMesh(AIMesh aiMesh) {
        final var numFaces = aiMesh.mNumFaces();
        final var aiFaces = aiMesh.mFaces();

        final var indices = new ArrayList<Integer>();

        for (int i = 0; i < numFaces; i++) {
            final var aiFace = aiFaces.get(i);
            final var buffer = aiFace.mIndices();

            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }

        final var result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }

        return result;
    }

    private static float[] getUVCoordsFromMesh(AIMesh aiMesh) {
        final var uvCoords = aiMesh.mTextureCoords(0);
        final var numUVCoords = uvCoords != null ? uvCoords.remaining() : 0;

        if (numUVCoords == 0) {
            return new float[]{};
        }

        final var coords = new float[numUVCoords * 2];

        for (int i = 0; i < numUVCoords * 2; i += 2) {
            final var textCoord = uvCoords.get();
            coords[i] = textCoord.x();
            coords[i + 1] = 1 - textCoord.y();
        }

        return coords;
    }
}
