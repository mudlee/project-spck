package spck.core.props;

import com.artemis.BaseSystem;
import org.joml.Vector2i;

import java.util.Collections;
import java.util.List;

public class Preferences {
  public enum RendererBackend {
    OPENGL,
  }

  public String title = "Project SPCK";
  public RendererBackend renderBackend = RendererBackend.OPENGL;
  public Antialiasing antialiasing = Antialiasing.OFF;
  public Vector2i windowSize = new Vector2i(1280, 720);
  public boolean vSync = false;
  public boolean fullscreen = false;
  public boolean debug = false;
  public List<BaseSystem> ecsSystems = Collections.emptyList();
}
