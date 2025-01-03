import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static String consoleMidOption = "------------------------------";

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

        Scanner scanner = new Scanner(System.in);

        calculateAndPrintAccuracy(classifier, testData);

        System.out.println("Would you like to predict your behaviour class? ");
        System.out.println("Type: Yes/yes/y OR No/no/n");
        System.out.println(consoleMidOption);
        String input = scanner.next();
        Boolean userSelection = userDecision(input);
        if (userSelection) {
            predictNewRecord(scanner, classifier);
        }
    }

    public static void predictNewRecord(Scanner scanner, NaiveBayesClassifier classifier) {
        System.out.println("Enter Age: ");
        int age = scanner.nextInt();

        System.out.println("Enter Gender (Male/Female): ");
        String genderInput = scanner.next();
        int gender = genderSelection(genderInput);

        System.out.println("Enter App Usage Time (minutes/day): ");
        int appUsageTime = scanner.nextInt();

        System.out.println("Enter Screen On Time (hours/day): ");
        double screenOnTime = scanner.nextDouble();

        PhoneUsage newRecord = new PhoneUsage(0, "", "", appUsageTime, screenOnTime, 0, 0, 0, age, gender, 0);

        // Predict the User Behavior Class
        int predictedClass = classifier.predict(newRecord);
        System.out.println("Predicted User Behavior Class: " + predictedClass);

        // Recommend a device based on behavior class
        String deviceRecommendation = classifier.recommendDevice(predictedClass);
        System.out.println("Recommended Device: " + deviceRecommendation);
    }


    public static int genderSelection(String input) {
        input = input.toLowerCase();
        int gender = 0;
        switch (input) {
            case "male" -> gender = 1;
            case "female" -> gender = 0;
            default -> System.out.println("None valid Gender input");
        }
        return gender;
    }

    public static boolean userDecision(String input) {
        boolean validBool = false;
        input = input.toLowerCase();
        switch (input) {
            case "yes" -> validBool = true;
            case "y" -> validBool = true;
            case "no" -> validBool = false;
            case "n" -> validBool = false;
        }
        return validBool;
    }

    public static void calculateAndPrintAccuracy(NaiveBayesClassifier classifier, ArrayList<PhoneUsage> testData) {
        int correct = 0;

        for (PhoneUsage record : testData) {
            int predicted = classifier.predict(record);
            if(predicted == record.getUserBehaviorClass()) {
                correct++;
            }
        }
        double accuracy = (double) correct / testData.size();
        System.out.println(consoleMidOption);
        System.out.println("Model accuracy Rate: " + String.format("%.3f", (accuracy * 100)) + "%");
        System.out.println(consoleMidOption);
    }
}
