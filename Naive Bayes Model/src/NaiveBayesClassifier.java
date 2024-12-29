import java.util.*;

public class NaiveBayesClassifier {
    private Map<Integer, Double> priors; // Prior probabilities for each class
    private Map<Integer, Map<String, Double>> categoricalLikelihoods; // For categorical features
    private Map<Integer, Map<String, double[]>> numericalStats; // Mean and stddev for numerical features

    public NaiveBayesClassifier() {
        priors = new HashMap<>();
        categoricalLikelihoods = new HashMap<>();
        numericalStats = new HashMap<>();
    }

    // Training the model
    public void fit(List<PhoneUsage> data) {
        // Step 1: Calculate prior probabilities P(C)
        Map<Integer, Integer> classCounts = new HashMap<>();
        int totalRecords = data.size();

        for (PhoneUsage record : data) {
            int userClass = record.getUserBehaviorClass();
            classCounts.put(userClass, classCounts.getOrDefault(userClass, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : classCounts.entrySet()) {
            priors.put(entry.getKey(), (double) entry.getValue() / totalRecords);
        }

        // Step 2: Calculate likelihoods for categorical and numerical features
        for (int userClass : classCounts.keySet()) {
            Map<String, double[]> classNumericalStats = new HashMap<>();
            Map<String, Double> classCategoricalLikelihoods = new HashMap<>();

            // Separate records by class
            List<PhoneUsage> classRecords = new ArrayList<>();
            for (PhoneUsage record : data) {
                if (record.getUserBehaviorClass() == userClass) {
                    classRecords.add(record);
                }
            }

            // Calculate numerical stats (mean and stddev)
            classNumericalStats.put("AppUsageTime", calculateMeanAndStdDev(classRecords, "AppUsageTime"));
            classNumericalStats.put("ScreenOnTime", calculateMeanAndStdDev(classRecords, "ScreenOnTime"));

            // Calculate categorical likelihoods
            classCategoricalLikelihoods.put("Gender=Male", calculateCategoricalLikelihood(classRecords, "Gender", "Male"));
            classCategoricalLikelihoods.put("Gender=Female", calculateCategoricalLikelihood(classRecords, "Gender", "Female"));

            numericalStats.put(userClass, classNumericalStats);
            categoricalLikelihoods.put(userClass, classCategoricalLikelihoods);
        }
    }

    // Predict the class for a new record
    public int predict(PhoneUsage record) {
        double maxPosterior = Double.NEGATIVE_INFINITY;
        int bestClass = -1;

        for (int userClass : priors.keySet()) {
            // Calculate posterior probability P(C|X)
            double posterior = Math.log(priors.get(userClass));

            // Add numerical likelihoods
            Map<String, double[]> stats = numericalStats.get(userClass);
            posterior += gaussianProbability(record.getAppUsageTime(), stats.get("AppUsageTime"));
            posterior += gaussianProbability(record.getScreenOnTime(), stats.get("ScreenOnTime"));

            // Add categorical likelihoods
            Map<String, Double> classCategoricalLikelihoods = categoricalLikelihoods.get(userClass);
            String genderKey = "Gender=" + record.getGender();
            posterior += Math.log(classCategoricalLikelihoods.getOrDefault(genderKey, 1e-9)); // Small smoothing constant

            if (posterior > maxPosterior) {
                maxPosterior = posterior;
                bestClass = userClass;
            }
        }

        return bestClass;
    }

    // Utility to calculate mean and standard deviation for numerical features
    private double[] calculateMeanAndStdDev(List<PhoneUsage> records, String feature) {
        double sum = 0.0, sumSq = 0.0;
        int n = records.size();

        for (PhoneUsage record : records) {
            double value = feature.equals("AppUsageTime") ? record.getAppUsageTime() : record.getScreenOnTime();
            sum += value;
            sumSq += value * value;
        }

        double mean = sum / n;
        double variance = (sumSq / n) - (mean * mean);
        return new double[]{mean, Math.sqrt(variance)};
    }

    // Utility to calculate categorical likelihoods
    private double calculateCategoricalLikelihood(List<PhoneUsage> records, String feature, String value) {
        int count = 0;
        for (PhoneUsage record : records) {
            if (feature.equals("Gender") && record.getGender().equals(value)) {
                count++;
            }
        }
        return (double) count / records.size();
    }

    // Gaussian probability density function
    private double gaussianProbability(double x, double[] stats) {
        double mean = stats[0];
        double stddev = stats[1];
        double exponent = Math.exp(-((x - mean) * (x - mean)) / (2 * stddev * stddev));
        return (1 / (Math.sqrt(2 * Math.PI) * stddev)) * exponent;
    }
}
