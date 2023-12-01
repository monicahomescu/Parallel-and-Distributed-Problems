import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Task implements Runnable {
    private final Graph graph;
    private final int startNode;
    private final List<Integer> resultPath;
    private final AtomicBoolean isFound;
    private final List<Integer> potentialPath;
    private final Lock mutex;

    public Task(Graph graph, int startNode, List<Integer> resultPath, AtomicBoolean isFound) {
        this.graph = graph;
        this.startNode = startNode;
        this.resultPath = resultPath;
        this.isFound = isFound;
        potentialPath = new ArrayList<>();
        mutex = new ReentrantLock();
    }

    private void searchForHamiltonianCycle(int node) {
        // add the current node to the potential path
        potentialPath.add(node);

        // check if a hamiltonian cycle is already found by another thread
        if (!isFound.get()) {
            // check if the potential path size is equal to the total number of nodes in the graph
            if (potentialPath.size() == graph.getSize()) {
                // check if the last node in the potential path has a connection to the starting node
                if (graph.isEdge(node, startNode)) {
                    // set the flag to indicate that a hamiltonian cycle is found
                    isFound.set(true);

                    // acquire the lock to safely update the result path
                    mutex.lock();

                    // clear and update the result path with the found hamiltonian cycle
                    resultPath.clear();
                    resultPath.addAll(potentialPath);

                    // release the lock after updating the result path
                    mutex.unlock();
                }
                // return to backtrack if the condition for hamiltonian cycle is not met
                return;
            }

            // explore neighbors of the current node in a depth-first manner
            for (int neighbour : graph.getNeighbours(node)) {
                // check if the neighbor is not already in the potential path to avoid cycles
                if (!potentialPath.contains(neighbour))
                    // recursively explore the neighbor
                    searchForHamiltonianCycle(neighbour);
            }
        }
    }

    @Override
    public void run() {
        searchForHamiltonianCycle(startNode);
    }
}
