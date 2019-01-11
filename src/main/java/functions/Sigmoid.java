package functions;

public class Sigmoid implements Func {
    @Override
    public double calc(double value) {
        return 1 / (1 + Math.exp(-value));
    }

    @Override
    public double derivation(double value) {
        return value * (1 - value);
    }
}
