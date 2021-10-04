import java.util.List;

public class Demo {
    public static void main(String[] args) {
        int[][] myArray = {
                {1,1,1,1,1},
                {0,0,1,1,1},
                {1,1,1,0,0},
                {0,1,0,1,0},
                {0,1,1,1,0}
        };

        TraversableMatrix myMatrixGraph = new TraversableMatrix(new Matrix(myArray));
        System.out.println(myMatrixGraph);
        myMatrixGraph.setStartIndex(new Index(0,0));
        DfsVisit<Index> dfsVisit = new DfsVisit<>();
        List<Index> connectedComponent = dfsVisit.traverse(myMatrixGraph);
        System.out.println(connectedComponent);

    }

}
