package app;

import data.DataSet;
import data.TrainData;
import general.Layer;
import general.Net;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private Canvas canvas;
    private List<int[]> inputs;
    private List<TrainData> trainData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initStage(stage);
        initNeuralNet();
    }

    private void initStage(Stage stage) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(200);
        anchorPane.setPrefHeight(200);
        canvas = new Canvas();
        canvas.setWidth(200);
        canvas.setHeight(200);
        anchorPane.getChildren().add(canvas);
        stage.setScene(new Scene(anchorPane));
        stage.show();
    }

    private void initNeuralNet() {
        Layer layer1 = new Layer(5);   // сначала создаем слои
        Layer layer2 = new Layer(10);
        Layer layer3 = new Layer(1);
        Net net = new Net(layer1, layer2, layer3);  // далее создаем сеть на базе слоев

        List<TrainData> data = new ArrayList<>();   // создаем данные для обучения, можно любыми числами, нормализация будет производится самостоятельно
        trainData = new ArrayList<>();
        double rnd1, rnd2;
        for (int i = 0; i < 10; i++) {
            rnd1 = Math.random() * 100;             // ниже одинаковые данные кладутся в разные листы, второй лист будет хранить ненормализованые данные
            rnd2 = Math.random() * 100;             // это связано с тем, что данные в первом листе нормализуются после обучения, оригинал не сохранится
            data.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{1}));
            trainData.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, Math.sin(rnd1), Math.sin(rnd2), rnd1 * rnd2}, new double[]{1}));

            rnd1 = Math.random() * 100 + 100;
            rnd2 = Math.random() * 100;
            data.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{0}));
            trainData.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{0}));

            rnd1 = Math.random() * 100;
            rnd2 = Math.random() * 100 + 100;
            data.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{0}));
            trainData.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{0}));

            rnd1 = Math.random() * 100 + 100;
            rnd2 = Math.random() * 100 + 100;
            data.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{1}));
            trainData.add(new TrainData(new double[]{rnd1, rnd2, rnd1 * rnd1, rnd2 * rnd2, rnd1 * rnd2}, new double[]{1}));
        }


        List<TrainData> test = new ArrayList<>();   // создаем данные для генерации
        inputs = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                test.add(new TrainData(new double[]{i, j, i * i, j * j, i * j}, new double[]{Math.random()}));
                inputs.add(new int[]{i, j});
            }
        }

        DataSet dataSet = new DataSet(data, test);  // создаем дата сет из данных по обучению для генерации
        trainAndDraw(net, dataSet);                 // это специфичный метод для задачи отображения результатов
    }

    private void trainAndDraw(Net net, DataSet dataSet) {
        Runnable runnable = () -> {                 // создаем поток для расчетов
            while (true) {                          // бесконечно повторяем итерации обучения и отображения
                try {
                    Thread.sleep(100);        // делаем задержку, чтобы картинка рисовалась не так быстро
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                                                    // обучаем сеть с фиксированным значением эпох, которое можно менять, для регулирования частоты обновления картинки
                net.train(dataSet, 0.1, 0.00001, 1000);
                                                    // рассчитываем карту согласно данным генерации
                List<double[]> outputs = net.calc(dataSet);
                Platform.runLater(() -> {           // рисуем карту
                    int x, y;
                    double value;
                    for (int i = 0; i < inputs.size(); i++) {
                        x = inputs.get(i)[0];
                        y = inputs.get(i)[1];
                        value = (outputs.get(i)[0] > 1) ? 1 : (outputs.get(i)[0] < 0) ? 0 : outputs.get(i)[0];
                        final Color color = new Color(value, value, value, 1);
                        canvas.getGraphicsContext2D().getPixelWriter().setColor(x, y, color);
                    }
                                                    // рисуем точки обучения
                    canvas.getGraphicsContext2D().setFill(Color.RED);
                    for (TrainData data : trainData) {
                        canvas.getGraphicsContext2D().fillOval(data.getInput()[0], data.getInput()[1], 2, 2);
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
}
