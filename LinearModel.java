package Weka;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.Evaluation;

import java.io.File;
public class LinearModel {
    private Instances trainData;
    private Instances evalData;
    private LinearRegression model;
    private Evaluation eval;

    public LinearModel(String csvFilePath) throws Exception {
        // Load dữ liệu từ file CSV
        csvFilePath = "Data\\cleanedData.csv";
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(csvFilePath));
        Instances data = loader.getDataSet();

        // Chuyển đổi dữ liệu từ CSV sang ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        File arffFile = new File("CleanedData.arff");
        saver.setFile(arffFile);
        saver.setDestination(arffFile);
        saver.writeBatch();

        System.out.println("Chuyển đổi thành công từ CSV sang ARFF.");

        // Chia tập dữ liệu thành tập huấn luyện và tập đánh giá
        data.randomize(new java.util.Random(1));
        int trainSize = (int) Math.round(data.numInstances() * 0.8); // 80% dữ liệu cho tập huấn luyện
        int evalSize = data.numInstances() - trainSize;
        trainData = new Instances(data, 0, trainSize);
        evalData = new Instances(data, trainSize, evalSize);

        // Chỉ định cột output là WIND
        trainData.setClassIndex(trainData.attribute("WIND").index());
        evalData.setClassIndex(evalData.attribute("WIND").index());

        // Khởi tạo mô hình hồi quy tuyến tính
        model = new LinearRegression();

        // Huấn luyện mô hình trên tập huấn luyện và đo thời gian
        long startTime = System.nanoTime();
        model.buildClassifier(trainData);
        long buildTime = System.nanoTime() - startTime;

        // Đánh giá mô hình bằng phương pháp cross-validation 10-fold và đo thời gian
        startTime = System.nanoTime();
        eval = new Evaluation(trainData);
        eval.crossValidateModel(model, trainData, 10, new java.util.Random(1));
        long evalTime = System.nanoTime() - startTime;

        // In kết quả đánh giá
        System.out.println("LinearModel");
        System.out.println("Evaluation Results:");
        System.out.println(eval.toSummaryString());
        System.out.println("Correlation coefficient: " + eval.correlationCoefficient());
        System.out.println("Mean absolute error: " + eval.meanAbsoluteError());
        System.out.println("Root mean squared error: " + eval.rootMeanSquaredError());
        System.out.println("Relative absolute error: " + eval.relativeAbsoluteError() + " %");
        System.out.println("Root relative squared error: " + eval.rootRelativeSquaredError() + " %");
        System.out.println("Total Number of Instances: " + eval.numInstances());
        System.out.println("Build Time (seconds): " + buildTime / 1e9);
        System.out.println("Evaluation Time (seconds): " + evalTime / 1e9);

        // In độ chính xác
        printRSquared();

        // Dự đoán giá trị và đo thời gian
        startTime = System.nanoTime();
        double[] predictions = predict();
        long predictTime = System.nanoTime() - startTime;
        System.out.println("Prediction Time (seconds): " + predictTime / 1e9);
    }

    public double[] predict() throws Exception {
        double[] predictions = new double[evalData.numInstances()];
        for (int i = 0; i < evalData.numInstances(); i++) {
            predictions[i] = model.classifyInstance(evalData.instance(i));
        }
        return predictions;
    }

    public void printRSquared() throws Exception {
        double rSquared = eval.correlationCoefficient() * eval.correlationCoefficient();
        System.out.println("R-squared: " + rSquared);
    }
}
