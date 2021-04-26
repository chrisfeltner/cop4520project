import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

public class SegmentTable<T> {
  // Declare constants for array sizes
  public static final int MIDDLE_SIZE = 256;
  public static final int SEGMENT_SIZE = 32;
  // Segment table will be a fragmented array
  public AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment<T>>>> currentTable;
  public AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment<T>>>> oldTable;
  public AtomicInteger oldTableCounter;

  /**
   * Create a Segment Table.
   *
   */
  public SegmentTable() {
    // Upon creation of segment table make segment 0 active for dummy node 0
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = new AtomicReferenceArray<>(1);
    outerArray.set(0, new AtomicReferenceArray<Segment<T>>(MIDDLE_SIZE));
    outerArray.get(0).set(0, new Segment<T>(SEGMENT_SIZE));
    currentTable = new AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment<T>>>>(outerArray, 1);
    oldTable = new AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment<T>>>>(null, 0);
    oldTableCounter = new AtomicInteger(-1);
  }

  /**
   * Finds the position (bucket) in the segment table and returns the node.
   *
   * @param bucket The value of the dummy node we want to get.
   * @return the dummy node we are looking for or null if it doesn't exist.
   */
  public Node<T> get(int bucket) {
    int[] stampHolder = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = this.currentTable.get(stampHolder);

    // Check if the bucket is within the current size
    if (bucket > stampHolder[0]) {
      // System.out.println("Bucket > numBuckets");
      return null;
    }
    int outerIndex = bucket / (MIDDLE_SIZE * SEGMENT_SIZE);
    int innerIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) / SEGMENT_SIZE;
    int segmentIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) % SEGMENT_SIZE;

    AtomicReferenceArray<Segment<T>> innerArray = outerArray.get(outerIndex);
    if (innerArray == null) {
      return null;
    }

    Segment<T> seg = innerArray.get(innerIndex);
    if (seg == null) {
      return null;
    }

    Node<T> node = seg.segment.get(segmentIndex);
    if (node == null) {
      return null;
    }
    return node;
  }

  /**
   * Sets the position (bucket) in the segment table to dummy node.
   *
   * @param bucket    The value of the dummy node we want to get.
   * @param dummyNode The node we want to set position bucket to.
   */
  public boolean set(int bucket, Node<T> dummyNode) {
    int[] stampHolder = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = this.currentTable.get(stampHolder);
    // Check if the bucket is within the current size
    if (bucket > stampHolder[0]) {
      return false;
    }
    int outerIndex = bucket / (MIDDLE_SIZE * SEGMENT_SIZE);
    int innerIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) / SEGMENT_SIZE;
    int segmentIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) % SEGMENT_SIZE;

    AtomicReferenceArray<Segment<T>> innerArray = outerArray.get(outerIndex);
    // If inner array is null create new array of segments
    if (innerArray == null) {
      outerArray.compareAndSet(outerIndex, null, new AtomicReferenceArray<Segment<T>>(MIDDLE_SIZE));
      innerArray = outerArray.get(outerIndex);
    }

    Segment<T> seg = innerArray.get(innerIndex);
    // If current segment is null create a new segment
    if (seg == null) {
      innerArray.compareAndSet(innerIndex, null, new Segment<T>(SEGMENT_SIZE));
      seg = innerArray.get(innerIndex);
    }

    // Get dummy node
    return seg.segment.compareAndSet(segmentIndex, null, dummyNode);
  }

  public boolean expand(int numBuckets) {
    int[] originalSize = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = this.currentTable.get(originalSize);
    if (numBuckets != originalSize[0]) {
      return false;
    }
    int newSize = originalSize[0] * 2;
    int outerArraySize = (newSize / (MIDDLE_SIZE * SEGMENT_SIZE)) + 1;
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> newOuterArray = new AtomicReferenceArray<AtomicReferenceArray<Segment<T>>>(
        outerArraySize);
    for (int i = 0; i <= originalSize[0] / (MIDDLE_SIZE * SEGMENT_SIZE); i++) {

      newOuterArray.set(i, outerArray.get(i));
    }
    // System.out.println(this.toString());
    return this.currentTable.compareAndSet(outerArray, newOuterArray, originalSize[0], newSize);
  }

  public boolean contract(int numBuckets) {
    int[] originalSize = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = this.currentTable.get(originalSize);
    if (numBuckets != originalSize[0]) {
      // System.out.println("Size mismatch");
      return false;
    }
    if (originalSize[0] > 1) {
      int[] oldTableSize = { 0 };
      AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> oldTable = this.oldTable.get(oldTableSize);
      int newSize = Math.max(originalSize[0] / 2, 1);
      int outerArraySize = Math.max((newSize / (MIDDLE_SIZE * SEGMENT_SIZE)), 1);
      AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> newOuterArray = new AtomicReferenceArray<AtomicReferenceArray<Segment<T>>>(
          outerArraySize);
      AtomicReferenceArray<Segment<T>> newInnerArray = new AtomicReferenceArray<Segment<T>>(MIDDLE_SIZE);
      Segment<T> newSegment = new Segment<T>(SEGMENT_SIZE);
      if (newSize % MIDDLE_SIZE == 0) {
        for (int i = 0; i < outerArraySize; i++) {
          // System.out.println("Copy " + i + " outer array");
          newOuterArray.set(i, outerArray.get(i));
        }
      } else {
        if (newSize % SEGMENT_SIZE == 0) {
          for (int i = 0; i < newSize / SEGMENT_SIZE; i++) {
            // System.out.println("Copy " + i + " inner array");
            newInnerArray.set(i, outerArray.get(0).get(i));
          }
          newOuterArray.set(0, newInnerArray);
        } else {
          for (int i = 0; i < newSize; i++) {
            // System.out.println("Copy " + i + " segment position");
            newSegment.segment.set(i, outerArray.get(0).get(0).segment.get(i));
          }
          newInnerArray.set(0, newSegment);
          newOuterArray.set(0, newInnerArray);
        }
      }

      boolean isNewTableSet = this.currentTable.compareAndSet(outerArray, newOuterArray, originalSize[0], newSize);
      boolean isOldTableSet = false;

      if (isNewTableSet) {
        isOldTableSet = this.oldTable.compareAndSet(oldTable, outerArray, oldTableSize[0], originalSize[0]);
      }

      if (isOldTableSet) {
        this.oldTableCounter.set(originalSize[0] - 1);
      }

      return isNewTableSet;
    }
    return false;
  }

  /**
   * Return the number of buckets (size of table)
   *
   * @return int number of buckets
   */
  public int numBuckets() {
    return this.currentTable.getStamp();
  }

  /**
   * Gets the outer array index for the bucket number
   *
   * @param bucket number
   * @return outer array index
   */
  private int getOuterIndex(int bucket) {
    return bucket / (MIDDLE_SIZE * SEGMENT_SIZE);
  }

  /**
   * Gets the inner array index for the bucket number
   *
   * @param bucket
   * @param outerIndex
   * @return inner array index
   */
  private int getInnerIndex(int bucket, int outerIndex) {
    // System.out.println("" + bucket + " " + outerIndex);
    return (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) / SEGMENT_SIZE;
  }

  /**
   * Gets the segment index for the bucket number
   *
   * @param bucket
   * @param outerIndex
   * @return segment index
   */
  private int getSegmentIndex(int bucket, int outerIndex) {
    return (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) % SEGMENT_SIZE;
  }

  private Node<T> getDummy(int bucket) {
    int outerIndex = getOuterIndex(bucket);
    int innerIndex = getInnerIndex(bucket, outerIndex);
    int segmentIndex = getSegmentIndex(bucket, outerIndex);

    Node<T> dummy = null;
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = this.oldTable.getReference();
    if (outerArray != null) {
      AtomicReferenceArray<Segment<T>> innerArray = outerArray.get(outerIndex);
      if (innerArray != null) {
        Segment<T> segment = innerArray.get(innerIndex);
        if (segment != null) {
          dummy = segment.segment.get(segmentIndex);
        }
      }
    }
    if (dummy != null) {
      return dummy;
    }
    return null;
  }

  /**
   * Returns a dummy that is eligible for deletion. Decrements the oldTableCounter
   * so that every operation returns a different dummy, and sets the counter to -1
   * when there are no more dummies to remove.
   *
   * @return Node<T> dummy to remove
   */
  public Node<T> getUselessDummy() {
    if (this.oldTableCounter.get() != -1) {
      int key = this.oldTableCounter.getAndDecrement();
      if (key >= this.currentTable.getStamp()) {
        return getDummy(key);
      } else {
        this.oldTableCounter.set(-1);
        return null;
      }
    }
    return null;
  }

  /**
   * Get the bucket number of the parent from the old table. This may not be the
   * same as on the new table since the size is different.
   *
   * @param myBucket bucket to get parent for
   * @return int parent bucket number
   */
  public int getOldParent(int myBucket) {
    int parent = this.oldTable.getStamp();
    do {
      parent = parent >> 1;
    } while (parent > myBucket);
    parent = myBucket - parent;
    return parent;
  }

  /**
   * toString for SegmentTable.
   *
   * @return String string representation of table
   */
  public String toString() {
    String s = "======================================================\nBUCKETS: \n";
    AtomicReferenceArray<AtomicReferenceArray<Segment<T>>> outerArray = this.currentTable.getReference();
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
              Node<T> node = outerArray.get(i).get(j).segment.get(k);
              if (node != null) {
                s = s.concat("\t\tsegment position : " + k + " " + node.toString() + " \n");
              }
            }
          }
        }
      }
    }
    return s;
  }
}
