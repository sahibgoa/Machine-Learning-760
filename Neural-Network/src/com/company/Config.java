/**
 * This class contains the configuration of the neural network
 *
 * @author Sahib Singh Pandori
 * @author Aman Lunia
 */

class Config {

    /* The number of hidden nodes in the hidden layer */
    static int NUM_HIDDEN_NODES = 2;
    /* The learning rate of the network */
    static double learningRate = 0.01;

    /* The maximum number of epochs to check after accuracy begins decreasing (for early stopping */
    static int MAX_EPOCHS_SINCE_LOWER_ACCURACY = 100;

    /* Whether to use the momentum term or not */
    static boolean useMomentumTerm = true;
    /* The beta value for the momentum term */
    static double beta = 0.9;

    /* Whether to use dropout or not */
    static boolean useDropout = false;
    /* The input masking probability threshold for dropout */
    static double INPUT_MASKING_PROB = 0.8;
    /* The hidden masking probability threshold for dropout */
    static double HIDDEN_MASKING_PROB = 0.5;

    /* The types of nodes in the neural network */
    static final int INPUT = 0;
    static final int INPUT_BIAS = 1;
    static final int HIDDEN = 2;
    static final int HIDDEN_BIAS = 3;
    static final int OUTPUT = 4;

}
