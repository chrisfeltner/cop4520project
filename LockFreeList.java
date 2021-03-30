import java.util.concurrent.atomic.AtomicMarkableReference;
import java.lang.Integer;

public class LockFreeList {
  Node head, tail, curr;

  public LockFreeList() {
    this.tail = new Node(Integer.MAX_VALUE - 1, new AtomicMarkableReference<Node>(null, false), true);
    this.head = new Node(0, new AtomicMarkableReference<Node>(this.tail, false), true);
    this.curr = head;
  }

  public static int makeOrdinaryKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    code = code >>> 1;
    code |= 1;
    return code;
  }

  public static int makeSentinelKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    return code;
  }

  public Window find(Node head, int key) {
    Node pred = null, curr = null, succ = null;
    boolean[] marked = { false };
    boolean snip;
    retry: while (true) {
      pred = head;
      curr = pred.next.getReference();
      while (true) {
        succ = curr.next.get(marked);
        while (marked[0]) {
          snip = pred.next.compareAndSet(curr, succ, false, false);
          if (!snip) {
            continue retry;
          }
          curr = succ;
          succ = curr.next.get(marked);
        }
        if (curr.key >= key) {
          return new Window(pred, curr);
        }
        pred = curr;
        curr = succ;
      }
    }
  }

  public boolean add(int data) {
    int key = makeOrdinaryKey(data);
    while (true) {
      Window window = find(head, key);
      Node pred = window.pred, curr = window.curr;
      if (curr.key == key) {
        return false;
      } else {
        Node node = new Node(data, new AtomicMarkableReference<Node>(curr, false), false);
        // node.next = new AtomicMarkableReference<Node>(curr, false);
        if (pred.next.compareAndSet(curr, node, false, false)) {
          return true;
        }
      }
    }
  }

  public boolean remove(int data) {
    int key = makeOrdinaryKey(data);
    boolean snip;
    while (true) {
      Window window = find(head, key);
      Node pred = window.pred, curr = window.curr;
      if (curr.key != key) {
        return false;
      } else {
        Node succ = curr.next.getReference();
        snip = curr.next.compareAndSet(succ, succ, false, true);
        if (!snip) {
          continue;
        }
        pred.next.compareAndSet(curr, succ, false, false);
        return true;
      }
    }
  }

  public boolean contains(int data) {
    boolean[] marked = { false };
    int key = makeOrdinaryKey(data);
    Node curr = head;
    while (curr.key < key) {
      curr = curr.next.getReference();
      Node succ = curr.next.get(marked);
    }
    return (curr.key == key && !marked[0]);
  }

  public String toString() {
    Node current = this.head;
    String string = "";
    while (current != null) {
      string += current.toString() + " " + current.key + " " + " -> ";
      current = current.next.getReference();
    }
    string += "NULL";
    return string;
  }
}