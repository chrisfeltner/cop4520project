import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

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
    this.head = new Node(0, 0, 1);
    this.itemCount = new AtomicInteger(1);
    this.curr = head;
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return true if key is in list
   */
  public boolean find(int keyToFind) {
    return findAfter(this.head, keyToFind);
  }

  /**
   * Check if a key is in the list.
   *
   * @param keyToFind The value to find in the list
   * @return true if key is in list
   */
  public boolean findAfter(Node head, int keyToFind) {
    Node prev = head;
    boolean[] markHolder = { false };
    while (true) {
      curr = prev.next.get(markHolder);
      // Condition 1: We reach the end of the list without finding key
      if (curr == null) {
        return false;
      }
      Node next = curr.next.get(markHolder);
      boolean currentMark = markHolder[0];
      int currentKey = curr.key;
      // Condition 2: The previous node is marked or is no longer the previous node
      if (prev.next.get(markHolder) != curr || markHolder[0] == true) {
        // Start over
        findAfter(head, keyToFind);
      }
      // The current node is not marked (not deleted)
      if (!currentMark) {
        if (Integer.compareUnsigned(currentKey, keyToFind) >= 0) {
          System.out.println("CURR KEY " + currentKey);
          System.out.println("KEYTOFIND " + keyToFind);
          return Integer.compareUnsigned(currentKey,keyToFind) == 0;
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
          findAfter(head, keyToFind);
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
    return insertAt(nodeToInsert, this.head);
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
      insertAfter = this.head;

    Node prev = insertAfter;
    System.out.println("insert" + nodeToInsert.readableKey + "After" + " " + insertAfter);
    System.out.println(Integer.compareUnsigned(nodeToInsert.key, insertAfter.key));



    while (insertAfter != null && Integer.compareUnsigned(nodeToInsert.key, insertAfter.key) < 0) {
      prev = insertAfter;
      insertAfter = insertAfter.next.getReference();
      //System.out.println("going" + insertAfter);
    }
    //System.out.println("insert" + nodeToInsert.readableKey + "After" + " " + insertAfter);
    if (insertAfter == null) {
      insertAfter = prev;
    }

    //System.out.println("insert" + nodeToInsert.readableKey + "After" + " " + insertAfter);

    int key = nodeToInsert.key;
    //System.out.println("INSERT AFTER " + insertAfter.readableKey);
    // If the key is already in the set, there is no insertion needed
    if (findAfter(insertAfter, key)) {
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
  public boolean delete(int keyToDelete) {
    return deleteAfter(this.head, keyToDelete);
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
  public boolean deleteAfter(Node startingPoint, int keyToDelete) {
    Node prev = startingPoint;
    Node current;
    Node succ;
    boolean[] markHolder = { false };
    while (true) {
      // Perform the same traversal as find but stop once node to delete is found
      while (true) {
        current = prev.next.get(markHolder);
        // Condition 1: We reach the end of the list without finding key
        if (current == null) {
          return false;
        }
        boolean currentMark = markHolder[0];
        int currentKey = current.key;
        // Condition 2: The previous node is marked or is no longer the previous node
        if (prev.next.get(markHolder) != current || markHolder[0] == true) {
          // Start over
          delete(keyToDelete);
        }
        // The current node is not marked (not deleted)
        if (!currentMark) {
          if (Integer.compareUnsigned(currentKey,keyToDelete) == 0) {
            break;
          } else if (currentKey > keyToDelete) {
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

  private void deleteNode(Node node) {
    node.key = 0;
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
