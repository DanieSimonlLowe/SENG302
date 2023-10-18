package nz.ac.canterbury.seng302.tab.entity;

public class Pair <T,O> {
    private T left;

    private O right;

    public Pair(T left, O right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public O getRight() {
        return right;
    }
}
