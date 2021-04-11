import java.util.concurrent.atomic.*;

public class SegmentTable {
    private static final int OUTER_SIZE = 2048;
    private static final int MIDDLE_SIZE = 2048;
    private static final int SEGMENT_SIZE = 4;
    public AtomicInteger size;
    public AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray;

    public SegmentTable()
    {
        this.outerArray = new AtomicReferenceArray<>(OUTER_SIZE);
        outerArray.set(0, new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
        outerArray.get(0).set(0, new Segment());
        size = new AtomicInteger(1);
    }

    public Node get(int bucket)
    {
        AtomicReferenceArray<Segment> innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
        if (innerArray == null)
            return null;
        Segment seg = innerArray.get(bucket / SEGMENT_SIZE);
        if (seg == null)
            return null;
        Node node = seg.segment.get(bucket % SEGMENT_SIZE);
        if (node == null)
            return null;
        return node;
    }

    public void set(int bucket, Node dummyNode)
    {
        AtomicReferenceArray<Segment> innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
        if (innerArray == null)
        {
            outerArray.set(bucket / (MIDDLE_SIZE * SEGMENT_SIZE), new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
            innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
        }
        Segment seg = innerArray.get(bucket / SEGMENT_SIZE);
        if (seg == null)
        {
            innerArray.set(bucket / SEGMENT_SIZE, new Segment());
            seg = innerArray.get(bucket / SEGMENT_SIZE);
        }
        seg.segment.set(bucket % SEGMENT_SIZE, dummyNode);
        size.incrementAndGet();
        return;
    }

    public int getNumBuckets()
    {
        return this.size.intValue();
    }
}