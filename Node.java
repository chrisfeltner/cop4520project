import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.BitSet;

public class Node {
  public AtomicMarkableReference<Node> next;
  public BitSet key;
  public int data;
  public boolean dummy;

  // // value nodes assigned true to MSB, when reversed becomes LSB
  // public Node(int data, AtomicMarkableReference<Node> next) {
  // this.data = data;
  // this.next = next;
  // this.dummy = false;
  // }

  // public Node(int data) {
  // this.data = data;
  // this.next = new AtomicMarkableReference<Node>(null, false);
  // this.dummy = false;
  // }

  public Node(int data, AtomicMarkableReference<Node> next, boolean isDummy) {
    this.key = intToBitSet(data, isDummy);
    this.data = data;
    this.next = new AtomicMarkableReference<Node>(null, false);
    // in the actual implementation....this would be by setting the reversed LSB to
    // 1;
    // we would have to parse the key to tell if its a dummy or not...
    if (isDummy) {
      this.dummy = true;
    } else {
      this.dummy = false;
    }
  }

  public static BitSet intToBitSet(int value, boolean isDummy) {
    BitSet newBitSet = new BitSet(32);
    int index = 32;
    while (value != 0) {
      if (value % 2 != 0) // get the bit of the last index
      {
        newBitSet.set(index);
      }
      index--;
      value = value >>> 1; // logical shift right by 1
    }
    if (!isDummy) {
      newBitSet.set(0);
    }
    return newBitSet;
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
