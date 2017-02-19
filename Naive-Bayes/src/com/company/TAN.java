package com.company;

import java.util.ArrayList;
import java.util.Map;

public class TAN {

    ArrayList<Feature> features;
    ArrayList<String> classValues;
    ArrayList<Instance> instances;
    public static ArrayList<MutualInformation> allInformation;
    NaiveBayes naiveBayes;

    /**
     * Constructor for the Tree Augmented Naive Bayes
     * @param classValues List of possible class values
     * @param features List of possible features
     */
    TAN(ArrayList<String> classValues, ArrayList<Feature> features) {
        this.features = features;
        this.classValues = classValues;
        allInformation = new ArrayList<>();
    }

    /**
     * Computes the conditional mutual information between pairs of features for the construction of
     * the maximum spanning tree (using Prim's algorithm)
     */
    public void computeMutualInformation(Feature featureX, Feature featureY, MutualInformation mi) {
        double mutualInformation = 0.0;
        for (String classValue: classValues) {
            for (String featureXValue: featureX.allowedValues) {
                for (String featureYValue: featureY.allowedValues) {
                    double pr = probabilityFeaturesGivenClass(featureX.featureName, featureXValue,
                                featureY.featureName, featureYValue, classValue);
                    mutualInformation += pr * naiveBayes.probabilityOfClass.get(classValue)
                                    * Math.log(pr
                                    / (naiveBayes.probabilityFeatureGivenClass(featureX.featureName,
                                        featureXValue, classValue)
                                    + naiveBayes.probabilityFeatureGivenClass(featureY.featureName,
                                        featureYValue, classValue))
                                    );
                }
            }
        }
        mi.mutualInformation = mutualInformation;
    }

    /**
     * TODO Creates a spanning tree using mutual information between pairs of features
     */
    
    public void createSpanningTree() {

        ArrayList<MutualInformation> spanningTreeEdges = new ArrayList<>(features.size() - 1);
        MutualInformation mi;
        // Find the mutual information for all sets of edges
        for (Feature featureX: features) {
            for (Feature featureY : features) {
                mi = new MutualInformation(featureX, featureY);
                this.computeMutualInformation(featureX, featureY, mi);
                allInformation.add(mi);
//                if (mi.mutualInformation > spanningTreeEdges.get(features.size() - 1).mutualInformation) {
//                    spanningTreeEdges.remove(features.size() - 1);
//                    spanningTreeEdges.add(mi);
//                    spanningTreeEdges.sort(MutualInformation.comparator);
//                }
            }
        }

        // Create graph where each node is a feature
        ArrayList<Node> nodes = new ArrayList<>();
        for (Feature feature: features)
            nodes.add(new Node(feature));

        // Prim's algorithm
        for (Node node: nodes)
            for (Node node1: nodes)
                if (node != node1)
                    node.children.add(node1);

        while (spanningTreeEdges.size() < features.size() - 1) {

        }


    }

    /**
     * Calculates the probability of a given class in the naive bayes net given feature values
     * @param featureList List of features values for the instance
     * @param classValue The class value for which the probability needs to be calculated
     * @return The probability of a class given features values
     */
    public double probabilityClassGivenFeatures(String[] featureList, String classValue) {
        return 1.0;
    }

    /**
     * Calculates the probability of a feature having a particular value given the class value of the example
     * @param featureName1 The features for which the probability is being calculated
     * @param featureName2 The features for which the probability is being calculated
     * @param featureValue1 The value of those features
     * @param featureValue2 The value of those features
     * @param classValue The class value of the example based on which the probability is being calculated
     * @return The probability of the feature having those feature values given the class
     */
    public double probabilityFeaturesGivenClass(String featureName1, String featureValue1,
                                                String featureName2, String featureValue2,
                                                String classValue) {
        int conditionalFeatureCount = 0, classCount = 0, allowedValues1 = 0, allowedValues2 = 0;
        for (Instance instance: instances) {
            if (instance.classValue.equals(classValue)) {
                boolean firstFound = false, secondFound = false;
                for (int i = 0; i < instance.features.length; i++) {
                    if (featureName1.equals(features.get(i).featureName) && featureValue1.equals(instance.features[i])) {
                        firstFound = true;
                        allowedValues1 = features.get(i).allowedValues.size();
                    }
                    else if (featureName2.equals(features.get(i).featureName) && featureValue2.equals(instance.features[i])) {
                        secondFound = true;
                        allowedValues2 = features.get(i).allowedValues.size();
                    }
                    else if (firstFound && secondFound) {
                        conditionalFeatureCount++;
                        break;
                    }
                }
                classCount++;
            }
        }
        return (double) (conditionalFeatureCount + 1)
                / (double) (classCount + (allowedValues1 * allowedValues2));
    }

    /**
     * Trains the given Tree Augmented - Naive Bayes net with the instances passed to it.
     * Calculates all conditional and class probabilities and stores them in the data structures of
     * the current object.
     * @param instances The instances to train the naive bayes net on
     */
    public void train(ArrayList<Instance> instances) {
        this.instances = instances;
        naiveBayes = new NaiveBayes(classValues, features);
        naiveBayes.train(instances);


    }

}
