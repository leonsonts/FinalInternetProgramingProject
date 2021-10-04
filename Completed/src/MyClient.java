import java.io.*;
import java.net.Socket;
import java.util.*;

public class MyClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1", 8010);
        System.out.println("client: Created Socket");

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
        ObjectInputStream fromServer = new ObjectInputStream(inputStream);
        boolean work = true;
        Matrix matrix = null;
        Scanner scan = new Scanner(System.in);
        String string;
        int row;
        int col;
        while (work)
        {
            System.out.println("Enter which task you want to perform: ");
            System.out.println("To generate a random matrix write: matrix");
            System.out.println("To generate a random weighted matrix write: weighted matrix");
            System.out.println("To find Connected Components write: connected");
            System.out.println("To find Shortest paths write: shortest paths");
            System.out.println("To play SubMarine write: submarine");
            System.out.println("To find shortest paths om weighted graph write: weighted");
            System.out.println("To show Matrix write: show matrix");
            System.out.println("To stop write : stop");
            switch (string = scan.nextLine()) {

                case "matrix": {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter row size");
                    int matrixrow = scanner.nextInt();
                    while(matrixrow == 0)
                    {
                        System.out.println("row can't be 0, enter another number! ");
                        matrixrow = scanner.nextInt();
                    }
                    System.out.println("Enter col size");
                    int matrixcol = scanner.nextInt();
                    while(matrixcol == 0)
                    {
                        System.out.println("col can't be 0, enter another number! ");
                        matrixcol = scanner.nextInt();
                    }
                    toServer.writeObject("random matrix");
                    toServer.writeObject(matrixrow);
                    toServer.writeObject(matrixcol);

                    matrix = new Matrix((Matrix)fromServer.readObject());
                    System.out.println("The matrix is");
                    matrix.printMatrix();
                    break;
                }
                case "weighted matrix": {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter row size");
                    int matrixrow = scanner.nextInt();
                    while(matrixrow == 0)
                    {
                        System.out.println("row can't be 0, enter another number! ");
                        matrixrow = scanner.nextInt();
                    }
                    System.out.println("Enter col size");
                    int matrixcol = scanner.nextInt();
                    while(matrixcol == 0)
                    {
                        System.out.println("col can't be 0, enter another number! ");
                        matrixcol = scanner.nextInt();
                    }
                    System.out.println("Enter positive bound");
                    int bond = scanner.nextInt();
                    toServer.writeObject("weighted matrix");
                    toServer.writeObject(matrixrow);
                    toServer.writeObject(matrixcol);
                    toServer.writeObject(bond);

                    matrix = new Matrix((Matrix)fromServer.readObject());
                    System.out.println("The matrix is");
                    matrix.printMatrix();
                    break;
                }
                case "connected": {

                    if (matrix == null) {
                        System.out.println("Please enter matrix first");
                        break;
                    }

                    toServer.writeObject("Connected");
                    List<HashSet<Index>> connect = new ArrayList<HashSet<Index>>((List<HashSet<Index>>) fromServer.readObject());
                    System.out.println("The Connected components are: " + connect);
                    break;

                }

                case "shortest paths":
                {
                    if(matrix == null)
                    {
                        System.out.println("Please enter matrix first");
                        break;
                    }
                    if(matrix.lengthCol() > 50 && matrix.lengthRow() > 50)
                    {
                        System.out.println("Matrix size is bigger then 50X50");
                        break;

                    }
                    if(matrix.lengthCol() != matrix.lengthRow())
                    {
                        System.out.println("Matrix is not square matrix!");
                        break;

                    }
                    System.out.println("Enter Start Index Row");
                    Index startIndex = matrix.indexCheck(scan.nextInt());
                    while (matrix.getValue(startIndex) == 0) {
                        System.out.println("Value at row "+ startIndex.getRow() + " col " + startIndex.getColumn() + " is 0," +
                                "please enter a another index");
                        startIndex = matrix.indexCheck(scan.nextInt());
                    }
                    System.out.println("Enter End Index Row");
                    Index endIndex = matrix.indexCheck(scan.nextInt());
                    while (matrix.getValue(endIndex) == 0) {
                        System.out.println("Value at row "+ endIndex.getRow() + " col " + endIndex.getColumn() + " is 0," +
                                "please enter a another index");
                        endIndex = matrix.indexCheck(scan.nextInt());
                    }
                    toServer.writeObject("start index");
                    toServer.writeObject(new Index(startIndex.getRow(), startIndex.getColumn()));
                    toServer.writeObject("end index");
                    toServer.writeObject(new Index(endIndex.getRow(), endIndex.getColumn()));
                    toServer.writeObject("ShortestPath");
                    try {
                        List<List<Node<Index>>> shortest = new ArrayList<>((List<List<Node<Index>>>) fromServer.readObject());
                        System.out.println("The shortest paths from " + startIndex + " to " + endIndex+ " are: " +shortest);
                    } catch (NullPointerException nullPointerException) {
                        System.out.println("not Connection between the  two Indices");}
                    scan.nextLine();//clean buffer

                    break;

                }
                case "show matrix":
                {
                    matrix.printMatrix();
                    break;
                }

                case "submarine":
                {
                    if (matrix == null) {
                        System.out.println("Please enter matrix first");
                        break;
                    }
                    toServer.writeObject("submarine");
                    int Submarine = (int) fromServer.readObject();
                    System.out.println("The number of submarines are: " + Submarine);
                    break;
                }
                case "weighted":
                {
                    if (matrix == null) {
                        System.out.println("Please enter matrix first");
                        break;
                    }
                    System.out.println("Enter Start Index Row");
                    Index startIndex = matrix.indexCheck(scan.nextInt());

                    toServer.writeObject("start index");
                    toServer.writeObject(new Index(startIndex.getRow(), startIndex.getColumn()));

                    System.out.println("Enter end Index Row");
                    Index endIndex = matrix.indexCheck(scan.nextInt());


                    toServer.writeObject("end index");
                    toServer.writeObject(new Index(endIndex.getRow(), endIndex.getColumn()));

                    toServer.writeObject("weighted");
                    try {
                        List<List<Node>> list = new ArrayList<>((List<List<Node>>) fromServer.readObject());
                        System.out.println("The shortest paths from "+startIndex+" to " + endIndex+ " is " + list);
                    } catch (NullPointerException nullPointerException)
                    {
                        System.out.println("Matrix contains negative circle");}
                    scan.nextLine();//clean buffer
                    break;
                }
                case "stop":
                {
                    toServer.writeObject("stop");
                    fromServer.close();
                    toServer.close();
                    socket.close();
                    System.out.println("client: Closed operational socket");
                    work = false;
                    break;
                }

                default:
                    throw new IllegalStateException("Unexpected value: " + (string = scan.nextLine()));
            }
        }
    }
}