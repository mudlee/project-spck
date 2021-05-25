package spck.core.render;

public class Mesh {
  private final float[] vertices;
  private final int[] indices;
  private final float[] normals;
  private final float[] uvCoords;

  public Mesh(float[] vertices, int[] indices, float[] normals, float[] uvCoords) {
    this.vertices = vertices;
    this.indices = indices;
    this.normals = normals;
    this.uvCoords = uvCoords;
  }

  public float[] getVertices() {
    return vertices;
  }

  public int[] getIndices() {
    return indices;
  }

  public float[] getNormals() {
    return normals;
  }

  public float[] getUvCoords() {
    return uvCoords;
  }
}
