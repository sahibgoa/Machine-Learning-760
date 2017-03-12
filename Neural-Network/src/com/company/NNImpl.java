import java.util.*;

/**
 * This class contains the functions relevant to the implementation of a single hidden layer
 * neural network
 *
 * @author Sahib Singh Pandori
 * @author Aman Lunia
 */

public class NNImpl {

    /* Create list of nodes for each layer*/
	private ArrayList<Node> inputNodes = null;
	private ArrayList<Node> hiddenNodes = null;
	private ArrayList<Node> outputNodes = null;

	/* Create list of proteins for training and tuning on the network*/
	private ArrayList<Protein> trainingSet = null;
	private ArrayList<Protein> tuningSet = null;

	/* Store the weights that performed best on the tuning set*/
	private double[][] bestHiddenWeights;
    private double[][] bestOutputWeights;


    /**
     * Constructor to initialize all parameters of the neural network
     * @param trainingSet The list of proteins to train on
     * @param tuningSet The list of proteins used to perform cross-validation
     * @param hiddenWeights Weights of edges from input to hidden layer
     * @param outputWeights Weights of edged from hidden to output layer
     */
	public NNImpl(ArrayList<Protein> trainingSet, ArrayList<Protein> tuningSet,
                  Double[][] hiddenWeights, Double[][] outputWeights) {
		this.trainingSet = trainingSet;
		this.tuningSet = tuningSet;

		// input layer nodes
		inputNodes = new ArrayList<>();
		int inputNodeCount = (2 * Protein.SLIDING_WINDOW_RADIUS + 1)
                                * Instance.proteinToNumber.get("A").size();
		int outputNodeCount = 3;
		for (int i = 0; i < inputNodeCount; i++) {
			Node node = new Node(0);
			inputNodes.add(node);
		}

		// bias node from input layer to hidden
		Node biasToHidden = new Node(1);
		inputNodes.add(biasToHidden);

		// hidden layer nodes
		hiddenNodes = new ArrayList<>();
		for (int i = 0; i < Config.NUM_HIDDEN_NODES; i++) {
			Node node = new Node(2);
			// Connecting hidden layer nodes with input layer nodes
			for (int j = 0; j < inputNodes.size(); j++) {
				NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}

		// bias node from hidden layer to output
		Node biasToOutput = new Node(3);
		hiddenNodes.add(biasToOutput);

		// Output node layer
		outputNodes = new ArrayList<>();
		for (int i = 0; i < outputNodeCount; i++) {
			Node node = new Node(4);
			// Connecting output layer nodes with hidden layer nodes
			for (int j = 0; j < hiddenNodes.size(); j++) {
				NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}
			outputNodes.add(node);
		}

		bestHiddenWeights = new double[hiddenWeights[0].length][hiddenWeights.length];
		bestOutputWeights = new double[outputWeights[0].length][outputWeights.length];
	}


	/**
	 * Get the output from the neural network for a single instance Return the
	 * idx with highest output values. For example if the outputs of the
	 * outputNodes are [0.1, 0.5, 0.2], it should return 1. The parameter is a
	 * single instance
	 * 
	 * @param inst The instance for which the output is calculated
	 */
    int calculateOutputForInstance(Instance inst) {
		for (int i = 0; i < inst.attributes.size(); i++)
			inputNodes.get(i).setInput(inst.attributes.get(i));

		inputNodes.get(inst.attributes.size()).setInput(1.0);

        for (Node hiddenNode : hiddenNodes)
            hiddenNode.calculateOutput();

		hiddenNodes.get(hiddenNodes.size() - 1).setInput(1.0);

        for (Node outputNode : outputNodes)
            outputNode.calculateOutput();

		int classification = 0;
		double maxOutput = outputNodes.get(0).getOutput();

		for (int i = 1; i < outputNodes.size(); i++) {
			if (outputNodes.get(i).getOutput() > maxOutput) {
				maxOutput = outputNodes.get(i).getOutput();
				classification = i;
			}
		}
		return classification;
	}


	/**
	 * Masks certain nodes randomly based on maskingProb
	 * 
	 * @param mask Array to store whether a node is masked or not
	 * @param maskingProb The threshold to decide whether to mask a node or not
	 */
	private void addMask(boolean[] mask, double maskingProb) {
		Random r = new Random();
		for (int node = 0; node < mask.length; node++)
			mask[node] = (r.nextDouble() >= maskingProb);
	}


	/**
	 * Computes the errors in the outputs as well as the deltas for all the
	 * weights in the network
	 * 
	 * @param error Error between output of network and instance
	 * @param errorOutput Deltas of edge weights between hidden layer and output layer
	 * @param errorHidden Deltas of edge weights between input layer and hidden layer
	 * @param inputMask Stores values corresponding to whether a input node is masked or not
	 * @param hiddenMask Stores values corresponding to whether a hidden node is masked or not
	 * @param inst Instance on which the errors are being calculated
	 */
	private void computeErrors(double[] error, double[][] errorOutput, double[][] errorHidden,
                               boolean[] inputMask, boolean[] hiddenMask, Instance inst,
                               double[][] predErrorHidden, double[][] predErrorOutput, double beta) {
        for (int j = 0; j < error.length; j++) {
			double output = outputNodes.get(j).getOutput();
            error[j] = (inst.classValues.get(j) - output) * output * (1 - output);
        }

		// Computing error in hidden to output weights
		for (int j = 0; j < hiddenNodes.size(); j++) {
			if (!hiddenMask[j])
				for (int k = 0; k < outputNodes.size(); k++) {
						errorOutput[j][k] = (Config.learningRate * hiddenNodes.get(j).getOutput() * error[k]);
						if (Config.useMomentumTerm) {
                            errorOutput[j][k] += (beta * predErrorOutput[j][k]);
                            predErrorOutput[j][k] = errorOutput[j][k];
                        }
				}
		}

		// Computing the error in input to hidden weights
		for (int a = 0; a < inputNodes.size(); a++) {
			if (!inputMask[a])
				for (int j = 0; j < hiddenNodes.size(); j++)
					if (!hiddenMask[j]) {
						double sigma = 0.0;
						for (int k = 0; k < outputNodes.size(); k++) {
                            sigma += outputNodes.get(k).parents.get(j).weight * error[k];
						}

						double output = hiddenNodes.get(j).getOutput();
                        errorHidden[a][j] = (Config.learningRate * inputNodes.get(a).getOutput() * sigma * output * (1 - output));

                        if (Config.useMomentumTerm) {
                            errorHidden[a][j] += (beta * predErrorHidden[a][j]);
                            predErrorHidden[a][j] = errorHidden[a][j];
                        }
					}
		}
	}


	/**
	 * Updates the weights of all the edges in the network
	 * 
	 * @param errorOutput Deltas of edge weights between hidden layer and output layer
	 * @param errorHidden Deltas of edge weights between input layer and hidden layer
	 * @param inputMask stores values corresponding to whether a input node is masked or not
	 * @param hiddenMask stores values corresponding to whether a hidden node is masked or not
	 */
	private void updateWeights(double[][] errorOutput, double[][] errorHidden, boolean[] inputMask,
			boolean[] hiddenMask) {
		for (int j = 0; j < hiddenNodes.size(); j++) {
			if (!hiddenMask[j])
				for (int k = 0; k < outputNodes.size(); k++) {
                    outputNodes.get(k).parents.get(j).weight += errorOutput[j][k];
				}
		}

		for (int j = 0; j < inputNodes.size(); j++) {
			if (!inputMask[j])
				for (int k = 0; k < hiddenNodes.size() - 1; k++)
					if (!hiddenMask[k]) {
                        hiddenNodes.get(k).parents.get(j).weight += errorHidden[j][k];
					}
		}
	}

	/**
	 * Calculates accuracy on the tuning set
	 * 
	 * @return the accuracy on the tuning set
	 */
    double calculateAccuracy(ArrayList<Protein> instances) {
		int correct = 0, output;
		long totalInstances = 0;
        for (Protein tuningInstance : instances)
            for (int j = 0; j < tuningInstance.aminoAcids.size(); j++, totalInstances++) {
                // Getting output from network
                output = this.calculateOutputForInstance(tuningInstance.aminoAcids.get(j));
                int actual_index = -1;
                for (int k = 0; k < 3; k++)
                    if (tuningInstance.aminoAcids.get(j).classValues.get(k) > 0.5)
                        actual_index = k;

                if (output == actual_index)
                    correct++;
            }
        return ((double) correct / (double) totalInstances);
	}


	/**
	 * Save the current neural network weights for future use
	 * 
	 * @param hiddenWeights The input to hidden layer edge weights
	 * @param outputWeights The hidden to output layer edge weights
	 */
	private void saveWeights(double[][] hiddenWeights, double[][] outputWeights) {
		for (int j = 0; j < inputNodes.size(); j++)
			for (int k = 0; k < hiddenNodes.size() - 1; k++)
				hiddenWeights[j][k] = hiddenNodes.get(k).parents.get(j).weight;

		for (int j = 0; j < hiddenNodes.size(); j++)
			for (int k = 0; k < outputNodes.size(); k++)
				outputWeights[j][k] = outputNodes.get(k).parents.get(j).weight;
	}


	/**
	 * Restore the neural network weights from a previous configuration
	 * 
	 * @param hiddenWeights The input to hidden layer edge weights
	 * @param outputWeights The hidden to output layer edge weights
	 */
	private void restoreWeights(double[][] hiddenWeights, double[][] outputWeights) {
		for (int j = 0; j < inputNodes.size(); j++)
			for (int k = 0; k < hiddenNodes.size() - 1; k++)
				hiddenNodes.get(k).parents.get(j).weight = hiddenWeights[j][k];

		for (int j = 0; j < hiddenNodes.size(); j++)
			for (int k = 0; k < outputNodes.size(); k++)
				outputNodes.get(k).parents.get(j).weight = outputWeights[j][k];
	}


	/**
	 * Train the neural networks with the given parameters The parameters are
	 * stored as attributes of this class
	 */
    void train() {
		Instance inst;
		int epochsSinceLowerAccuracy = 0, numEpoch = 0;
		double prevBestAccuracy = 0.0;

		// Loop to implement early stopping
		while (epochsSinceLowerAccuracy < Config.MAX_EPOCHS_SINCE_LOWER_ACCURACY) {
			// Keeps track of number of epochs
			numEpoch++;
			// Loops over all proteins in the training set
            for (Protein trainingInstance : trainingSet) {
                // Errors for (n-1) iteration, used with momentum term
                double[][] predErrorOutput = new double[hiddenNodes.size()][outputNodes.size()];
                double[][] predErrorHidden = new double[inputNodes.size()][hiddenNodes.size()];
                // Loops over the sliding window of amino acid in the protein
                for (int q = 0; q < trainingInstance.aminoAcids.size(); q++) {
                    inst = trainingInstance.aminoAcids.get(q);
                    calculateOutputForInstance(inst);

                    double[][] errorOutput = new double[hiddenNodes.size()][outputNodes.size()];
                    double[][] errorHidden = new double[inputNodes.size()][hiddenNodes.size()];

                    double[] error = new double[outputNodes.size()];

                    boolean[] inputMask = new boolean[inputNodes.size()];
                    boolean[] hiddenMask = new boolean[hiddenNodes.size()];

                    if (Config.useDropout) {
                        // Randomly chooses to mask certain nodes and edges
                        addMask(inputMask, Config.INPUT_MASKING_PROB);
                        addMask(hiddenMask, Config.HIDDEN_MASKING_PROB);
                    }

                    // Computes the errors on instance based on the masks
                    computeErrors(error, errorOutput, errorHidden, inputMask, hiddenMask, inst, predErrorHidden,
                            predErrorOutput, Config.beta);

                    // Update all the weights
                    updateWeights(errorOutput, errorHidden, inputMask, hiddenMask);

                }
                //calculateOutputForInstance(inst);
            }

			double accuracy = calculateAccuracy(this.tuningSet);

			if (accuracy > prevBestAccuracy) {
				epochsSinceLowerAccuracy = 0;
				// save the weights corresponding to the highest accuracy
				saveWeights(bestHiddenWeights, bestOutputWeights);
				// Save the best accuracy
				prevBestAccuracy = accuracy;
			} else {
				epochsSinceLowerAccuracy++;

			}

			// System.out.println("Epoch: " + numEpoch + " Accuracy: " + accuracy);
		}

		// Set the weights to the best weights found during all epochs run
		restoreWeights(bestHiddenWeights, bestOutputWeights);

	}

}
