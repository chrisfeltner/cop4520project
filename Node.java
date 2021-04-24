import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node<T> {
  public T data;
  public int bucket;
  public int key;
  public AtomicMarkableReference<Node<T>> next;
  public boolean dummy;

  /**
   * Node constructor
   *
   * @param data    The data of a node
   * @param next    The reference to the next node
   * @param isDummy true if it is a bucket node, otherwise false
   */
  public Node(T data, AtomicMarkableReference<Node<T>> next, boolean isDummy) {
    if (data != null) {
      if (isDummy) {
        this.key = makeSentinelKey(data.hashCode());
      } else {
        this.key = makeOrdinaryKey(data.hashCode());
      }
    }

    this.data = data;
    this.next = next;
    this.dummy = isDummy;
  }

  public Node(T data, boolean isDummy) {
    if (data != null) {
      if (isDummy) {
        this.key = makeSentinelKey(data.hashCode());
      } else {
        this.key = makeOrdinaryKey(data.hashCode());
      }
    }

    this.data = data;
    this.next = new AtomicMarkableReference<Node<T>>(null, false);
    this.dummy = isDummy;
  }

  public Node(int bucket) {
    this.data = null;
    this.bucket = bucket;
    this.next = new AtomicMarkableReference<Node<T>>(null, false);
    this.dummy = true;
    this.key = makeSentinelKey(bucket);
  }

  /**
   * Generates a Key for a non-bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   * @return the oridinary key for the node.
   */
  public static int makeOrdinaryKey(int hashCode) {
    Integer code = hashCode & 0x00FFFFFF;
    code = Integer.reverse(code);
    code |= 1;
    // System.out.println(Integer.toUnsignedString​(code));
    return code;
  }

  /**
   * Generates a Key for a bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   * @return the sentinel key for the data.
   */
  public static int makeSentinelKey(int hashCode) {
    Integer code = hashCode & 0x00FFFFFF;
    code = Integer.reverse(code);
    // System.out.println(Integer.toUnsignedString​(code));
    return code;
  }

  /**
   * make a binary string
   * 
   * @param intToMake The integer we are converting from.
   * @return the binary string of the integer.
   */
  public static String makeBinaryString(int intToMake) {
    return String.format("%32s", Integer.toBinaryString(intToMake)).replace(' ', '0');
  }

  /**
   * prints the node
   */
  public String toString() {
    if (this.next == null) {
      return "NULL";
    }
    boolean mark = next.isMarked();
    String k;
    if (this.dummy) {
      k = "D_" + this.bucket;
    } else {
      k = "N_" + this.data.toString();
    }
    return "(" + k + ", " + mark + ")";
  }
}
