import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
  public AtomicMarkableReference<Node> next;
  public int key;
  public Integer data;
  public boolean dummy;

  /**
   * Constructor for the Node object.
   * 
   * @param data  The data of a node used to create the key.
   * @param next  AtomicMarkableReference to the next node.
   * @param dummy boolean indicating whether the node is a bucket/sentinel node or
   *              not.
   */
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

  /**
   * Generates a Key for a non-bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   */
  public static int makeOrdinaryKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    code = code >>> 1;
    code |= 1;
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
    return code;
  }

  /**
   * Printing method of each node. Helpful for testing.
   * 
   */
  public String toString() {
    boolean mark = next.isMarked();
    String k;
    if (this.dummy) {
      k = "D_" + this.data;
    } else {
      k = "" + this.data;
    }
    return "(" + k + ", " + mark + ")";
  }
}
