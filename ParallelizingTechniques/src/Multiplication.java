import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Multiplication {
    public Polynomial RegularSequential(Polynomial p1, Polynomial p2) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= p1.getDegree() + p2.getDegree(); i++)
            coefficients.add(0);

        Polynomial result = new Polynomial(coefficients);

        for (int i = 0; i <= p1.getDegree(); i++) {
            for (int j = 0; j <= p2.getDegree(); j++) {
                int value = result.getCoefficient(i + j)+ p1.getCoefficient(i) * p2.getCoefficient(j);
                result.setCoefficient(i + j, value);
            }
        }

        return result;
    }

    public Polynomial RegularParallel(Polynomial p1, Polynomial p2) throws InterruptedException {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= p1.getDegree() + p2.getDegree(); i++)
            coefficients.add(0);

        Polynomial result = new Polynomial(coefficients);

        int numberOfThreads = 3;
        // compute how many parts the result will be split into
        int step = (p1.getDegree() + p2.getDegree() + 1) / numberOfThreads;
        if (step == 0)
            step = 1;

        List<Runnable> runnables = new ArrayList<>();

        for(int i = 0; i <= p1.getDegree() + p2.getDegree(); i += step) {
            Task task = new Task(i, i + step, p1, p2, result);
            runnables.add(task);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (Runnable runnable : runnables)
            executorService.submit(runnable);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        return result;
    }

    private Polynomial add(Polynomial p1, Polynomial p2) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= Math.max(p1.getDegree(), p2.getDegree()); i++) {
            int coefficient1 = (i <= p1.getDegree()) ? p1.getCoefficient(i) : 0;
            int coefficient2 = (i <= p2.getDegree()) ? p2.getCoefficient(i) : 0;

            coefficients.add(coefficient1 + coefficient2);
        }

        return new Polynomial(coefficients);
    }

    private Polynomial subtract(Polynomial p1, Polynomial p2) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= Math.max(p1.getDegree(), p2.getDegree()); i++) {
            int coefficient1 = (i <= p1.getDegree()) ? p1.getCoefficient(i) : 0;
            int coefficient2 = (i <= p2.getDegree()) ? p2.getCoefficient(i) : 0;

            coefficients.add(coefficient1 - coefficient2);
        }

        return new Polynomial(coefficients);
    }

    private Polynomial padZeros(Polynomial p, int count) {
        List<Integer> coefficients = new ArrayList<>(p.getCoefficients());

        // add zeros to the front (left) of the original coefficients list
        for (int i = 0; i < count; i++)
            coefficients.add(0, 0);

        return new Polynomial(coefficients);
    }

    public Polynomial KaratsubaSequential(Polynomial p1, Polynomial p2) {
        // use regular multiplication if polynomial is too small to split
        if (p1.getDegree() < 2 || p2.getDegree() < 2)
            return RegularSequential(p1, p2);

        // find the middle to split the polynomials in half
        int n = Math.max(p1.getDegree(), p2.getDegree()) / 2;

        // split the polynomials into low part (small terms) and high part (big terms)
        Polynomial p1Low = new Polynomial(p1.getCoefficients().subList(0, n));
        Polynomial p1High = new Polynomial(p1.getCoefficients().subList(n, p1.getLength()));
        Polynomial p2Low = new Polynomial(p2.getCoefficients().subList(0, n));
        Polynomial p2High = new Polynomial(p2.getCoefficients().subList(n, p2.getLength()));

        // trick to make only 3 multiplications instead of multiplying each of the 4 pairs (multiply recursively)
        // [P1(X) * Q1(X)] * X^2n + [(P1(X)+P2(X)) * (Q1(X)+Q2(X)) - P1(X)*Q1(X) - P2(X)*Q2(X)] * X^n + P2(X)*Q2(X)

        // P1(X) * Q1(X) <=> p1High * p2High
        Polynomial p1p2High = KaratsubaSequential(p1High, p2High);
        // (P1(X)+P2(X)) * (Q1(X)+Q2(X)) <=> (p1High+p1Low) * (p2High+p2Low)
        Polynomial p1p2HighLow = KaratsubaSequential(add(p1High, p1Low), add(p2High, p2Low));
        // P2(X) * Q2(X) <=> p1Low * p2Low
        Polynomial p1p2Low = KaratsubaSequential(p1Low, p2Low);

        // [P1(X) * Q1(X)] * X^2n <=> add 2 * n zeros to p1High * p2High
        Polynomial r1 = padZeros(p1p2High, 2 * n);
        // [(P1(X)+P2(X)) * (Q1(X)+Q2(X)) - P1(X)*Q1(X) - P2(X)*Q2(X)] * X^n <=> add n zeros to (p1High+p1Low) * (p2High+p2Low) - p1High*p2High - p1Low*p2Low
        Polynomial r2 = padZeros(subtract(subtract(p1p2HighLow, p1p2High), p1p2Low), n);

        // add the final computed elements
        return add(add(r1, r2), p1p2Low);
    }

    public Polynomial KaratsubaParallel(Polynomial p1, Polynomial p2, int depth) throws ExecutionException, InterruptedException {
        // use regular multiplication if depth is exceeded
        if (depth > 3)
            return RegularSequential(p1, p2);

        // use regular multiplication if polynomial is too small to split
        if (p1.getDegree() < 2 || p2.getDegree() < 2)
            return RegularSequential(p1, p2);

        // find the middle to split the polynomials in half
        int n = Math.max(p1.getDegree(), p2.getDegree()) / 2;

        // split the polynomials into low part (small terms) and high part (big terms)
        Polynomial p1Low = new Polynomial(p1.getCoefficients().subList(0, n));
        Polynomial p1High = new Polynomial(p1.getCoefficients().subList(n, p1.getLength()));
        Polynomial p2Low = new Polynomial(p2.getCoefficients().subList(0, n));
        Polynomial p2High = new Polynomial(p2.getCoefficients().subList(n, p2.getLength()));

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // trick to make only 3 multiplications instead of multiplying each of the 4 pairs (multiply recursively)
        // [P1(X) * Q1(X)] * X^2n + [(P1(X)+P2(X)) * (Q1(X)+Q2(X)) - P1(X)*Q1(X) - P2(X)*Q2(X)] * X^n + P2(X)*Q2(X)

        // P1(X) * Q1(X) <=> p1High * p2High
        Future<Polynomial> p1p2HighFuture = executorService.submit(() -> KaratsubaParallel(p1High, p2High, depth + 1));
        // (P1(X)+P2(X)) * (Q1(X)+Q2(X)) <=> (p1High+p1Low) * (p2High+p2Low)
        Future<Polynomial> p1p2HighLowFuture = executorService.submit(() -> KaratsubaParallel(add(p1High, p1Low), add(p2High, p2Low), depth + 1));
        // P2(X) * Q2(X) <=> p1Low * p2Low
        Future<Polynomial> p1p2LowFuture = executorService.submit(() -> KaratsubaParallel(p1Low, p2Low, depth + 1));

        Polynomial p1p2High = p1p2HighFuture.get();
        Polynomial p1p2HighLow = p1p2HighLowFuture.get();
        Polynomial p1p2Low = p1p2LowFuture.get();

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);

        // [P1(X) * Q1(X)] * X^2n <=> add 2 * n zeros to p1High * p2High
        Polynomial r1 = padZeros(p1p2High, 2 * n);
        // [(P1(X)+P2(X)) * (Q1(X)+Q2(X)) - P1(X)*Q1(X) - P2(X)*Q2(X)] * X^n <=> add n zeros to (p1High+p1Low) * (p2High+p2Low) - p1High*p2High - p1Low*p2Low
        Polynomial r2 = padZeros(subtract(subtract(p1p2HighLow, p1p2High), p1p2Low), n);

        // add the final computed elements
        return add(add(r1, r2), p1p2Low);
    }
}
