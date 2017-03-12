import java.util.*;

/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check Config.java for details
 *
 * @author Sahib Singh Pandori
 * @author Aman Lunia
 */

public class Node {

	private int type = 0;
	/* Array List that will contain the parents (including bias node) with weights if applicable */
	ArrayList<NodeWeightPair> parents = null;

	private double inputValue;
	private double outputValue = 0.0;

    /**
     * Create a node of a given type
     * @param type the type of the node
     */
	public Node(int type) {
		if (type > 4 || type < 0) {
			System.out.println("Incorrect value for node type");
			System.exit(1);
		} else
			this.type = type;

		if (type == Config.HIDDEN || type == Config.OUTPUT)
			parents = new ArrayList<>();
	}

    /**
     * For an input node sets the input value which will be the value of a particular attribute
     * @param inputValue the input value of the node
     */
	void setInput(double inputValue) {
		if (type == Config.INPUT)
			this.inputValue = inputValue;
	}

	/**
	 * Calculate the output of a ReLU node. You can assume that outputs of the
	 * parent nodes have already been calculated You can get this value by using
	 * getOutput()
	 */
	void calculateOutput() {
        // Not an input or bias node
		if (type == Config.HIDDEN || type == Config.OUTPUT) {
            double sum = 0.0;
            for (NodeWeightPair parent : parents)
                sum += parent.weight * parent.node.getOutput();
			// Sigmoid activation function
			this.outputValue = 1.0 / (1.0 + Math.exp(-1.0 * sum));
		}
	}

    /**
     * Returns the output calculated using the activation function at a node
     * @return the output value of the node
     */
	double getOutput() {
		if (type == Config.INPUT) {
		    if (Config.useDropout)
                return inputValue ;//* (Config.INPUT_MASKING_PROB);
		    else
		        return inputValue;
        }
		else if (type == Config.INPUT_BIAS || type == Config.HIDDEN_BIAS)
			return 1.00;
		else {
            if (Config.useDropout)
                return outputValue ;//* (Config.HIDDEN_MASKING_PROB);
            else
                return outputValue;
        }
	}
}
