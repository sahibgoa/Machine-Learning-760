package com.company;

import java.util.ArrayList;

/**
 * Created by Sahib Pandori on 2/8/2017.
 */
public class Feature {

    public String featureName;
    public ArrayList<String> allowedValues;

    public Feature(String featureName, ArrayList<String> allowedValues) {
        this.featureName = featureName;
        this.allowedValues = allowedValues;
    }

}
