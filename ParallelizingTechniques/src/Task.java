public class Task implements Runnable {
    private final int start;
    private final int end;
    private final Polynomial p1;
    private final Polynomial p2;
    private final Polynomial result;

    public Task(int start, int end, Polynomial p1, Polynomial p2, Polynomial result) {
        this.start = start;
        this.end = end;
        this.p1 = p1;
        this.p2 = p2;
        this.result = result;
    }

    @Override
    public void run() {
        // iterate over the range of coefficients to be computed
        for (int i = start; i < end; i++) {
            // check if the index is within the bounds of the result polynomial
            if (i > result.getDegree() + 1)
                return;

            // iterate over the coefficients of the input polynomials
            for (int j = 0; j <= i; j++) {
                // check if the indices are within the bounds of the input polynomials
                if (j <= p1.getDegree() && (i - j) <= p2.getDegree()) {
                    // compute the product of corresponding coefficients and update the result polynomial
                    int value = result.getCoefficient(i) + p1.getCoefficient(j) * p2.getCoefficient(i - j);
                    result.setCoefficient(i, value);
                }
            }
        }
    }
}
