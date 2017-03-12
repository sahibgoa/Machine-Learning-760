import java.util.*;

/**
 * Holds data for a particular instance. Attributes are represented as an ArrayList of Integers
 * Class labels are represented as an ArrayList of Integers. For example, a 3-class instance
 * will have classValues as [0 1 0] meaning this instance has class 1.
 * Do not modify
 *
 * @author Sahib Singh Pandori
 * @author Aman Lunia
 */
 

public class Instance {

	ArrayList<Integer> attributes;
	ArrayList<Integer> classValues;
    static Map<String,ArrayList<Integer>> structureToNumber;
    static Map<String,ArrayList<Integer>> proteinToNumber;

    /**
     * Constructor to initialize the attributes and classValues list
     */
	public Instance() {
		attributes = new ArrayList<>();
		classValues = new ArrayList<>();
	}

    /**
     * Initializes the map which implements one hot encoding for input and output layer
     */
	static void initializeMap() {
	    proteinToNumber = new HashMap<>();
	    structureToNumber = new HashMap<>();

	    // Represents the mapping for the secondary structure of protein
        // 'h' - alpha helix, 'e' - beta strand, '_' - coil
	    structureToNumber.put("h", new ArrayList<>(Arrays.asList(1,0,0)));
	    structureToNumber.put("e", new ArrayList<>(Arrays.asList(0,1,0)));
	    structureToNumber.put("_", new ArrayList<>(Arrays.asList(0,0,1)));

        // Represents the mapping of amino acids in protein (X = solvent)
	    proteinToNumber.put("A", new ArrayList<>(Arrays.asList(1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        proteinToNumber.put("C", new ArrayList<>(Arrays.asList(0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("D", new ArrayList<>(Arrays.asList(0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("E", new ArrayList<>(Arrays.asList(0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        proteinToNumber.put("F", new ArrayList<>(Arrays.asList(0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("G", new ArrayList<>(Arrays.asList(0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        proteinToNumber.put("H", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        proteinToNumber.put("I", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0)));
        proteinToNumber.put("K", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0)));
        proteinToNumber.put("L", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("M", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("N", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("P", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0)));
	    proteinToNumber.put("Q", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0)));
        proteinToNumber.put("R", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0)));
        proteinToNumber.put("S", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0)));
	    proteinToNumber.put("T", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0)));
	    proteinToNumber.put("V", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0)));
        proteinToNumber.put("W", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0)));
	    proteinToNumber.put("Y", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0)));
	    proteinToNumber.put("X", new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1)));
    }
	
}
