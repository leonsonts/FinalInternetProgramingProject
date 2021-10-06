import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ThreadLocalBFS<T> {

    protected final ThreadLocal<Queue<List<Node<T>>>> WorkingQueue =ThreadLocal
            .withInitial(LinkedList::new);
    protected final ThreadLocal<LinkedHashSet<Node<T>>> finished = ThreadLocal
            .withInitial(LinkedHashSet::new);

    /*
    Traverse receive a path of graph which hold the start index and end index
    this is classic bfs algorithm, for each new path that we found the path list add to the working queue which is queue of lists
    and check it's neighbors too.
    * */

    public List<List<Node<T>>> traverse(Traversable<T> someGraph) {
        System.out.println(Thread.currentThread().getId());
        List<Node<T>> path = new ArrayList<>();
        List<List<Node<T>>> finalpath = new ArrayList<>();
        path.add(someGraph.getOrigin());
        WorkingQueue.get().add(path);
        while (!WorkingQueue.get().isEmpty())//work until no more new paths found
        {
            path = WorkingQueue.get().poll();
            Node<T> lastVis = path.get(path.size() - 1);//take the last node in the list that we found
            if (path.contains(someGraph.getEnd())) //if the path conatins the ending node, add the path to the list
            {
                finalpath.add(path);
            }
            Collection<Node<T>> reachableNodes = someGraph.getReachableNodes(lastVis);
            reachableNodes.addAll(someGraph.getReachableNodesWithCross(lastVis)); // Added
            finished.get().add(lastVis);
            for (Node<T> singleReachableNode : reachableNodes) {
                if (!finished.get().contains(singleReachableNode)) {
                    List<Node<T>> newpath = new ArrayList<>(path);
                    newpath.add(singleReachableNode);
                    WorkingQueue.get().add(newpath);
                }

            }
        }
        return finalpath;
    }

}
