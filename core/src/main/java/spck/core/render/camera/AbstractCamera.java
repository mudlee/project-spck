package spck.core.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCamera implements Camera {
  private static final Logger log = LoggerFactory.getLogger(AbstractCamera.class);
  private final Matrix4f projectionMatrix = new Matrix4f();
  private final Matrix4f viewMatrix = new Matrix4f();
  private final Vector3f position = new Vector3f();
  private final Vector3f rotation = new Vector3f();
  private final Vector3f tmpViewMatrixUpVec = new Vector3f(0, 1, 0);
  private final Vector3f camFrontVec = new Vector3f(0, 0, -1); // RIGHT HANDED
  private final Vector3f tmpViewMatrixPositionVec = new Vector3f().zero();
  private boolean projectionMatrixChanged;
  private boolean viewMatrixChanged;
  protected int width;
  protected int height;
  
  protected abstract void updateProjectionMatrix(Matrix4f projectionMatrix);

  @Override
  public void resize(int width, int height) {
    log.debug("Camera resized to {}x{}", width, height);
    this.width = width;
    this.height = height;
    projectionMatrixChanged = true;
    viewMatrixChanged = true;
  }

  @Override
  public boolean update() {
    boolean changed = false;

    if(projectionMatrixChanged) {
      updateProjectionMatrix(projectionMatrix);
      projectionMatrixChanged = false;
      changed = true;
    }

    if(viewMatrixChanged) {
      updateViewMatrix();
      viewMatrixChanged = false;
      changed = true;
    }

    return changed;
  }

  @Override
  public void setRotation(Vector3f rotation) {
    this.rotation.set(rotation);
  }

  @Override
  public void setPosition(Vector3f position) {
    this.position.set(position);
  }

  @Override
  public Vector3f getPosition() {
    return position;
  }

  @Override
  public Vector3f getRotation() {
    return rotation;
  }

  @Override
  public Matrix4f getViewMatrix() {
    return viewMatrix;
  }

  @Override
  public Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }

  private void updateViewMatrix() {
    viewMatrix.identity();
    tmpViewMatrixPositionVec.set(position);

    viewMatrix.lookAt(position, tmpViewMatrixPositionVec.add(camFrontVec), tmpViewMatrixUpVec);
  }
}
