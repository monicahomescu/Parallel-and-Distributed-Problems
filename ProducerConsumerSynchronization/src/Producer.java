import java.util.List;

public class Producer extends Thread {
    private final ItemsQueue queue;
    private final List<Integer> vector1;
    private final List<Integer> vector2;

    public Producer(ItemsQueue queue, List<Integer> vector1, List<Integer> vector2) {
        this.queue = queue;
        this.vector1 = vector1;
        this.vector2 = vector2;
    }

    @Override
    public void run() {
        for (int i = 0; i < vector1.size(); i++) {
            try {
                Integer element1 = vector1.get(i);
                Integer element2 = vector2.get(i);
                Integer product = element1 * element2;
                queue.add(product);

                System.out.printf("Producer: added product %d * %d = %d to queue\n", element1, element2, product);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
