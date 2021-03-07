import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
    public AtomicMarkableReference<Node> next;
    public int key;

    public Node(int key, AtomicMarkableReference<Node> next)
    {
        this.key = key;
        this.next = next;
    }
}
