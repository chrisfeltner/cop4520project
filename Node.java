import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
  public AtomicMarkableReference<Node> next;
  public int key;
  public Integer data;
  public boolean dummy;

  // value nodes assigned true to MSB, when reversed becomes LSB
  public Node(Integer data, AtomicMarkableReference<Node> next, boolean dummy) {
    if (dummy) {
      this.key = makeSentinelKey(data);
    } else {
      this.key = makeOrdinaryKey(data);
    }
    this.data = data;
    this.next = next;
    this.dummy = false;
  }

  public static int makeOrdinaryKey(int x) {
    Integer code = x & 0x00FFFFFF;
    code = Integer.reverse(code);
    code = code >>> 1;
    code |= 1;
    return code;
  }

  public static int makeSentinelKey(int key) {
    Integer code = key & 0x00FFFFFF;
    code = Integer.reverse(code);
    return code;
  }

  public String toString() {
    boolean mark = next.isMarked();
    String k;
    if (this.dummy)
      k = "D_" + this.data;
    else
      k = "" + this.data;
    return "(" + k + ", " + mark + ")";
  }
}
