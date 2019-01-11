package data;

import java.util.List;

public class NormalizationSet {

    private List<NormalizationRatios> inputRatios;
    private List<NormalizationRatios> outputRatios;

    public NormalizationSet(List<NormalizationRatios> inputRatios, List<NormalizationRatios> outputRatios) {
        this.inputRatios = inputRatios;
        this.outputRatios = outputRatios;
    }

    public List<NormalizationRatios> getInputRatios() {
        return inputRatios;
    }

    public void setInputRatios(List<NormalizationRatios> inputRatios) {
        this.inputRatios = inputRatios;
    }

    public List<NormalizationRatios> getOutputRatios() {
        return outputRatios;
    }

    public void setOutputRatios(List<NormalizationRatios> outputRatios) {
        this.outputRatios = outputRatios;
    }
}
