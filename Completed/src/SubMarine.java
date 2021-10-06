import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
 // The class receives a matrix and returns the number of valid submarine in the matrix
public class SubMarine {

    private Matrix matrix;
    private Connected connectedComp;
    private ThreadPoolExecutor threadPool;
    private HashSet<Index> currentSingleComp;
    private Index[]  IndexArray;
    private int [] height=null;
    private ReadWriteLock lock;

    public SubMarine(Matrix matrix) {
        this.matrix = matrix;
        connectedComp = new Connected(this.matrix);
        threadPool = new ThreadPoolExecutor(5, 10, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        lock=new ReentrantReadWriteLock();

    }

    //finding maxArea in each SCC
    /*
     * To find if the component is valid we calculated the area of the rectangle
     * for doing that we used a function that gets the sum of each column in the matrix
     *
     *then from the heights array we can calculate the max area in this component
     * and if its the same as its size its a valid rectangle
     * */
    public int[] getHeights()
    {
        Arrays.sort(IndexArray);//sorting the index array from the smallest to biggest
        Stack<Integer> stackOfCols = new Stack<>();
        System.out.println(Arrays.toString(IndexArray));
        int numOfDiffCols =0;
        for (int i = 0; i < IndexArray.length; i++)
        {
            if(!stackOfCols.contains(IndexArray[i].getColumn()))
                //finding different cols and defining the size of the array
            {
                numOfDiffCols++;
                stackOfCols.add(IndexArray[i].getColumn());
            }
        }
        int [] height = new int[numOfDiffCols];
        stackOfCols.clear();
        int maxRowInComp=IndexArray[IndexArray.length-1].getRow();
        for (int i = 0; i < IndexArray.length; i++)
        {
            if(!stackOfCols.contains(IndexArray[i].getColumn()))
            {
                stackOfCols.add(IndexArray[i].getColumn());

                for (int j = 0; j <= maxRowInComp; j++)
                {
                    Index check = new Index(j,IndexArray[i].getColumn());
                    if (currentSingleComp.contains(check)==true)
                    {
                        height[check.getColumn()%numOfDiffCols]+=1;
                    }
                    else height[check.getColumn()%numOfDiffCols]=0;

                }
            }
        }
        return height;
    }
    /*
     * here we receive a heights array
     * then we calculate for this array the max rectangle size
     * */
    public int findMaxArea(int []height)
    {
        int max=0;
        Stack<Integer> stack= new Stack<>();
        stack.add(0);
        for (int i = 1; i < height.length ; i++)
        {
            int curr = height[i];
            if(stack.isEmpty()|| curr>=height[stack.peek()]){
                stack.add(i);
            }
            else {
                while (!stack.isEmpty() && curr < height[stack.peek()])
                {
                    int temp=height[stack.pop()];
                    if(stack.isEmpty())
                    {
                        max=Math.max(max,temp*i);
                    }
                    else {
                        max=Math.max(max, temp*(i-stack.peek()-1));
                    }
                }
            }
            stack.add(i);
        }
        if(!stack.isEmpty())
        {
            while (!stack.isEmpty() )
            {
                int i=height.length;
                int temp=height[stack.pop()];
                if(stack.isEmpty())
                {
                    max=Math.max(max,temp*i);
                }
                else {
                    max=Math.max(max, temp*(i-stack.peek()-1));
                }
            }

        }
        return max;
    }

    /*The function finds how many valid submarine exists in the matrix
    first the function finds all the connected components
    * for each component bigger than one, a new thread is created to check if it is a valid submarine
    *if it is, it adds one to the total count of how many submarine found
    *writelock is been locked before entering critical section to ensure only one writes to the variable
    * */
    public int submarineFind() throws ExecutionException, InterruptedException {

        Callable<Integer> task = () -> {

            int max = 0;
            height = getHeights();
            max = Math.max(max, findMaxArea(height));
            if (max == currentSingleComp.size()) {
                return 1;
            }
            return 0;
        };

        List<HashSet<Index>> scc = connectedComp.ConnectedComponentsWithCross();
        Stack<HashSet<Index>> stack = null;

        int marinesFound = 0;
        int i = 0;

        for (i = 0; i < scc.size(); i++) {

            currentSingleComp = scc.get(i);
            if (currentSingleComp.size() > 1) {
                IndexArray = new Index[currentSingleComp.size()];
                IndexArray = currentSingleComp.toArray(new Index[currentSingleComp.size()]);
                Future<Integer> futureTask = threadPool.submit(task);
                lock.writeLock().lock();
                try {
                    marinesFound += futureTask.get();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException ee) {
                    ee.printStackTrace();
                }
                lock.writeLock().unlock();
            }
        }
        threadPool.shutdown();
        if (marinesFound < scc.size()){
            System.out.println("Invalid input");
            return -1;
        }
            System.out.println("marines found " + marinesFound);
            return marinesFound;


    }
}