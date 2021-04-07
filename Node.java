import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
  public AtomicMarkableReference<Node> next;
  public int key;
  public boolean dummy;

  // value nodes assigned true to MSB, when reversed becomes LSB
  public Node(int key, AtomicMarkableReference<Node> next) {
    this.key = key;
    this.next = next;
    this.dummy = false;
  }

  public Node(int key) {
    this.key = key;
    this.next = new AtomicMarkableReference<Node>(null, false);
    this.dummy = false;
  }

  public Node(int key, int dummy) {
    this.key = key;
    this.next = new AtomicMarkableReference<Node>(null, false);
    // in the actual implementation....this would be by setting the reversed LSB to
    // 1;
    // we would have to parse the key to tell if its a dummy or not...
    if (dummy == 1)
      this.dummy = true;
  }

  /**
   * FOR TOSTRING()
   * Generates a Key for a non-bucket / sentinel node.
   *
   * @param data The data of a node used to create the key.
   */
  public static int makeOrdinaryKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    code |= 1;
    return code;
  }

  /**
   * FOR TOSTRING()
   * Generates a Key for a bucket / sentinel node.
   *
   * @param data The data of a node used to create the key.
   */
  public static int makeSentinelKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    return code;
  }



  public String toString() {
    boolean mark = next.isMarked();
    String k;
    if (this.dummy)
      k = "D_" + this.key;
    else
      k = "" + this.key;
    return "(" + k + ", " + mark + ")";
  }
}
