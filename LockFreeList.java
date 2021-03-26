import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.BitSet;

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
  public LockFreeList(Node head, int itemCount) {
    if (head == null) {
      this.head = new Node(0, new AtomicMarkableReference<Node>(null, false), false);
      this.itemCount = new AtomicInteger(0);
    } else {
      this.head = head;
      this.itemCount = new AtomicInteger(itemCount);
    }
    this.curr = head;
  }

  public LockFreeList() {
    this.head = new Node(0, new AtomicMarkableReference<Node>(null, false), false);
    this.itemCount = new AtomicInteger(0);
    this.curr = head;
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

  /**
   * Check if a curr is greater than key.
   *
   * @param lhs left hand side of the equation
   * @param rhs right hand side of the equation
   * @return true if curr is greater than key
   */
  public static boolean greaterThan(BitSet lhs, BitSet rhs) {
    BitSet xor = (BitSet) lhs.clone();
    xor.xor(rhs);
    int firstDifferent = xor.length() - 1;
    if (rhs.get(firstDifferent)) {
      return false; // rhs is greater
    } else {
      return true; // lhs is greater
    }
  }

  /**
   * Check if a curr is less than key.
   *
   * @param lhs left hand side of the equation
   * @param rhs right hand side of the equation
   * @return true if curr is greater than key
   */
  public static boolean lessThan(BitSet lhs, BitSet rhs) {
    BitSet xor = (BitSet) lhs.clone();
    xor.xor(rhs);
    int firstDifferent = xor.length() - 1;
    if (rhs.get(firstDifferent)) {
      return true; // lhs is less than rhs
    } else {
      return false; // rhs is less than lhs
    }
  }

  /**
   * Check if a key is in the list.
   *
   * @param dataToFind The value to find in the list
   * @return true if key is in list
   */
  public boolean find(int dataToFind) {
    Node prev = this.head;
    BitSet keyToFind = intToBitSet(dataToFind, false);
    boolean[] markHolder = { false };
    while (true) {
      curr = prev.next.get(markHolder);
      // Condition 1: We reach the end of the list without finding key
      if (curr == null) {
        return false;
      }
      Node next = curr.next.get(markHolder);
      boolean currentMark = markHolder[0];
      BitSet currentKey = curr.key;
      // Condition 2: The previous node is marked or is no longer the previous node
      if (prev.next.get(markHolder) != curr || markHolder[0] == true) {
        // Start over
        find(dataToFind);
      }
      // The current node is not marked (not deleted)
      if (!currentMark) {
        if (currentKey.equals(keyToFind) || greaterThan(keyToFind, currentKey)) {
          return currentKey.equals(keyToFind);
        } else {
          prev = curr.next.get(markHolder);
        }
      } else {
        // The current node has been marked for deletion!
        if (prev.next.compareAndSet(curr, next, false, false)) {
          // Node will be garbage collected by Java runtime
          deleteNode(curr);
        } else {
          // Deletion failed; try again
          find(dataToFind);
        }
      }
      prev = curr;
    }
  }

  /**
   * Insert a node after the head of the list.
   *
   * @param nodeToInsert A new node with key set, but next reference null
   * @return True if successfully inserted
   */
  public boolean insert(Node nodeToInsert) {
    int data = nodeToInsert.data;
    // If the data is already in the set, there is no insertion needed
    if (find(data)) {
      return false;
    }
    // Set next reference for node
    Node nextInList = this.head.next.getReference();
    nodeToInsert.next = new AtomicMarkableReference<>(nextInList, false);
    if (head.next.compareAndSet(nextInList, nodeToInsert, false, false)) {
      // The head's next reference still points to nodeToInsert and head has not
      // been marked for deletion. If the CAS operation returns true, head's next
      // reference points to the inserted node
      return true;
    } else {
      // Try again; We can assume that it is illegal to delete head
      return insert(nodeToInsert);
    }
  }

  /**
   * Insert a node after another node.
   *
   * @param nodeToInsert The new node with next pointer null
   * @param insertAfter  The node to insert nodeToInsert after
   * @return True if successful
   */
  public boolean insertAt(Node nodeToInsert, Node insertAfter) {
    if (insertAfter == null)
      return insert(nodeToInsert);
    Node prev = null;
    while (insertAfter != null && lessThan(nodeToInsert.key, insertAfter.key)) {
      prev = insertAfter;
      insertAfter = insertAfter.next.getReference();
    }
    if (insertAfter == null) {
      insertAfter = prev;
    }
    int data = nodeToInsert.data;
    // If the data is already in the set, there is no insertion needed
    if (find(data)) {
      return false;
    }
    Node nextInList = insertAfter.next.getReference();
    nodeToInsert.next = new AtomicMarkableReference<>(nextInList, false);
    if (insertAfter.next.compareAndSet(nextInList, nodeToInsert, false, false)) {
      // insertAfter's next reference still points to nodeToInsert and insertAfter has
      // not
      // been marked for deletion. If the CAS operation returns true, insertAfter's
      // next
      // reference points to the inserted node
      return true;
    } else {
      // insertAfter may have been deleted from the list, so trying again will
      // probably result in
      // a stack overflow. We should allow the caller to handle the failure to insert.
      return false;
    }
  }

  /**
   * Delete the node with specified key if it exists.
   *
   * @param keyToDelete The key of the node we are trying to delete
   * @return True if successful
   */
  public boolean delete(int dataToDelete) {
    Node prev = this.head;
    Node current;
    Node succ;
    boolean[] markHolder = { false };
    BitSet keyToDelete = intToBitSet(dataToDelete, false);
    while (true) {
      // Perform the same traversal as find but stop once node to delete is found
      while (true) {
        current = prev.next.get(markHolder);
        // Condition 1: We reach the end of the list without finding key
        if (current == null) {
          return false;
        }
        boolean currentMark = markHolder[0];
        BitSet currentKey = current.key;
        // Condition 2: The previous node is marked or is no longer the previous node
        if (prev.next.get(markHolder) != current || markHolder[0] == true) {
          // Start over
          delete(dataToDelete);
        }
        // The current node is not marked (not deleted)
        if (!currentMark) {
          if (currentKey.equals(keyToDelete)) {
            break;
          } else if (greaterThan(currentKey, keyToDelete)) {
            return false;
          } else {
            prev = current.next.get(markHolder);
          }
        }
        prev = current;
      }

      succ = current.next.getReference();
      // Try to perform logical deletion, if we can't then try again
      if (!current.next.compareAndSet(succ, succ, false, true)) {
        continue;
      }
      // Try to perform physical deletion, if we can't then find will
      if (prev.next.compareAndSet(current, succ, false, false)) {
        deleteNode(current);
      }
      return true;
    }
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
  public boolean deleteAfter(Node startingPoint, int dataToDelete) {
    Node prev = startingPoint;
    Node current;
    Node succ;
    boolean[] markHolder = { false };
    BitSet keyToDelete = intToBitSet(dataToDelete, false);
    while (true) {
      // Perform the same traversal as find but stop once node to delete is found
      while (true) {
        current = prev.next.get(markHolder);
        // Condition 1: We reach the end of the list without finding key
        if (current == null) {
          return false;
        }
        boolean currentMark = markHolder[0];
        BitSet currentKey = current.key;
        // Condition 2: The previous node is marked or is no longer the previous node
        if (prev.next.get(markHolder) != current || markHolder[0] == true) {
          // Start over
          delete(dataToDelete);
        }
        // The current node is not marked (not deleted)
        if (!currentMark) {
          if (currentKey.equals(keyToDelete)) {
            break;
          } else if (greaterThan(currentKey, keyToDelete)) {
            return false;
          } else {
            prev = current.next.get(markHolder);
          }
        }
        prev = current;
      }

      succ = current.next.getReference();
      if (!current.next.compareAndSet(succ, succ, false, true)) {
        continue;
      }
      if (prev.next.compareAndSet(current, succ, false, false)) {
        deleteNode(current);
      }
      return true;
    }
  }

  // TODO: when adding buckets, make sure to add a bucket/dummy node check so we
  // dont delete a bucket
  private void deleteNode(Node node) {
    node.data = 0;
    node.next = null;
  }

  /**
   * Returns a string representation of the list.
   */
  public String toString() {
    Node current = this.head;
    String string = "";
    while (current != null) {
      string += current.toString() + " -> ";
      current = current.next.getReference();
    }
    string += "NULL";
    return string;
  }
}
