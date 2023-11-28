import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ItemsQueue {
    private final Queue<Integer> queue;
    private final int capacity;
    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    public ItemsQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    //called by producer
    public void add(Integer item) throws InterruptedException {
        lock.lock();

        //producer waits while queue is full
        while(queue.size() == capacity)
            notFull.await();

        //producer can proceed to add item to queue
        queue.add(item);

        //consumer is notified that queue is not empty
        notEmpty.signal();

        lock.unlock();
    }

    //called by consumer
    public Integer remove() throws InterruptedException {
        lock.lock();

        //consumer waits while queue is empty
        while(queue.size() == 0)
            notEmpty.await();

        //consumer can proceed to remove item from queue
        Integer item = queue.remove();

        //producer is notified that queue is not full
        notFull.signal();

        lock.unlock();

        return item;
    }
}
