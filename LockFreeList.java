import java.lang.Integer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList<T> {
  Node<T> head;
  AtomicInteger itemCount;
  Node<T> curr;

  /**
   * Create a new LockFreeList.
   *
   */
  public LockFreeList() {
    this.head = new Node<T>(0);
    this.itemCount = new AtomicInteger(1);
    this.curr = head;
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return Window containing the predecessor and current nodes
   */
  public Window<T> find(Node<T> nodeToFind) {
    return findAfter(this.head, nodeToFind);
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return Window containing the predecessor and current nodes
   */
  public Window<T> findAfter(Node<T> head, Node<T> nodeToFind) {
    Node<T> pred = null;
    Node<T> curr = null;
    Node<T> succ = null;
    boolean[] marked = { false };
    boolean snip;
    retry: while (true) {
      pred = head;
      if (pred == null) {
        System.out.println("PRED IS NULL???????");
        return new Window(null, null);
      }
      curr = pred.next.getReference();
      while (true) {
        if (curr == null) {
          return new Window<T>(pred, curr);
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
        if (Integer.compareUnsigned(curr.key, nodeToFind.key) >= 0) {
          return new Window<T>(pred, curr);
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
  public Node<T> insert(Node<T> toInsert) {
    return insertAt(this.head, toInsert);
  }

  /**
   * Insert a node after another node.
   *
   * @param head    The head or shortcut in the list (where to start)
   * @param data    The data to be inserted
   * @param isDummy Whether or not the node is a bucket
   * @return Node that was inserted
   */
  public Node<T> insertAt(Node<T> head, Node<T> toInsert) {
    while (true) {
      Window<T> window = findAfter(head, toInsert);
      Node<T> pred = window.pred;
      Node<T> curr = window.curr;
      if (curr != null && Integer.compareUnsigned(curr.key, toInsert.key) == 0) {
        return null;
      } else {
        toInsert.next.set(curr, false);
        if (pred.next.compareAndSet(curr, toInsert, false, false)) {
          return toInsert;
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
  public Node<T> delete(Node<T> toDelete) {
    return deleteAfter(this.head, toDelete);
  }

  /**
   * Try to delete the node with specified key from the list starting at the
   * specified starting point (used for deletion from hash table using shortcut
   * references).
   *
   * @param head Node reference where we will begin our traversal
   * @param key  The key of the node we are trying to delete
   * @return Node that was deleted
   */
  public Node<T> deleteAfter(Node<T> head, Node<T> toDelete) {
    boolean snip;
    while (true) {
      Window<T> window = findAfter(head, toDelete);
      Node<T> pred = window.pred;
      Node<T> curr = window.curr;
      if (curr == null || Integer.compareUnsigned(curr.key, toDelete.key) != 0) {
        return null;
      } else {
        Node<T> succ = curr.next.getReference();
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
<<<<<<< HEAD
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
=======
>>>>>>> b9e643b40104615919010c26575d267fe2fc39d5
   * Returns a string representation of the list.
   */
  public String toString() {
    Node<T> current = this.head;
    String string = "";
    while (current != null) {
      string += current.toString() + " -> ";
      current = current.next.getReference();
    }
    string += "NULL";
    return string;
  }
}
