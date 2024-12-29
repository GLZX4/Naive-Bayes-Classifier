import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String filePath = "data/user_behavior_dataset.csv";
        CSVReader csvReader = new CSVReader();
        ArrayList<PhoneUsage> data = csvReader.readCSV(filePath);

        int trainSize = (int) (data.size() * 0.8);
        ArrayList<PhoneUsage> trainData = new ArrayList<>(data.subList(0, trainSize));
        ArrayList<PhoneUsage> testData = new ArrayList<>(data.subList(trainSize, data.size()));

        // Train the Naive Bayes classifier
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        classifier.fit(trainData);

        // Get user input for new prediction
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Age: ");
        int age = scanner.nextInt();

        System.out.println("Enter Gender (Male/Female): ");
        String gender = scanner.next();

        System.out.println("Enter App Usage Time (minutes/day): ");
        int appUsageTime = scanner.nextInt();

        System.out.println("Enter Screen On Time (hours/day): ");
        double screenOnTime = scanner.nextDouble();

        // Create a new PhoneUsage object with the user's input
        PhoneUsage newRecord = new PhoneUsage(0, "", "", appUsageTime, screenOnTime, 0, 0, 0, age, gender, 0);

        // Predict the User Behavior Class
        int predictedClass = classifier.predict(newRecord);
        System.out.println("Predicted User Behavior Class: " + predictedClass);
    }
}
