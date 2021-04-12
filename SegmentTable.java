import java.util.concurrent.atomic.*;

public class SegmentTable {
    // Declare constants for array sizes
    public static final int OUTER_SIZE = 2048;
    public static final int MIDDLE_SIZE = 2048;
    public static final int SEGMENT_SIZE = 4;
    public AtomicInteger size;
    // Segment table will be a fragmented array
    public AtomicReferenceArray<AtomicReferenceArray<Segment>> outerArray;

    public SegmentTable()
    {
        // Upon creation of segment table make segment 0 active for dummy node 0
        this.outerArray = new AtomicReferenceArray<>(OUTER_SIZE);
        outerArray.set(0, new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
        outerArray.get(0).set(0, new Segment());
        size = new AtomicInteger(1);
    }

    public Node get(int bucket)
    {
        // Divide by middle array size times segment size to get outer array position
        AtomicReferenceArray<Segment> innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
        if (innerArray == null)
            return null;
        // Divide by segment size to get current segment
        Segment seg = innerArray.get(bucket / SEGMENT_SIZE);
        if (seg == null)
            return null;
        // Bucket % segment size gives the proper position in the segment where the dummy node exists
        Node node = seg.segment.get(bucket % SEGMENT_SIZE);
        if (node == null)
            return null;
        return node;
    }

    public void set(int bucket, Node dummyNode)
    {
        // Divide by middle array size times segment size to get outer array position
        AtomicReferenceArray<Segment> innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
        // If inner array is null create new array of segments
        if (innerArray == null)
        {
            outerArray.set(bucket / (MIDDLE_SIZE * SEGMENT_SIZE), new AtomicReferenceArray<Segment>(MIDDLE_SIZE));
            innerArray = outerArray.get(bucket / (MIDDLE_SIZE * SEGMENT_SIZE));
        }
        // Divide by segment size to get current segment
        Segment seg = innerArray.get(bucket / SEGMENT_SIZE);
        // If current segment is null create a new segment
        if (seg == null)
        {
            innerArray.set(bucket / SEGMENT_SIZE, new Segment());
            seg = innerArray.get(bucket / SEGMENT_SIZE);
        }
        // Bucket % segment size gives the proper position in the segment where the dummy node should be inserted
        seg.segment.set(bucket % SEGMENT_SIZE, dummyNode);
        size.incrementAndGet();
        return;
    }

    public int getNumBuckets()
    {
        return this.size.intValue();
    }
}