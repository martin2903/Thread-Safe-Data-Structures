package thread_safe_queue;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadSafeQueue<T> {
    private Queue<T> queue;
    private boolean isTerminated;
    private boolean isEmpty;
    private int capacity;
    public ThreadSafeQueue(int capacity) {
        this.queue = new LinkedList<T>();
        this.isTerminated = false;
        this.isEmpty = true;
        this.capacity = capacity;
    }

    /** Adds to the queue. Back pressure is applied ensuring that the queue size does not grow exponentially.
     * When capacity is reached, the producer adding to the queue, waits for the consumer to remove items before
     * continuing execution.*/
    public synchronized void add(T val){

        while(queue.size() == capacity){
            try{
                wait();
            }catch(InterruptedException ex){

            }
        }
        queue.add(val);
        isEmpty = false;
        notify(); // Notify if a consumer thread is waiting for work on this Queue object
    }

    public synchronized T remove(){

        while(isEmpty && !isTerminated) {
            try{
                wait();
            }catch(InterruptedException ex){
            }
        }

        if(queue.size() == 1)
            isEmpty = true;

        if(queue.size() == 0 && isTerminated)
            return null;

        // If after removing an item from the queue, queue size is 1 below its capacity, wake up producers.
        if(queue.size() - 1 == capacity - 1)
            notifyAll();

        System.out.println("queue size:" + queue.size());

        return queue.remove();
    }

    /**Signals that the producer thread is done.*/
    public synchronized void terminated(){
        isTerminated = true;
        notifyAll(); //notify all waiting consumers and wake them all up.
    }
}
