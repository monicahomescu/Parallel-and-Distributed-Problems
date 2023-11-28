public class Consumer extends Thread {
    private final ItemsQueue queue;
    private final Integer length;
    private Integer sum;

    public Consumer(ItemsQueue queue, Integer length) {
        this.queue = queue;
        this.length = length;
        this.sum = 0;
    }

    @Override
    public void run() {
        for (int i = 0; i < length; i++) {
            try {
                Integer value = queue.remove();
                sum += value;

                System.out.printf("Consumer: removed product %d from queue, current sum is %d + %d = %d\n", value, sum - value, value, sum);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.out.printf("\nscalar product = %d\n", sum);
    }
}
