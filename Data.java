package Preprocessing;
import java.util.HashMap;
public class Data {
    private HashMap<String, String> data;

    public Data(String[] labels, String[] dataraw) {
        data = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            data.put(labels[i], dataraw[i]);
        }
    }
    public String get(String label) {
        return data.get(label);
    }
    public void update(String label, String value) {
        data.put(label, value);
    }
    public void displayData() {
        for (String key : data.keySet()) {
            System.out.print(key + ": " + data.get(key) + " ");
        }
        System.out.println();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}