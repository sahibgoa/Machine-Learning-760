package com.company;

import java.util.ArrayList;

class Feature {

    String featureName;
    ArrayList<String> allowedValues;

    Feature(String featureName, ArrayList<String> allowedValues) {
        this.featureName = featureName;
        this.allowedValues = allowedValues;
    }

}
