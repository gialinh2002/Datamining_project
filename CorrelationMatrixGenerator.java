package Preprocessing;
import java.text.DecimalFormat;

public class CorrelationMatrixGenerator {
    private Dataset dataset;

    public CorrelationMatrixGenerator(Dataset dataset) {
        this.dataset = dataset;
    }

    public void generateAndPrintCorrelationMatrix() {
        if (dataset == null) {
            System.out.println("Error: Dataset not loaded.");
            return;
        }

        // Get data from the dataset
        double[][] dataArray = new double[dataset.getData().size()][dataset.getLabelStrings().length];
        String[] labels = dataset.getLabelStrings();
        int rowIndex = 0;
        for (Data dataEntry : dataset.getData().values()) {
            int colIndex = 0;
            for (String label : labels) {
                String value = dataEntry.get(label);
                dataArray[rowIndex][colIndex] = Double.parseDouble(value);
                colIndex++;
            }
            rowIndex++;
        }

        // Calculate correlation matrix
        double[][] correlationMatrix = calculateCorrelationMatrix(dataArray);

        // Print correlation matrix
        DecimalFormat df = new DecimalFormat("#0.###");
        int labelColumnWidth = calculateColumnWidth(labels);
        System.out.print(padRight("", labelColumnWidth));
        for (String label : labels) {
            System.out.print(padRight(label, labelColumnWidth));
        }
        System.out.println();
        for (int i = 0; i < correlationMatrix.length; i++) {
            System.out.print(padRight(labels[i], labelColumnWidth));
            for (int j = 0; j < correlationMatrix[i].length; j++) {
                System.out.print(padRight(df.format(correlationMatrix[i][j]), labelColumnWidth));
            }
            System.out.println();
        }
    }

    private String padRight(String str, int width) {
        return String.format("%-" + width + "s", str);
    }

    private int calculateColumnWidth(String[] labels) {
        int maxLabelLength = 0;
        for (String label : labels) {
            maxLabelLength = Math.max(maxLabelLength + 1, label.length());
        }
        return Math.max(maxLabelLength, 6); // Ensure minimum width for numeric values
    }

    private double[][] calculateCorrelationMatrix(double[][] data) {
        int numRows = data.length;
        int numCols = data[0].length;
        double[][] correlationMatrix = new double[numCols][numCols];

        // Calculate means
        double[] means = new double[numCols];
        for (int j = 0; j < numCols; j++) {
            double sum = 0;
            for (int i = 0; i < numRows; i++) {
                sum += data[i][j];
            }
            means[j] = sum / numRows;
        }

        // Calculate standard deviations
        double[] stdDeviations = new double[numCols];
        for (int j = 0; j < numCols; j++) {
            double sumSqDiff = 0;
            for (int i = 0; i < numRows; i++) {
                sumSqDiff += Math.pow(data[i][j] - means[j], 2);
            }
            stdDeviations[j] = Math.sqrt(sumSqDiff / numRows);
        }

        // Calculate correlation coefficients
        for (int i = 0; i < numCols; i++) {
            for (int j = i; j < numCols; j++) {
                double sum = 0;
                for (int k = 0; k < numRows; k++) {
                    sum += ((data[k][i] - means[i]) * (data[k][j] - means[j]));
                }
                correlationMatrix[i][j] = correlationMatrix[j][i] = sum
                        / (numRows * stdDeviations[i] * stdDeviations[j]);
            }
        }

        return correlationMatrix;
    }
}