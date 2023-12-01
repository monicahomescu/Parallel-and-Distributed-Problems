import java.util.*;
import static java.util.Collections.shuffle;

public class Graph {
    private final List<Integer> nodes;
    private final List<List<Integer>> edges;

    public Graph(int numberOfNodes) {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        for (int node = 0; node < numberOfNodes; node++) {
            nodes.add(node);
            edges.add(new ArrayList<>());
        }
    }

    public int getSize() {
        return edges.size();
    }

    public List<Integer> getNeighbours(int node) {
        return edges.get(node);
    }

    public boolean isEdge(int node1, int node2) {
        return edges.get(node1).contains(node2);
    }

    private void addEdge(int node1, int node2) {
        edges.get(node1).add(node2);
    }

    public void generateNonHamiltonianGraph() {
        Random random = new Random();
        int node1, node2;

        // add random edges (but not enough for a hamiltonian cycle to be formed)
        for (int i = 0; i < nodes.size() - 1; i++) {
            do {
                node1 = random.nextInt(nodes.size());
                node2 = random.nextInt(nodes.size());
            } while (node1 == node2 || isEdge(node1, node2));

            addEdge(node1, node2);
        }
    }

    public void generateHamiltonianGraph() {
        // shuffle the nodes
        List<Integer> shuffledNodes = new ArrayList<>(nodes);
        shuffle(shuffledNodes);

        // add the hamiltonian cycle (cycle that goes through each node exactly once)
        for (int i = 0; i < nodes.size() - 1; i++)
            addEdge(shuffledNodes.get(i), shuffledNodes.get(i + 1));
        addEdge(shuffledNodes.get(nodes.size() - 1), shuffledNodes.get(0));

        Random random = new Random();
        int node1, node2;

        // add random edges
        for (int i = 0; i < nodes.size(); i++) {
            do {
                node1 = random.nextInt(nodes.size());
                node2 = random.nextInt(nodes.size());
            } while (node1 == node2 || isEdge(node1, node2));

            addEdge(node1, node2);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Nodes: ").append(nodes).append("\n");

        stringBuilder.append("Edges:\n");
        for (int i = 0; i < nodes.size(); i++)
            stringBuilder.append(nodes.get(i)).append(" -> ").append(edges.get(i)).append("\n");

        return stringBuilder.toString();
    }
}
