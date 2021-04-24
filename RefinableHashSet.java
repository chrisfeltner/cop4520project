import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReentrantLock;

public class RefinableHashSet<T> extends BaseHashSet<T>
{
    AtomicMarkableReference<Thread> owner;
    volatile ReentrantLock[] locks;
    String name;

    public RefinableHashSet(int capacity) {
        super(capacity);
        locks = new ReentrantLock[capacity];

        for(int i = 0; i < capacity; i++)
        {
            locks[i] = new ReentrantLock();
        }

        owner = new AtomicMarkableReference<Thread>(null, false);
        this.name = "ReFiHaSet_" + capacity;
    }

    @Override
    public void acquire(T x) {
        boolean [] mark  = {true};
        Thread me = Thread.currentThread();
        Thread who;

        while(true)
        {
            do{
                who = owner.get(mark);
            }
            while (mark[0] && who != me);

            ReentrantLock[] oldLocks = locks;
            ReentrantLock oldLock = oldLocks[x.hashCode() % oldLocks.length];
            oldLock.lock();
            who = owner.get(mark);

            if((!mark[0] || who == me) && locks == oldLocks)
            {
                return;
            }
            else {
                oldLock.unlock();
            }
        }
    }

    @Override
    public void release(T x)
    {
        locks[x.hashCode() % locks.length].unlock();
    }

    @Override
    public void resize() {
        int oldCapacity = table.length;
        boolean[] mark = {false};

        int newCapacity = 2 * oldCapacity;
        Thread me = Thread.currentThread();

        if(owner.compareAndSet(null, me, false, true)){
            try {
                if(table.length != oldCapacity)
                {
                    return;
                }

                quiesce();

                List<T>[] oldTable = table;
                table = (List<T>[]) new List[newCapacity];

                for(int i = 0; i < newCapacity; i++)
                {
                    table[i] = new ArrayList<T> ();
                }

                locks = new ReentrantLock[newCapacity];

                for(int j = 0; j < locks.length; j++)
                {
                    locks[j] = new ReentrantLock();
                }

                initializeFrom(oldTable);
            } finally {
                owner.set(null, false);
            }
        }
    }

    @Override
    public boolean policy() {
        return setSize / table.length > 4;
    }

    private void initializeFrom(List<T>[] oldTable) {
        for(List<T> bucket : oldTable) {
            for(T x : bucket) {
                int myBucket = Math.abs(x.hashCode() % table.length);
                table[myBucket].add(x);
            }
        }
    }

    protected void quiesce() {
        for(ReentrantLock lock : locks) {
            while(lock.isLocked())
            {

            }
        }
    }
}

abstract class BaseHashSet<T> {
    protected List<T>[] table;
    protected int setSize;

    public BaseHashSet(int capacity)
    {
        setSize = 0;
        table = (List<T>[]) new List[capacity];

        for(int i = 0; i < capacity; i++)
        {
            table[i] = new ArrayList<T>();
        }
    }
    public String printSet(){
        String string = "";
        for(int i = 0; i < table.length; i++)
        {
            if(i != 0)
            {
                string += "\n";
            }
            string += "ROW ";
            string += i;
            for(int j = 0; j < table[i].size(); j++)
            {
                if(j == 0)
                {
                    string += ": ";
                    string += table[i].get(j).toString();
                }
                else
                {
                    string += " -> ";
                    string += table[i].get(j).toString();
                }
            }
        }

        return string;
    }

    public boolean contains(T x) {
        acquire(x);

        try {
            int myBucket = Math.abs(x.hashCode() % table.length);
            return table[myBucket].contains(x);
        } finally {
            release(x);
        }
    }

    public boolean add (T x) {
        boolean result = false;
        acquire(x);

        try {
            int myBucket = Math.abs(x.hashCode() % table.length);

            if(!table[myBucket].contains(x))
            {
                table[myBucket].add(x);
                result = true;
                setSize++;
            }
        } finally {
            release(x);
        }

        if (policy())
        {
            resize();
        }

        return result;
    }

    public boolean remove(T x) {
        acquire(x);
        try {
            int myBucket = Math.abs(x.hashCode() % table.length);
            boolean result = table[myBucket].remove(x);
            setSize = result ? setSize - 1 : setSize;
            return result;
        } finally {
            release(x);
        }
    }





    public abstract void acquire(T x);

    public abstract void release(T x);

    public abstract void resize();

    public abstract boolean policy();
}
