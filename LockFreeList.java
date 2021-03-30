import java.util.concurrent.atomic.AtomicMarkableReference;
import java.lang.Integer;

public class LockFreeList {
  Node head, tail, curr;

  // public LockFreeList() {
  // this.head = new Node(0, new AtomicMarkableReference<Node>(null, false),
  // true);
  // this.head.next = new Node(Integer.MAX_VALUE, new
  // AtomicMarkableReference<Node>(null, false), true);
  // //this.itemCount = new AtomicInteger(0);
  // this.curr = head;
  // }
  public LockFreeList() {
    this.tail = new Node(1000, new AtomicMarkableReference<Node>(null, false), true);
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

// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.atomic.AtomicMarkableReference;

// public class LockFreeList {
// Node head;
// // AtomicInteger itemCount;
// Node curr;

// // /**
// // * Create a new LockFreeList using the head of an existing LockFreeList.
// // *
// // * @param head The head of an existing LockFreeList
// // * @param itemCount The number of items in an existing list
// // */
// // public LockFreeList(Node head, int itemCount) {
// // if (head == null) {
// // // first node is sentinal node with value 0
// // this.head = new Node(0, new AtomicMarkableReference<Node>(null, false),
// true);
// // this.head.next = new Node(Integer.MAX_VALUE, new
// AtomicMarkableReference<Node>(null, false), true);
// // this.itemCount = new AtomicInteger(0);
// // } else {
// // this.head = head;
// // this.itemCount = new AtomicInteger(itemCount);
// // }
// // this.curr = head;
// // }

// public LockFreeList() {
// this.head = new Node(0, new AtomicMarkableReference<Node>(null, false),
// true);
// this.head.next = new Node(Integer.MAX_VALUE, new
// AtomicMarkableReference<Node>(null, false), true);
// //this.itemCount = new AtomicInteger(0);
// this.curr = head;
// }

// public static int makeOrdinaryKey(int x) {
// Integer code = x & 0x00FFFFFF;
// code = Integer.reverse(code);
// code |= 1;
// return code;
// }

// public static int makeSentinelKey(int key) {
// Integer code = key & 0x00FFFFFF;
// code = Integer.reverse(code);
// return code;
// }

// /**
// * Check if a key is in the list.
// *
// * @param keyToFind The value to find in the list
// * @return true if key is in list
// */
// public boolean contains(int data) {
// int key = makeOrdinaryKey(data);
// Window window = find(head, key);
// Node curr = window.curr;
// return (curr.key == key);
// // Node prev = this.head;
// // boolean[] markHolder = { false };
// // while (true) {
// // curr = prev.next.get(markHolder);
// // // Condition 1: We reach the end of the list without finding key
// // if (curr == null) {
// // return false;
// // }
// // Node next = curr.next.get(markHolder);
// // boolean currentMark = markHolder[0];
// // int currentKey = curr.key;
// // // Condition 2: The previous node is marked or is no longer the previous
// node
// // if (prev.next.get(markHolder) != curr || markHolder[0] == true) {
// // // Start over
// // contains(keyToFind);
// // }
// // // The current node is not marked (not deleted)
// // if (!currentMark) {
// // if (currentKey >= keyToFind) {
// // return currentKey == keyToFind;
// // } else {
// // prev = curr.next.get(markHolder);
// // }
// // } else {
// // // The current node has been marked for deletion!
// // if (prev.next.compareAndSet(curr, next, false, false)) {
// // // Node will be garbage collected by Java runtime
// // deleteNode(curr);
// // } else {
// // // Deletion failed; try again
// // contains(keyToFind);
// // }
// // }
// // prev = curr;
// // }
// }

// /**
// * Insert a node after the head of the list.
// *
// * @param nodeToInsert A new node with key set, but next reference null
// * @return True if successfully inserted
// */
// public boolean insert(Node nodeToInsert) {
// int key = nodeToInsert.key;
// // If the key is already in the set, there is no insertion needed
// if (contains(key)) {
// return false;
// }
// // Set next reference for node
// Node nextInList = this.head.next.getReference();
// nodeToInsert.next = new AtomicMarkableReference<>(nextInList, false);
// if (head.next.compareAndSet(nextInList, nodeToInsert, false, false)) {
// // The head's next reference still points to nodeToInsert and head has not
// // been marked for deletion. If the CAS operation returns true, head's next
// // reference points to the inserted node
// return true;
// } else {
// // Try again; We can assume that it is illegal to delete head
// return insert(nodeToInsert);
// }
// }

// /**
// * Insert a node after another node.
// *
// * @param nodeToInsert The new node with next pointer null
// * @param insertAfter The node to insert nodeToInsert after
// * @return True if successful
// */
// public boolean insertAt(Node nodeToInsert, Node insertAfter) {
// if (insertAfter == null)
// return insert(nodeToInsert);
// Node prev = null;
// while (insertAfter != null && nodeToInsert.key < insertAfter.key) {
// prev = insertAfter;
// insertAfter = insertAfter.next.getReference();
// }
// if (insertAfter == null) {
// insertAfter = prev;
// }
// int key = nodeToInsert.key;
// // If the key is already in the set, there is no insertion needed
// if (contains(key)) {
// return false;
// }
// Node nextInList = insertAfter.next.getReference();
// nodeToInsert.next = new AtomicMarkableReference<>(nextInList, false);
// if (insertAfter.next.compareAndSet(nextInList, nodeToInsert, false, false)) {
// // insertAfter's next reference still points to nodeToInsert and insertAfter
// has
// // not
// // been marked for deletion. If the CAS operation returns true, insertAfter's
// // next
// // reference points to the inserted node
// return true;
// } else {
// // insertAfter may have been deleted from the list, so trying again will
// // probably result in
// // a stack overflow. We should allow the caller to handle the failure to
// insert.
// return false;
// }
// }

// /**
// * Delete the node with specified key if it exists.
// *
// * @param keyToDelete The key of the node we are trying to delete
// * @return True if successful
// */
// public boolean delete(int keyToDelete) {
// Node prev = this.head;
// Node current;
// Node succ;
// boolean[] markHolder = { false };
// while (true) {
// // Perform the same traversal as find but stop once node to delete is found
// while (true) {
// current = prev.next.get(markHolder);
// // Condition 1: We reach the end of the list without finding key
// if (current == null) {
// return false;
// }
// boolean currentMark = markHolder[0];
// int currentKey = current.key;
// // Condition 2: The previous node is marked or is no longer the previous node
// if (prev.next.get(markHolder) != current || markHolder[0] == true) {
// // Start over
// delete(keyToDelete);
// }
// // The current node is not marked (not deleted)
// if (!currentMark) {
// if (currentKey == keyToDelete) {
// break;
// } else if (currentKey > keyToDelete) {
// return false;
// } else {
// prev = current.next.get(markHolder);
// }
// }
// prev = current;
// }

// succ = current.next.getReference();
// // Try to perform logical deletion, if we can't then try again
// if (!current.next.compareAndSet(succ, succ, false, true)) {
// continue;
// }
// // Try to perform physical deletion, if we can't then find will
// if (prev.next.compareAndSet(current, succ, false, false)) {
// deleteNode(current);
// }
// return true;
// }
// }

// /**
// * Try to delete the node with specified key from the list starting at the
// * specified starting point (used for deletion from hash table using shortcut
// * references).
// *
// * @param startingPoint Node reference where we will begin our traversal
// * @param keyToDelete The key of the node we are trying to delete
// * @return True if successful
// */
// public boolean deleteAfter(Node startingPoint, int keyToDelete) {
// Node prev = startingPoint;
// Node current;
// Node succ;
// boolean[] markHolder = { false };
// while (true) {
// // Perform the same traversal as find but stop once node to delete is found
// while (true) {
// current = prev.next.get(markHolder);
// // Condition 1: We reach the end of the list without finding key
// if (current == null) {
// return false;
// }
// boolean currentMark = markHolder[0];
// int currentKey = current.key;
// // Condition 2: The previous node is marked or is no longer the previous node
// if (prev.next.get(markHolder) != current || markHolder[0] == true) {
// // Start over
// delete(keyToDelete);
// }
// // The current node is not marked (not deleted)
// if (!currentMark) {
// if (currentKey == keyToDelete) {
// break;
// } else if (currentKey > keyToDelete) {
// return false;
// } else {
// prev = current.next.get(markHolder);
// }
// }
// prev = current;
// }

// succ = current.next.getReference();
// if (!current.next.compareAndSet(succ, succ, false, true)) {
// continue;
// }
// if (prev.next.compareAndSet(current, succ, false, false)) {
// deleteNode(current);
// }
// return true;
// }
// }

// private void deleteNode(Node node) {
// node.key = 0;
// node.next = null;
// }

// /**
// * Returns a string representation of the list.
// */
// public String toString() {
// Node current = this.head;
// String string = "";
// while (current != null) {
// string += current.toString() + " -> ";
// current = current.next.getReference();
// }
// string += "NULL";
// return string;
// }
// }
