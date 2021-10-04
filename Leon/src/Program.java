public class Program {
    public static void main(String[] args) {
        int[][] myArray = {
                {1,1,1,1,1},
                {0,0,1,1,1},
                {1,1,1,0,0},
                {0,1,0,1,0},
                {0,1,1,1,0}
        };
        Matrix leonMatrix = new Matrix(myArray);
        System.out.println(leonMatrix);
    }
}
