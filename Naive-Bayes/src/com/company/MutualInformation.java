package com.company;

import java.util.Comparator;

public class MutualInformation {

    public Feature featureX;
    public Feature featureY;
    public double mutualInformation;

    public static Comparator<MutualInformation> comparator = (o1, o2) -> (o1.mutualInformation > o2.mutualInformation ? 1 : -1);

    /**
     * Constructor for the mutual information class
     * @param featureX The first feature (where the edge begins)
     * @param featureY The second feature (where the edge points)
     */
    MutualInformation(Feature featureX, Feature featureY) {
        this.featureX = featureX;
        this.featureY = featureY;
    }

}
