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

    // Training the model, a bit slow but works
    //code adapted below from:
    // https://github.com/namsor/Java-Naive-Bayes-Classifier-JNBC/tree/master
    // https://stackoverflow.com/questions/10059594/a-simple-explanation-of-naive-bayes-classification
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

            classNumericalStats.put("AppUsageTime", calculateMeanAndStdDev(classRecords, "AppUsageTime"));
            classNumericalStats.put("ScreenOnTime", calculateMeanAndStdDev(classRecords, "ScreenOnTime"));

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

            Map<String, double[]> stats = numericalStats.get(userClass);
            posterior += gaussianProbability(record.getAppUsageTime(), stats.get("AppUsageTime"));
            posterior += gaussianProbability(record.getScreenOnTime(), stats.get("ScreenOnTime"));

            Map<String, Double> classCategoricalLikelihoods = categoricalLikelihoods.get(userClass);
            String genderKey = "Gender=" + record.getGender();
            // some Laplace smoothing, else defaults to scientific notation of 1/1billionth to still provide a value.
            //otherwise the classifier just crashes.
            posterior += Math.log(classCategoricalLikelihoods.getOrDefault(genderKey, 1e-9));

            if (posterior > maxPosterior) {
                maxPosterior = posterior;
                bestClass = userClass;
            }
        }
        return bestClass;
    }


    //helper method
    private void calculateDeviceRecommendations(List<PhoneUsage> trainData) {
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

            // code below adapted from: https://stackoverflow.com/questions/16246821/how-to-get-values-and-keys-from-hashmap

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

    //code below adapted from: https://www.baeldung.com/java-calculate-standard-deviation

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

    private double calculateCategoricalLikelihood(List<PhoneUsage> records, String feature, int value) {
        int count = 0;
        for (PhoneUsage record : records) {
            if (feature.equals("Gender") && record.getGender() == value) {
                count++;
            }
        }
        return (double) count / records.size();
    }

    // Code Below Adapted from:
    // https://introcs.cs.princeton.edu/java/22library/Gaussian.java.html
    // https://www.baeldung.com/java-calculate-standard-deviation
    private double gaussianProbability(double x, double[] stats) {
        double mean = stats[0];
        double stddev = stats[1];

        double variance = stddev * stddev;
        double exponentNumerator = (x - mean) * (x - mean);
        double exponentDenominator = 2 * variance;

        double exponent = Math.exp(-(exponentNumerator / exponentDenominator));
        double gaussianFactor = 1 / (Math.sqrt(2 * Math.PI) * stddev);

        return gaussianFactor * exponent;
    }
}