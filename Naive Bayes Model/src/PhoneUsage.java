public class PhoneUsage {
    private int userId;
    private String deviceModel;
    private String operatingSystem;
    private int appUsageTime;
    private double screenOnTime;
    private int batteryDrain;
    private int numOfAppsInstalled;
    private String PreferredAppCategory;
    private int dataUsage;
    private int age;
    private int gender;
    private int userBehaviorClass;

    public PhoneUsage(int userId, String deviceModel, String operatingSystem, int appUsageTime,
                      double screenOnTime, String PreferredAppCategory, int batteryDrain, int numberOfAppsInstalled,
                      int dataUsage, int age, int gender, int userBehaviorClass) {
        this.userId = userId;
        this.deviceModel = deviceModel;
        this.operatingSystem = operatingSystem;
        this.appUsageTime = appUsageTime;
        this.screenOnTime = screenOnTime;
        this.batteryDrain = batteryDrain;
        this.numOfAppsInstalled = numberOfAppsInstalled;
        this.PreferredAppCategory = PreferredAppCategory;
        this.dataUsage = dataUsage;
        this.age = age;
        this.gender = gender;
        this.userBehaviorClass = userBehaviorClass;
    }

    // Only Getters to be used as each record should be immutable once created.
    // Any Modifications or cleaning should be done in CSVReader.

    public int getUserId() {
        return userId;
    }
    public String getDeviceModel() {
        return deviceModel;
    }
    public String getOperatingSystem() {
        return operatingSystem;
    }
    public int getAppUsageTime() {
        return appUsageTime;
    }
    public double getScreenOnTime() {
        return screenOnTime;
    }
    public String getPreferredAppCategory() {
        return PreferredAppCategory;
    }
    public int getBatteryDrain() {
        return batteryDrain;
    }
    public int getNumOfAppsInstalled() {
        return numOfAppsInstalled;
    }
    public int getDataUsage() {
        return dataUsage;
    }
    public int getAge() {
        return age;
    }
    public int getGender() {
        return gender;
    }
    public int getUserBehaviorClass() {
        return userBehaviorClass;
    }
}