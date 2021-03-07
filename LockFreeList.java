import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList {
    Node head;

    public LockFreeList(Node head) 
    {
        if(head == null)
        {
            head = new Node(Integer.MIN_VALUE, new AtomicMarkableReference<Node>(null, false));
        }
    }

    public boolean find(int keyToFind)
    {
        Node prev = this.head;
        boolean markHolder[] = {false};
        while(true)
        {
            Node current = prev.next.get(markHolder);
            // Condition 1: We reach the end of the list without finding key
            if(current == null)
            {
                return false;
            }
            Node next = current.next.get(markHolder);
            boolean currentMark = markHolder[0];
            int currentKey = current.key;
            // Condition 2: The previous node is marked or is no longer the previous node
            if(prev.next.get(markHolder) != current || markHolder[0] == true)
            {
                // Start over
                find(keyToFind);
            }
            // The current node is not marked (not deleted)
            if(!currentMark)
            {
                if(currentKey >= keyToFind)
                {
                    return currentKey == keyToFind;
                }
                else
                {
                    prev = current.next.get(markHolder);
                }
            }
            else
            {
                // The current node has been marked for deletion!
                if(prev.next.compareAndSet(current, next, false, false))
                {
                    // Node will be garbage collected by Java runtime
                    deleteNode(current);
                }
                else
                {
                    // Deletion failed; try again
                    find(keyToFind);
                }
            }
            prev = current;
        }
    }

    private void deleteNode(Node node)
    {
        node.key = 0;
        node.next = null;
    }
}
