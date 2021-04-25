
import java.util.concurrent.atomic.AtomicInteger;

public class SplitOrderHashMap<T> {
  double MAX_LOAD = 2;

  final double MIN_LOAD = 0.5;
  final boolean CONTRACT = true;
  AtomicInteger itemCount;
  AtomicInteger numBuckets;
  // underlying LockFreeList

  String name;

  LockFreeList<T> lockFreeList;

  // dynamically sized buckets array
  SegmentTable<T> buckets;

  /**
   * Create a Split Ordered hash with initial Node.
   *
   */
  public SplitOrderHashMap() {
    // size of bucket list
    this.numBuckets = new AtomicInteger(1);
    this.buckets = new SegmentTable<T>();
    // this.buckets.add(null);
    // Node head = new Node(0, 0, 1);
    // num items in hash map
    this.itemCount = new AtomicInteger(0);

    this.lockFreeList = new LockFreeList<T>();
    this.buckets.set(0, this.lockFreeList.head);
  }

  public SplitOrderHashMap(int maxLoad) {
    // size of bucket list
    this.numBuckets = new AtomicInteger(1);
    this.buckets = new SegmentTable<T>();
    // this.buckets.add(null);
    // Node head = new Node(0, 0, 1);
    // num items in hash map
    this.itemCount = new AtomicInteger(0);

    this.lockFreeList = new LockFreeList<T>();
    this.buckets.set(0, this.lockFreeList.head);
    MAX_LOAD = maxLoad;
    this.name = "splitOrderHash_MaxLoad_" + maxLoad;
  }

  /**
   * Returns true if the given key is an ordinary key (LSB is 1)
   *
   * @param key to check
   * @return true if key is ordinary, false otherwise
   */
  public static boolean isOrdinaryKey(int key) {
    return (key & 1) == 1;
  }

  /**
   * Returns true if the given key is a sentinel (dummy) key (LSB is 0)
   *
   * @param key to check
   * @return true if key is sentinel (dummy), false otherwise
   */
  public static boolean isSentinelKey(int key) {
    return !isOrdinaryKey(key);
  }

  /**
   * @return num items in hash map.
   */
  public int num_items() {
    return this.itemCount.intValue();
  }

  /**
   * @return num buckets in hash map.
   */
  public int numBuckets() {
    return this.buckets.currentTable.getStamp();
  }

  /**
   * Gets the parent of a given bucket. * @param myBucket The bucket whose parent
   * will be gotten
   *
   * @return the index of the parent bucket
   */
  private int getParent(int myBucket) {
    if (myBucket == 1) {
      return 0;
    }
    int parent = this.buckets.currentTable.getStamp();
    do {
      parent = parent >> 1;
    } while (parent > myBucket);
    parent = myBucket - parent;
    return parent;
  }

  /**
   * Used Internally by insert() and constructors.
   *
   * @param bucket the bucket to initialize
   */
  private void initialize_bucket(int bucket) {
    // this would be binary
    int parent = getParent(bucket);
    // System.out.println("Initialize bucket " + bucket + " with parent " + parent);
    if (this.buckets.get(parent) == null) {
      // System.out.println("PARENTS does not exist so we're initialize parent: \t" +
      // parent);
      initialize_bucket(parent);
    }
    Node<T> newDummy = new Node<T>(bucket);

    Node<T> result = this.lockFreeList.insertAt(this.buckets.get(parent), newDummy);

    if (result != null) {
      // finally, init bucket with dummy node
      this.buckets.set(bucket, result);
    }
    // Another thread may have initialized the bucket in the time it took us to do
    // it.
    // Insert fails on duplicate keys, so let's check if the segment table has the
    // element.
    else if (this.buckets.get(bucket) == null) {

      this.buckets.set(bucket, result);
    }
  }

  /**
   * Try to find a certain data in the map.
   *
   * @param data the data to find
   * @return whether the data was found in the map
   */
  public boolean find(T data) {
    if (CONTRACT) {
      removeUselessDummy();
    }
    boolean retry = true;
    Node<T> curr = null;
    int numBuckets = this.buckets.currentTable.getStamp();
    int bucketIndex = data.hashCode() % numBuckets;
    while (retry) {
      Node<T> bucket = this.buckets.get(bucketIndex);
      if (bucket == null) {
        // recursively initialize parent bucket if it doesn't already exist. modulo
        initialize_bucket(bucketIndex);
      }

      if (bucket == null) {
        bucketIndex = getParent(bucketIndex);
        continue;
      }
      Window<T> window = this.lockFreeList.findAfter(bucket, new Node<T>(data, false));
      curr = window.curr;
      if (curr == null) {
        if (numBuckets != this.buckets.currentTable.getStamp()) {
          retry = true;
          continue;
        }
      }
      retry = false;
    }

    if (curr != null && curr.data != null && curr.data.equals(data)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Try to delete a certain data in the map
   *
   * @param data the data to delete
   * @return whether or not the data was deleted in the map
   */
  public boolean delete(T data) {
    if (CONTRACT) {
      removeUselessDummy();
    }
    boolean retry = true;
    int numBuckets = this.buckets.currentTable.getStamp();
    int bucketIndex = data.hashCode() % numBuckets;
    while (retry) {

      if (this.buckets.get(bucketIndex) == null) {
        // recursively initialize parent bucket if it doesn't already exist. modulo
        initialize_bucket(bucketIndex);
      }

      Node<T> result = this.lockFreeList.deleteAfter(this.buckets.get(bucketIndex), new Node<T>(data, false));
      if (result == null) {
        if (this.find(data)) {
          bucketIndex = getParent(bucketIndex);
          continue;
        }
        return false;
      }
      retry = false;
      break;
    }

    int localNumBuckets = this.buckets.currentTable.getStamp();
    if ((double) (this.itemCount.decrementAndGet() / localNumBuckets) < (1 / MAX_LOAD)) {
      if (CONTRACT) {
        // System.out.println("Contracting");
        this.buckets.contract(localNumBuckets);
      }

    }
    return true;
  }

  /**
   * Try to insert a certain data in the map
   *
   * @param data the data to insert
   * @return whether or not the data was inserted in the map
   */
  public boolean insert(T data) {
    if (CONTRACT) {
      removeUselessDummy();
    }

    boolean retry = true;
    int numBuckets = this.buckets.currentTable.getStamp();
    int bucketIndex = data.hashCode() % numBuckets;
    while (retry) {

      // System.out.println("Inserting " + data + " at bucket " + bucketIndex);

      if (this.buckets.get(bucketIndex) == null) {
        // System.out.println("Bucket " + bucketIndex + " does not exist.");
        initialize_bucket(bucketIndex);
      }

      // fail to insertAt into the lockFreeList, return 0
      Node<T> result = this.lockFreeList.insertAt(this.buckets.get(bucketIndex), new Node<T>(data, false));
      if (result == null) {
        if (!this.find(data)) {
          bucketIndex = getParent(bucketIndex);
          continue;
        }
        // System.out.println("Failed");
        return false;
      }
      retry = false;
      break;
    }

    int localNumBuckets = this.buckets.currentTable.getStamp();
    // System.out.println(localNumBuckets);
    if ((double) (this.itemCount.incrementAndGet() / localNumBuckets) >= MAX_LOAD) {
      // System.out.println("Expand");
      this.buckets.expand(localNumBuckets);
    }

    return true;
  }

  /**
   * "Tax" for operation: old dummy node removal. Removes two dummy nodes that are
   * no longer accessible.
   */
  private void removeUselessDummy() {
    for (int i = 0; i < 2; i++) {
      Node<T> dummy = buckets.getUselessDummy();
      if (dummy != null) {
        // System.out.println("Removing " + dummy.bucket);
        int parent = getParent(dummy.bucket);
        Node<T> parentNode = this.buckets.get(parent);
        while (parentNode == null) {
          parent = getParent(parent);
          parentNode = this.buckets.get(parent);
        }
        this.lockFreeList.deleteAfter(this.buckets.get(parent), dummy);
      }
    }

  }

  /**
   * @return a string representation of the map.
   */
  public String toString() {
    String s = this.buckets.toString();
    return s.concat("\nUNDERLYING LIST:\n" + this.lockFreeList.toString());
  }
}
