import java.util.concurrent.atomic.AtomicInteger;

public class SplitOrderHashMap<T> {
  final double MAX_LOAD = 2;
  final double MIN_LOAD = 0.5;
  final boolean CONTRACT = true;
  AtomicInteger itemCount;
  AtomicInteger numBuckets;
  // underlying LockFreeList
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
    return this.buckets.numBuckets();
  }

  /**
   * Gets the parent of a given bucket. * @param myBucket The bucket whose parent
   * will be gotten
   * 
   * @return the index of the parent bucket
   */
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
   * 
   * @param bucket the bucket to initialize
   */
  private void initialize_bucket(int bucket) {
    // this would be binary
    int parent = getParent(bucket);
    if (this.buckets.get(parent) == null) {
      // System.out.println("PARENTS does not exist so we're initialize parent: \t" +
      // parent);
      initialize_bucket(parent);
    }

    Node<T> result = this.lockFreeList.insertAt(this.buckets.get(parent), new Node<T>(bucket));

    if (result != null) {
      // finally, init bucket with dummy node
      // System.out.println("Initializing bucket " + bucket);
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
    int bucketIndex = data.hashCode() % numBuckets();
    Node<T> bucket = this.buckets.get(bucketIndex);
    if (bucket == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucketIndex);
      bucket = this.buckets.get(bucketIndex);
    }

    // TODO: need a findAt() function
    Window<T> window = this.lockFreeList.findAfter(bucket, new Node<T>(data, false));
    Node<T> curr = window.curr;
    if (curr != null && curr.data.equals(data)) {
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
    int bucketIndex = data.hashCode() % numBuckets();
    Node<T> bucket = this.buckets.get(bucketIndex);
    if (bucket == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucketIndex);
    }

    Node<T> result = this.lockFreeList.deleteAfter(this.buckets.get(bucketIndex), new Node<T>(data, false));
    if (result == null) {
      return false;
    }
    int localNumBuckets = numBuckets();
    if ((double) (this.itemCount.decrementAndGet() / localNumBuckets) < MIN_LOAD) {
      if (CONTRACT) {
        // System.out.println("Contracting");
        this.buckets.contract();
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
    // System.out.println("Inserting " + data);
    int bucketIndex = data.hashCode() % numBuckets();

    if (this.buckets.get(bucketIndex) == null) {
      initialize_bucket(bucketIndex);
    }

    // fail to insertAt into the lockFreeList, return 0
    Node<T> result = this.lockFreeList.insertAt(this.buckets.get(bucketIndex), new Node<T>(data, false));
    if (result == null) {
      // System.out.println("Could NOT insert " + data);
      // delete node
      return false;
    }

    int localNumBuckets = numBuckets();
    // System.out.println(localNumBuckets);
    if ((double) (this.itemCount.incrementAndGet() / localNumBuckets) >= MAX_LOAD) {
      // System.out.println("Expand");
      this.buckets.expand();
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
        int parent = buckets.getOldParent(dummy.bucket);
        Node<T> parentNode = this.buckets.get(parent);
        while (parentNode == null) {
          parent = buckets.getOldParent(parent);
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
