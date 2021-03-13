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
    this.next = null;
    this.dummy = false;
  }

  public Node(int key, int dummy) {
    this.key = key;
    this.next = null;
    // in the actual implementation....this would be by setting the reversed LSB to 1;
    // we would have to parse the key to tell if its a dummy or not...
    if (dummy == 1)
      this.dummy = true;
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
