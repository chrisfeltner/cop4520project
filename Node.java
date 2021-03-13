import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
  public AtomicMarkableReference<Node> next;
  public int key;

  // value nodes assigned true to MSB, when reversed becomes LSB
  public Node(int key, AtomicMarkableReference<Node> next) {
    this.key = key;
    this.next = next;
  }

  public Node(int key) {
    this.key = key;
    this.next = null;
  }

  public String toString() {
    boolean mark = next.isMarked();
    return "(" + this.key + ", " + mark + ")";
  }
}
