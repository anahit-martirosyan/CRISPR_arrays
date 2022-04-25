import java.util.Vector;

public class CRISPRSearchEngine {
    private DNASequence dnaSequence;
    private String currentPattern;
    private Vector<CRISPRArray> crisprArrays;

    CRISPRSearchEngine(DNASequence dnaSequence) {
        this.dnaSequence = dnaSequence;
        crisprArrays = new Vector<>();
    }

    /**
     * Finds exact repeats.
     * @return a CRISPRArray object populated with exact repeats.
     * */
    private CRISPRArray findExactRepeats() {
        // find sequence matching currentPattern using Boyer-Moore algorithm
        // scan to right to find more matches
        return new CRISPRArray();
    }

    /**
     * Extends exact repeats to left and right
     * @param crispr a CRISPR array with only exact repeats
     * */
    private void extendExactRepeats(CRISPRArray crispr) {
        // expand repeats if new nucleotides do not differ than a given threshold
        // TODO modify crispr object
    }

    /**
     * Validates given CRISPR array
     * 1. Checks if repeats are in range of given min and max length
     * 2. Checks if spacers are in range of given min and max length
     * 3. Checks if spacers differ from each other (uses Levenshtein Distance)
     * 4. Checks if spacers differ from repeats (uses Levenshtein Distance)
     * 5. Checks that Spacers are similar sized
     * */
    private boolean validateCRISPRArray(CRISPRArray crispr) {
        return true;
    }

    /**
     * Main function finding CRISPR arrays in DNA sequence
     * @return vector of CRISPR arrays found in the sequence
     * */
    public Vector<CRISPRArray> findCRISPRs() {
        // select search window size sequence - currentPattern,
        // find matches to currentPattern,
        // extend found repeats
        // validate found CRISPR array
        // add CRISPR array to CRISPRSArrays if it is valid
        return crisprArrays;
    }
}
