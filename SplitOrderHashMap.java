import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.ArrayList;


public class SplitOrderHashMap {
  int MAX_LOAD = 100;

  AtomicInteger itemCount;
  AtomicInteger size;
  // underlying LockFreeList
  LockFreeList lockFreeList;

  // dynamically sized buckets array
  ArrayList<Node> buckets;
  /**
   * Create a Split Ordered hash with initial Node
   *
   * @param head      The head of an existing LockFreeList (Node)
   * @param itemCount The number of items in an existing list
   */
  public SplitOrderHashMap(Node head, int itemCount) {
    this.lockFreeList = new LockFreeList(head, itemCount);
    this.itemCount = new AtomicInteger(0);
    this.size = new AtomicInteger(0);

    // init buckets with Null (UNINITIALIZED)
    this.buckets = new ArrayList<Node>(9);
    for (int i = 0; i < 9; i++) {
      this.buckets.add(null);
    }
    initialize_bucket(0);
  }

  /**
   * Create a Split Ordered hash from scratch
   */
  public SplitOrderHashMap() {
    this.lockFreeList = new LockFreeList();
    this.itemCount = new AtomicInteger(0);
    this.size = new AtomicInteger(0);
    this.buckets = new ArrayList<Node>(9);
    // initialize the 0th bucket to a reference to "0" key at beginning.
    // is it alright if this is 0
    initialize_bucket(0);
  }

  public int num_items() {
    return this.itemCount.intValue();
  }

  public int size() {
    return this.size.intValue();
  }
  private int _getParent(int bucket_num) {
    return bucket_num % this.buckets.size();
  }

  private int _bitKey(int number) {
    return number;
  }

  /**
   * Used Internally by insert() and constructors.
   */
  private void initialize_bucket(int bucket) {
    // this would be binary
    int bk_bucket = _bitKey(bucket);

    int parent = _getParent(bk_bucket);
    Node bucket_loc = this.buckets.get(parent);
    if (bucket_loc == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(parent);
    }

    // make this bits
    int bk_parent = _bitKey(parent);

    Node dummy = new Node(bk_parent);
    // if insert doesn't fail, dummy node with parent key now in list.
    //                            node to insert / insert after
    if (!this.lockFreeList.insertAt(dummy, this.buckets.get(bk_parent))) {
      // does this violate our linearizability??? if another thread calls find()
      // delete dummy if insert failed. reset with curr from the find operation in insert call
      dummy = this.lockFreeList.curr;
    }

    // finally, init bucket with dummy node
    this.buckets.set(bk_bucket, dummy);

    // pseudocode get parent macro that unsets buckets most sig, turned on bit. if exact
    // dummy node already exists in list, maybe another process already tried to initialize same bucket
    // in this case, fail and p

    // parent = GET_PARENT(bucket)
  }


  public int find(int key) {
    int bucket = key % size();
    Node bucket_loc = this.buckets.get(bucket);
    if (bucket_loc == null) {
      // recursively initialize parent bucket if it doesn't already exist. modulo
      initialize_bucket(bucket);
    }

    // TODO: need a findAt() function
    if (this.lockFreeList.find(key))
      return 1;
    else
      return 0;

  }

  public int delete(int key) {

    int bucket = key % size();
    Node bucket_loc = this.buckets.get(bucket);
    if (bucket_loc == null) {
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
    Node newNode = new Node(key); // next is null

    int bucket = key % size();

    if (this.buckets.get(bucket) == null) {
      initialize_bucket(bucket);
    }

    // fail to insertAt into the lockFreeList, return 0
    if (!this.lockFreeList.insertAt(this.buckets.get(_bitKey(bucket)), newNode))
    {
      // delete node
      return 0;
    }

    int csize = this.itemCount.intValue();
    if (this.itemCount.getAndIncrement() / csize  > MAX_LOAD)
      this.size.compareAndSet(csize, 2 * csize);

    return 1;
  }

  /**
   * Returns a string representation of the list.
   */
  public String toString() {
    return this.lockFreeList.toString();
  }
}
