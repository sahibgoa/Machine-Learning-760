/**
 * This class makes the data structure to store the relation between a node and an edge in a
 * neural network
 *
 * @author Sahib Singh Pandori
 */

class NodeWeightPair {

    /* The parent node */
	Node node;
	/* Weight of this connection */
	double weight;

    /**
     * Constructor for the class
     * @param node The node in the pair
     * @param weight The weight of the edge
     */
	NodeWeightPair(Node node, double weight) {
		this.node = node;
		this.weight = weight;
	}
}