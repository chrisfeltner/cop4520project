import java.util.concurrent.atomic.*;

public class Segment {
    private static final int SEGMENT_SIZE = 4;
    AtomicReferenceArray<Node> segment;

    public Segment()
    {
        this.segment = new AtomicReferenceArray<Node>(SEGMENT_SIZE);
    }
}
