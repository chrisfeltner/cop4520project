import java.util.concurrent.atomic.*;

public class Segment {
    private static final int SEGMENT_SIZE = 4;
    AtomicReferenceArray<Node> segment;
    // A segment is simply an atomic array of node references
    public Segment()
    {
        this.segment = new AtomicReferenceArray<Node>(SEGMENT_SIZE);
    }
}
