import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class SegmentTable {
  // Declare constants for array sizes
  public static final int OUTER_SIZE = 2048;
  public static final int MIDDLE_SIZE = 2048;
  public static final int SEGMENT_SIZE = 4;
  public AtomicInteger size;
  // Segment table will be a fragmented array
  public AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray;

  /**
   * Create a Segment Table.
   *
   */
  public SegmentTable() {
    // Upon creation of segment table make segment 0 active for dummy node 0
    this.outerArray = new AtomicReferenceArray<>(OUTER_SIZE);
    outerArray.set(0, new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
    outerArray.get(0).set(0, new Segment(SEGMENT_SIZE));
    size = new AtomicInteger(1);
  }

  /**
   * Finds the position (bucket) in the segment table and returns the node.
   *
   * @param bucket The value of the dummy node we want to get.
   * @return the dummy node we are looking for or null if it doesn't exist.
   */
  public Node get(int bucket) {
    // Divide by middle array size times segment size to get outer array position
    AtomicReferenceArray<Segment> innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
    if (innerArray == null) {
      return null;
    }
    // Divide by segment size to get current segment
    Segment seg = innerArray.get(bucket / SEGMENT_SIZE);
    if (seg == null) {
      return null;
    }
    // Bucket % segment size gives the proper position in the segment where the
    // dummy node exists
    Node node = seg.segment.get(bucket % SEGMENT_SIZE);
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
  public void set(int bucket, Node dummyNode) {
    // Divide by middle array size times segment size to get outer array position
    AtomicReferenceArray<Segment> innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
    // If inner array is null create new array of segments
    if (innerArray == null) {
      outerArray.compareAndSet(bucket / (MIDDLE_SIZE * SEGMENT_SIZE), null,
          new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
      innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
    }
    // Divide by segment size to get current segment
    Segment seg = innerArray.get(bucket / SEGMENT_SIZE);
    // If current segment is null create a new segment
    if (seg == null) {
      innerArray.compareAndSet(bucket / SEGMENT_SIZE, null, new Segment(SEGMENT_SIZE));
      seg = innerArray.get(bucket / SEGMENT_SIZE);
    }
    // Bucket % segment size gives the proper position in the segment
    // where the dummy node should be inserted
    if (seg.segment.compareAndSet(bucket % SEGMENT_SIZE, null, dummyNode)) {
      size.incrementAndGet();
    }
    return;
  }

  public int getNumBuckets() {
    return this.size.intValue();
  }
}