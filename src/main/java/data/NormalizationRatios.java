package data;

public class NormalizationRatios {

    private double k;
    private double b;

    public NormalizationRatios(double k, double b) {
        this.k = k;
        this.b = b;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
}
