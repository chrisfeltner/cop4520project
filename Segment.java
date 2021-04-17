import java.util.concurrent.atomic.AtomicReferenceArray;

public class Segment {
  AtomicReferenceArray<Node> segment;

  // A segment is simply an atomic array of node references
  public Segment(int size) {
    this.segment = new AtomicReferenceArray<Node>(size);
  }
}
