package lock_free_stack;
public class StackNode<T> {
    T val;
    StackNode<T> next;

    public  StackNode(T val) {
        this.val = val;
    }

    public StackNode(){}


}
