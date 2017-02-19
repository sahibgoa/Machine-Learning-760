package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class NaiveBayes {

    HashMap<String, Double> probabilityOfClass;
    HashMap<String, double[][]> featureToProbability;
    public ArrayList<Feature> features;
    public ArrayList<String> classValues;

    /**
     * Constructor for the Naive Bayes net implementation
     * @param classValues List of all class values in the training set
     * @param features List of all the features in the training set
     */
    public NaiveBayes(ArrayList<String> classValues, ArrayList<Feature> features) {
        this.probabilityOfClass = new HashMap<>();
        this.featureToProbability = new HashMap<>();
        this.features = features;
        this.classValues = classValues;
        for (Feature feature: features)
            featureToProbability.put(feature.featureName,
                    new double[feature.allowedValues.size()][classValues.size()]);
    }

    /**
     * Calculates the probability of a given class in the naive bayes net given feature values
     * @param featureList List of features values for the instance
     * @param classValue The class value for which the probability needs to be calculated
     * @return The probability of a class given features values
     */
    public double probabilityClassGivenFeatures(String[] featureList, String classValue) {
        double numerator = probabilityOfClass.get(classValue);
        double denominator = 0.0;
        for (int i = 0; i < featureList.length; i++)
            numerator *= probabilityFeatureGivenClass(features.get(i).featureName, featureList[i], classValue);

        for (String classVal: classValues) {
            double product = probabilityOfClass.get(classVal);
            for (int i = 0; i < featureList.length; i++)
                product *= probabilityFeatureGivenClass(features.get(i).featureName, featureList[i], classVal);
            denominator += product;
        }
        return (numerator/denominator);
    }

    /**
     * Calculates the probability of a feature having a particular value given the class value of the example
     * @param featureName The feature for which the probability is being calculated
     * @param featureValue The value of that feature
     * @param classValue The class value of the example based on which the probability is being calculated
     * @return The probability of the feature having that feature value given the class
     */
    public double probabilityFeatureGivenClass(String featureName, String featureValue, String classValue) {
        int i = -1, j = -1;
        for (Feature feature: features) {
            if (featureName.equals(feature.featureName)) {
                i = feature.allowedValues.indexOf(featureValue);
                j = classValues.indexOf(classValue);
            }
        }
        if (i == -1)
            return -1.0;
        return featureToProbability.get(featureName)[i][j];
    }

    /**
     * Returns the total probability of an example having the given class value
     * @param classValue The class value whose probability is being considered
     * @return The total probability of the class value
     */
    public double probabilityClass(String classValue) {
        return probabilityOfClass.get(classValue);
    }

    /**
     * Trains the given Naive Bayes net with the instances passed to it.
     * Calculates all conditional and class probabilities and stores them in the data structures of
     * the current object.
     * @param instances The instances to train the naive bayes net on
     */
    public void train(ArrayList<Instance> instances) {

        // Stores the total number of values that can be stored in the CPT
        int totalCount = classValues.size() * features.size();
        for (int i = 0; i < features.size(); i++)
            totalCount *= features.get(i).allowedValues.size();

        // Initialize all class probabilities to zero
        for (int i = 0; i < bayes.classValues.size(); i++)
            probabilityOfClass.put(classValues.get(i), 0.0);

        // For each instance, initializes the probability of each class and the conditional
        // probabilities for a feature given class
        for (Instance instance: instances) {

            // Add the total number of each class occurrence
            probabilityOfClass.put(instance.classValue, probabilityOfClass.get(instance.classValue) + 1.0);

            // Populate feature to probability tables with feature to number of occurrences
            for (int i = 0; i < instance.features.length; i++) {
                double[][] conditionalProbabilities =
                        featureToProbability.get(features.get(i).featureName);
                conditionalProbabilities[features.get(i).allowedValues.indexOf(instance.features[i])][classValues.indexOf(instance.classValue)]++;
                featureToProbability.put(features.get(i).featureName, conditionalProbabilities);
            }

        }

        /* Add pseudo counts to the CPTs
        for(Feature feature: bayes.features)
            for (int j = 0; j < bayes.classValues.size(); j++)
                for (int k = 0; k < feature.allowedValues.size(); k++) {
                    double[][] conditionalProbabilities;
                    conditionalProbabilities = featureToProbability.get(feature.featureName);
                    conditionalProbabilities[k][j]++;
                    featureToProbability.put(feature.featureName, conditionalProbabilities);
                }
        */

        for (Feature feature: features) {
            double[][] conditionalProbabilities = featureToProbability.get(feature.featureName);
            double sum[] = new double[conditionalProbabilities.length];
            for (int i = 0; i < conditionalProbabilities.length; i++) {
                for (int j = 0; j < conditionalProbabilities[i].length; j++) {
                    // P(X=x|Y) = [(# of X=x) + 1] / [(# of Y) + (# of X)]
                    conditionalProbabilities[i][j] = (conditionalProbabilities[i][j] + 1) /
                            (probabilityOfClass.get(classValues.get(j))
                                    + conditionalProbabilities.length);
                    //sum[i] += conditionalProbabilities[i][j];
                }
            }
            /* Normalize all values so that sum of P(featureValues | classValue = c) = 1
            for (int i = 0; i < conditionalProbabilities.length; i++)
                for (int j = 0; j < conditionalProbabilities[i].length; j++)
                    conditionalProbabilities[i][j] /= sum[i];*/
        }

        int numFeaturesValues = totalCount / bayes.classValues.size();
        for (String classValue: classValues)
            probabilityOfClass.put(classValue, probabilityOfClass.get(classValue) / (double)instances.size());

    }

}
