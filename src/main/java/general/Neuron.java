package general;

import functions.Func;

import java.util.ArrayList;
import java.util.List;

public class Neuron {

    private List<NeuronPair> backwardPairs;
    private double error;
    private double value;

    public Neuron() {
        backwardPairs = new ArrayList<>();
        error = 1;
        value = 1;
    }

    public void calcValue(Func func, double value) {
        setValue(func.calc(value));
    }

    public List<NeuronPair> getBackwardPairs() {
        return backwardPairs;
    }

    public void setBackwardPairs(List<NeuronPair> backwardPairs) {
        this.backwardPairs = backwardPairs;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
