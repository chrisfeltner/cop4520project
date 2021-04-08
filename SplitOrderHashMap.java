import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitOrderHashMap {
  final double MAX_LOAD = 2;
  final static int THRESHHOLD = 10;
  final static int DIGIT_COUNT = 8;
  AtomicInteger itemCount;
  AtomicInteger size;
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
    this.size = new AtomicInteger(1);
    this.buckets = new ArrayList<Node>(size.intValue());
    this.buckets.add(null);
    Node head = new Node(makeSentinelKey(0), 0, 1);
    this.buckets.set(0, head);
    // num items in hash map
    this.itemCount = new AtomicInteger(0);

    this.lockFreeList = new LockFreeList(head);


  }

  /**
   * Generates a Key for a non-bucket / sentinel node.
   *
   * @param data The data of a node used to create the key.
   */
  public static int makeOrdinaryKey(int data) {
    Integer code;
    if (DIGIT_COUNT == 8)
      code = data & 0x000000FF;
    else
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
    if (DIGIT_COUNT == 8)
      code = data & 0x000000FF;
    else
      code = data & 0x00FFFFFF;
    code = Integer.reverse(code);
    return code;
  }

  public int num_items() {
    return this.itemCount.intValue();
  }

  public int size() {
    return this.size.intValue();
  }

  private int getParent(int myBucket) {
    int parent = size();
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
    int bucketKey = makeSentinelKey(bucket);
    int parent = getParent(bucket);

    // make this bits
    int parentKey = makeSentinelKey(parent);

    Node dummy = new Node(bucketKey, bucket, 1);
    // if insert doesn't fail, dummy node with parent key now in list.
    // node to insert / insert after
    //System.out.println("INIT BUCKET " + this.buckets.get(parent));
    if (!this.lockFreeList.insertAt(dummy, this.buckets.get(parent))) {
      // does this violate our linearizability??? if another thread calls find()
      // delete dummy if insert failed. reset with curr from the find operation in
      // insert call
      dummy = this.lockFreeList.curr;
      dummy.dummy = true;
    }

    // finally, init bucket with dummy node
    this.buckets.set(bucket, dummy);

    // pseudocode get parent macro that unsets buckets most sig, turned on bit. if
    // exact
    // dummy node already exists in list, maybe another process already tried to
    // initialize same bucket
    // in this case, fail and p

    // parent = GET_PARENT(bucket)
  }

  public int find(int key) {
    int bucket = key % size();
    Node bucketIndex = this.buckets.get(bucket);
    if (bucketIndex == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucket);
    }

    // TODO: need a findAt() function
    if (this.lockFreeList.findAfter(bucketIndex, key))
      return 1;
    else
      return 0;

  }

  public int delete(int key) {

    int bucket = key % size();
    Node bucketIndex = this.buckets.get(bucket);
    if (bucketIndex == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucket);
    }

    if (!this.lockFreeList.deleteAfter(this.buckets.get(bucket), key)) {
      return 0;
    }

    this.itemCount.getAndDecrement();

    return 1;

  }

  public int insert(int key) {
    // this key will be binary eventually
    Node newNode = new Node(makeOrdinaryKey(key), key, 0); // next is null

    int bucket = key % size();

    if (this.buckets.get(bucket) == null) {
      // System.out.println("BUCKET DOESN'T EXIST YET");
      initialize_bucket(bucket);
    }
    // System.out.println("Testing");
    // System.out.println(this);

    // fail to insertAt into the lockFreeList, return 0
    if (!this.lockFreeList.insertAt(newNode, this.buckets.get(bucket))) {
      System.out.println("Could NOT insert " + key);
      // delete node
      return 0;
    }

    int csize = size();
    if ((double) (this.itemCount.incrementAndGet() / csize) > MAX_LOAD) {
      // System.out.println("EXPANDING");
      this.size.compareAndSet(csize, 2 * csize);
      // double size of array list add nulls
      // TODO: how does this resizing work with binary???
      for (int i = 0; i < csize; i++) {
        this.buckets.add(null);
      }
    }
    // System.out.println("Variable 1 = " + this.itemCount.get());
    // System.out.println("Variable 2 = " + this.size.get());
    return 1;
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
