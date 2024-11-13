import java.util.*;

public class Preprocessor {

    public List<PatientRecord> discretizeData(List<PatientRecord> dataset) {
        for (PatientRecord record : dataset) {
            record.age = discretizeAge(Double.parseDouble(record.age));
            record.ejectionFraction = discretizeEjectionFraction(Double.parseDouble(record.ejectionFraction));
            record.sodium = discretizeSodium(Integer.parseInt(record.sodium));
        }
        return dataset;
    }

    public String discretizeAge(double age) {
        // Adjusted age bins to better represent different risks
        if (age < 35) {
            return "Young";
        } else if (age >= 35 && age < 55) {
            return "Middle-aged";
        } else {
            return "Elderly";
        }
    }

    public String discretizeEjectionFraction(double ef) {
        // New bins for more granularity in risk assessment
        if (ef < 20) {
            return "Very Low"; // Highest risk
        } else if (ef < 30) {
            return "Low"; // Still high risk
        } else if (ef <= 45) {
            return "Moderate"; // Medium risk
        } else {
            return "Normal"; // Lowest risk
        }
    }

    public String discretizeSodium(int sodium) {
        // Adjusted sodium bins, based on medical standards
        if (sodium < 135) {
            return "Low";
        } else if (sodium <= 145) {
            return "Normal";
        } else {
            return "High";
        }
    }
}
