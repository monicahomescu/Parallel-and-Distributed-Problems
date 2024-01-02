import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static List<Integer> findHamiltonianCycle(int[][] graph, int currentNode, List<Integer> path) throws InterruptedException, ExecutionException {
        // check if all the nodes are present in the path
        if (path.size() == graph.length) {
            // check if there is an edge from the current node to the starting node
            if (graph[currentNode][path.get(0)] == 1) {
                // add the starting node to the path to form the cycle
                path.add(path.get(0));
                return path;    // Hamiltonian cycle found
            }
            else
                return new ArrayList<>(); // no Hamiltonian cycle found
        }

        // list to store the results of the tasks
        List<Future<List<Integer>>> futures = new ArrayList<>();

        // create a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        // try each node
        for (int i = 0; i < graph.length; i++) {
            // check if there is an edge from the current node to the next node
            // check if the next node is not already present in the path
            if (graph[currentNode][i] == 1 && !path.contains(i)) {
                // create a new path with the next node added to the current path
                List<Integer> newPath = new ArrayList<>(path);
                newPath.add(i);

                // create a new task that follows the next possible node
                final int node = i;
                Callable<List<Integer>> task = () -> findHamiltonianCycle(graph, node, newPath);
                futures.add(executorService.submit(task));
            }
        }

        // stop the thread pool
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // wait for the results of the tasks
        for (Future<List<Integer>> future : futures) {
            // get the result of the task
            List<Integer> result = future.get();

            // return the first non-empty result
            if (!result.isEmpty())
                return result;  // Hamiltonian cycle found
        }

        return new ArrayList<>();   // no Hamiltonian cycle found
    }

    private static void printResult(int[][] graph) throws InterruptedException, ExecutionException {
        // start with an initial empty path
        List<Integer> path = new ArrayList<>();
        // start with node 0
        int startNode = 0;
        // add the node to the path
        path.add(startNode);

        // call the recursive function that searches for a Hamiltonian cycle
        List<Integer> resultPath = findHamiltonianCycle(graph, startNode, path);

        // check and print the result
        if (resultPath.isEmpty())
            System.out.println("There is no Hamiltonian cycle.");
        else
            System.out.print("The Hamiltonian cycle is: " + resultPath + ".\n");
    }

    public static int[][] generateGraph(int size) {
        int[][] graph = new int[size][size];

        for (int i = 0; i < size - 1; i++)
            graph[i][i + 1] = 1;

        graph[size - 1][0] = 1;

        return graph;
    }

    public static void testProgram(int[][] graph) throws ExecutionException, InterruptedException {
        long startTime = System.nanoTime();

        printResult(graph);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Duration: " + duration + " ms");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("\n-- GRAPH 1 --");
        int[][] graph1 = {
                {0, 1, 1, 0, 0},
                {1, 0, 1, 1, 0},
                {1, 1, 0, 1, 1},
                {0, 1, 1, 0, 1},
                {0, 0, 1, 1, 0}
        };
        testProgram(graph1);

        System.out.println("\n-- GRAPH 2 --");
        int[][] graph2 = {
                {0, 1, 0, 1, 0},
                {1, 0, 1, 0, 1},
                {0, 1, 0, 1, 0},
                {1, 0, 1, 0, 1},
                {0, 1, 0, 1, 0}
        };
        testProgram(graph2);

        System.out.println("\n-- GRAPH 3 --");
        int[][] graph3 = generateGraph(1000);
        testProgram(graph3);
    }
}
