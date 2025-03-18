import java.io.*;
import java.util.*;
import java.util.function.*;

public class CSVReader {
    private ArrayList<PhoneUsage> recordsWithMissingVals = new ArrayList<>();

    public ArrayList<PhoneUsage> readOriginalCSV(String filePath) {
        ArrayList<PhoneUsage> phoneUsageList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                try {
                    int userId = Integer.parseInt(fields[0].trim());
                    String deviceModel = fields[1].trim();
                    String operatingSystem = fields[2].trim();
                    int appUsageTime = parseInteger(fields[3].trim());
                    double screenOnTime = parseDouble(fields[4].trim());
                    int batteryDrain = parseInteger(fields[5].trim());
                    int numberOfAppsInstalled = parseInteger(fields[6].trim());
                    int dataUsage = parseInteger(fields[7].trim());
                    int age = parseInteger(fields[8].trim());
                    int gender = parseGender(fields[9].trim());
                    int userBehaviorClass = parseInteger(fields[10].trim());

                    // Check for missing values
                    if (appUsageTime == -1 || screenOnTime == -1 || batteryDrain == -1 ||
                            numberOfAppsInstalled == -1 || dataUsage == -1 || age == -1 || gender == -1) {

                        recordsWithMissingVals.add(new PhoneUsage(userId, deviceModel, operatingSystem,
                                appUsageTime, screenOnTime, "Unknown", batteryDrain, numberOfAppsInstalled,
                                dataUsage, age, gender, userBehaviorClass));
                    } else {
                        phoneUsageList.add(new PhoneUsage(userId, deviceModel, operatingSystem,
                                appUsageTime, screenOnTime, "Unknown", batteryDrain, numberOfAppsInstalled,
                                dataUsage, age, gender, userBehaviorClass));
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        processMissingValues(phoneUsageList);
        return phoneUsageList;
    }

    public ArrayList<PhoneUsage> readSyntheticCSV(String filePath) {
        ArrayList<PhoneUsage> phoneUsageList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine(); // Skip the header
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                try {
                    int userId = Integer.parseInt(fields[0].trim());
                    int gender = parseGender(fields[1].trim());
                    int age = parseInteger(fields[2].trim());
                    int appUsageTime = parseInteger(fields[3].trim());
                    double screenOnTime = parseDouble(fields[4].trim());
                    String preferredAppCategory = fields[5].trim();
                    int userBehaviorClass = parseInteger(fields[6].trim());

                    PhoneUsage usage = new PhoneUsage(userId, "Unknown", "Unknown", appUsageTime, screenOnTime, preferredAppCategory, 0, 0, 0, age, gender, userBehaviorClass);

                    if (gender == -1 || age == -1 || appUsageTime == -1 || screenOnTime == -1) {
                        recordsWithMissingVals.add(usage);
                    } else {
                        phoneUsageList.add(usage);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        processMissingValues(phoneUsageList);
        return phoneUsageList;
    }


    private void processMissingValues(ArrayList<PhoneUsage> completeRecords) {
        if (recordsWithMissingVals.isEmpty()) return;

        int modeGender = findMode(completeRecords, PhoneUsage::getGender);

        double meanAppUsage = findMean(completeRecords, PhoneUsage::getAppUsageTime);
        double meanScreenOnTime = findMean(completeRecords, PhoneUsage::getScreenOnTime);
        double meanBatteryDrain = findMean(completeRecords, PhoneUsage::getBatteryDrain);
        double meanAge = findMean(completeRecords, PhoneUsage::getAge);

        for (PhoneUsage usage : recordsWithMissingVals) {
            if (usage.getGender() == -1) usage.setGender(modeGender);
            if (usage.getAppUsageTime() == -1) usage.setAppUsageTime((int) meanAppUsage);
            if (usage.getScreenOnTime() == -1) usage.setScreenOnTime(meanScreenOnTime);
            if (usage.getBatteryDrain() == -1) usage.setBatteryDrain((int) meanBatteryDrain);
            if (usage.getAge() == -1) usage.setAge((int) meanAge);
            completeRecords.add(usage);
        }

        recordsWithMissingVals.clear();
    }


    private int findMode(ArrayList<PhoneUsage> records, ToIntFunction<PhoneUsage> getter) {
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();

        // Count frequency of each value
        for (PhoneUsage record : records) {
            int currentValue = getter.applyAsInt(record);
            frequencyMap.put(currentValue, frequencyMap.getOrDefault(currentValue, 0) + 1);
        }

        int mode = -1;
        int maxFrequency = -1;

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            int value = entry.getKey();
            int frequency = entry.getValue();

            if (frequency > maxFrequency) {
                maxFrequency = frequency;
                mode = value;
            }
        }

        return mode == -1 ? 1 : mode;
    }

    private double findMean(ArrayList<PhoneUsage> records, ToDoubleFunction<PhoneUsage> getter) {
        return records.stream().mapToDouble(getter).average().orElse(0);
    }


    public int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1; // Mark as missing
        }
    }

    public double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int parseGender(String genderToParse) {
        genderToParse = genderToParse.toLowerCase();
        return switch (genderToParse) {
            case "male" -> 1;
            case "female" -> 2;
            default -> -1;
        };
    }
}
