import java.util.ArrayList;

/**
 * This class contains the name of the features and all allowed values (if feature is discrete)
 */

class Feature {

    String featureName;
    ArrayList<String> allowedValues;

    Feature(String featureName, ArrayList<String> allowedValues) {
        this.featureName = featureName;
        this.allowedValues = allowedValues;
    }

}
