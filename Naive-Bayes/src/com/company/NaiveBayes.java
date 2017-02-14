package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class NaiveBayes {

    private HashMap<String, Double> probabilityOfClass;
    private HashMap<String, Double[][]> featureToProbability;
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
                    new Double[feature.allowedValues.size()][classValues.size()]);
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
            double product = 1.0;
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
        for (Instance instance: instances) {

            // Calculate the probability of each class
            if (probabilityOfClass.containsKey(instance.classValue))
                probabilityOfClass.put(instance.classValue,
                        probabilityOfClass.get(instance.classValue) + 1.0/(double)instances.size());
            else
                probabilityOfClass.put(instance.classValue, 1.0/(double)instances.size());

            // Populate feature to probability tables with feature to no. of occurrences
            for (int i = 0; i < instance.features.length; i++) {
                Double[][] conditionalProbabilities;
                if (featureToProbability.containsKey(features.get(i).featureName)) {
                    conditionalProbabilities = featureToProbability.get(features.get(i).featureName);
                    conditionalProbabilities[features.get(i).allowedValues.indexOf(instance.features[i])][classValues.indexOf(instance.classValue)]++;
                    featureToProbability.put(features.get(i).featureName, conditionalProbabilities);
                }
            }

        }

        for (Feature feature: features) {
            Double[][] conditionalProbabilities = featureToProbability.get(feature.featureName);
            for (int i = 0; i < conditionalProbabilities.length; i++) {
                for (int j = 0; j < conditionalProbabilities[i].length; j++) {
                    conditionalProbabilities[i][j] /= (probabilityOfClass.get(classValues.get(j)) * instances.size());
                }
            }
        }
    }

}
