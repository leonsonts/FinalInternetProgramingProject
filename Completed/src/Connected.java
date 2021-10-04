import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;
/*This class finds all the Connected Components in the matrix
The class uses Thread local Dfs which can be run on separated threads
to find each component;


* */

public class Connected {

    private Matrix matrix;
    private ThreadPoolExecutor threadPool;
    private Index source;
    private ThreadLocalDfsVisit<Index> threadLocalDfsVisit;
    private TraversableMatrix traversable;

    public Connected(Matrix matrix) {

        this.matrix = matrix;
        this.threadPool = null;
        this.source = new Index(0, 0);
        this.traversable = new TraversableMatrix(this.matrix);
        traversable.setStartIndex(new Index(0, 0));
        threadLocalDfsVisit = new ThreadLocalDfsVisit<>();
    }

    protected ThreadLocal<Set<Index>> setThreadLocal =
            ThreadLocal.withInitial(() -> new HashSet<>());
    /*
     * This function finds all the connected components which also connected with cross
     * the function runs on the matrix rows and cols, each time it finds a value of one
     * it creates a new thread which find all connected nodes to it.
     * the thread return a list, which adds to setThreadlocal to insure thread safe
     * at the end the function sorts by size from small to big.
     * */

    public List<HashSet<Index>> ConnectedComponentsWithCross() throws ExecutionException, InterruptedException {

        Callable <HashSet<Index>> task = () -> {

            HashSet<Index> visited = new HashSet<>();
            traversable.setStartIndex(source);
            visited = threadLocalDfsVisit.traverseWithCross(traversable);
            return visited;
        };
        threadPool = new ThreadPoolExecutor(5, 10, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        HashSet<Index> visited = new HashSet<>();
        List<HashSet<Index>> newVisited = new ArrayList<>();

        for (int i = 0; i < matrix.lengthRow(); i++) {
            for (int j = 0; j < matrix.lengthCol(); j++) {
                source = new Index(i, j);
                if (matrix.getValue(source) == 1 && !setThreadLocal.get().contains(source)) {
                    Future<HashSet<Index>> futureTask = threadPool.submit(task);
                    try {
                        visited=futureTask.get();
                        setThreadLocal.get().addAll(visited);
                        newVisited.add(visited);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (ExecutionException ee){ee.printStackTrace(); }
                }
            }
        }

        threadPool.shutdown();
        Collections.sort(newVisited, new Comparator<Set<?>>() {//sort the components from smallest to biggest size
            @Override
            public int compare(Set<?> o1, Set<?> o2) {
                return Integer.valueOf(o1.size()).compareTo(o2.size());
            }
        });

        return newVisited;
    }

}