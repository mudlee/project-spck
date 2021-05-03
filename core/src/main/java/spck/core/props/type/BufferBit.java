package spck.core.props.type;

public class BufferBit {
  public final int color;
  public final int depth;
  public final int stencil;

  public BufferBit(int color, int depth, int stencil) {
    this.color = color;
    this.depth = depth;
    this.stencil = stencil;
  }
}
