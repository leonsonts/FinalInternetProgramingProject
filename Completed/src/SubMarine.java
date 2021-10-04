import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/*
 * The class received a matrix and returns the number of valid submarine in the matrix
 *
 *
 *
 * */
public class SubMarine {

    private Matrix matrix;
    private Connected connectedCopm;
    private ThreadPoolExecutor threadPool;
    private HashSet<Index> hashSet;
    private Index[]  IndexArray;
    private int [] height=null;
    private ReadWriteLock lock;

    public SubMarine(Matrix matrix) {
        this.matrix = matrix;
        connectedCopm = new Connected(this.matrix);
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
        Stack<Integer> r = new Stack<>();
        System.out.println(Arrays.toString(IndexArray));
        int counterC =0;
        for (int i = 0; i < IndexArray.length; i++)
        {
            if(r.contains(IndexArray[i].getColumn())==false)//finding diffrent cols and defining the size of the array
            {
                counterC++;
                r.add(IndexArray[i].getColumn());
            }
        }
        int [] height = new int[counterC];
        r.clear();
        int rowTemp=IndexArray[IndexArray.length-1].getRow();
        for (int i = 0; i < IndexArray.length; i++)
        {
            if(r.contains(IndexArray[i].getColumn())==false)
            {
                r.add(IndexArray[i].getColumn());

                for (int j = 0; j <= rowTemp; j++)
                {
                    Index check = new Index(j,IndexArray[i].getColumn());
                    if (hashSet.contains(check)==true)
                    {
                        height[check.getColumn()%counterC]+=1;
                    }
                    else height[check.getColumn()%counterC]=0;

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
    * for each component bigger then one, a new thread is created to check if it is a valid submarine
    *if it is, it adds one to the total count of how many submarine found
    *writelock is been locked before entering critical section to insure only one writes to the variable
    * */
    public int submarineFind() throws ExecutionException, InterruptedException {

        Callable<Integer> task = () -> {

            int max = 0;
            height = getHeights();
            max = Math.max(max, findMaxArea(height));
            if (max == hashSet.size()) {
                return 1;
            }
            return 0;
        };

        List<HashSet<Index>> comp = connectedCopm.ConnectedComponentsWithCross();
        Stack<HashSet<Index>> stack = null;

        int marinesFound = 0;
        int i = 0;

        for (i = 0; i < comp.size(); i++) {

            hashSet = comp.get(i);
            if (hashSet.size() > 1) {
                IndexArray = new Index[hashSet.size()];
                IndexArray = hashSet.toArray(new Index[hashSet.size()]);
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
        System.out.println("marines found " + marinesFound);
        return marinesFound;

    }
}