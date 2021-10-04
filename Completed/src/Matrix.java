import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Matrix  implements Serializable {

    public int[][] primitiveMatrix;
    public Matrix(int[][] oArray) {
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }
    public  Matrix(Matrix matrix)
    {
        this(matrix.primitiveMatrix);
    }
    public Matrix(int row, int col) {
        Random r = new Random();
        primitiveMatrix = new int[row][col];
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[0].length; j++) {
                primitiveMatrix[i][j] = r.nextInt(2);
            }
        }
        for (int[] crow : primitiveMatrix) {
            String s = Arrays.toString(crow);
            System.out.println(s);
        }
        System.out.println("\n");
    }
    public  Matrix(int row, int col, int bond) {
        Random r = new Random();
        primitiveMatrix = new int[row][col];
        for (int i = 0; i < primitiveMatrix.length; i++) {
            int counter=0;
            for (int j = 0; j < primitiveMatrix[0].length; j++) {
                counter++;

                primitiveMatrix[i][j] = r.nextInt(bond);

            }
        }
        for (int[] crow : primitiveMatrix) {
            String s = Arrays.toString(crow);
            System.out.println(s);
        }
        System.out.println("\n");
    }



    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


    @NotNull
    public Collection<Index> getNeighbors(@NotNull final Index index) {
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try {
            extracted = primitiveMatrix[index.row + 1][index.column];
            list.add(new Index(index.row + 1, index.column));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row][index.column + 1];
            list.add(new Index(index.row, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row - 1][index.column];
            list.add(new Index(index.row - 1, index.column));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row][index.column - 1];
            list.add(new Index(index.row, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return list;
    }

    @NotNull
    public Collection<Index> getNeighborsWithCross(@NotNull final Index index) {
        Collection<Index> list = getNeighbors(index);
        int extracted = -1;
        try {
            extracted = primitiveMatrix[index.row - 1][index.column - 1];
            list.add(new Index(index.row - 1, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        try {
            extracted = primitiveMatrix[index.row + 1][index.column + 1];
            list.add(new Index(index.row + 1, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row + 1][index.column - 1];
            list.add(new Index(index.row + 1, index.column - 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            extracted = primitiveMatrix[index.row - 1][index.column + 1];
            list.add(new Index(index.row - 1, index.column + 1));
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        return list;
    }

    public int getValue(@NotNull final Index index) {
        return primitiveMatrix[index.row][index.column];
    }

    public void printMatrix() {
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }


    public int lengthRow()
    {
        return primitiveMatrix.length;
    }
    public int lengthCol() { return primitiveMatrix[0].length; }
    public Index indexCheck(int row)
    {
        Scanner scan = new Scanner(System.in);
        while (row> lengthRow()-1)
        {
            System.out.println("The Index Is Invalid Please Enter Another");
            row = scan.nextInt();
        }
        System.out.println("Enter Index Col");
        int col=scan.nextInt();
        while (col > lengthCol()-1)
        {
            System.out.println("The Index Is Invalid Please Enter Another");
            col = scan.nextInt();
        }
        Index index = new Index(row,col);
        return index;
    }
}