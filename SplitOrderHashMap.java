import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class SplitOrderHashMap {
  final double MAX_LOAD = 1;
  final double MIN_LOAD = 0.5;
  AtomicInteger itemCount;
  AtomicInteger numBuckets;
  // underlying LockFreeList
  LockFreeList lockFreeList;

  // dynamically sized buckets array
  SegmentTable buckets;

  /**
   * Create a Split Ordered hash with initial Node.
   *
   */
  public SplitOrderHashMap() {
    // size of bucket list
    this.numBuckets = new AtomicInteger(1);
    this.buckets = new SegmentTable();
    // this.buckets.add(null);
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
   * @return the ordinary key of the data
   */
  public static int makeOrdinaryKey(int key) {
    Integer code;
    code = key & 0x00FFFFFF;
    code = Integer.reverse(code);
    code |= 1;
    return code;
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
   * 
   * Generates a Key for a bucket / sentinel node.
   *
   * @param data The data of a node used to create the key.
   * @return the sentinel key of the data node.
   */
  public static int makeSentinelKey(int key) {
    Integer code;
    code = key & 0x00FFFFFF;
    code = Integer.reverse(code);
    return code;
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
    // int bucketKey = makeSentinelKey(bucket);
    int parent = getParent(bucket);
    if (this.buckets.get(parent) == null) {
      // System.out.println("PARENTS does not exist so we're initialize parent: \t" +
      // parent);
      initialize_bucket(parent);
    }

    Node result = this.lockFreeList.insertAt(this.buckets.get(parent), bucket, true);

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
  public boolean find(int data) {
    int bucketIndex = data % numBuckets();
    Node bucket = this.buckets.get(bucketIndex);
    if (bucket == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucketIndex);
      bucket = this.buckets.get(bucketIndex);
    }

    // TODO: need a findAt() function
    Window window = this.lockFreeList.findAfter(bucket, makeOrdinaryKey(data));
    Node pred = window.pred, curr = window.curr;
    if (curr != null && curr.data == data)
      return true;
    else
      return false;
  }

  /**
   * Try to delete a certain data in the map
   * 
   * @param data the data to delete
   * @return whether or not the data was deleted in the map
   */
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
    int localNumBuckets = numBuckets();
    if ((double) (this.itemCount.decrementAndGet() / localNumBuckets) < MIN_LOAD) {
      this.numBuckets.compareAndSet(localNumBuckets, localNumBuckets / 2);
    }
    return true;
  }

  /**
   * Try to insert a certain data in the map
   * 
   * @param data the data to insert
   * @return whether or not the data was inserted in the map
   */
  public boolean insert(int data) {
    // System.out.println("Inserting " + data);
    int bucketIndex = data % numBuckets();

    if (this.buckets.get(bucketIndex) == null) {
      initialize_bucket(bucketIndex);
    }

    // fail to insertAt into the lockFreeList, return 0
    Node result = this.lockFreeList.insertAt(this.buckets.get(bucketIndex), data, false);
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
   * @return a string representation of the map.
   */
  public String toString() {
    String s = "======================================================\nBUCKETS: \n";
    AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray = this.buckets.currentTable.getReference();
    final int outerLength = outerArray.length();
    final int innerLength = SegmentTable.MIDDLE_SIZE;
    final int segSize = SegmentTable.SEGMENT_SIZE;
    // Loop through all active segments in order to print the segment table
    for (int i = 0; i < outerLength; i++) {
      if (outerArray.get(i) != null) {
        s = s.concat("outer array position : " + i + "\n");
        for (int j = 0; j < innerLength; j++) {
          if (outerArray.get(i).get(j) != null) {
            s = s.concat("\tinner array position : " + j + "\n");
            for (int k = 0; k < segSize; k++) {
              Node node = outerArray.get(i).get(j).segment.get(k);
              if (node != null) {
                s = s.concat("\t\tsegment position : " + k + " " + node.toString() + " \n");
              }
            }
          }
        }
      }
    }
    return s; // s.concat("\nUNDERLYING LIST:\n" + this.lockFreeList.toString());
  }
}
