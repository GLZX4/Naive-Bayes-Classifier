import java.io.*;
import java.util.*;

public class CSVReader {
    public ArrayList<PhoneUsage> readCSV(String filePath) {
        ArrayList<PhoneUsage> phoneUsageList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip the header
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                // Parse each field to its respective type
                int userId = Integer.parseInt(fields[0].trim());
                String deviceModel = fields[1].trim();
                String operatingSystem = fields[2].trim();
                int appUsageTime = Integer.parseInt(fields[3].trim());
                double screenOnTime = Double.parseDouble(fields[4].trim());
                int batteryDrain = Integer.parseInt(fields[5].trim());
                int numberOfAppsInstalled = Integer.parseInt(fields[6].trim());
                int dataUsage = Integer.parseInt(fields[7].trim());
                int age = Integer.parseInt(fields[8].trim());
                String gender = fields[9].trim();
                int userBehaviorClass = Integer.parseInt(fields[10].trim());

                PhoneUsage usage = new PhoneUsage(userId, deviceModel, operatingSystem, appUsageTime,
                        screenOnTime, batteryDrain, numberOfAppsInstalled,
                        dataUsage, age, gender, userBehaviorClass);
                phoneUsageList.add(usage);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return phoneUsageList;
    }
}
