import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    private static Polynomial generatePolynomial(int degree) {
        Random random = new Random();

        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= degree; i++)
            coefficients.add(random.nextInt(10));

        return new Polynomial(coefficients);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Multiplication multiplication = new Multiplication();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n0 - Exit");
            System.out.println("1 - Regular Sequential");
            System.out.println("2 - Regular Parallel");
            System.out.println("3 - Karatsuba Sequential");
            System.out.println("4 - Karatsuba Parallel");

            int choice = scanner.nextInt();

            if (choice == 0)
                break;

            Random random = new Random();
            int degree = random.nextInt(10) + 1;
            Polynomial p1 = generatePolynomial(degree);
            Polynomial p2 = generatePolynomial(degree);
            Polynomial result;

            long startTime = System.nanoTime();

            switch (choice) {
                case 1 -> result = multiplication.RegularSequential(p1, p2);
                case 2 -> result = multiplication.RegularParallel(p1, p2);
                case 3 -> result = multiplication.KaratsubaSequential(p1, p2);
                case 4 -> result = multiplication.KaratsubaParallel(p1, p2, 1);
                default -> {
                    System.out.println("Invalid choice!");
                    continue;
                }
            }

            long endTime = System.nanoTime();
            double duration = ((double) endTime - (double) startTime) / 1_000_000_000.0;
            System.out.println("Duration: " + duration + " seconds");

            System.out.println("Polynomial 1: " + p1);
            System.out.println("Polynomial 2: " + p2);
            System.out.println("Result: " + result.toString());
        }
    }
}
