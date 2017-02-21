package com.company;

import java.util.ArrayList;

abstract class BayesNetwork {

    abstract double probabilityClassGivenFeatures(String[] featureList, String classValue);

    abstract void train(ArrayList<Instance> instances);

}
