import java.io.*;
import java.util.*;

public class CSVReader {
    public ArrayList<PhoneUsage> readOriginalCSV(String filePath) {
        ArrayList<PhoneUsage> phoneUsageList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine(); // Skip the header
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                try {
                    int userId = Integer.parseInt(fields[0].trim());
                    String deviceModel = fields[1].trim();
                    String operatingSystem = fields[2].trim();
                    int appUsageTime = Integer.parseInt(fields[3].trim());
                    double screenOnTime = Double.parseDouble(fields[4].trim());
                    int batteryDrain = Integer.parseInt(fields[5].trim());
                    int numberOfAppsInstalled = Integer.parseInt(fields[6].trim());
                    int dataUsage = Integer.parseInt(fields[7].trim());
                    int age = Integer.parseInt(fields[8].trim());
                    int gender = parseGender(fields[9].trim());
                    int userBehaviorClass = Integer.parseInt(fields[10].trim());

                    PhoneUsage usage = new PhoneUsage(userId, deviceModel, operatingSystem, appUsageTime,
                            screenOnTime, "Unknown", batteryDrain, numberOfAppsInstalled,
                            dataUsage, age, gender, userBehaviorClass);

                    phoneUsageList.add(usage);
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
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
                    String gender = fields[1].trim();
                    int age = Integer.parseInt(fields[2].trim());
                    int appUsageTime = Integer.parseInt(fields[3].trim());
                    double screenOnTime = Double.parseDouble(fields[4].trim());
                    String preferredAppCategory = fields[5].trim();
                    int userBehaviorClass = Integer.parseInt(fields[6].trim());

                    PhoneUsage usage = new PhoneUsage(userId, "Unknown", "Unknown", appUsageTime, screenOnTime, preferredAppCategory, 0, 0, 0, age, parseGender(gender), userBehaviorClass);

                    phoneUsageList.add(usage);
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return phoneUsageList;
    }

    public int parseGender(String genderToParse) {
        genderToParse = genderToParse.toLowerCase();
        int gender = -1;
        switch (genderToParse) {
            case "male", "Male" -> gender = 1;
            case "female" -> gender = 2;
            default ->
                System.err.println("Invalid gender: " + genderToParse);
        }
        return gender;
    }
}
