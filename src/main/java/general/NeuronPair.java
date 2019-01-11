package general;

public class NeuronPair {

    private Neuron neuron;
    private double weight;

    public NeuronPair(Neuron neuron, double weight) {
        this.neuron = neuron;
        this.weight = weight;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public void setNeuron(Neuron neuron) {
        this.neuron = neuron;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
