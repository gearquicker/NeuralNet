package data;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

    private List<TrainData> trainData;
    private List<TrainData> testData;


    public DataSet(List<TrainData> trainData, List<TrainData> testData) {
        this.trainData = trainData;
        this.testData = testData;
    }

    public List<TrainData> getAllData() {
        List<TrainData> ans = new ArrayList<>();
        ans.addAll(trainData);
        ans.addAll(testData);
        return ans;
    }

    public List<TrainData> getTrainData() {
        return trainData;
    }

    public void setTrainData(List<TrainData> trainData) {
        this.trainData = trainData;
    }

    public List<TrainData> getTestData() {
        return testData;
    }

    public void setTestData(List<TrainData> testData) {
        this.testData = testData;
    }

}
