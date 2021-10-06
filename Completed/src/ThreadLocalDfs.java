import java.util.*;
/**
 * When we need to make sure that every thread has its own local data structures  - synchronization
 * is not the solution.
 * TLS- Thread Local Storage
 */
public class ThreadLocalDfs<T>
{
    protected final ThreadLocal<Stack<Node<T>>> threadLocalStack =
            ThreadLocal.withInitial(()->new Stack<>());
    protected final ThreadLocal<Set<Node<T>>> threadLocalSet =
            ThreadLocal.withInitial(LinkedHashSet::new);
    /*
       push origin to the Stack V
       while stack is not empty: V
           removed = pop operation V
           insert to finished V
           invoke getReachableNodes method on removed node V
           for each reachableNode: V
               if current reachableNode is NOT in stack (just discovered)
               &&  current reachableNode is NOT in finished
               push to stack
    */
    public HashSet<T> traverseWithCross(Traversable<T> someGraph){

        threadLocalStack.get().push(someGraph.getOrigin());

        while(!threadLocalStack.get().isEmpty()){
            Node<T> poppedNode = threadLocalStack.get().pop();
            threadLocalSet.get().add(poppedNode);

            Collection<Node<T>> reachableNodes = someGraph.getReachableNodesWithCross(poppedNode);
            for (Node<T> singleReachableNode: reachableNodes){
                if (!threadLocalSet.get().contains(singleReachableNode) &&
                        !threadLocalStack.get().contains(singleReachableNode))
                {
                    threadLocalStack.get().push(singleReachableNode);
                }
            }
        }
        HashSet<T> connectedComponent = new LinkedHashSet<>();
        for (Node<T> node: threadLocalSet.get()) connectedComponent.add(node.getData());


        return connectedComponent;
    }

    /*
    This function receive a starting point and then finds all Reachable Nodes With Cross
    and adds them to local thread set.
    This function repeat itself until no more reachable nodes are found.
    */

}