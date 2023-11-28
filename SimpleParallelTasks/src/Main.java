import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void initializeMatrix(Integer[][] matrix) {
        int value = 1;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = value;
                value++;
            }
        }
    }

    public static void printMatrix(Integer[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++)
                System.out.print(matrix[i][j] + " ");
            System.out.println();
        }
    }

    public static void executeTasks(int task, int approach, int numberOfThreads, Integer[][] matrix1, Integer[][] matrix2, Integer[][] result) throws InterruptedException {
        List<Runnable> runnables = new ArrayList<>();

        int count = (matrix1.length * matrix2[0].length) / numberOfThreads;     //number of elements for each task to compute
        int remainder = (matrix1.length * matrix2[0].length) % numberOfThreads;     //number of extra elements left for last task if applicable

        for (int k = 0; k < numberOfThreads; k++) {     //number of tasks assigned
            int start = k * count;      //position number of the first element to compute in the current task
            if (k + 1 == numberOfThreads && remainder != 0)     //add leftover elements to last task
                count += remainder;

            if (task == 1)
                runnables.add(new RowTask(start, count, matrix1, matrix2, result));
            else if (task == 2)
                runnables.add(new ColumnTask(start, count, matrix1, matrix2, result));
            else
                //k - position number of the first element to compute in the current task
                //numberOfThreads - number of elements to step over to get to next element
                runnables.add(new KthTask(k, numberOfThreads, matrix1, matrix2, result));
        }

        if (approach == 1) {    //actual thread
            List<Thread> threads = new ArrayList<>();
            for (Runnable runnable : runnables)
                threads.add(new Thread(runnable));

            for (Thread thread : threads)
                thread.start();

            for (Thread thread : threads)
                thread.join();
        } else {    //thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            for (Runnable runnable : runnables)
                executorService.submit(runnable);

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Number of rows matrix 1:");
        int rows = scanner.nextInt();

        System.out.println("\nNumber of columns matrix 1/ Number of rows matrix 2:");
        int columns_rows = scanner.nextInt();

        System.out.println("\nNumber of columns matrix 2:");
        int columns = scanner.nextInt();

        Integer[][] matrix1 = new Integer[rows][columns_rows];
        Integer[][] matrix2 = new Integer[columns_rows][columns];
        Integer[][] result = new Integer[rows][columns];

        initializeMatrix(matrix1);
        initializeMatrix(matrix2);

        System.out.println("\nTask (1 - row, 2 - column, 3 - kth):");
        int task = scanner.nextInt();

        System.out.println("\nApproach (1 - actual thread, 2 - thread pool):");
        int approach = scanner.nextInt();

        System.out.println("\nNumber of threads:");
        int numberOfThreads = scanner.nextInt();

        System.out.println();

        long startTime = System.currentTimeMillis();

        executeTasks(task, approach, numberOfThreads, matrix1, matrix2, result);

        long endTime = System.currentTimeMillis();
        System.out.println("-- Time consumed: " + (endTime - startTime) / 1000.0 + " seconds --");

        //System.out.println("\nMatrix 1:");
        //printMatrix(matrix1);

        //System.out.println("\nMatrix 2:");
        //printMatrix(matrix2);

        //System.out.println("\nResult:");
        //printMatrix(result);
    }
}
