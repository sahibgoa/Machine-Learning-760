import java.util.*;

/**
 * Holds data for a particular instance. Attributes are represented as an ArrayList of Integers
 * Class labels are represented as an ArrayList of Integers. For example, a 3-class instance
 * will have classValues as [0 1 0] meaning this instance has class 1.
 * Do not modify
 *
 * @author Sahib Singh Pandori
 */
 

class Instance {

    ArrayList<Double> attributes;
    ArrayList<Integer> classValues;

    /**
     * Constructor to initialize the attributes and classValues list
     */
    Instance(String[] values) {
        attributes = new ArrayList<>();
        classValues = new ArrayList<>();
        for (int i = 0; i < values.length - 1; i++)
            this.attributes.add(Double.parseDouble(values[i]));
        for (String value: Neuralnet.classValues) {
            if (values[values.length - 1].equals(value))
                classValues.add(1);
            else
                classValues.add(0);
        }
    }

    int getClassIndex() {
        for (int i = 0; i < classValues.size(); i++)
            if (classValues.get(i) == 1)
                return i;
        return -1;
    }
}