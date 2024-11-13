import java.util.*;

public class Main {

    public static void main(String[] args) {

        String filepath = "data/heart_failure_clinical_records_dataset.csv";
        CSVReader reader = new CSVReader(filepath);
        List<PatientRecord> dataset = reader.read();

        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        classifier.train(dataset);

        // Test the classifier with a new patient record
        PatientRecord newPatient = new PatientRecord("56", Gender.Female, 1, 1, true, "13.0", 1, "140", -1);

        String prediction = classifier.predict(newPatient);
        int deathEvent = Integer.parseInt(prediction);
        String result = (deathEvent == 1) ? "likely" : "unlikely";
        System.out.println("Prediction for new Patient: " + result + " to Survive");
    }
}