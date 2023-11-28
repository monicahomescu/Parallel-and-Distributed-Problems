public class RowTask implements Runnable {
    private final Integer start;    //the position number of the first element to compute
    private final Integer count;    //the number of elements to compute
    private final Integer[][] matrix1;
    private final Integer[][] matrix2;
    private final Integer[][] result;

    public RowTask(Integer start, Integer count, Integer[][] matrix1, Integer[][] matrix2, Integer[][] result) {
        this.start = start;
        this.count = count;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
    }

    private int computeElement(int i, int j) {
        int element = 0;

        for (int k = 0; k < matrix1[0].length; k++)     //common number of columns/rows
            element += matrix1[i][k] * matrix2[k][j];

        return element;
    }

    @Override
    public void run() {
        int i = start / result[0].length;   //line of first element to compute
        int j = start % result[0].length;   //column of first element to compute

        for (int c = 0; c < count; c++) {
            //System.out.println("Task " + (start / count) + " - Computed element at position (" + i + ", " + j + ")");
            result[i][j] = computeElement(i, j);

            j++;
            if (j == result[0].length) {    //reset column if equal to number of columns
                j = 0;
                i++;
            }
        }
    }
}
