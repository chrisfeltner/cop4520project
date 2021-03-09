import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;


public class SplitOrderHashMap {
  AtomicInteger size;
  LockFreeList lockFreeList;
  AtomicReferenceArray<Node> buckets;
  /**
   * Create a Split Ordered hash with initial Node
   *
   * @param head      The head of an existing LockFreeList (Node)
   * @param itemCount The number of items in an existing list
   */
  public SplitOrderHashMap(Node head, int itemCount) {
    this.lockFreeList = new LockFreeList(head, itemCount);
    this.size = lockFreeList.itemCount;
    this.buckets = new AtomicReferenceArray<Node>(9);
    initialize_bucket(0, this.lockFreeList.head);
  }

  /**
   * Create a Split Ordered hash from scratch
   */
  public SplitOrderHashMap() {
    this.lockFreeList = new LockFreeList();
    this.size = lockFreeList.itemCount;
    this.buckets = new AtomicReferenceArray<Node>(9);
    // initialize the 0th bucket to a reference to "0" key at beginning.
    // is it alright if this is 0
    initialize_bucket(0, this.lockFreeList.head);
  }

  /**
   * Used Internally by insert() and constructors.
   */
  private void initialize_bucket(int bucket, Node node) {
    this.buckets.set(bucket, node);
  }


  /**
   * Returns a string representation of the list.
   */
  public String toString() {
    return this.lockFreeList.toString();
  }
}
