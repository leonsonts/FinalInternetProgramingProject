import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements adapter/wrapper/decorator design pattern
 */
public class TraversableMatrix implements Traversable<Index> {
    protected final Matrix matrix;
    private Index startIndex;
    private Index endIndex;
    public TraversableMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Index getStartIndex() {
        if (this.startIndex == null) throw new NullPointerException("start index is not initialized");
        return startIndex;
    }

    public Index getEndIndex() {
        if (this.endIndex == null) throw new NullPointerException("End index is not initialized");
        return endIndex;
    }

    public void setEndIndex(Index endIndex) {
        this.endIndex = endIndex;
    }

    public void setStartIndex(Index startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public Node<Index> getEnd() throws NullPointerException{
        if (this.endIndex == null) throw new NullPointerException("End index is not initialized");
        return new Node<>(this.endIndex);

    }


    @Override
    public Node<Index> getOrigin() throws NullPointerException{
        if (this.startIndex == null) throw new NullPointerException("start index is not initialized");
        return new Node<>(this.startIndex);

    }
    @Override

    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighbors(someNode.getData())) {
            if (matrix.getValue(index) == 1)
            {
                Node<Index> indexNode = new Node<>(index, someNode);
                reachableIndex.add(indexNode);
            }
        }
        return reachableIndex;
    }

    public Collection<Node<Index>> getReachableNodesWithCross(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighborsWithCross(someNode.getData())) {
            if (matrix.getValue(index) == 1) {
                Node<Index> indexNode = new Node<>(index, someNode);
                reachableIndex.add(indexNode);
            }
        }
        return reachableIndex;
    }
    public Collection<Node<Index>> getneighbores(Node<Index> someNode)
    {
        // Neighbors param is recently added
        List<Node<Index>> reachableIndex = new ArrayList<>();
        Collection<Index> Neighbors = new ArrayList<>();
        Neighbors = this.matrix.getNeighbors(someNode.getData());
        Neighbors.addAll(this.matrix.getNeighborsWithCross(someNode.getData()));
        for (Index index : Neighbors) {
            Node<Index> indexNode = new Node<>(index, someNode);
            reachableIndex.add(indexNode);

        }
        return reachableIndex;


/*
        List<Node<Index>> reachableIndex = new ArrayList<>();
         matrix.getNeighborsWithCross(someNode.getData()).stream().filter(i->matrix.getValue(i)==1).
                 map(indexNode->reachableIndex.add(indexNode)).collect(Collectors.toList());
        return reachableIndex;
*/
    }


    @Override
    public String toString() {
        return matrix.toString();
    }
}