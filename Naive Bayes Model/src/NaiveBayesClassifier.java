import java.util.*;

public class NaiveBayesClassifier {
    private Map<Integer, Double> priors;
    private Map<Integer, Map<String, Double>> categoricalLikelihoods;
    private Map<Integer, Map<String, double[]>> numericalStats;
    private Map<Integer, String> behaviorToDevice;

    public NaiveBayesClassifier() {
        priors = new HashMap<>();
        categoricalLikelihoods = new HashMap<>();
        numericalStats = new HashMap<>();
        behaviorToDevice = new HashMap<>();
    }

    // Training the model, a bit slow but works as far as i can tell
    public void fit(List<PhoneUsage> data) {

        Map<Integer, Integer> classCounts = new HashMap<>();
        int totalRecords = data.size();

        for (PhoneUsage record : data) {
            int userClass = record.getUserBehaviorClass();
            classCounts.put(userClass, classCounts.getOrDefault(userClass, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : classCounts.entrySet()) {
            priors.put(entry.getKey(), (double) entry.getValue() / totalRecords);
        }

        for (int userClass : classCounts.keySet()) {
            Map<String, double[]> classNumericalStats = new HashMap<>();
            Map<String, Double> classCategoricalLikelihoods = new HashMap<>();

            List<PhoneUsage> classRecords = new ArrayList<>();
            for (PhoneUsage record : data) {
                if (record.getUserBehaviorClass() == userClass) {
                    classRecords.add(record);
                }
            }

            // Calculate the numerical stats by mean and stddev
            classNumericalStats.put("AppUsageTime", calculateMeanAndStdDev(classRecords, "AppUsageTime"));
            classNumericalStats.put("ScreenOnTime", calculateMeanAndStdDev(classRecords, "ScreenOnTime"));

            // Calculate categorical likelihoods
            classCategoricalLikelihoods.put("Gender=1", calculateCategoricalLikelihood(classRecords, "Gender", 1));
            classCategoricalLikelihoods.put("Gender=2", calculateCategoricalLikelihood(classRecords, "Gender", 2));

            numericalStats.put(userClass, classNumericalStats);
            categoricalLikelihoods.put(userClass, classCategoricalLikelihoods);
            calculateDeviceRecommendations(data);
        }
    }

    public int predict(PhoneUsage record) {
        double maxPosterior = Double.NEGATIVE_INFINITY;
        int bestClass = -1;

        for (int userClass : priors.keySet()) {
            // Calculate posterior probability P(C|X)
            double posterior = Math.log(priors.get(userClass));

            // Adding numerical likelihoods
            Map<String, double[]> stats = numericalStats.get(userClass);
            posterior += gaussianProbability(record.getAppUsageTime(), stats.get("AppUsageTime"));
            posterior += gaussianProbability(record.getScreenOnTime(), stats.get("ScreenOnTime"));

            // Adding categorical likelihoods
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


    // Calculate recommended device based on specific Behaviour Class
    private void calculateDeviceRecommendations(List<PhoneUsage> trainData) {
        // Map to count device occurrences for each behavior class
        Map<Integer, Map<String, Integer>> deviceCounts = new HashMap<>();

        for (PhoneUsage record : trainData) {
            int behaviorClass = record.getUserBehaviorClass();
            String deviceModel = record.getDeviceModel();

            deviceCounts.putIfAbsent(behaviorClass, new HashMap<>());
            Map<String, Integer> deviceMap = deviceCounts.get(behaviorClass);
            deviceMap.put(deviceModel, deviceMap.getOrDefault(deviceModel, 0) + 1);
        }

        for (Map.Entry<Integer, Map<String, Integer>> entry : deviceCounts.entrySet()) {
            int behaviorClass = entry.getKey();
            Map<String, Integer> devices = entry.getValue();

            String recommendedDevice = devices.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();

            behaviorToDevice.put(behaviorClass, recommendedDevice);
        }
    }

    public String recommendDevice(int behaviorClass) {
        return behaviorToDevice.getOrDefault(behaviorClass, "No recommendation available");
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
    private double calculateCategoricalLikelihood(List<PhoneUsage> records, String feature, int value) {
        int count = 0;
        for (PhoneUsage record : records) {
            if (feature.equals("Gender") && record.getGender() == value) {
                count++;
            }
        }
        return (double) count / records.size();
    }

    // Gaussian probability density function
    // Code Below Adapted from: https://introcs.cs.princeton.edu/java/22library/Gaussian.java.html
    private double gaussianProbability(double x, double[] stats) {
        double mean = stats[0];
        double stddev = stats[1];
        double exponent = Math.exp(-((x - mean) * (x - mean)) / (2 * stddev * stddev));
        return (1 / (Math.sqrt(2 * Math.PI) * stddev)) * exponent;
    }
}
