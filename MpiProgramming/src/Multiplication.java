import java.util.ArrayList;
import java.util.List;

public class Multiplication {
    public static Polynomial combineResults(Object[] polynomials) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= ((Polynomial) polynomials[0]).getDegree(); i++)
            coefficients.add(0);

        Polynomial result = new Polynomial(coefficients);

        // add all the polynomials to the result
        for (Object polynomial: polynomials)
            result = add(result, (Polynomial) polynomial);

        return result;
    }

    public static Polynomial multiplyRegularWithBounds(int start, int end, Polynomial p1, Polynomial p2) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= p1.getDegree() + p2.getDegree(); i++)
            coefficients.add(0);

        Polynomial result = new Polynomial(coefficients);

        // multiply the coefficients of p1 that are within the bounds with p2
        for (int i = start; i < end; i++) {
            for (int j = 0; j <= p2.getDegree(); j++) {
                int value = result.getCoefficient(i + j)+ p1.getCoefficient(i) * p2.getCoefficient(j);
                result.setCoefficient(i + j, value);
            }
        }

        return result;
    }

    public static Polynomial multiplyRegular(Polynomial p1, Polynomial p2) {
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

    private static Polynomial add(Polynomial p1, Polynomial p2) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= Math.max(p1.getDegree(), p2.getDegree()); i++) {
            int coefficient1 = (i <= p1.getDegree()) ? p1.getCoefficient(i) : 0;
            int coefficient2 = (i <= p2.getDegree()) ? p2.getCoefficient(i) : 0;

            coefficients.add(coefficient1 + coefficient2);
        }

        return new Polynomial(coefficients);
    }

    private static Polynomial subtract(Polynomial p1, Polynomial p2) {
        List<Integer> coefficients = new ArrayList<>();

        for (int i = 0; i <= Math.max(p1.getDegree(), p2.getDegree()); i++) {
            int coefficient1 = (i <= p1.getDegree()) ? p1.getCoefficient(i) : 0;
            int coefficient2 = (i <= p2.getDegree()) ? p2.getCoefficient(i) : 0;

            coefficients.add(coefficient1 - coefficient2);
        }

        return new Polynomial(coefficients);
    }

    private static Polynomial padZeros(Polynomial p, int count) {
        List<Integer> coefficients = new ArrayList<>(p.getCoefficients());

        // add zeros to the front (left) of the original coefficients list
        for (int i = 0; i < count; i++)
            coefficients.add(0, 0);

        return new Polynomial(coefficients);
    }

    public static Polynomial multiplyKaratsuba(Polynomial p1, Polynomial p2) {
        // use regular multiplication if polynomial is too small to split
        if (p1.getDegree() < 2 || p2.getDegree() < 2)
            return multiplyRegular(p1, p2);

        // find the middle to split the polynomials in half
        int n = Math.min(p1.getDegree(), p2.getDegree()) / 2;

        // split the polynomials into low part (small terms) and high part (big terms)
        Polynomial p1Low = new Polynomial(p1.getCoefficients().subList(0, n));
        Polynomial p1High = new Polynomial(p1.getCoefficients().subList(n, p1.getLength()));
        Polynomial p2Low = new Polynomial(p2.getCoefficients().subList(0, n));
        Polynomial p2High = new Polynomial(p2.getCoefficients().subList(n, p2.getLength()));

        // trick to make only 3 multiplications instead of multiplying each of the 4 pairs (multiply recursively)
        // [P1(X) * Q1(X)] * X^2n + [(P1(X)+P2(X)) * (Q1(X)+Q2(X)) - P1(X)*Q1(X) - P2(X)*Q2(X)] * X^n + P2(X)*Q2(X)

        // P1(X) * Q1(X) <=> p1High * p2High
        Polynomial p1p2High = multiplyKaratsuba(p1High, p2High);
        // (P1(X)+P2(X)) * (Q1(X)+Q2(X)) <=> (p1High+p1Low) * (p2High+p2Low)
        Polynomial p1p2HighLow = multiplyKaratsuba(add(p1High, p1Low), add(p2High, p2Low));
        // P2(X) * Q2(X) <=> p1Low * p2Low
        Polynomial p1p2Low = multiplyKaratsuba(p1Low, p2Low);

        // [P1(X) * Q1(X)] * X^2n <=> add 2 * n zeros to p1High * p2High
        Polynomial r1 = padZeros(p1p2High, 2 * n);
        // [(P1(X)+P2(X)) * (Q1(X)+Q2(X)) - P1(X)*Q1(X) - P2(X)*Q2(X)] * X^n <=> add n zeros to (p1High+p1Low) * (p2High+p2Low) - p1High*p2High - p1Low*p2Low
        Polynomial r2 = padZeros(subtract(subtract(p1p2HighLow, p1p2High), p1p2Low), n);

        // add the final computed elements
        return add(add(r1, r2), p1p2Low);
    }
}
