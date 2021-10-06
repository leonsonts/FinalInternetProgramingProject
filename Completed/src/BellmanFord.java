import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;
/*
 * This class uses classic BellmanFord algorithm to find all shortest paths from source to destination
 * on a weighted matrix with  possible negative weights
 *First the function runs find path to initialize the distance matrix from source to destination
 * then it calls find Path function that runs on the distance matrix to find all the shortest paths
 *
 * */
public class BellmanFord {

    private Matrix matrix;
    private ReadWriteLock readWriteLock;
    private ThreadPoolExecutor threadPool;
    private  Index source;
    private TraversableMatrix traversable;
    private Queue<Index> indexQueue;
    private int[][] dist;
    private Future<List<Node>> futureList;
    private List<List<Node>> allShortestPath;
    private Node<Index> localnode;
    private Set<Index> finished;


    protected ThreadLocal<Set<Node>> localPathFind =
            ThreadLocal.withInitial(() -> new HashSet<>());

    public BellmanFord(Matrix matrix,Index source, Index destination)
    {
        this.matrix = matrix;
        //this.threadPool = null;
        this.traversable = new TraversableMatrix(this.matrix);
        this.traversable.setStartIndex(new Index(source.getRow(),source.getColumn())); // phase 3
        this.traversable.setEndIndex(new Index(destination.getRow(),destination.getColumn()));
        indexQueue = new LinkedList<>();
        dist = new int[matrix.lengthRow()][matrix.lengthCol()];
        readWriteLock = new ReentrantReadWriteLock();
        allShortestPath = new LinkedList<>();
        localnode = new Node<>(traversable.getEndIndex());
        finished = new HashSet<>();
        threadPool = new ThreadPoolExecutor(50, 100, 20,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }
    /*
     * This callable gets a distance matrix
     * it runs from the end to the start. this function works as a greedy algorithm.
     * for each node it checks its neighbors and finds the min neighbors
     *if there is two or more neighbors that are equal to min, the callable creates a new thread that find the other path.
     * when the last element in the list equal to the start index, the callable return a list which is a path from start to finish.
     * */
    Callable  <List<Node>>  task = () ->
    {
        List<Node> pathList = new LinkedList<>();
        /*  Node<Index> node = localnode;*/
        pathList.add(localnode);
        Queue<List<Node>> queueList = new LinkedList<>();
        queueList.add(pathList);
        while (!queueList.isEmpty())
        {
            Collection<Node<Index>> neighbor = new ArrayList<>();
            pathList = queueList.poll();
            int last = pathList.size()-1;
            Node node = pathList.get(last);
            localPathFind.get().add(node);
            neighbor = traversable.getNeighbors(node);

            int min = Integer.MAX_VALUE;
            if (pathList.get((pathList.size() - 1)).getData().equals(traversable.getStartIndex()))
            {

                return pathList;
            }
            for (Node neighborNode : neighbor)
            {
                if (!localPathFind.get().contains(neighborNode))
                {
                    Index index1 = (Index) neighborNode.getData();
                    if (dist[index1.getRow()][index1.getColumn()] <= min) {
                        min = dist[index1.getRow()][index1.getColumn()];
                    }
                }
            }
            int counter = 0;
            for (Node neighborNode : neighbor)
            {
                Index index = (Index) neighborNode.getData();

                if (dist[index.getRow()][index.getColumn()] == min && !localPathFind.get().contains(neighborNode))
                {

                    counter++;
                    if (counter > 1) {
                        localnode = neighborNode;
                        findShortestPaths();


                    } else {
                        pathList.add(neighborNode);
                        queueList.add(pathList);
                    }

                }
            }
        }
        return null;//can't reach here!
    };

    /*
     * Calls the callable for each new path available
     * after receiving the paths list, we run on each list to add the node's father until we reach the starting node
     * and it all to the list.
     * also we used write lock to insure only one thread at a time reach into the critical section.
     * */
    public void findShortestPaths() throws ExecutionException, InterruptedException {


        futureList = threadPool.submit(task);
        try {

            allShortestPath.add(futureList.get());

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } catch (ExecutionException executionException) {
            executionException.printStackTrace();
        }
        if (futureList.isDone()) {
            readWriteLock.writeLock().lock();
            for (List<Node> list : allShortestPath) {
                if (list.get(0).getParent() != null)
                {

                    Node node = list.get(0).getParent();
                    while (node != null) {
                        list.add(node);
                        Node node1 = node.getParent();
                        node = node1;
                    }
                }

            }
            for (List<Node> currentList: allShortestPath)
                Collections.reverse(currentList);
            readWriteLock.writeLock().unlock();
            System.out.println(allShortestPath);
        }

    }

    /*This function calculates the total lowest cost to go from source index to every index in the matrix
     *also detects negative weight cycle by checking if there is relaxation.
     * after calculates it calls the find path function to get all the shortest paths,
     * and returns it as list of lists
     * */
    public List<List<Node>> BellmanFordAlgo() throws ExecutionException, InterruptedException {

        for (int i = 0; i < matrix.lengthRow(); ++i)
            for(int j = 0; j < matrix.lengthCol();j++)
            {
                dist[i][j] = Integer.MAX_VALUE;
            }
        dist[traversable.getStartIndex().getRow()][traversable.getStartIndex().getColumn()] = matrix.getValue(traversable.getStartIndex());
        int matrixSize = matrix.lengthRow() * matrix.lengthCol();
        indexQueue.add(traversable.getStartIndex());
        while (!indexQueue.isEmpty())
        {

            Collection<Index> neighbor = new ArrayList<>();
            Index index = indexQueue.poll();
            finished.add(index);
            neighbor = matrix.getNeighbors(index);
            neighbor.addAll(matrix.getNeighborsWithCross(index)); // Added
            for(Index index1 : neighbor) {

                if (dist[index.getRow()][index.getColumn()] != Integer.MAX_VALUE &&
                        dist[index.getRow()][index.getColumn()] + matrix.getValue(index1) < dist[index1.getRow()][index1.getColumn()]) {

                    dist[index1.getRow()][index1.getColumn()] = matrix.getValue(index1) + dist[index.getRow()][index.getColumn()];


                }
                if (!indexQueue.contains(index1) && !finished.contains(index1))
                {
                    indexQueue.add(index1);
                }

            }
        }
        source = new Index(traversable.getEndIndex().getRow(),traversable.getEndIndex().getColumn());
        System.out.println(Arrays.deepToString(dist).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));

        findShortestPaths();
        threadPool.shutdown();
        return allShortestPath;
    }
}