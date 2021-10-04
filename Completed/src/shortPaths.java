import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
/*
 This class finds all the shortest paths from source index to destination index
 working only on binary matrix.
 the class receive a matrix, source index and destination index and initialize them in the constructor.
 using the findShortPaths function the class finds all the shortest paths from start to finish;
 */


public class shortPaths implements Serializable {

    private Matrix matrix;
    private TraversableMatrix traversable;

    public shortPaths(Matrix matrix,Index source, Index destination) {

        this.matrix = matrix;
        this.traversable = new TraversableMatrix(this.matrix);
        this.traversable.setStartIndex(new Index(source.getRow(),source.getColumn())); // phase 3
        this.traversable.setEndIndex(new Index(destination.getRow(),destination.getColumn()));

    }

    /*This function finds the shortest paths from source to destination using bfs
     *The function calls bfs.traverse that returns all the paths from source to destination
     *then the function finds the minimum size of the all Paths List
     *and runs again to find which list is the same size of minimum list and adds to the final list which holds only the shortest paths'
     *
     * */
    public List<List<Node<Index>>> findShortestPaths()  throws ExecutionException, InterruptedException {

        BFSvisit<Index> BFS = new BFSvisit<>();
        List<List<Node<Index>>> allPathsList = new ArrayList<>();
        List<List<Node<Index>>> shortestPathsList = new ArrayList<>();
        Integer min = null;
        allPathsList = BFS.traverse(traversable);//finds all the paths from source to destination
        if(allPathsList.size() == 0)
        {
            return null;
        }
        min = allPathsList.get(0).size();
        min = findMin(min,allPathsList);

        for(List<Node<Index>> list : allPathsList)//find which list is the same size of min and adds it to the Shortest Path List
        {

            if(min == list.size())
            {
                if (!shortestPathsList.contains(list))
                shortestPathsList.add(list);
            }
        }
        System.out.println(shortestPathsList);
        return shortestPathsList;

    }

    /*Finds the min size list in the all paths list*/
    public int findMin(int min, List<List<Node<Index>>> allPathsList )
    {  for(List<Node<Index>> list : allPathsList)
    {
        if(min > list.size())
        {
            min = list.size();
        }

    }

        return min;
    }
}
