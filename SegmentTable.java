import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

public class SegmentTable {
  // Declare constants for array sizes
  public static final int MIDDLE_SIZE = 256;
  public static final int SEGMENT_SIZE = 32;
  // Segment table will be a fragmented array
  public AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment>>> currentTable;
  public AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment>>> oldTable;
  public AtomicInteger oldTableCounter;

  /**
   * Create a Segment Table.
   *
   */
  public SegmentTable() {
    // Upon creation of segment table make segment 0 active for dummy node 0
    AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray = new AtomicReferenceArray<>(1);
    outerArray.set(0, new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
    outerArray.get(0).set(0, new Segment(SEGMENT_SIZE));
    currentTable = new AtomicStampedReference<AtomicReferenceArray<AtomicReferenceArray<Segment>>>(outerArray, 1);
    oldTableCounter = new AtomicInteger(-1);
  }

  /**
   * Finds the position (bucket) in the segment table and returns the node.
   *
   * @param bucket The value of the dummy node we want to get.
   * @return the dummy node we are looking for or null if it doesn't exist.
   */
  public Node get(int bucket) {
    int[] stampHolder = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray = this.currentTable.get(stampHolder);
    // Check if the bucket is within the current size
    if (bucket > stampHolder[0]) {
      return null;
    }
    int outerIndex = bucket / (MIDDLE_SIZE * SEGMENT_SIZE);
    int innerIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) / SEGMENT_SIZE;
    int segmentIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) % SEGMENT_SIZE;

    AtomicReferenceArray<Segment> innerArray = outerArray.get(outerIndex);
    if (innerArray == null) {
      return null;
    }

    Segment seg = innerArray.get(innerIndex);
    if (seg == null) {
      return null;
    }

    Node node = seg.segment.get(segmentIndex);
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
  public boolean set(int bucket, Node dummyNode) {
    int[] stampHolder = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray = this.currentTable.get(stampHolder);
    // Check if the bucket is within the current size
    if (bucket > stampHolder[0]) {
      return false;
    }
    int outerIndex = bucket / (MIDDLE_SIZE * SEGMENT_SIZE);
    int innerIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) / SEGMENT_SIZE;
    int segmentIndex = (bucket - (outerIndex * MIDDLE_SIZE * SEGMENT_SIZE)) % SEGMENT_SIZE;

    AtomicReferenceArray<Segment> innerArray = outerArray.get(outerIndex);
    // If inner array is null create new array of segments
    if (innerArray == null) {
      outerArray.compareAndSet(outerIndex, null, new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
      innerArray = outerArray.get(outerIndex);
    }

    Segment seg = innerArray.get(innerIndex);
    // If current segment is null create a new segment
    if (seg == null) {
      innerArray.compareAndSet(innerIndex, null, new Segment(SEGMENT_SIZE));
      seg = innerArray.get(innerIndex);
    }

    // Get dummy node
    return seg.segment.compareAndSet(segmentIndex, null, dummyNode);
  }

  public boolean expand() {
    int[] originalSize = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray = this.currentTable.get(originalSize);
    int newSize = originalSize[0] * 2;
    int outerArraySize = (newSize / (MIDDLE_SIZE * SEGMENT_SIZE)) + 1;
    AtomicReferenceArray<AtomicReferenceArray<Segment>> newOuterArray = new AtomicReferenceArray<AtomicReferenceArray<Segment>>(
        outerArraySize);
    for (int i = 0; i <= originalSize[0] / (MIDDLE_SIZE * SEGMENT_SIZE); i++) {
      newOuterArray.set(i, outerArray.get(i));
    }
    return this.currentTable.compareAndSet(outerArray, newOuterArray, originalSize[0], newSize);
  }

  public boolean contract() {
    int[] originalSize = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray = this.currentTable.get(originalSize);

    int[] oldTableSize = { 0 };
    AtomicReferenceArray<AtomicReferenceArray<Segment>> oldTable = this.oldTable.get(oldTableSize);
    int newSize = originalSize[0] / 2;
    int outerArraySize = Math.max((newSize / (MIDDLE_SIZE * SEGMENT_SIZE)), 1);
    AtomicReferenceArray<AtomicReferenceArray<Segment>> newOuterArray = new AtomicReferenceArray<AtomicReferenceArray<Segment>>(
        outerArraySize);
    AtomicReferenceArray<Segment> newInnerArray = new AtomicReferenceArray<Segment>(MIDDLE_SIZE);
    Segment newSegment = new Segment(SEGMENT_SIZE);
    if (newSize % MIDDLE_SIZE == 0) {
      for (int i = 0; i < outerArraySize; i++) {
        newOuterArray.set(i, outerArray.get(i));
      }
    } else {
      if (newSize % SEGMENT_SIZE == 0) {
        for (int i = 0; i < newSize / SEGMENT_SIZE; i++) {
          newInnerArray.set(i, outerArray.get(0).get(i));
        }
        newOuterArray.set(0, newInnerArray);
      } else {
        for (int i = 0; i < newSize; i++) {
          newSegment.segment.set(i, outerArray.get(0).get(0).segment.get(i));
        }
        newInnerArray.set(0, newSegment);
        newOuterArray.set(0, newInnerArray);
      }
    }

    boolean oldTableSet = this.oldTable.compareAndSet(oldTable, outerArray, oldTableSize[0], originalSize[0]);

    boolean newTableSet = this.currentTable.compareAndSet(outerArray, newOuterArray, originalSize[0], newSize);

    if (oldTableSet) {
      this.oldTableCounter.set(0);
    }

    return newTableSet;
  }

  public int numBuckets() {
    return this.currentTable.getStamp();
  }

}