import java.util.*;

/*This class return all possible paths from source index to destination index.
this class uses bfs algorithm to find all paths;
*/
public class BFSvisit<T>
{

    private Queue<List<Node<T>>> WorkingQueue; // queue for discovered nodes
    private LinkedHashSet<Node<T>>  finished;       // set for finished node

    public BFSvisit() {
        WorkingQueue = new LinkedList<>();
        finished = new LinkedHashSet<>();
    }
    /*Traverse receive a path of graph which hold the start index and end index
    this is classic bfs algorithm, for each new path that we found the path list add to the working queue which is queue of lists
    and check it's neighbors too.


    * */

    public List<List<Node<T>>> traverse(Traversable<T> partOfGraph)
    {
        List<Node<T>> path = new ArrayList<>();
        List<List<Node<T>>> finalpath = new ArrayList<>();
        path.add(partOfGraph.getOrigin());
        WorkingQueue.add(path);
        while(!WorkingQueue.isEmpty())//work until no more new paths found
        {
            path = WorkingQueue.poll();
            Node<T> lastVis = path.get(path.size() - 1);//take the last node in the list that we found
            if(path.contains(partOfGraph.getEnd())) //if the path conatins the ending node, add the path to the list
            {
                finalpath.add(path);
            }
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(lastVis);
            reachableNodes.addAll(partOfGraph.getReachableNodesWithCross(lastVis)); // Added
            finished.add(lastVis);
            for (Node<T> singleReachableNode : reachableNodes)
            {
                if (!finished.contains(singleReachableNode))
                {
                    List<Node<T>> newpath = new ArrayList<>(path);
                    newpath.add(singleReachableNode);
                    WorkingQueue.add(newpath);
                }

            }
        }
        return finalpath;
    }
}