import java.util.*;
/*
 * This class works with DFS and Thread Local to insure thread safety
 * The class find for each source index it's connected components and returns it as hashset*/
public class ThreadLocalDfsVisit<T>
{
    protected final ThreadLocal<Stack<Node<T>>> stackThreadLocal =
            ThreadLocal.withInitial(Stack::new);
    protected final ThreadLocal<Set<Node<T>>> setThreadLocal =
            ThreadLocal.withInitial(()->new LinkedHashSet<>());//LinkedHashSet is used to maintain order in the hash set

    protected void threadLocalPush(Node<T> node){
        stackThreadLocal.get().push(node);
    }


    protected Node<T> threadLocalPop(){
        return stackThreadLocal.get().pop();
    }
    /*This function receive a starting point and then finds all it's Reachable Nodes With Cross
    and adds them to set thread local list
    This function repeat itself until no more reachable nodes are found
    and returns one component as Hash Set list.

    * */
    public HashSet<T> traverseWithCross(Traversable<T> partOfGraph){

        threadLocalPush(partOfGraph.getOrigin());

        while(!stackThreadLocal.get().isEmpty()){
            Node<T> poppedNode = threadLocalPop();
            setThreadLocal.get().add(poppedNode);

            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodesWithCross(poppedNode);
            for (Node<T> singleReachableNode: reachableNodes){
                if (!setThreadLocal.get().contains(singleReachableNode) &&
                        !stackThreadLocal.get().contains(singleReachableNode))
                {
                    threadLocalPush(singleReachableNode);
                }
            }
        }
        HashSet<T> blackList = new LinkedHashSet<>();
        for (Node<T> node: setThreadLocal.get()){
            blackList.add(node.getData());
        }

        return blackList;
    }

}