import java.util.HashMap;
import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CRISPRSearchEngine {
    private DNASequence dnaSequence;
    private String currentPattern;

    private CRISPRArray currentCRISPRArray;
    private int minNumberRepeats = 3;
    private int minRepeatLength = 19;
    private int maxRepeatLength = 38;
    private int searchWindowLength = 8;
    private int minSpacerLength = 19;
    private int maxSpacerLength = 48;

    private static final int SCAN_RANGE = 24;

    private double spacerToSpacerMaxSimilarity = 0.62;
    private int spacerToSpacerLengthDiff = 12;
    private int spacerToRepeatLengthDiff = 30;
    private Vector<CRISPRArray> crisprArrays;

    CRISPRSearchEngine(DNASequence dnaSequence, int minNumberRepeats, int minRepeatLength,
                       int maxRepeatLength, int searchWindowLength, int minSpacerLength, int maxSpacerLength) {
        this.dnaSequence = dnaSequence;
        this.minNumberRepeats = minNumberRepeats;
        this.minRepeatLength = minRepeatLength;
        this.maxRepeatLength = maxRepeatLength;
        this.searchWindowLength = searchWindowLength;
        this.minSpacerLength = minSpacerLength;
        this.maxSpacerLength = maxSpacerLength;
        crisprArrays = new Vector<>();
    }


    /**
     * Finds more repeats of currentPattern by scanning to right
     * @precondition currentCRISPRArray is initialized and have at least two repeats
     * */
    private void scanRight() {
        int lastRepeatIndex = currentCRISPRArray.getRepeatIndex(currentCRISPRArray.getNumRepeats() - 1);
        int secondToLastRepeatIndex = currentCRISPRArray.getRepeatIndex(currentCRISPRArray.getNumRepeats() - 2);

        while(true) {
            int repeatSpacing = lastRepeatIndex - secondToLastRepeatIndex;
            int beginSearch = max(lastRepeatIndex + repeatSpacing - SCAN_RANGE, lastRepeatIndex + currentPattern.length() + minSpacerLength);
            int endSearch = min(lastRepeatIndex + repeatSpacing + currentPattern.length() + SCAN_RANGE + 1, dnaSequence.length());

            if (beginSearch > dnaSequence.length() - 1 || beginSearch >= endSearch) {
                return;
            }

            String searchSequence = dnaSequence.subSequence(beginSearch, endSearch);

            int repeatIndex = BoyerMoore.search(searchSequence, currentPattern); // index relative to the beginning of search region
            if (repeatIndex < 0) {
                // Pattern not found
                return;
            }

            secondToLastRepeatIndex = lastRepeatIndex;
            lastRepeatIndex = beginSearch + repeatIndex;

            currentCRISPRArray.addRepeat(lastRepeatIndex);
        }
    }

    /**
     * Finds exact repeats.
     * @return a CRISPRArray object populated with exact repeats.
     * */
    private void findExactRepeats(int beginIndex) {
        // find sequence matching currentPattern using Boyer-Moore algorithm
        // scan to right to find more matches

        currentPattern = dnaSequence.subSequence(beginIndex, beginIndex + searchWindowLength);
        int beginSearch = beginIndex + minSpacerLength + minRepeatLength;
        int endSearch = min(beginIndex + maxSpacerLength + maxRepeatLength + searchWindowLength + 1, dnaSequence.length());

        if (beginSearch > dnaSequence.length() - 1 || beginSearch >= endSearch) {
            return;
        }


        String searchSequence =  dnaSequence.subSequence(beginSearch, endSearch);

        int index = BoyerMoore.search(searchSequence, currentPattern); // index relative to the beginning of search region
        if (index < 0) {
            // Pattern not found
            return;
        }


        int repeatIndex = beginSearch + index;
        currentCRISPRArray = new CRISPRArray(dnaSequence, beginIndex, repeatIndex, currentPattern.length());

        scanRight();
        System.out.println(currentCRISPRArray);

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

    void addCRISPRArray() {
        crisprArrays.add(currentCRISPRArray);
        currentCRISPRArray = null;
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

        int skips = minRepeatLength - (2 * searchWindowLength - 1);
        if (skips < 1)
            skips = 1;
        for(int i = 0; i < dnaSequence.length() - searchWindowLength; i += skips) {
            findExactRepeats(i);
            // TODO
            if (currentCRISPRArray != null) {
                addCRISPRArray();
            }
        }
        return crisprArrays;
    }
}
