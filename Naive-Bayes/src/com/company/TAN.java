package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TAN {

    ArrayList<Feature> features;
    ArrayList<String> classValues;
    ArrayList<Instance> instances;
    NaiveBayes naiveBayes;
    static ArrayList<Node> spanningTreeEdges;
    static HashMap<Feature, ArrayList<FeaturePair>> featureToFeaturePairs;
    double[][] CPT;
    double[][][][][] X1X2YTable;

    /**
     * Constructor for the Tree Augmented Naive Bayes
     * @param classValues List of possible class values
     * @param features List of possible features
     */
    TAN(ArrayList<String> classValues, ArrayList<Feature> features) {
        this.features = features;
        this.classValues = classValues;
    }

    /**
     * Find the mutual information for all sets of edges and returns the table containing all values
     */
    public double[][] computeMutualInformation() {
        double[][] mutualInformation = new double[features.size()][features.size()];
        for (int i = 0; i < features.size(); i++) {
            for (int j = i + 1; j < features.size(); j++) {
                double[][] x1y = new double[features.get(i).allowedValues.size()][classValues.size()];
                double[][] x2y = new double[features.get(j).allowedValues.size()][classValues.size()];
                double[][][] x1x2y = new double[features.get(i).allowedValues.size()][features.get(j).allowedValues.size()][classValues.size()];

                for (Instance instance: instances) {
                    int x1 = features.get(i).allowedValues.indexOf(instance.features[i]);
                    int x2 = features.get(j).allowedValues.indexOf(instance.features[j]);
                    int y = classValues.indexOf(instance.classValue);
                    x1x2y[x1][x2][y]++;
                    x1y[x1][y]++;
                    x2y[x2][y]++;
                }

                double mi = 0.0;
                for (int k = 0; k < x1y.length; k++) {
                    for (int l = 0; l < x2y.length; l++) {
                        for (int m = 0; m < classValues.size(); m++) {

                            double px1x2y = (x1x2y[k][l][m] + 1.0) / (instances.size() + (x1y.length * x2y.length * classValues.size()));
                            double px1x2GivenY = (x1x2y[k][l][m] + 1.0) / (naiveBayes.frequencyOfClass.get(classValues.get(m)) + (x1y.length * x2y.length));
                            double x1GivenY = (x1y[k][m] + 1.0) / (naiveBayes.frequencyOfClass.get(classValues.get(m)) + x1y.length);
                            double x2GivenY = (x2y[l][m] + 1.0) / (naiveBayes.frequencyOfClass.get(classValues.get(m)) + x2y.length);

                            mi += px1x2y * (Math.log(px1x2GivenY / (x1GivenY * x2GivenY)) / Math.log(2));
                        }
                    }
                }
                mutualInformation[i][j] = mi;
            }
        }
        for (int i = 0; i < mutualInformation.length; i++)
            for (int j = 0; j < mutualInformation[i].length; j++) {
                if (i == j)
                    mutualInformation[i][j] = -1;
                mutualInformation[j][i] = mutualInformation[i][j];
            }

        return mutualInformation;
    }

    public void Prim(ArrayList<Node> nodes) {

        spanningTreeEdges = new ArrayList<>(features.size() - 1);
        nodes.get(0).key = 0.0;
        Node startNode = nodes.get(0);
        spanningTreeEdges.add(startNode);

        for(int i = 0; i < CPT.length; i++){
            CPT[i][0] = 0;
        }

        while (spanningTreeEdges.size() < nodes.size()) {
            spanningTreeEdges.sort(Node.comparator);
            Node maxChild = null, maxParent = null;
            double maxKey = Double.MIN_VALUE;

            for (Node node : spanningTreeEdges) {
                for (int i = 0; i < nodes.size(); i++) {
                    if (CPT[features.indexOf(node.node)][i] > maxKey && !spanningTreeEdges.contains(nodes.get(i))) {
                        maxKey = CPT[features.indexOf(node.node)][i];
                        maxChild = nodes.get(i);
                        maxParent = node;
                    }
                }
            }
            maxChild.parent = maxParent;
            spanningTreeEdges.add(maxChild);
            for (int i = 0; i < CPT.length; i++)
                CPT[i][nodes.indexOf(maxChild)] = 0;
        }

        for (Node node: nodes)
            node.children.clear();

        for (Node node: nodes)
            if (node.parent != null)
                node.parent.children.add(node);
    }




    /**
     * Creates a spanning tree using mutual information between pairs of features
     */
    public void createSpanningTree() {

        CPT = computeMutualInformation();
        for (int i = 0; i < CPT.length; i++) {
            for (int j = 0; j < CPT[i].length; j++) {
                System.out.print(CPT[i][j] + " ");
            }
            System.out.println();
        }

        // Create graph where each node is a feature
        ArrayList<Node> nodes = new ArrayList<>();
        for (Feature feature: features)
            nodes.add(new Node(feature));

        // Add edges between all nodes
        for (Node node: nodes)
            for (Node node1: nodes)
                if (node != node1)
                    node.children.add(node1);

        // Prim's algorithm
        Prim(nodes);

    }

    /**
     * Calculates the probability of a given class in the naive bayes net given feature values
     * @param featureList List of features values for the instance
     * @param classValue The class value for which the probability needs to be calculated
     * @return The probability of a class given features values
     */
    public double probabilityClassGivenFeatures(String[] featureList, String classValue) {
        double numerator = 1.0, denominator = 0.0;
        for (Node node: spanningTreeEdges) {
            if (node.parent != null)
                numerator *= probabilityFeatureGivenClassAndFeature(node.node.featureName, featureList[features.indexOf(node.node)], node.parent.node.featureName, featureList[features.indexOf(node.parent.node)], classValue);
            else
                numerator *= naiveBayes.probabilityFeatureGivenClass(node.node.featureName, featureList[features.indexOf(node.node)], classValue);
        }
        numerator *= naiveBayes.probabilityOfClass.get(classValue);

        for (String classVal: classValues) {
            numerator = 1.0;
            for (Node node: spanningTreeEdges) {
                if (node.parent != null)
                    numerator *= probabilityFeatureGivenClassAndFeature(node.node.featureName, featureList[features.indexOf(node.node)], node.parent.node.featureName, featureList[features.indexOf(node.parent.node)], classVal);
                else
                    numerator *= naiveBayes.probabilityFeatureGivenClass(node.node.featureName, featureList[features.indexOf(node.node)], classVal);
            }
            numerator *= naiveBayes.probabilityOfClass.get(classVal);
            denominator += numerator;
        }

        return numerator / denominator;
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

        if (featureName1.equals(featureName2))
            return naiveBayes.probabilityFeatureGivenClass(featureName1, featureValue1, classValue);

        double conditionalFeatureCount, classCount, allowedValues1, allowedValues2;
        Feature feature1 = null, feature2 = null;

        // Find the feature objects associated with the given feature names
        for (Feature feature: features) {
            if (feature.featureName.equals(featureName1))
                feature1 = feature;
            if (feature.featureName.equals(featureName2))
                feature2 = feature;
        }

        conditionalFeatureCount = X1X2YTable[features.indexOf(feature1)][feature1.allowedValues.indexOf(featureValue1)][features.indexOf(feature2)][feature2.allowedValues.indexOf(featureValue2)][classValues.indexOf(classValue)];
        classCount = naiveBayes.frequencyOfClass.get(classValue);
        allowedValues1 = feature1.allowedValues.size();
        allowedValues2 = feature2.allowedValues.size();
        return (conditionalFeatureCount + 1.0) / (classCount + (allowedValues1 * allowedValues2));
    }

    /**
     * This method calculates the probability of a feature given the value of another feature and
     * the class
     * @param featureName The name of the feature whose probability is being calculated
     * @param featureValue The value of the above feature
     * @param givenFeatureName The name of the feature that is given
     * @param givenFeatureValue The value of the feature above
     * @param classValue The class value given
     * @return The probability of a feature given a class and another feature
     */
    public double probabilityFeatureGivenClassAndFeature(String featureName,
                                                         String featureValue,
                                                         String givenFeatureName,
                                                         String givenFeatureValue,
                                                         String classValue) {
        if (featureName.equals(givenFeatureName))
            return naiveBayes.probabilityFeatureGivenClass(featureName, featureValue, classValue);

        double conditionalFeatureCount, classCount = 0, allowedValues1;
        Feature feature1 = null, feature2 = null;

        // Find the feature objects associated with the given feature names
        for (Feature feature: features) {
            if (feature.featureName.equals(featureName))
                feature1 = feature;
            if (feature.featureName.equals(givenFeatureName))
                feature2 = feature;
        }

        for (int i = 0; i < X1X2YTable.length; i++)
            for (int j = 0; j < X1X2YTable[i].length; j++)
                classCount += X1X2YTable[i][j][features.indexOf(feature2)][feature2.allowedValues.indexOf(givenFeatureValue)][classValues.indexOf(classValue)];

        conditionalFeatureCount = X1X2YTable[features.indexOf(feature1)][feature1.allowedValues.indexOf(featureValue)][features.indexOf(feature2)][feature2.allowedValues.indexOf(givenFeatureValue)][classValues.indexOf(classValue)];
        allowedValues1 = feature1.allowedValues.size();

        return (conditionalFeatureCount + 1.0) / (classCount + allowedValues1);
    }

    /**
     * Trains the given Tree Augmented - Naive Bayes net with the instances passed to it.
     * Calculates all conditional and class probabilities and stores them in the data structures of
     * the current object.
     * @param instances The instances to train the naive bayes net on
     */
    void train(ArrayList<Instance> instances) {
        this.instances = instances;
        featureToFeaturePairs = new HashMap<>();
        naiveBayes = new NaiveBayes(classValues, features);
        naiveBayes.train(instances);

        /* Initializes the Map to map each feature to all feature pairs its associated with */
        for (Feature feature: features) {
            ArrayList<FeaturePair> featurePairs = new ArrayList<>();
            for (Feature feature1: features) {
                featurePairs.add(new FeaturePair(feature, feature1));
            }
            featureToFeaturePairs.put(feature, featurePairs);
        }

        // Table for all features and class values to store how many times each occurred
        X1X2YTable = new double[features.size()][][][][];
        for (int i = 0; i < features.size(); i++) {
            X1X2YTable[i] = new double[features.get(i).allowedValues.size()][features.size()][][];
            for (int j = 0; j < features.get(i).allowedValues.size(); j++) {
                for (int k = 0; k < features.size(); k++) {
                    X1X2YTable[i][j][k] = new double[features.get(k).allowedValues.size()][classValues.size()];
                }
            }
        }

        /* Initialize table of 2 features occurring given class */
        for (Instance instance: instances) {
            for (int i = 0; i < instance.features.length; i++) {
                for (int j = 0; j < instance.features.length; j++) {
                    X1X2YTable[i][features.get(i).allowedValues.indexOf(instance.features[i])][j][features.get(j).allowedValues.indexOf(instance.features[j])][classValues.indexOf(instance.classValue)]++;
//                    Feature feature = features.get(i);
//                    ArrayList<FeaturePair> featurePairs = featureToFeaturePairs.get(feature);
//                    for (int j = 0; j < instance.features.length; j++) {
//                        Feature feature1 = features.get(j);
//                        if (feature == feature1)
//                            continue;
//                        int k;
//                        for (k = 0; k < featurePairs.size(); k++) {
//                            if (featurePairs.get(k).featureY == feature1)
//                                break;
//                        }
//                        double[][][] probabilityXY = featurePairs.get(k).prXY;
//                        probabilityXY[feature.allowedValues.indexOf(instance.features[features.indexOf(feature)])][feature1.allowedValues.indexOf(instance.features[features.indexOf(feature1)])][classValues.indexOf(instance.classValue)]++;
//                        featurePairs.get(k).prXY = probabilityXY;
//                    }
//                    featureToFeaturePairs.put(feature, featurePairs);
                }
            }
        }


        createSpanningTree();
    }

}
