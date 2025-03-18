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

        System.out.println("Would You like to integrate synthetic data? ");
        System.out.println("Type: Yes/yes/y OR No/no/n");
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
        calculateStatistics(classifier, testData);

        userPredictNewRecordDecision(classifier, scanner);
    }

    public static void runModelSyntheticData(String filepath, String syntheticPath, CSVReader csvReader, NaiveBayesClassifier classifier, Scanner scanner) {
        ArrayList<PhoneUsage> realData = csvReader.readOriginalCSV(filepath);
        ArrayList<PhoneUsage> syntheticData = csvReader.readSyntheticCSV(syntheticPath);
        ArrayList<PhoneUsage> combinedData = new ArrayList<>(realData);

        combinedData.addAll(syntheticData);
        ArrayList<PhoneUsage> testData = trainModel(combinedData, classifier);

        calculateAndPrintAccuracy(classifier, testData);
        calculateStatistics(classifier, testData);

        userPredictNewRecordDecision(classifier, scanner);
    }

    public static ArrayList<PhoneUsage> trainModel(ArrayList<PhoneUsage> dataset, NaiveBayesClassifier classifier) {
        // get the number of occurrences of the dataset and take 80% of them from the front.
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

        int predictedClass = classifier.predict(newRecord);
        System.out.println("Predicted User Behavior Class: " + predictedClass);

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
            default -> System.out.println("Incorrect Entry");
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

    public static void calculateStatistics(NaiveBayesClassifier classifier, ArrayList<PhoneUsage> testData) {
        int[] truePositive = new int[6];
        int[] falsePositive = new int[6];
        int[] falseNegative = new int[6];
        int[] totalActual = new int[6];

        int totalRecords = testData.size();

        for (PhoneUsage record : testData) {
            int actual = record.getUserBehaviorClass();
            int predicted = classifier.predict(record);

            totalActual[actual]++;

            if (predicted == actual) {
                truePositive[actual]++;
            } else {
                falsePositive[predicted]++;
                falseNegative[actual]++;
            }
        }
        // console spacing adapted from: https://stackoverflow.com/questions/53842078/how-to-add-spacing-to-console-output

        System.out.printf("%-15s %-10s %-10s %-10s %-10s %-10s%n", "User Class", "Precision", "Recall", "F1-Score", "Support", "Lift");

        for (int i = 1; i <= 5; i++) {
            // ignoring dead entries
            if (totalActual[i] == 0) continue;

            // calculating statistics induced from: https://www.picsellia.com/post/understanding-the-f1-score-in-machine-learning-the-harmonic-mean-of-precision-and-recall#:~:text=email%20as%20spam.-,Recall:,instances%20wrongly%20predicted%20as%20negative.
            double precision = truePositive[i] / (double) (truePositive[i] + falsePositive[i]);
            double recall = truePositive[i] / (double) (truePositive[i] + falseNegative[i]);
            double f1Score = 2 * (precision * recall) / (precision + recall);

            double support = totalActual[i] / (double) totalRecords;
            double baselinePrecision = support;
            double lift = precision / baselinePrecision;

            System.out.printf("%-15d %-10.2f %-10.2f %-10.2f %-10.2f %-10.2f%n", i, precision, recall, f1Score, support * 100, lift);
        }
        System.out.println(consoleMidOption);
    }
}
