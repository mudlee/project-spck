package spck.core.props.type;

public class BufferBitType {
  public final int COLOR;
  public final int DEPTH;
  public final int STENCIL;

  public BufferBitType(int color, int depth, int stencil) {
    this.COLOR = color;
    this.DEPTH = depth;
    this.STENCIL = stencil;
  }
}
