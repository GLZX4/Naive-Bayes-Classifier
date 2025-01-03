import java.io.*;
import java.util.*;

public class CSVReader {
    public ArrayList<PhoneUsage> readCSV(String filePath) {
        ArrayList<PhoneUsage> phoneUsageList = new ArrayList<>();

        //CSV reading code adapted from: https://www.baeldung.com/java-csv-file-array
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip the header
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

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
                        screenOnTime, batteryDrain, numberOfAppsInstalled,
                        dataUsage, age, gender, userBehaviorClass);
                phoneUsageList.add(usage);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return phoneUsageList;
    }

    public int parseGender(String genderToParse) {
        genderToParse = genderToParse.toLowerCase();
        int gender = 0;
        switch (genderToParse) {
            case "male" -> gender = 1;
            case "female" -> gender = 0;
            default -> System.out.println("None valid Gender input");
        }
        return gender;
    }
}
