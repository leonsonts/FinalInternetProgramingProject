import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MatrixIHandler implements IHandler, Serializable {
    private Matrix matrix;
    private Index start,end;
    //Reset params between clients - possible shared instance among clients/tasks.
    private void resetParams(){
        this.matrix = null;
        this.start = null;
        this.end = null;
    }

    @Override
    public void handle(InputStream fromClient, OutputStream toClient)
            throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);
        this.resetParams();
        // in order to use same handler between tasks/clients
        // Use switch-case in order to get command from the client
        boolean doWork = true;
        while(doWork){

            switch(objectInputStream.readObject().toString()) {

                case "random matrix":
                {
                    int row = (int) objectInputStream.readObject();
                    int col = (int) objectInputStream.readObject();
                    this.matrix = new Matrix(row,col);
                    this.matrix.printMatrix();
                    objectOutputStream.writeObject(matrix);
                    break;

                }
                case "weighted matrix":
                {
                    int row = (int) objectInputStream.readObject();
                    int col = (int) objectInputStream.readObject();
                    int bound= (int) objectInputStream.readObject();

                    this.matrix = new Matrix(row,col,bound);
                    this.matrix.printMatrix();
                    objectOutputStream.writeObject(matrix);
                    break;

                }

                case "Connected":
                {
                    try {
                        Connected connected = new Connected(matrix);
                        List<HashSet<Index>> lst = connected.ConnectedComponentsWithCross();
                        objectOutputStream.writeObject(lst);
                        System.out.println(lst);
                        break;
                    }catch (NullPointerException nullPointerException){}

                }


                case "ShortestPath":{
                    try {

                        shortPaths shortPaths = new shortPaths(matrix, start, end);
                        List<List<Node<Index>>> lst = shortPaths.findShortestPaths();
                        System.out.println(lst);
                        if (lst == null) {
                            objectOutputStream.writeObject(null);
                        } else objectOutputStream.writeObject(lst);

                    }catch (NullPointerException nullPointerException){}

                    break;

                }

                case "submarine": {

                    try {
                        SubMarine submarine = new SubMarine(matrix);
                        int count = submarine.submarineFind();
                        objectOutputStream.writeObject(count);

                    } catch (NullPointerException nullPointerException) {
                    }
                    break;
                }
                case "weighted":
                {

                    try {
                        BellmanFord bellmanFord = new BellmanFord(
                                matrix,
                                start,
                                end
                        );
                        List<List<Node>> lst = bellmanFord.BellmanFordAlgo();
                        objectOutputStream.writeObject(lst);

                    } catch (NullPointerException nullPointerException) {nullPointerException.printStackTrace();}
                    break;
                }

                case "start index":{
                    this.start = (Index)objectInputStream.readObject();
                    break;
                }

                case "end index":{
                    this.end = (Index)objectInputStream.readObject();
                    break;
                }

                case "stop":
                {
                    System.out.println("Closing the socket");
                    doWork = false;
                    break;
                }
            }
        }
    }

}