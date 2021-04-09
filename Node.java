import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
  public int data;
  public int key;
  public AtomicMarkableReference<Node> next;
  public boolean dummy;

  // value nodes assigned true to MSB, when reversed becomes LSB
  public Node(int data, AtomicMarkableReference<Node> next, boolean isDummy) {
    if (isDummy) {
      this.key = makeSentinelKey(data);
    } else {
      this.key = makeOrdinaryKey(data);
    }
    this.data = data;
    this.next = next;
    this.dummy = isDummy;
  }

  /**
   * Generates a Key for a non-bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   */
  public static int makeOrdinaryKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    code |= 1;
    // System.out.println(Integer.toUnsignedString​(code));
    return code;
  }

  /**
   * Generates a Key for a bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   */
  public static int makeSentinelKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    // System.out.println(Integer.toUnsignedString​(code));
    return code;
  }

  public static String makeBinaryString(int intToMake) {
    return String.format("%32s", Integer.toBinaryString(intToMake)).replace(' ', '0');
  }

  public String toString() {
    if (this.next == null)
    {
      return "NULL";
    }
    boolean mark = next.isMarked();
    String k;
    if (this.dummy) {
      k = "D_" + this.data;
    } else {
      k = "N_" + this.data;
    }
    return "(" + k + ", " + mark + ")";
  }
}
