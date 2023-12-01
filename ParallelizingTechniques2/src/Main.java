import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Graph graph = new Graph(10);
        //graph.generateHamiltonianGraph();
        graph.generateNonHamiltonianGraph();

        List<Integer> resultPath = new ArrayList<>();
        AtomicBoolean isFound = new AtomicBoolean(false);

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        long startTime = System.nanoTime();

        // search for a hamiltonian cycle for each node in the graph
        for (int i = 0; i < graph.getSize(); i++)
            executorService.submit(new Task(graph, i, resultPath, isFound));

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("\nDuration: " + duration + " ms");

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("\nGraph:\n" + graph);
        if (isFound.get())
            System.out.println("Hamiltonian cycle: " + resultPath);
        else
            System.out.println("Hamiltonian cycle: not found");
    }
}
