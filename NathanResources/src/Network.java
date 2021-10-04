
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Network {
    private Collection<Node> members;
    /**
     * ReadLock - several threads can acquire this lock - no other
     * thread can acquire the lock. ReadLock.lock()
     * write lock
     * if a thread already acquired the write lock - reading threads will wait until writing threads
     * releases lock
     *
     * WriteLock
     * If any other thread is either reading or writing, the thread
     * that tries to acquire this lock will wait
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * constructor only instantiates members Collection
     * nodes will be added using the addNodes function
     * initialization using setNodes
     */

    public Network(){
//        this.members = new LinkedHashSet<>();
        this.members = new LinkedHashSet<>();
    }

    // return number of members in our network
    public int getSizeOfNetwork(){
        try{
            readWriteLock.readLock().lock();
            return members.size();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    // checks if the given node is a present in our network
    public boolean containsNode(final Node node) {
//        boolean exists = false;
//        readWriteLock.readLock().lock();
//        exists = members.contains(node);
//        readWriteLock.readLock().unlock();
//        return exists;
        try {
            readWriteLock.readLock().lock();
            return members.contains(node);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * @param nodes - overwrite current network nodes and returns to the network itself
     * @return
     */
    public Network setNodes(Collection<Node> nodes) {
        readWriteLock.writeLock().lock();
        if(nodes!=members){
            this.members.clear();
            this.members.addAll(nodes);
        }
        readWriteLock.writeLock().unlock();
        return this;



    }

    public Network addNodes(Collection<Node> nodes) {
        readWriteLock.writeLock().lock();
        members.addAll(nodes);
        readWriteLock.writeLock().unlock();
        return this;
    }
}