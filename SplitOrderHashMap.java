import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitOrderHashMap {
  final double MAX_LOAD = 2;
  final static int THRESHHOLD = 10;
  AtomicInteger itemCount;
  AtomicInteger numBuckets;
  // underlying LockFreeList
  LockFreeList lockFreeList;

  // dynamically sized buckets array
  ArrayList<Node> buckets;

  /**
   * Create a Split Ordered hash with initial Node.
   *
   */
  public SplitOrderHashMap() {
    // size of bucket list
    this.numBuckets = new AtomicInteger(1);
    this.buckets = new ArrayList<Node>(numBuckets.intValue());
    this.buckets.add(null);
    // Node head = new Node(0, 0, 1);
    // num items in hash map
    this.itemCount = new AtomicInteger(0);

    this.lockFreeList = new LockFreeList();
    this.buckets.set(0, this.lockFreeList.head);
  }

  /**
   * Generates a Key for a non-bucket / sentinel node.
   *
   * @param data The data of a node used to create the key.
   */
  public static int makeOrdinaryKey(int data) {
    Integer code;
    code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    code |= 1;
    return code;
  }

  /**
   * FOR TOSTRING()
   * Generates a Key for a bucket / sentinel node.
   *
   * @param data The data of a node used to create the key.
   */
  public static int makeSentinelKey(int data) {
    Integer code;
    code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    return code;
  }

  public int num_items() {
    return this.itemCount.intValue();
  }

  public int numBuckets() {
    return this.numBuckets.intValue();
  }

  private int getParent(int myBucket) {
    int parent = numBuckets();
    do {
      parent = parent >> 1;
    } while (parent > myBucket);
    parent = myBucket - parent;
    return parent;
  }



  /**
   * Used Internally by insert() and constructors.
   */
  private void initialize_bucket(int bucket) {
    // this would be binary
    // int bucketKey = makeSentinelKey(bucket);
    int parent = getParent(bucket);
    if (this.buckets.get(parent) == null)
    {
      System.out.println("PARENTS does not exist so we're initialize parent: \t" + parent);
      initialize_bucket(parent);
    }

    Node result = this.lockFreeList.insertAt(this.buckets.get(parent), bucket , true);

    if (result != null)
    {
      // finally, init bucket with dummy node
      this.buckets.set(bucket, result);
    }

  }

  public boolean find(int data) {
    int bucketIndex = data % numBuckets();
    Node bucket = this.buckets.get(bucketIndex);
    if (bucket == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucketIndex);
    }

    // TODO: need a findAt() function
    Window window = this.lockFreeList.findAfter(bucket, makeOrdinaryKey(data));
    Node pred = window.pred, curr = window.curr;
    if (curr != null)
      return true;
    else
      return false;
  }
  
  public boolean delete(int data) {
    int bucketIndex = data % numBuckets();
    Node bucket = this.buckets.get(bucketIndex);
    if (bucket == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucketIndex);
    }

    Node result = this.lockFreeList.deleteAfter(this.buckets.get(bucketIndex), data);
    if (result == null) {
      return false;
    }
    this.itemCount.getAndDecrement();
    return true;
  }

  public boolean insert(int data) {
    int bucketIndex = data % numBuckets();

    if (this.buckets.get(bucketIndex) == null) {
      initialize_bucket(bucketIndex);
    }

    // fail to insertAt into the lockFreeList, return 0
    Node result = this.lockFreeList.insertAt(this.buckets.get(bucketIndex), data, false);
    if (result == null) {
      System.out.println("Could NOT insert " + data);
      // delete node
      return false;
    }

    int localNumBuckets = numBuckets();
    if ((double) (this.itemCount.incrementAndGet() / localNumBuckets) >= MAX_LOAD) {
      // System.out.println("EXPANDING");
      this.numBuckets.compareAndSet(localNumBuckets, 2 * localNumBuckets);
      // double size of array list add nulls
      // TODO: how does this resizing work with binary???
      for (int i = localNumBuckets; i < 2*localNumBuckets; i++) {
        this.buckets.add(null);
      }
    }
    return true;
  }

  /**
   * Returns a string representation of the map.
   */
  public String toString() {
    String s = "======================================================\nBUCKETS: \n";
    int i = 0;
    for (Node bucket : buckets) {
      String b;
      if (bucket == null) {
        b = "null";
      } else {
        b = bucket.toString();
      }
      s = s.concat("bucket" + i + ": " + b + "\n");
      i += 1;
    }
    return s.concat("\nUNDERLYING LIST:\n" + this.lockFreeList.toString());
  }
}
