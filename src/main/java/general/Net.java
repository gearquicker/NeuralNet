package general;

import data.DataSet;
import data.NormalizationRatios;
import data.NormalizationSet;
import functions.Func;
import functions.Sigmoid;
import data.TrainData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Net {

    private List<Layer> layers;
    private Func func;
    private NormalizationSet normalizationSet;

    public Net(Layer... layers) {
        this.layers = Arrays.asList(layers);
        initNeuronLinks();
    }

    private void initNeuronLinks() {
        for (int i = 0; i < layers.size(); i++) {
            for (Neuron neuron : layers.get(i).getNeurons()) {
                if (i == 0) {
                    List<NeuronPair> neuronPairs = new ArrayList<>();
                    for (Neuron ignored : layers.get(i).getNeurons()) {
                        neuronPairs.add(new NeuronPair(null, Math.random() - 0.5));
                    }
                    neuron.setBackwardPairs(neuronPairs);
                } else {
                    List<NeuronPair> neuronPairs = new ArrayList<>();
                    for (Neuron n : layers.get(i - 1).getNeurons()) {
                        neuronPairs.add(new NeuronPair(n, 1));
                    }
                    neuronPairs.add(new NeuronPair(layers.get(i - 1).getShiftNeuron(), Math.random() - 0.5));
                    neuron.setBackwardPairs(neuronPairs);
                }
            }
        }
    }

    private void calcValues(double[] input) {
        Func f = (func == null) ? new Sigmoid() : func;
        double[] values = new double[layers.get(0).getNeurons().size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = input[i];
            } catch (Exception ignored) {
                values[i] = 1;
            }
        }

        double sum;
        for (Neuron neuron : layers.get(0).getNeurons()) {
            sum = 0;
            for (int i = 0; i < neuron.getBackwardPairs().size(); i++) {
                sum = sum + values[i] * neuron.getBackwardPairs().get(i).getWeight();
            }
            neuron.calcValue(f, sum);
        }
        for (int i = 1; i < layers.size(); i++) {
            for (Neuron neuron : layers.get(i).getNeurons()) {
                sum = 0;
                for (NeuronPair pair : neuron.getBackwardPairs()) {
                    sum = sum + pair.getNeuron().getValue() * pair.getWeight();
                }
                neuron.calcValue(f, sum);
            }
        }
    }

    private void calcErrors(double[] output) {
        double[] values = new double[layers.get(layers.size() - 1).getNeurons().size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = output[i];
            } catch (Exception ignored) {
                values[i] = 1;
            }
        }

        for (int i = 0; i < layers.get(layers.size() - 1).getNeurons().size(); i++) {
            Neuron neuron = layers.get(layers.size() - 1).getNeurons().get(i);
            neuron.setError(values[i] - neuron.getValue());
        }
        double sum;
        for (int i = layers.size() - 2; i >= 0; i--) {
            for (int j = 0; j < layers.get(i).getNeurons().size(); j++) {
                sum = 0;
                for (int h = 0; h < layers.get(i + 1).getNeurons().size(); h++) {
                    Neuron neuron = layers.get(i + 1).getNeurons().get(h);
                    sum = sum + neuron.getBackwardPairs().get(j).getWeight() * neuron.getError();
                }
                layers.get(i).getNeurons().get(j).setError(sum);
            }
        }
    }

    private void calcNewWeights(double[] input, double trainRatio) {
        Func f = (func == null) ? new Sigmoid() : func;
        double[] values = new double[layers.get(0).getNeurons().size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = input[i];
            } catch (Exception ignored) {
                values[i] = 1;
            }
        }

        for (int i = 0; i < values.length; i++) {
            Neuron neuron = layers.get(0).getNeurons().get(i);
            ArrayList<NeuronPair> newPairs = new ArrayList<>();
            for (int j = 0; j < neuron.getBackwardPairs().size(); j++) {
                newPairs.add(new NeuronPair(null, neuron.getBackwardPairs().get(j).getWeight() + trainRatio * neuron.getError() * f.derivation(neuron.getValue()) * values[j]));
            }
            neuron.setBackwardPairs(newPairs);
        }
        for (int i = 1; i < layers.size(); i++) {
            for (Neuron neuron : layers.get(i).getNeurons()) {
                ArrayList<NeuronPair> newPairs = new ArrayList<>();
                for (int j = 0; j < neuron.getBackwardPairs().size(); j++) {
                    NeuronPair pair = neuron.getBackwardPairs().get(j);
                    newPairs.add(new NeuronPair(pair.getNeuron(), pair.getWeight() + trainRatio * neuron.getError() * f.derivation(neuron.getValue()) * pair.getNeuron().getValue()));
                }
                neuron.setBackwardPairs(newPairs);
            }
        }
    }

    private double calcExitError() {
        double error = 0;
        int iteration = 0;
        for (Neuron neuron : layers.get(layers.size() - 1).getNeurons()) {
            error = error + Math.pow(neuron.getError(), 2);
            iteration++;
        }
        return error / iteration;
    }

    private void normalizeDataSet(DataSet dataSet) {
        calcNormalizationSet(dataSet);

        List<TrainData> data = dataSet.getAllData();
        for (TrainData trainData : data) {
            double[] input = new double[layers.get(0).getNeurons().size()];
            List<NormalizationRatios> ratios = normalizationSet.getInputRatios();
            for (int i = 0; i < input.length; i++) {
                input[i] = (trainData.getInput()[i] - ratios.get(i).getB()) / ratios.get(i).getK() + 0.1;
            }
            trainData.setInput(input);
        }
        for (TrainData trainData : dataSet.getTrainData()) {
            double[] output = new double[layers.get(layers.size() - 1).getNeurons().size()];
            List<NormalizationRatios> ratios = normalizationSet.getOutputRatios();
            for (int i = 0; i < output.length; i++) {
                output[i] = (trainData.getOutput()[i] - ratios.get(i).getB()) / ratios.get(i).getK() + 0.1;
            }
            trainData.setOutput(output);
        }
    }

    private void denormalizeOutputs(List<double[]> outputs) {
        List<NormalizationRatios> ratios = normalizationSet.getOutputRatios();
        for (double[] output : outputs) {
            for (int i = 0; i < output.length; i++) {
                output[i] = (output[i] - 0.1) * ratios.get(i).getK() + ratios.get(i).getB();
            }
        }
    }

    private void calcNormalizationSet(DataSet dataSet) {
        List<NormalizationRatios> input = new ArrayList<>();
        List<NormalizationRatios> output = new ArrayList<>();

        double min, max;
        List<TrainData> data = dataSet.getAllData();
        for (int i = 0; i < layers.get(0).getNeurons().size(); i++) {
            max = -1E307;
            min = 1E307;
            for (TrainData trainData : data) {
                max = Math.max(max, trainData.getInput()[i]);
                min = Math.min(min, trainData.getInput()[i]);
            }
            input.add(new NormalizationRatios((max - min) / 0.8 + 0.1, min));
        }

        for (int i = 0; i < layers.get(layers.size() - 1).getNeurons().size(); i++) {
            max = -1E307;
            min = 1E307;
            for (TrainData trainData : dataSet.getTrainData()) {
                max = Math.max(max, trainData.getOutput()[i]);
                min = Math.min(min, trainData.getOutput()[i]);
            }
            output.add(new NormalizationRatios((max - min) / 0.8 + 0.1, min));
        }
        normalizationSet = new NormalizationSet(input, output);
    }

    public void train(DataSet dataSet, double trainRatio, double maxError, int maxIterations) {
        normalizeDataSet(dataSet);
        double error;
        int epoch = 0;
        do {
            error = 0;
            for (TrainData dataItem : dataSet.getTrainData()) {
                calcValues(dataItem.getInput());
                calcErrors(dataItem.getOutput());
                error = error + calcExitError();
                calcNewWeights(dataItem.getInput(), trainRatio);
            }
            System.out.println(epoch + "\t" + error);
            epoch++;
        } while (epoch < maxIterations && error > maxError);
    }

    public List<double[]> calc(DataSet dataSet) {
        List<double[]> outputs = new ArrayList<>();
        for (TrainData input : dataSet.getTestData()) {
            double[] output = new double[layers.get(layers.size() - 1).getNeurons().size()];
            calcValues(input.getInput());
            for (int i = 0; i < output.length; i++) {
                output[i] = layers.get(layers.size() - 1).getNeurons().get(i).getValue();
            }
            outputs.add(output);
        }

        denormalizeOutputs(outputs);
        return outputs;
    }


    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public Func getFunc() {
        return func;
    }

    public void setFunc(Func func) {
        this.func = func;
    }
}
