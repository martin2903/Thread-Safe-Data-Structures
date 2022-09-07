package lock_free_stack;
import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadSafeLockFreeStack<T> {
    private AtomicReference<StackNode<T>> top;
    private int size;

    public ThreadSafeLockFreeStack(){
        this.top = new AtomicReference<>();
    }
    /**
      First we get the current top reference from the atomic reference. Then we point the current atomic top to the new top.
      It will only change if top.get() value is equal to the expected value for that reference at the time of calling compareAndSet (i.e, no other thread changed it),
      it will set it to newTop. Otherwise, it will repeat the process to ensure there are no race conditions on the reference. I.e, we want to make sure
      that between reading from the head on line 26 and changing it on line 28, its value didn't change.
     */

    public void push(T value){
        // The candidate new top node.
        StackNode<T> newTop = new StackNode<>(value);
        ++size;

        while(true){
            StackNode<T> currentTop = top.get();
            newTop.next = currentTop;
            if(top.compareAndSet(currentTop, newTop))
                break;
            else{
                try{
                    Thread.sleep(1);
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }

    }

    /**
     * The current top node is removed only if it had not been updated in the period between reading its value and trying to update it.
     */
    public T pop(){
        StackNode<T> currentTop = top.get();
        StackNode<T> nextTop;
        // As with push(), we are ensuring that the top did not change by the time we are setting it to the new val.
        while(currentTop != null){
            nextTop = currentTop.next;
            if(top.compareAndSet(currentTop, nextTop)){
                --size;
                break;
            }
            else {
                try{
                    Thread.sleep(1);
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                currentTop = top.get(); //Get the changed top if compareAndSet failed
            }
        }
        return currentTop != null ? currentTop.val : null;
    }

    public int size(){
        return size;
    }

    public T peek(){
        return top.get().val;
    }
}
