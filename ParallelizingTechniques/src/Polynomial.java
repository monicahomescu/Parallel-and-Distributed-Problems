import java.util.List;

public class Polynomial {
    private final List<Integer> coefficients;

    public Polynomial(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public List<Integer> getCoefficients() {
        return coefficients;
    }

    public int getLength() {
        return coefficients.size();
    }

    public int getDegree() {
        return coefficients.size() - 1;
    }

    public int getCoefficient(int index) {
        return coefficients.get(index);
    }

    public void setCoefficient(int index, int element) {
        coefficients.set(index, element);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int degree = coefficients.size() - 1; degree >= 0; degree--) {
            int coefficient = coefficients.get(degree);

            // check if the coefficient is non-zero
            if (coefficient != 0) {
                // if this is not the first term, add a '+' separator
                if (result.length() > 0)
                    result.append(" + ");

                // if the degree is 0 or the coefficient is different from 1, append the coefficient
                if (degree == 0 || coefficient != 1)
                    result.append(coefficient);

                // if the degree is greater than 0, append 'x'
                if (degree > 0)
                    result.append("x");

                // if the degree is greater than 1, append '^' and the degree
                if (degree > 1)
                    result.append("^").append(degree);
            }
        }

        return result.toString();
    }
}
