import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.lang.Integer;
public class LockFreeList {
  Node head;
  AtomicInteger itemCount;
  Node curr;

  /**
   * Create a new LockFreeList using the head of an existing LockFreeList.
   *
   * @param head      The head of an existing LockFreeList
   * @param itemCount The number of items in an existing list
   */
  public LockFreeList(Node head) {
    this.head = head;
    this.itemCount = new AtomicInteger(1);
    this.curr = head;
  }

  public LockFreeList() {
    this.head = new Node(0, new AtomicMarkableReference<Node>(null, false), true);
    this.itemCount = new AtomicInteger(1);
    this.curr = head;
  }

  // for Sarah
  // // Tests for the list constructor
  // @Test 
  // public void testLockFreeList() throws Exception
  // {
  //   LockFreeList list = new LockFreeList();
  //   // checking head node
  //   assertEquals(list.head.dummy, true);
  //   assertEquals(Integer.compareUnsigned(0, list.head.key), 0);
  //   //checking tail
  //   assertEquals(list.head.next, null);
  // }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return true if key is in list
   */
  public Window find(int keyToFind) {
    return findAfter(this.head, keyToFind);
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return true if key is in list
   */
  public Window findAfter(Node head, int keyToFind) {
    Node pred = null;
    Node curr = null;
    Node succ = null;
    boolean[] marked = { false };
    boolean snip;
    retry: while (true) {
      pred = head;
      if (pred == null)
      {
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
   * @param nodeToInsert A new node with key set, but next reference null
   * @return True if successfully inserted
   */
  public Node insert(int data) {
    return insertAt(this.head, data, false);
  }

  /**
   * Insert a node after another node.
   *
   * @param nodeToInsert The new node with next pointer null
   * @param insertAfter  The node to insert nodeToInsert after
   * @return True if successful
   */
  public Node insertAt(Node head, int data, boolean isDummy) {
    int key;
    if (isDummy) key = makeSentinelKey(data);
    else key = makeOrdinaryKey(data);

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
   * Delete the node with specified key if it exists.
   *
   * @param keyToDelete The key of the node we are trying to delete
   * @return True if successful
   */
  public Node delete(int data) {
    return deleteAfter(this.head, data);
  }

  /**
   * Try to delete the node with specified key from the list starting at the
   * specified starting point (used for deletion from hash table using shortcut
   * references).
   *
   * @param startingPoint Node reference where we will begin our traversal
   * @param keyToDelete   The key of the node we are trying to delete
   * @return True if successful
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
