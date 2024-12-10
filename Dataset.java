package Preprocessing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
public class Dataset {
    private LinkedHashMap<String, Data> data;
    private String[] labels;

    public Dataset(String url) {
        data = new LinkedHashMap<>();
        loadData(url);
    }
    public LinkedHashMap<String, Data> getData() {
        return data;
    }

    public String[] getLabelStrings() {
        return labels;
    }

    private void loadData(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            if ((line = reader.readLine()) != null) {
                labels = line.split(",");
            }

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                data.put(values[0], new Data(labels, values));
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double[][] calculateStatistics() {
        int numCols = labels.length;
        double[][] statistics = new double[numCols][5]; // 5 statistics: mean, stdDev, Q1, Q2, Q3

        // Initialize arrays
        for (int i = 0; i < numCols; i++) {
            List<Double> columnValues = new ArrayList<>();
            for (Data rowData : data.values()) {
                double value = Double.parseDouble(rowData.get(labels[i]));
                columnValues.add(value);
            }

            // Calculate statistics
            double[] sortedValues = columnValues.stream().mapToDouble(Double::doubleValue).sorted().toArray();
            statistics[i][0] = calculateMean(sortedValues);
            statistics[i][1] = calculateStandardDeviation(sortedValues, statistics[i][0]);
            statistics[i][2] = calculatePercentile(sortedValues, 0.25);
            statistics[i][3] = calculatePercentile(sortedValues, 0.50);
            statistics[i][4] = calculatePercentile(sortedValues, 0.75);
        }

        // Print statistics
        System.out.println("Column\tMean\tStdDev\tQ1\tQ2\tQ3");
        for (int i = 0; i < numCols; i++) {
            System.out.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", labels[i], statistics[i][0], statistics[i][1],
                    statistics[i][2], statistics[i][3], statistics[i][4]);
        }
        return statistics;
    }

    private double calculateMean(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    private double calculateStandardDeviation(double[] values, double mean) {
        double sumSquaredDiff = 0;
        for (double value : values) {
            sumSquaredDiff += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumSquaredDiff / values.length);
    }

    private double calculatePercentile(double[] sortedValues, double percentile) {
        int index = (int) Math.ceil(percentile * sortedValues.length) - 1;
        return sortedValues[index];
    }

    public void printTop10Data() {
        int count = 0;

        // Print labels
        for (String label : labels) {
            System.out.printf("%-10s ", label);
        }
        System.out.println();

        // Print data
        for (Map.Entry<String, Data> entry : data.entrySet()) {
            Data dataEntry = entry.getValue();
            for (String label : labels) {
                System.out.printf("%-10s ", dataEntry.get(label));
            }
            System.out.println();
            count++;
            if (count >= 10) {
                break;
            }
        }
    }

    public int cleanNullValue() {
        int removedLines = 0;
        Iterator<Map.Entry<String, Data>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Data> entry = iterator.next();
            Data dataEntry = entry.getValue();
            boolean hasNull = false;
            for (String label : labels) {
                String value = dataEntry.get(label);
                if (value == null || value.equalsIgnoreCase("NA")) {
                    hasNull = true;
                    break;
                }
            }
            if (hasNull) {
                iterator.remove();
                removedLines++;
            }
        }
        System.out.println("Removed " + removedLines + " lines with Null or NA values!");
        replaceStringByDouble();
        return removedLines;
    }

    public void replaceStringByDouble() {
        for (Map.Entry<String, Data> entry : data.entrySet()) {
            Data dataEntry = entry.getValue();
            String[] values = new String[labels.length];
            for (int i = 0; i < labels.length; i++) {
                values[i] = dataEntry.get(labels[i]);
                if (values[i] != null && values[i].contains("\"")) {
                    values[i] = values[i].replace("\"", "");
                }
            }
            data.put(entry.getKey(), new Data(labels, values));
        }
    }

    public void removeLabel(String labelToRemove) {
        List<String> newLabelsList = new ArrayList<>();
        for (String label : labels) {
            String labelString = label.replace("\"", "");
            if (!labelString.equals(labelToRemove)) {
                newLabelsList.add(label);
            }
        }

        System.out.println(newLabelsList.toString() + " " + labelToRemove);

        LinkedHashMap<String, Data> newData = new LinkedHashMap<>();
        for (Map.Entry<String, Data> entry : data.entrySet()) {
            String[] oldValues = new String[newLabelsList.size()];
            int index = 0;
            for (String newLabel : newLabelsList) {
                oldValues[index++] = entry.getValue().get(newLabel);
            }
            newData.put(entry.getKey(), new Data(newLabelsList.toArray(new String[0]), oldValues));
        }

        data = newData;
        labels = newLabelsList.toArray(new String[0]);
    }


    public int getColumnIndex(String columnName) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].replace("\"", "").equals(columnName)) {
                return i;
            }
        }
        return -1; // Return -1 if the column is not found
    }

    public void saveToCSV() {
        String filePath = "Data\\cleanedData.csv";
        try {
            if (Files.exists(Paths.get(filePath))) {
                Files.delete(Paths.get(filePath));
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            for (int i = 0; i < labels.length; i++) {
                writer.write(labels[i]);
                if (i < labels.length - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            for (Map.Entry<String, Data> entry : data.entrySet()) {
                Data dataEntry = entry.getValue();
                for (int i = 0; i < labels.length; i++) {
                    writer.write(dataEntry.get(labels[i]));
                    if (i < labels.length - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            writer.close();
            System.out.println("Data saved to CSV file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortByColumn(String columnName) {
        final int[] columnIndex = { -1 };
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].replace("\"", "").equals(columnName)) {
                columnIndex[0] = i;
                break;
            }
        }

        if (columnIndex[0] == -1) {
            System.out.println("Column not found: " + columnName);
            return;
        }

        List<Map.Entry<String, Data>> entries = new ArrayList<>(data.entrySet());

        entries.sort((entry1, entry2) -> {
            String value1 = entry1.getValue().get(labels[columnIndex[0]]);
            String value2 = entry2.getValue().get(labels[columnIndex[0]]);

            value1 = value1 != null ? value1 : "";
            value2 = value2 != null ? value2 : "";

            try {
                Double double1 = Double.parseDouble(value1);
                Double double2 = Double.parseDouble(value2);
                return double1.compareTo(double2);
            } catch (NumberFormatException e) {
                return value1.compareTo(value2);
            }
        });

        LinkedHashMap<String, Data> sortedData = new LinkedHashMap<>();
        for (Map.Entry<String, Data> entry : entries) {
            sortedData.put(entry.getKey(), entry.getValue());
        }
        data = sortedData;
    }

    public void removeOutliers(double threshold, double[][] statistics, String[] exceptLabels) {
        if (statistics == null) {
            System.out.println("Error: Statistics not available.");
            return;
        }
        double[] iqr = new double[statistics.length];
        for (int i = 0; i < iqr.length; i++) {
            double q1 = statistics[i][2]; // Q1
            double q3 = statistics[i][4]; // Q3
            iqr[i] = q3 - q1; // Compute IQR for current column
        }

        String[] labels = getLabelStrings();
        int removedLines = 0; // Counter for removed lines

        for (int i = 0; i < iqr.length; i++) {
            String currentLabel = labels[i].replace("\"", ""); // Remove any quotes around the label
            if (Arrays.asList(exceptLabels).contains(currentLabel)) {
                // Skip this column if it's in the exceptLabels array
                continue;
            }

            double lowerBound = statistics[i][2] - threshold * iqr[i];
            double upperBound = statistics[i][4] + threshold * iqr[i];

            // Remove outliers from the dataset
            for (Iterator<Map.Entry<String, Data>> iterator = data.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String, Data> entry = iterator.next();
                Data dataEntry = entry.getValue();
                double value = Double.parseDouble(dataEntry.get(labels[i]));
                if (value < lowerBound || value > upperBound) {
                    iterator.remove();
                    removedLines++; // Increment counter for removed lines
                }
            }
        }

        System.out.println("Removed " + removedLines + " lines as outliers.");
    }
     public void scaleMultipleColumn(String[] labelsName, double minRange, double maxRange) {
        // Define decimal format for one decimal place
        DecimalFormat df = new DecimalFormat("#0.000");

        for (String labelName : labelsName) {
            // Find the index of the label
            labelName = "\""+labelName+"\"";
            System.out.println("Current label: " + labelName + " scaling");
            int index = -1;
            for (int i = 0; i < labels.length; i++) {
                if (labels[i].equals(labelName)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                System.out.println("Label " + labelName + " not found.");
                continue;
            }

            // Extract values of the specified column
            double[] columnValues = new double[data.size()];
            int i = 0;
            for (Data rowData : data.values()) {
                String value = rowData.get(labelName);
                if (value != null && !value.isEmpty()) {
                    columnValues[i++] = Double.parseDouble(value);
                }
            }

            // Find min and max values in the column
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (double value : columnValues) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }

            // Scale values to the specified range
            double range = max - min;
            for (Data rowData : data.values()) {
                String value = rowData.get(labelName);
                if (value != null && !value.isEmpty()) {
                    double scaledValue = minRange + ((Double.parseDouble(value) - min) / range) * (maxRange - minRange);
                    rowData.update(labelName, df.format(scaledValue)); // Format scaled value
                }
            }
            System.out.println("Done");
        }

}
}
