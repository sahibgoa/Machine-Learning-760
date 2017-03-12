import java.util.ArrayList;

public abstract class BayesNetwork {

    abstract double probabilityClassGivenFeatures(String[] featureList, String classValue);

    abstract void train(ArrayList<Instance> instances);

}
