import java.util.concurrent.atomic.AtomicReferenceArray;

public class Segment<T> {
  AtomicReferenceArray<Node<T>> segment;

  // A segment is simply an atomic array of node references
  public Segment(int size) {
    this.segment = new AtomicReferenceArray<Node<T>>(size);
  }
}
