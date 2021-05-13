package spck.core.render.camera;

import org.joml.Matrix4f;

public class PerspectiveCamera extends AbstractCamera {
  private float fov;
  private float zNear;
  private float zFar;

  /**
   * Initialising a perspective projection.
   * <p>
   * Example: new PerspectiveCamera(60f, 0.01f, 1000f);
   *
   * @param fov,   for default, use 60
   * @param zNear, for default, use 0.01
   * @param zFar,  for default, use 1000
   * @return PerspectiveCamera
   */
  public PerspectiveCamera(float fov, float zNear, float zFar) {
    super();
    this.fov = fov;
    this.zNear = zNear;
    this.zFar = zFar;
  }

  @Override
  protected void updateProjectionMatrix(Matrix4f projectionMatrix) {
    float aspect = (float)width / (float)height;
    projectionMatrix.identity(); // TODO
    projectionMatrix.setPerspective(
        (float) java.lang.Math.toRadians(fov),
        aspect,
        zNear,
        zFar
    );
  }
}
