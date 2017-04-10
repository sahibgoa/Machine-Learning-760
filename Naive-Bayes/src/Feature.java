import java.util.ArrayList;

public class Feature {

    String featureName;
    ArrayList<String> allowedValues;

    Feature(String featureName, ArrayList<String> allowedValues) {
        this.featureName = featureName;
        this.allowedValues = allowedValues;
    }

}
