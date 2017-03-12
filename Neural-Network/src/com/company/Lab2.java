import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This class contains the main method as well as the code to read the data file
 *
 * @author Sahib Singh Pandori
 * @author Aman Lunia
 */

public class Lab2 {

    /**
     * Main method for the neural network program. Reads all the data from the file and handles
     * all API calls to the NNImpl class
     *
     * @param args the arguments passed to the program (includes filename only)
     */
	public static void main(String[] args) {

		// Checking for correct number of arguments
		if (args.length != 1) {
			System.out.println("usage: java Lab2 <dataset> ");
			System.exit(-1);
		}
		
		// Reading the training set
		ArrayList<Protein> trainingSet = getData(args[0]);
		ArrayList<Protein> tuningSet = new ArrayList<>();
		ArrayList<Protein> testSet = new ArrayList<>();

		// Creating the tuning and testing set
        for (int i = 0; i < trainingSet.size(); i++) {
            if ((i+1)%5 == 0)
                tuningSet.add(trainingSet.remove(i));
            else if (i%5 == 0)
                testSet.add(trainingSet.remove(i));
        }

        // Reading the weights
		Double[][] hiddenWeights = new Double[Config.NUM_HIDDEN_NODES][];
		for(int i = 0; i < hiddenWeights.length; i++)
			hiddenWeights[i] = new Double[(((2 * Protein.SLIDING_WINDOW_RADIUS) + 1)
                    * Instance.proteinToNumber.get("A").size())
                    + 1];
		
		Double [][] outputWeights = new Double[3][];
		for (int i = 0; i < outputWeights.length; i++) {
			outputWeights[i] = new Double[hiddenWeights.length+1];
		}
		
		readWeights(hiddenWeights, outputWeights);

        // Initialize the neural network
        NNImpl nn = new NNImpl(trainingSet, tuningSet, hiddenWeights, outputWeights);
        // Train the neural network on the training data
		nn.train();

		int output;
        for (Protein tuningInstance : testSet)
            for (int j = 0; j < tuningInstance.aminoAcids.size(); j++) {
                // Getting output from network
                output = nn.calculateOutputForInstance(tuningInstance.aminoAcids.get(j));
                if (output == 0)
                    System.out.println("h");
                else if (output == 1)
                    System.out.println("e");
                else
                    System.out.println("_");
            }
    }

    /**
     * Reads the data from the file with the given filename
     * @param filename The filename of the file with all the data
     * @return List of Protein objects from which individual instances will be created
     */
	private static ArrayList<Protein> getData(String filename) {
        Instance.initializeMap();
        ArrayList<Protein> proteins = new ArrayList<>();
		
		try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
			boolean addToList = false;
			ArrayList<String> rawDataList = new ArrayList<>();

			// Read the file line by line and create a list of proteins
			while (in.ready()) { 
				String line = in.readLine().trim();
				if (line.length() < 2)
				    continue;
				String prefix = line.substring(0, 2);
				if (prefix.equals("# ") || prefix.equals("//"))
				    continue;
				else if (prefix.equals("<>") && rawDataList.size() == 0)
					addToList = true;
				else if (line.equals("end") || line.equals("<end>") || line.equals("<>")) {
					proteins.add(new Protein(rawDataList));
					rawDataList.clear();
				}
				else if (addToList)
					rawDataList.add(line);
			}
			in.close();
			if (rawDataList.size() != 0)
                proteins.add(new Protein(rawDataList));
			return proteins;
		} catch(Exception e) {
		    e.printStackTrace();
			System.out.println("Could not read instances: " + e);
		}
		return null;
	}


    /**
     * Randomly initializes the weights to a value between 0 and 0.01
     * @param hiddenWeights 2D array to store the weights from the input to hidden layer
     * @param outputWeights 2D array to store the weights from the hidden to output layer
     */
	private static void readWeights(Double[][] hiddenWeights, Double[][] outputWeights) {
		Random r = new Random();
			
		for(int i = 0; i < hiddenWeights.length; i++)
			for(int j = 0; j < hiddenWeights[i].length; j++)
				hiddenWeights[i][j] = r.nextDouble() * 0.01;
				
		for(int i = 0; i < outputWeights.length; i++)
			for (int j = 0; j < outputWeights[i].length; j++)
				outputWeights[i][j] = r.nextDouble() * 0.01;
	

	}
}
