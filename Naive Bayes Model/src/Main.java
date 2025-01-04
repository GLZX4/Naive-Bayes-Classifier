import java.util.*;
import java.util.Scanner;

public class Main {
    public static String consoleMidOption = "-----------------------------------------";

    public static void main(String[] args) {
        String realDataPath = "data/user_behavior_dataset.csv";
        String syntheticDataPath = "data/synthetic_phone_usage.csv";
        CSVReader csvReader = new CSVReader();
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Would You like to synthetic data? ");
        boolean decision = userDecision(scanner);
        if (decision) {
            runModelSyntheticData(realDataPath, syntheticDataPath, csvReader, classifier, scanner);
        } else {
            runModelNoSyntheticData(realDataPath, csvReader, classifier, scanner);
        }
    }

    public static void userPredictNewRecordDecision(NaiveBayesClassifier classifier, Scanner scanner) {
        System.out.println("Would you like to predict your behaviour class? ");
        System.out.println("Type: Yes/yes/y OR No/no/n");
        System.out.println(consoleMidOption);
        boolean userSelection = userDecision(scanner);
        if (userSelection) {
            predictNewUserRecord(scanner, classifier);
        }
    }

    public static void runModelNoSyntheticData(String filePath, CSVReader csvReader, NaiveBayesClassifier classifier, Scanner scanner) {
        ArrayList<PhoneUsage> realData = csvReader.readOriginalCSV(filePath);
        ArrayList<PhoneUsage> testData = trainModel(realData, classifier);

        calculateAndPrintAccuracy(classifier, testData);
        calculateStatics(classifier, testData);

        userPredictNewRecordDecision(classifier, scanner);
    }

    public static void runModelSyntheticData(String filepath, String syntheticPath, CSVReader csvReader, NaiveBayesClassifier classifier, Scanner scanner) {
        ArrayList<PhoneUsage> realData = csvReader.readOriginalCSV(filepath);
        ArrayList<PhoneUsage> syntheticData = csvReader.readSyntheticCSV(syntheticPath);

        ArrayList<PhoneUsage> combinedData = new ArrayList<>(realData);
        combinedData.addAll(syntheticData);
        ArrayList<PhoneUsage> testData = trainModel(combinedData, classifier);

        calculateAndPrintAccuracy(classifier, testData);
        calculateStatics(classifier, testData);

        userPredictNewRecordDecision(classifier, scanner);
    }

    public static ArrayList<PhoneUsage> trainModel(ArrayList<PhoneUsage> dataset, NaiveBayesClassifier classifier) {
        int trainSize = (int) (dataset.size() * 0.8);
        ArrayList<PhoneUsage> trainData = new ArrayList<>(dataset.subList(0, trainSize));
        ArrayList<PhoneUsage> testData = new ArrayList<>(dataset.subList(trainSize, dataset.size()));

        classifier.fit(trainData);

        return testData;
    }

    public static void predictNewUserRecord(Scanner scanner, NaiveBayesClassifier classifier) {
        System.out.println("Enter Age: ");
        int age = scanner.nextInt();

        System.out.println("Enter Gender (Male/Female): ");
        String genderInput = scanner.next();
        int gender = genderSelection(genderInput);

        System.out.println("Enter App Usage Time (minutes/day): ");
        int appUsageTime = scanner.nextInt();

        System.out.println("Enter Screen On Time (hours/day): ");
        double screenOnTime = scanner.nextDouble();

        PhoneUsage newRecord = new PhoneUsage(0, "", "", appUsageTime, screenOnTime, "",0, 0, 0, age, gender, 0);

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

    public static boolean userDecision(Scanner scanner) {
        boolean validBool = false;
        String input = scanner.nextLine();
        input = input.toLowerCase();
        switch (input) {
            case "yes", "y" -> validBool = true;
            case "no", "n" -> validBool = false;
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

    public static void calculateStatics(NaiveBayesClassifier classifier, ArrayList<PhoneUsage> testData) {
        int[] truePositive = new int[6]; // For classes 1-5
        int[] falsePositive = new int[6];
        int[] falseNegative = new int[6];

        for (PhoneUsage record : testData) {
            int actual = record.getUserBehaviorClass();
            int predicted = classifier.predict(record);

            if (predicted == actual) {
                truePositive[actual]++;
            } else {
                falsePositive[predicted]++;
                falseNegative[actual]++;
            }
        }

        for (int i = 1; i <= 5; i++) {
            double precision = truePositive[i] / (double) (truePositive[i] + falsePositive[i]);
            double recall = truePositive[i] / (double) (truePositive[i] + falseNegative[i]);
            double f1Score = 2 * (precision * recall) / (precision + recall);

            System.out.printf("Class %d - Precision: %.2f, Recall: %.2f, F1-Score: %.2f%n", i, precision, recall, f1Score);
        }
        System.out.println(consoleMidOption);
    }
}
