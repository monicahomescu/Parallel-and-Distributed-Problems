import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<>();
        int numberOfAccounts = 5;
        for (int i = 1; i <= numberOfAccounts; i++)
            accounts.add(new Account(i, 1000));
        Bank bank = new Bank(accounts);

        AtomicBoolean threadsAreRunning = new AtomicBoolean(true);
        AtomicInteger numberOfTransactionsSoFar = new AtomicInteger(0);

        int numberOfTransactions = 10;
        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        try {
            Runnable performOperations = () -> {
                while (numberOfTransactionsSoFar.get() < numberOfTransactions) {
                    Random rand = new Random();

                    int senderId;
                    int receiverId;
                    do {
                        senderId = rand.nextInt(5) + 1;
                        receiverId = rand.nextInt(5) + 1;
                    }
                    while (senderId == receiverId);

                    int min = 10;
                    int max = 200;
                    int amount = (int) (Math.random() * ((max - min) + 1) + min);

                    try {
                        int serialNumber = bank.transferMoney(senderId, receiverId, amount);
                        if (serialNumber != -1) {
                            Transaction transaction = new Transaction(serialNumber, senderId, receiverId, amount);
                            System.out.println(transaction.toString());
                            numberOfTransactionsSoFar.addAndGet(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                threadsAreRunning.set(false);
            };

            Runnable checkOperations = () -> {
                while (threadsAreRunning.get()) {
                    try {
                        Thread.sleep(5000);
                        if (bank.logCheck() && bank.consistencyCheck())
                            System.out.println("\nLog and consistency checks passed!\n");
                        else
                            System.out.println("\nLog and consistency checks failed!\n");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            long startTime = System.currentTimeMillis();

            executorService.submit(performOperations);
            executorService.submit(checkOperations);

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

            long endTime = System.currentTimeMillis();

            System.out.println("-- Time consumed: " + (endTime - startTime) / 1000.0 + " seconds --");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bank.logCheck() && bank.consistencyCheck())
                System.out.println("\nLog and consistency checks passed!\n");
            else
                System.out.println("\nLog and consistency checks failed!\n");

            bank.printAccounts();
        }
    }
}
