package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class bayes {

    public static ArrayList<Feature> features;
    public static ArrayList<String> classValues;

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java bayes <trainfile> <testfile> <n/t>");
            System.exit(1);
        }

        features = new ArrayList<>();
        classValues = new ArrayList<>();
        ArrayList<Instance> trainingSet = readFile(args[0]);
        ArrayList<Instance> testSet = readFile(args[1]);

        NaiveBayes naiveBayes = new NaiveBayes(classValues, features);
        naiveBayes.train(trainingSet);

        // TODO Test the naive bayes net on the test set
        for (Instance instance: testSet) {
            double maxProbability = 0.0;
            int classification = 0;
            for (String classValue: classValues) {
                double pr = naiveBayes.probabilityClassGivenFeatures(instance.features, classValue);
                if (pr > maxProbability) {
                    maxProbability = pr;
                    classification = classValues.indexOf(classValue);
                }
            }
            System.out.println(classValues.get(classification) + " " + instance.classValue + " "
                    + maxProbability);
        }

    }

    public static ArrayList<Instance> readFile(String filename) {
        Scanner file = null;
        try {
            file = new Scanner(new File(filename));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        ArrayList<Instance> instances = new ArrayList<>();
        boolean isTraining = features.isEmpty();
        while (file.hasNextLine()) {
            String line = file.nextLine().trim();
            Instance instance;

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
