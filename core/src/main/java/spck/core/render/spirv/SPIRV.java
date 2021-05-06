package spck.core.render.spirv;

import org.lwjgl.util.shaderc.Shaderc;
import spck.core.Disposable;

public class SPIRV implements Disposable {
  private final byte[] bytes;
  private final long compilationResult;

  public SPIRV(byte[] bytes, long compilationResult) {
    this.bytes = bytes;
    this.compilationResult = compilationResult;
  }

  @Override
  public void dispose() {
    Shaderc.shaderc_result_release(compilationResult);
  }

  public byte[] getBytes() {
    return bytes;
  }
}
