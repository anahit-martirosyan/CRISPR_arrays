import java.util.HashMap;
import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CRISPRSearchEngine {
    private DNASequence dnaSequence;
    private String currentPattern;

    private CRISPRArray currentCRISPRArray;
    private int minNumberRepeats;
    private int minRepeatLength;
    private int maxRepeatLength;
    private int searchWindowLength;
    private int minSpacerLength;
    private int maxSpacerLength;

    private static final int SCAN_RANGE = 24;
    private static final double SIMILARITY_THRESHOLD = 0.75;
    private static final double SPACER_TO_SPACER_MAX_SIMILARITY = 0.62;
    

    private static final int SPACER_TO_SPACER_LENGTH_DIFF = 12;
    private static final int SPACER_TO_REPEAT_LENGTH_DIFF = 30;
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
        int lastRepeatIndex = currentCRISPRArray.getRepeatPosition(currentCRISPRArray.getNumRepeats() - 1);
        int secondToLastRepeatIndex = currentCRISPRArray.getRepeatPosition(currentCRISPRArray.getNumRepeats() - 2);

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
    }

    /**
     * Checks if nucleotides in extended position do not differ more than a SIMILARITY_THRESHOLD
     * @param extensionLength is positive if extending to right, and negative if extending to left.
     * */
    private boolean canExtendTo(int extensionLength) {
        HashMap<Character, Integer> nucleotideCountMap = new HashMap<>();

        for (int k = 0; k < currentCRISPRArray.getNumRepeats(); k++ ) {
            int currRepeatStartIndex = currentCRISPRArray.getRepeatPosition(k);
            char lastNucleotide = dnaSequence.getNucleotide(currRepeatStartIndex + extensionLength);
            int count = nucleotideCountMap.get(lastNucleotide) != null ? nucleotideCountMap.get(lastNucleotide) : 0;
            nucleotideCountMap.put(lastNucleotide, count + 1);
        }

        for (int count: nucleotideCountMap.values()) {
            double percent = (double)count / currentCRISPRArray.getNumRepeats();
            if (percent >= SIMILARITY_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extends crispr repeats to right. Checks if nucleotides do not differ more than SIMILARITY_THRESHOLD
     * */
    private void extendRight() {
        assert currentCRISPRArray != null;

        int extensionLength = currentCRISPRArray.getRepeatLength();
        int shortestRepeatSpacing = currentCRISPRArray.getShortestRepeatSpacing();
        int maxExtensionLength = shortestRepeatSpacing - CRISPRArraysFinder.minSpacerLength;
        int lastRepeatIndex = currentCRISPRArray.getRepeatPosition(currentCRISPRArray.getNumRepeats() - 1);

        while (extensionLength <= maxExtensionLength && lastRepeatIndex + extensionLength < dnaSequence.length()) {
            // extend to right
            if (canExtendTo(extensionLength)) {
                ++extensionLength;
            } else {
                break;
            }
        }
        --extensionLength;
        currentCRISPRArray.extendRight(extensionLength - currentCRISPRArray.getRepeatLength() + 1);
    }

    /**
     * Extends crispr repeats to left. Checks if nucleotides do not differ more than SIMILARITY_THRESHOLD
     * */
    private void extendLeft() {
        assert currentCRISPRArray != null;

        int extensionLength = 0;
        int shortestRepeatSpacing = currentCRISPRArray.getShortestRepeatSpacing();
        int maxExtensionLength = shortestRepeatSpacing - CRISPRArraysFinder.minSpacerLength - currentCRISPRArray.getRepeatLength() + 1;
        int firstRepeatIndex = currentCRISPRArray.getRepeatPosition(0);

        while (extensionLength <= maxExtensionLength && firstRepeatIndex - extensionLength >= 0) {
            // extend to left
            if (canExtendTo(-extensionLength)) {
                ++extensionLength;
            } else {
                break;
            }
        }
        --extensionLength;
        currentCRISPRArray.extendLeft(extensionLength);
    }

    /**
     * Extends exact repeats to left and right
     * */
    private void extendExactRepeats() {
        assert currentCRISPRArray != null;
        // expand repeats if new nucleotides do not differ more than a SIMILARITY_THRESHOLD
        extendRight();
        extendLeft();
    }

    private boolean hasValidNumberOfRepeats() {
        return currentCRISPRArray.getNumRepeats() >= minNumberRepeats;
    }

    private boolean hasValidRepeatLength() {
        return currentCRISPRArray.getRepeatLength() >= minRepeatLength
                && currentCRISPRArray.getRepeatLength() <= maxRepeatLength;
    }


    /**
     * Validates given CRISPR array
     * 1. Checks if repeats are in range of given min and max length
     * 2. Checks if spacers are in range of given min and max length
     * 3. Checks if spacers differ from each other (uses Levenshtein Distance)
     * 4. Checks if spacers differ from repeats (uses Levenshtein Distance)
     * 5. Checks that Spacers are similar sized
     * */
    private boolean isValidCRISPRArray() {
        assert currentCRISPRArray != null;

        return hasValidNumberOfRepeats() && hasValidRepeatLength()
                && currentCRISPRArray.hasNonRepeatingSpacers(SPACER_TO_SPACER_MAX_SIMILARITY)
                && currentCRISPRArray.hasSimilarlySizedSpacers(SPACER_TO_SPACER_LENGTH_DIFF, SPACER_TO_REPEAT_LENGTH_DIFF);
    }

    /**
    * Adds currentCRISPRArray to crisprArrays.
     * Should be called after currentCRISPRArray is validated.
    * */
    void addCRISPRArray() {
        crisprArrays.add(currentCRISPRArray);
        currentCRISPRArray = null;
    }

    /**
     * Main function finding CRISPR arrays in DNA sequence
     * @return vector of CRISPR arrays found in the sequence
     * */
    public Vector<CRISPRArray> findCRISPRs() {
        // 1. select search window size sequence - currentPattern,
        // 2. find matches to currentPattern,
        // 3. extend found repeats
        // 4. validate found CRISPR array
        // 5. add CRISPR array to CRISPRSArrays if it is valid

        int skips = minRepeatLength - (2 * searchWindowLength - 1);
        if (skips < 1)
            skips = 1;
        for(int i = 0; i < dnaSequence.length() - searchWindowLength; i += skips) {
            currentPattern = dnaSequence.subSequence(i, i + searchWindowLength);
            findExactRepeats(i);
            if (currentCRISPRArray != null) {
                extendExactRepeats();
                if (isValidCRISPRArray()) {
                    i = currentCRISPRArray.getEndIndex() + 1;
                    addCRISPRArray();
                }
            }
        }
        return crisprArrays;
    }
}
