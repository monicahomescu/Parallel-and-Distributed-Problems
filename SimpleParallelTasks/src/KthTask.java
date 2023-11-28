public class KthTask implements Runnable {
    private final Integer start;    //the position number of the first element to compute
    private final Integer step;     //the number of elements to step over until next element
    private final Integer[][] matrix1;
    private final Integer[][] matrix2;
    private final Integer[][] result;

    public KthTask(Integer start, Integer step, Integer[][] matrix1, Integer[][] matrix2, Integer[][] result) {
        this.start = start;
        this.step = step;
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

        while (i < result.length && j < result[0].length) {     //iterate until matrix is finished
            //System.out.println("Task " + start + " - Computed element at position (" + i + ", " + j + ")");
            result[i][j] = computeElement(i, j);

            j += step;  //jump to next element on current line
            if (j >= result[0].length) {    ///reassign column if equal or greater than number of columns
                j = j % result[0].length;
                i++;
            }
        }
    }
}
