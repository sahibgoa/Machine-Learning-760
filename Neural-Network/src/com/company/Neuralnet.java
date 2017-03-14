package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class contains the main method as well as the code to read the data file
 *
 * @author Sahib Singh Pandori
 */

public class Neuralnet {

    // Stores all features (i.e. feature names and allowed values)
    private static ArrayList<Feature> features;
    // Stores all class values
    static ArrayList<String> classValues;

    /**
     * Main method for the neural network program. Reads all the data from the file and handles
     * all API calls to the NNImpl class
     *
     * @param args the arguments passed to the program (includes filename only)
     */
    public static void main(String[] args) {

        // Checking for correct number of arguments
        if (args.length != 1) {
            System.out.println("usage: java Neuralnet <dataset> ");
            System.exit(-1);
        }

        features = new ArrayList<>();
        classValues = new ArrayList<>();

        // Reading the training set
        ArrayList<Instance> trainingSet = getData(args[0]);
        ArrayList<Instance> tuningSet = new ArrayList<>();
        ArrayList<Instance> testSet = new ArrayList<>();
        // ArrayList<Instance> trainingSetBackup = new ArrayList<>(trainingSet);

        double[] classDistribution = new double[classValues.size()];
        ArrayList<ArrayList<Instance>> instanceDistribution = new ArrayList<>();
        ArrayList<ArrayList<Instance>> kFoldInstances = new ArrayList<>();

        for (int i = 0; i < classValues.size(); i++) {
            instanceDistribution.add(new ArrayList<>());
        }

        // Divide instances based on the class values of each instance
        for (Instance trainingInstance : trainingSet) {
            classDistribution[trainingInstance.getClassIndex()] += 1.0;
            instanceDistribution.get(trainingInstance.getClassIndex()).add(trainingInstance);
        }

        // Divide the training set into k-folds
        for (int i = 0; i < Config.NUM_FOLDS; i++) {
            kFoldInstances.add(new ArrayList<>());
            for (int j = 0; j < instanceDistribution.size(); j++) {
                int numInstances = (int) (classDistribution[j] / Config.NUM_FOLDS);
                for (int k = 0; k < numInstances; k++) {
                    kFoldInstances.get(i).add(instanceDistribution.get(j).remove((int) (Math.random() * instanceDistribution.get(j).size())));
                }
            }
        }

        int foldIndex = 0;
        for (int j = 0; j < instanceDistribution.size(); j++) {
            for (; foldIndex < Config.NUM_FOLDS && instanceDistribution.get(j).size() != 0; foldIndex++) {
                kFoldInstances.get(foldIndex).add(instanceDistribution.get(j).remove((int) (Math.random() * instanceDistribution.get(j).size())));
            }
            foldIndex %= Config.NUM_FOLDS;
        }

        // Reading the weights
        double[][] hiddenWeights = new double[Config.NUM_HIDDEN_NODES][];
        for (int i = 0; i < hiddenWeights.length; i++)
            hiddenWeights[i] = new double[trainingSet.get(0).attributes.size() + 1];

        double[][] outputWeights = new double[3][];
        for (int i = 0; i < outputWeights.length; i++) {
            outputWeights[i] = new double[hiddenWeights.length + 1];
        }

        readWeights(hiddenWeights, outputWeights);

        // Initialize the neural network
        NNImpl nn = new NNImpl(joinKMinus1Folds(kFoldInstances, 0), tuningSet, hiddenWeights, outputWeights);
        for (int i = 0; i < Config.NUM_FOLDS; i++) {
            // Set one fold as the test set
            testSet = kFoldInstances.get(i);
            // Train the neural network on the training data
            nn.train();
            if (i != 0)
                // Create training set with k-1 folds
                nn.trainingSet = joinKMinus1Folds(kFoldInstances, i);
        }

        testSet = trainingSet;
        Collections.shuffle(testSet);

        int correct = 0, classification = 0, in = 0;
        double[] output;
        for (Instance testInstance : testSet) {
            // Getting output from network
            output = nn.calculateOutputForInstance(testInstance);

            double sum = 0.0, maxOutput = 0.0;
            // Get the sum to normalize the output
            for (int i = 0; i < output.length; i++) {
                sum += output[i];
                if (output[i] > maxOutput) {
                    classification = i;
                    maxOutput = output[i];
                }
            }

            for (int i = 0; i < kFoldInstances.size(); i++) {
                if (kFoldInstances.get(i).contains(testInstance))
                    foldIndex = i;
            }

            // Output format
            if (classValues.get(classification).equals("Rock"))
                System.out.println(++in + " " + foldIndex + " "
                    + classValues.get(classification) + " "
                    + classValues.get(testInstance.getClassIndex()) + " "
                    + (output[classification] / sum));

            if (classification == testInstance.getClassIndex())
                correct++;
        }
        System.out.println("\nAccuracy: " + (((1.0 * correct)/testSet.size())));
    }

    /**
     * Reads the ARFF file and stores all attributes, their allowed values as well as all instances.
     * It also differentiates between training and test set by checking if the feature vector has
     * been initialized or not.
     *
     * @param filename The filename of the file with all the data
     * @return List of Instance objects from which individual instances will be created
     */
    private static ArrayList<Instance> getData(String filename) {
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
                values[1] = values[1].toLowerCase();
                String[] allowedValues = null;
                if (!values[2].equals("numeric") || values[1].equals("'class'")) {
                    values[2] = values[2].substring(1, values[2].length() - 1).trim();
                    allowedValues = values[2].split("\\s*,\\s*");
                }
                if (!values[1].equals("'class'")) {
                    if (allowedValues != null)
                        features.add(new Feature(values[1].substring(1, values[1].length() - 1),
                                new ArrayList<>(Arrays.asList(allowedValues))));
                    else
                        features.add(new Feature(values[1].substring(1, values[1].length() - 1), null));
                }
                else
                    classValues.addAll(Arrays.asList(allowedValues));
            } else if (line.charAt(0) != '@')
                instances.add(new Instance(line.split("\\s*,\\s*")));
        }
        return instances;
    }

    /**
     * Randomly initializes the weights to a value between 0 and 0.01
     *
     * @param hiddenWeights 2D array to store the weights from the input to hidden layer
     * @param outputWeights 2D array to store the weights from the hidden to output layer
     */
    private static void readWeights(double[][] hiddenWeights, double[][] outputWeights) {
        Random r = new Random();

        for (int i = 0; i < hiddenWeights.length; i++)
            for (int j = 0; j < hiddenWeights[i].length; j++)
                hiddenWeights[i][j] = r.nextGaussian() * 0.01;

        for (int i = 0; i < outputWeights.length; i++)
            for (int j = 0; j < outputWeights[i].length; j++)
                outputWeights[i][j] = r.nextGaussian() * 0.01;


    }

    private static ArrayList<Instance> joinKMinus1Folds(ArrayList<ArrayList<Instance>> kFoldInstances, int testsetIndex) {
        ArrayList<Instance> trainingSet = new ArrayList<>();
        for (int i = 0; i < kFoldInstances.size(); i++) {
            if (i == testsetIndex)
                continue;
            trainingSet.addAll(kFoldInstances.get(i));
        }
        return trainingSet;
    }
}
