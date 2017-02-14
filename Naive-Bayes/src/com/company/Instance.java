package com.company;

/**
 * Created by Sahib Pandori on 2/7/2017.
 */
public class Instance {

    public String[] features;
    public String classValue;

    public Instance(String[] values) {
        features = new String[values.length - 1];
        for (int i = 0; i < values.length - 1; i++) {
            this.features[i] = values[i];
        }
        this.classValue = values[values.length - 1];
    }

}
