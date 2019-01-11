package general;

import java.util.ArrayList;
import java.util.List;

public class Layer {

    private List<Neuron> neurons;
    private Neuron shiftNeuron;

    public Layer(int neuronsCount) {
        neurons = new ArrayList<>();
        for (int i = 0; i < neuronsCount; i++) {
            neurons.add(new Neuron());
        }
        shiftNeuron = new Neuron();
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }

    public Neuron getShiftNeuron() {
        return shiftNeuron;
    }

    public void setShiftNeuron(Neuron shiftNeuron) {
        this.shiftNeuron = shiftNeuron;
    }
}
