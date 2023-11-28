import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> vector1 = Arrays.asList(2, 3, 4);
        List<Integer> vector2 = Arrays.asList(1, 5, 6);
        Integer length = vector1.size();

        System.out.println("\nvector 1 = " + vector1);
        System.out.println("vector 2 = " + vector2 + "\n");

        ItemsQueue queue = new ItemsQueue(10);
        Producer producer = new Producer(queue, vector1, vector2);
        Consumer consumer = new Consumer(queue, length);

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
