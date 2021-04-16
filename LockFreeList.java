import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.lang.Integer;

public class LockFreeList {
  Node head;
  AtomicInteger itemCount;
  Node curr;

  /**
   * Create a new LockFreeList
   *
   */
  public LockFreeList() {
    this.head = new Node(0, new AtomicMarkableReference<Node>(null, false), true);
    this.itemCount = new AtomicInteger(1);
    this.curr = head;
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return Window containing the predecessor and current nodes
   */
  public Window find(int keyToFind) {
    return findAfter(this.head, keyToFind);
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return Window containing the predecessor and current nodes
   */
  public Window findAfter(Node head, int keyToFind) {
    Node pred = null;
    Node curr = null;
    Node succ = null;
    boolean[] marked = { false };
    boolean snip;
    retry: while (true) {
      pred = head;
      if (pred == null) {
        System.out.println("PRED IS NULL???????");
      }
      curr = pred.next.getReference();
      while (true) {
        if (curr == null) {
          return new Window(pred, curr);
        }
        succ = curr.next.get(marked);
        while (marked[0]) {
          snip = pred.next.compareAndSet(curr, succ, false, false);
          if (!snip) {
            continue retry;
          }
          curr = succ;
          succ = curr.next.get(marked);
        }
        if (Integer.compareUnsigned(curr.key, keyToFind) >= 0) {
          return new Window(pred, curr);
        }
        pred = curr;
        curr = succ;
      }
    }

  }

  /**
   * Insert a node after the head of the list.
   *
   * @param data the data to be inserted
   * @return Node that was inserted
   */
  public Node insert(int data) {
    return insertAt(this.head, data, false);
  }

  /**
   * Insert a node after another node.
   *
   * @param head    The head or shortcut in the list (where to start)
   * @param data    The data to be inserted
   * @param isDummy Whether or not the node is a bucket
   * @return Node that was inserted
   */
  public Node insertAt(Node head, int data, boolean isDummy) {
    int key;
    if (isDummy)
      key = makeSentinelKey(data);
    else
      key = makeOrdinaryKey(data);

    while (true) {
      Window window = findAfter(head, key);
      Node pred = window.pred, curr = window.curr;
      if (curr != null && Integer.compareUnsigned(curr.key, key) == 0) {
        return null;
      } else {
        Node node = new Node(data, new AtomicMarkableReference<Node>(curr, false), isDummy);
        if (pred.next.compareAndSet(curr, node, false, false)) {
          return node;
        }
      }
    }
  }

  /**
   * Delete the node with specified data if it exists.
   *
   * @param data The data of the node we are trying to delete
   * @return Node of what we deleted
   */
  public Node delete(int data) {
    return deleteAfter(this.head, data);
  }

  /**
   * Try to delete the node with specified key from the list starting at the
   * specified starting point (used for deletion from hash table using shortcut
   * references).
   *
   * @param head Node reference where we will begin our traversal
   * @param data The data of the node we are trying to delete
   * @return Node that was deleted
   */
  public Node deleteAfter(Node head, int data) {
    int key = makeOrdinaryKey(data);
    boolean snip;
    while (true) {
      Window window = findAfter(head, key);
      Node pred = window.pred, curr = window.curr;
      if (Integer.compareUnsigned(curr.key, key) != 0) {
        return null;
      } else {
        Node succ = curr.next.getReference();
        snip = curr.next.compareAndSet(succ, succ, false, true);
        if (!snip) {
          continue;
        }
        pred.next.compareAndSet(curr, succ, false, false);
        return curr;
      }
    }
  }

  /**
   * Generates a Key for a non-bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   * @return the key of a non-bucket node.
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
   * @return the sentinel key for the data.
   */
  public static int makeSentinelKey(int data) {
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    // System.out.println(Integer.toUnsignedString​(code));
    return code;
  }

  /**
   * Returns a string representation of the list.
   */
  public String toString() {
    Node current = this.head;
    String string = "";
    while (current != null) {
      string += current.toString() + " " + Integer.toUnsignedString(current.key) + " " + " -> ";
      current = current.next.getReference();
    }
    string += "NULL";
    return string;
  }
}
