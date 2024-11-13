import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaiveBayesClassifier {

    private Map<Integer, Integer> classCounts; // Count of each class (deathEvent = 0 or 1)
    private Map<String, Map<String, Integer>> attributeCounts; // Conditional counts per attribute value and class
    private int totalRecords; // Total number of records

    public NaiveBayesClassifier() {
        this.classCounts = new HashMap<>();
        this.attributeCounts = new HashMap<>();
    }

    public void train(List<PatientRecord> dataset) {
        totalRecords = dataset.size();

        for (PatientRecord record : dataset) {
            int classLabel = record.deathEvent;

            // Update class counts
            classCounts.put(classLabel, classCounts.getOrDefault(classLabel, 0) + 1);

            // Update attribute counts for each attribute
            updateAttributeCounts("age", discretizeAge(Double.parseDouble(record.age)), classLabel);
            updateAttributeCounts("gender", record.sex.toString(), classLabel);
            updateAttributeCounts("anaemia", String.valueOf(record.anaemia), classLabel);
            updateAttributeCounts("diabetes", String.valueOf(record.diabetes), classLabel);
            updateAttributeCounts("smoking", String.valueOf(record.smoking), classLabel);
            updateAttributeCounts("ejectionFraction", discretizeEjectionFraction(Double.parseDouble(record.ejectionFraction)), classLabel);
            updateAttributeCounts("highBloodPressure", String.valueOf(record.highBloodPressure), classLabel);
            updateAttributeCounts("sodium", discretizeSodium(Integer.parseInt(record.sodium)), classLabel);
        }
        printModel();
    }

    private void updateAttributeCounts(String attributeName, String attributeValue, int classLabel) {
        String key = attributeName + ":" + attributeValue;

        // Initialize the attribute counts map if not present
        attributeCounts.putIfAbsent(key, new HashMap<>());
        Map<String, Integer> valueCounts = attributeCounts.get(key);

        // Update counts for the current class
        String classKey = String.valueOf(classLabel);
        valueCounts.put(classKey, valueCounts.getOrDefault(classKey, 0) + 1);
    }

    public String predict(PatientRecord newRecord) {
        double maxProbability = -1.0;
        String predictedClass = null;

        // Calculate the probability for each class (deathEvent = 0 or 1)
        for (Integer classLabel : classCounts.keySet()) {
            double classProbability = calculateClassProbability(newRecord, classLabel);
            if (classProbability > maxProbability) {
                maxProbability = classProbability;
                predictedClass = String.valueOf(classLabel);
            }
        }
        return predictedClass;
    }

    private double calculateClassProbability(PatientRecord record, int classLabel) {
        double weight = (classLabel == 1) ? 3.0 : 1.0;  // Give higher weight to class 1
        double probability = ((double) classCounts.get(classLabel) / totalRecords) * weight;
        System.out.println("Initial Probability for Class " + classLabel + " (with weighting): " + probability);

        // Multiply with each attribute's conditional probability
        probability *= printAndGetConditionalProbability("age", record.age, classLabel);
        probability *= printAndGetConditionalProbability("gender", record.sex.toString(), classLabel);
        probability *= printAndGetConditionalProbability("anaemia", String.valueOf(record.anaemia), classLabel);
        probability *= printAndGetConditionalProbability("diabetes", String.valueOf(record.diabetes), classLabel);
        probability *= printAndGetConditionalProbability("smoking", String.valueOf(record.smoking), classLabel);
        probability *= printAndGetConditionalProbability("ejectionFraction", record.ejectionFraction, classLabel);
        probability *= printAndGetConditionalProbability("highBloodPressure", String.valueOf(record.highBloodPressure), classLabel);
        probability *= printAndGetConditionalProbability("sodium", record.sodium, classLabel);

        return probability;
    }


    private double printAndGetConditionalProbability(String attributeName, String attributeValue, int classLabel) {
        double probability = getConditionalProbability(attributeName, attributeValue, classLabel);
        System.out.println("Conditional Probability of " + attributeName + " = " + attributeValue +
                " for Class " + classLabel + ": " + probability);
        return probability;
    }

    private double getConditionalProbability(String attributeName, String attributeValue, int classLabel) {
        String key = attributeName + ":" + attributeValue;
        Map<String, Integer> valueCounts = attributeCounts.get(key);

        if (valueCounts == null || !valueCounts.containsKey(String.valueOf(classLabel))) {
            // Laplace smoothing: if no counts found, assume 1 occurrence
            return 1.0 / (classCounts.get(classLabel) + 1);
        }

        int count = valueCounts.get(String.valueOf(classLabel));
        return (double) count / classCounts.get(classLabel);
    }

    private String discretizeAge(double age) {
        if (age < 40) return "Young";
        if (age <= 60) return "Middle-aged";
        if (age <= 75) return "Old";
        return "Very Old";
    }

    private String discretizeEjectionFraction(double ef) {
        if (ef < 20) return "Very Low";
        if (ef < 30) return "Low";
        if (ef <= 45) return "Moderate";
        return "Normal";
    }

    private String discretizeSodium(int sodium) {
        if (sodium < 135) return "Low";
        if (sodium <= 145) return "Normal";
        return "High";
    }

    public void printModel() {
        System.out.println("Class counts: " + classCounts);
        System.out.println("Attribute counts: " + attributeCounts);
    }
}
