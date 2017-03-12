import java.util.ArrayList;

/**
 * Class specific to the protein dataset provided. For the full dataset go to
 * ftp://ftp.ics.uci.edu/pub/machine-learning-databases/molecular-biology/protein-secondary-structure/
 *
 * @author Sahib Singh Pandori
 * @author Aman Lunia
 */

public class Protein {

    ArrayList<Instance> aminoAcids;
    static int SLIDING_WINDOW_RADIUS = 8;

    public Protein(ArrayList<String> rawFileData) {

        aminoAcids = new ArrayList<>();

        // Pad data with solvent at the beginning
        for (int i = 0; i < SLIDING_WINDOW_RADIUS; i++)
            rawFileData.add(0, "X");

        // Pad data with solvent at the end
        for (int i = 0; i < SLIDING_WINDOW_RADIUS; i++)
            rawFileData.add("X");

        // Begin the sliding window procedure
        for (int i = SLIDING_WINDOW_RADIUS; i < rawFileData.size() - SLIDING_WINDOW_RADIUS; i++) {
            Instance instance = new Instance();
            for (int j = i - SLIDING_WINDOW_RADIUS; j < i + SLIDING_WINDOW_RADIUS; j++) {
                String[] values = rawFileData.get(j).split(" ");
                instance.attributes.addAll(Instance.proteinToNumber.get(values[0]));
            }
            String[] values = rawFileData.get(i).split(" ");
            instance.classValues = Instance.structureToNumber.get(values[1]);
            // Add instance to list
            aminoAcids.add(instance);
        }
    }

}
