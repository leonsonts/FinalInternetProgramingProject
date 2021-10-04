import java.util.Collection;

public interface Traversable<T> {
    public Node<T> getOrigin();
    public Collection<Node<T>> getReachableNodes(Node<T> someNode);
    public Node<T> getEnd();

    public T getStartIndex();
    public Collection<Node<T>> getReachableNodesWithCross(Node<T> poppedNode);
}