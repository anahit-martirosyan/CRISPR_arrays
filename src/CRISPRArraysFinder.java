import java.util.Vector;

public class CRISPRArraysFinder {

    // TODO remove hardcoded values
    private static String sequenceFile = "";
    private static DNASequence dnaSequence;
    private static int minNumberRepeats = 3;
    private static int minRepeatLength = 19;
    private static int maxRepeatLength = 38;
    private static int searchWindowLength = 8;
    private static int minSpacerLength = 19;
    private static int maxSpacerLength = 48;

    /**
     * Parses command line arguments and sets members of the class
     * */
    private static void parseArgs(String[] args) {}

    /**
     * Main function finding CRISPR arrays in DNA sequence
     * @param dnaSequence the DNA sequence provided by the user
     * @return vector of CRISPR arrays found in the sequence
     * */
    private static Vector<CRISPRArray> findCRIPSRs(DNASequence dnaSequence) {
        CRISPRSearchEngine searchEngine = new CRISPRSearchEngine(dnaSequence, minNumberRepeats, minRepeatLength, maxRepeatLength, searchWindowLength, minSpacerLength, maxSpacerLength);
        return searchEngine.findCRISPRs();
    }

    /**
     * Function visualizing program output
     * */
    private static void printCRISPRs(Vector<CRISPRArray> crisprs) {
        for (CRISPRArray crispr: crisprs) {
            System.out.println(crispr);
        }
    }

    public static void main(String[] args) {

        parseArgs(args);
        dnaSequence = DNASequence.build(sequenceFile);
        Vector<CRISPRArray> crisprs = findCRIPSRs(dnaSequence);
        printCRISPRs(crisprs);

    }
}
