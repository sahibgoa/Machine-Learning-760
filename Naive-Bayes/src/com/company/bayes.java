package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class bayes {

    // Stores all features (i.e. feature names and allowed values)
    private static ArrayList<Feature> features;
    // Stores all class values
    static ArrayList<String> classValues;

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: java bayes <trainfile> <testfile> <n/t>");
            System.exit(1);
        }

        features = new ArrayList<>();
        classValues = new ArrayList<>();
        ArrayList<Instance> trainingSet = readFile(args[0]);
        ArrayList<Instance> testSet = readFile(args[1]);

        if (args[2].trim().equals("n")) {
            NaiveBayes naiveBayes = new NaiveBayes(classValues, features);
            naiveBayes.train(trainingSet);

            // Test the naive bayes net on the test set
            for (Instance instance : testSet) {
                double maxProbability = 0.0;
                int classification = 0;
                for (String classValue : classValues) {
                    double pr = naiveBayes.probabilityClassGivenFeatures(instance.features, classValue);
                    if (pr > maxProbability) {
                        maxProbability = pr;
                        classification = classValues.indexOf(classValue);
                    }
                }
                System.out.println(classValues.get(classification) + " " + instance.classValue + " "
                        + maxProbability);
            }
        } else if (args[2].trim().equals("t")) {
            TAN tan = new TAN(classValues, features);
            tan.train(trainingSet);

            for (Feature feature: features)
                for (Feature feature1: features) {
                    for (String value : feature.allowedValues)
                        for (String value1 : feature1.allowedValues)
                            for (String classVal : classValues)
                                System.out.println("Pr(" + features.indexOf(feature) + "=" + feature.allowedValues.indexOf(value) + "|" + features.indexOf(feature1) + "=" + feature1.allowedValues.indexOf(value1) + "," + "Y=" + classValues.indexOf(classVal) + ") = " + tan.probabilityFeatureGivenClassAndFeature(feature.featureName, value, feature1.featureName, value1, classVal));
                    System.out.println();
                }

            for (Node node: TAN.spanningTreeEdges) {
                System.out.print(node.node.featureName);
                if (node.parent != null)
                    System.out.print(" " + node.parent.node.featureName);
                System.out.println(" class");
            }
            System.out.println();

            int correct = 0;
            // Test the naive bayes net on the test set
            for (Instance instance : testSet) {
                double maxProbability = 0.0, sum = 0.0;
                int classification = 0;
                double[] pr = new double[classValues.size()];
                int i = 0;
                for (String classValue : classValues) {
                    pr[i] = tan.probabilityClassGivenFeatures(instance.features, classValue);
                    if (pr[i] > maxProbability) {
                        maxProbability = pr[i];
                        classification = classValues.indexOf(classValue);
                    }
                    sum += pr[i];
                    i++;
                }
                // Normalize probabilities
                for (i = 0; i < classValues.size(); i++)
                    pr[i] /= sum;
                maxProbability = pr[classification];

                if (classification == classValues.indexOf(instance.classValue))
                    correct++;
                System.out.println(classValues.get(classification) + " " + instance.classValue + " "
                        + maxProbability);
            }
            System.out.println("\n" + correct);
        }
    }

    /**
     * Reads the ARFF file and stores all attributes, their allowed values as well as all instances.
     * It also differentiates between training and test set by checking if the feature vector has
     * been initialized or not.
     * @param filename The ARFF file to be read
     * @return ArrayList of all instances in the file
     */
    private static ArrayList<Instance> readFile(String filename) {
        Scanner file = null;
        try {
            file = new Scanner(new File(filename));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        assert file != null;
        ArrayList<Instance> instances = new ArrayList<>();
        boolean isTraining = features.isEmpty();
        while (file.hasNextLine()) {
            String line = file.nextLine().trim();
            if (line.charAt(0) == '%')
                continue;
            else if (line.charAt(0) == '@' && isTraining) {
                if (line.contains("@relation") || line.contains("@data"))
                    continue;
                // Read the attributes
                String[] values = line.split("\\s+", 3);
                values[2] = values[2].trim();
                values[2] = values[2].substring(1, values[2].length() - 1).trim();
                String[] allowedValues = values[2].split("\\s*,\\s*");
                if (!values[1].equals("'class'"))
                    features.add(new Feature(values[1].substring(1, values[1].length() - 1),
                            new ArrayList<>(Arrays.asList(allowedValues))));
                else
                    classValues.addAll(Arrays.asList(allowedValues));
            } else if (line.charAt(0) != '@')
                instances.add(new Instance(line.split("\\s*,\\s*")));
        }
        return instances;
    }
}
