package com.company;

import java.util.ArrayList;

/**
 * Created by Sahib Pandori on 2/19/2017.
 */
public class FeaturePair {

    Feature featureX;
    Feature featureY;
    double[][][] prXY;

    FeaturePair (Feature featureX, Feature featureY) {
        this.featureX = featureX;
        this.featureY = featureY;
        this.prXY = new double[featureX.allowedValues.size()][featureY.allowedValues.size()][bayes.classValues.size()];

        /*for (int i = 0; i < featureX.allowedValues.size(); i++) {
            for (int j = 0; j < featureY.allowedValues.size(); j++) {
                for (int k = 0; k < bayes.classValues.size(); k++) {
                    this.prXY[i][j][k] = 1;
                }
            }
        }*/
    }

}
