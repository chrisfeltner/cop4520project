import java.util.concurrent.atomic.AtomicMarkableReference;
import java.lang.Integer;

public class LockFreeList {
  Node head;
  Node tail;
  Node curr;

  /**
   * Creates a new LockFreeList.
   *
   */
  public LockFreeList() {
    this.tail = new Node(Integer.MAX_VALUE, new AtomicMarkableReference<Node>(null, false), true);
    this.head = new Node(0, new AtomicMarkableReference<Node>(this.tail, false), true);
    this.curr = head;
  }

  /**
   * Generates a Key for a non-bucket / sentinel node.
   * 
   * @param data The data of a node used to create the key.
   */
  public static int makeOrdinaryKey(int data) {
    // System.out.println("1. data \t" + data);
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
    // System.out.println("2. data \t" + data);
    Integer code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    // System.out.println(Integer.toUnsignedString​(code));
    return code;
  }

  /**
   * Traverses the list for the position desired by the add and remove methods.
   * 
   * @param head the starting node of the list
   * @param key  the key that is being searched for
   */
  public Window find(Node head, int key) {
    Node pred = null;
    Node curr = null;
    Node succ = null;
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
        if (Integer.compareUnsigned(curr.key,key) >= 0) {
          return new Window(pred, curr);
        }
        pred = curr;
        curr = succ;
      }
    }
  }

  /**
   * Adds a node with the desired data to the list.
   * 
   * @param data The data of the node to be added.
   */
  public boolean add(int data) {
    int key = makeOrdinaryKey(data);
    while (true) {
      Window window = find(head, key);
      Node pred = window.pred, curr = window.curr;
      if (Integer.compareUnsigned(curr.key,key) == 0) {
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

  /**
   * Removes a node with the desired data to the list.
   * 
   * @param data The data of the node to be removed.
   */
  public boolean remove(int data) {
    int key = makeOrdinaryKey(data);
    boolean snip;
    while (true) {
      Window window = find(head, key);
      Node pred = window.pred, curr = window.curr;
      if (Integer.compareUnsigned(curr.key,key) != 0) {
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

  /**
   * Determines whether a node with the data is in the list.
   * 
   * @param data The data of the node that is searched for.
   */
  public boolean contains(int data) {
    boolean[] marked = { false };
    int key = makeOrdinaryKey(data);
    Node curr = head;
    while (Integer.compareUnsigned(curr.key,key) < 0) {
      curr = curr.next.getReference();
      Node succ = curr.next.get(marked);
    }
    return (Integer.compareUnsigned(curr.key,key) == 0 && !marked[0]);
  }

  // public Node getSentinel(int index){
  //   int key = makeSentinelKey(index);
  //   boolean splice;
  //   while (true) {
  //     Window window = find(head, key);
  //     Node pred = window.pred;
  //     Node curr = window.curr;
  //     if (curr.key == key) {
  //       return new LockFreeList();
  //     } else {
  //       Node node = new Node(index, new AtomicMarkableReference<Node>(curr, false), false); // ?
  //       node.next.set(pred.next.getReference(), false);
  //       splice = pred.next.compareAndSet(curr, node, false, false);
  //       if (splice)
  //         return new LockFreeList();
  //       else
  //         continue;
  //     }
  //   }
  // }

  /**
   * Printing method of the list. Helpful for testing.
   * 
   */
  public String toString() {
    Node current = this.head;
    String string = "";
    while (current != null) {
      string += current.toString() + " " + Integer.toUnsignedString​(current.key) + " " + " -> ";
      current = current.next.getReference();
    }
    string += "NULL";
    return string;
  }
}