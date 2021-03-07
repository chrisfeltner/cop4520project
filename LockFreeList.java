import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList {
    Node head;
    AtomicInteger itemCount;

    public LockFreeList(Node head, int itemCount) 
    {
        if(head == null)
        {
            this.head = new Node(Integer.MIN_VALUE, new AtomicMarkableReference<Node>(null, false));
            this.itemCount = new AtomicInteger(0);
        }
        else
        {
            this.head = head;
            this.itemCount = new AtomicInteger(itemCount);
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

    public boolean insert(Node nodeToInsert)
    {
        int key = nodeToInsert.key;
        // If the key is already in the set, there is no insertion needed
        if(find(key))
        {
            return false;
        }
        // Set next reference for node
        Node nextInList = this.head.next.getReference();
        nodeToInsert.next = new AtomicMarkableReference<>(nextInList, false);
        if(head.next.compareAndSet(nextInList, nodeToInsert, false, false))
        {
            // The head's next reference still points to nodeToInsert and head has not
            // been marked for deletion. If the CAS operation returns true, head's next
            // reference points to the inserted node
            return true;
        }
        else
        {
            // Try again
            return insert(nodeToInsert);
        }
    }

    private void deleteNode(Node node)
    {
        node.key = 0;
        node.next = null;
    }
}
