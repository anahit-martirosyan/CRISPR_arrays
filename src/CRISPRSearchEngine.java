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
    private CRISPRArray extendExactRepeats(CRISPRArray crispr) {
        // expand repeats if new nucleotides do not differ than a given threshold
        // TODO modify crispr object
    	int numRepeats = crispr.numRepeats();
		int firstRposition = crispr.repeatAt(0);
		int lastRposition = crispr.repeatAt(numRepeats-1);

		int shortestRepeatSpacing = crispr.repeatAt(1) - crispr.repeatAt(0);
		for (int i = 0; i < crispr.numRepeats() - 1; i++) {
			int currRepeatIndex = crispr.repeatAt(i);
			int nextRepeatIndex = crispr.repeatAt(i + 1);
			int currRepeatSpacing = nextRepeatIndex - currRepeatIndex;
			if (currRepeatSpacing < shortestRepeatSpacing)
				shortestRepeatSpacing = currRepeatSpacing;
		}

		int sequenceLength = dnaSequence.length();

		int rightExtensionLength = CRISPRArraysFinder.searchWindowLength;
		int maxRightExtensionLength = shortestRepeatSpacing - CRISPRArraysFinder.minSpacerLength;


		int currRepeatStartIndex;
		String currRepeat;
		int Acount, Ccount, Gcount, Tcount;
		Acount = Ccount = Gcount = Tcount = 0;
		double thresholdVal;
		boolean done = false;

		thresholdVal = .75;

		//(from the right side) extend the length of the repeat to the right as long as the last base of all repeats are at least threshold
		while (!done && (rightExtensionLength <= maxRightExtensionLength) && (lastRposition + rightExtensionLength < sequenceLength))
		{	for (int k = 0; k < crispr.numRepeats(); k++ ) {	currRepeatStartIndex = crispr.repeatAt(k);
				currRepeat = dnaSequence.subSequence(currRepeatStartIndex, currRepeatStartIndex + rightExtensionLength);
				char lastChar = currRepeat.charAt(currRepeat.length() - 1);

				if (lastChar == 'A')	Acount++;
				if (lastChar == 'C')	Ccount++;
				if (lastChar == 'G')	Gcount++;
				if (lastChar == 'T')	Tcount++;
			}

			double percentA = (double)Acount/crispr.numRepeats();
			double percentC = (double)Ccount/crispr.numRepeats();
			double percentG = (double)Gcount/crispr.numRepeats();
			double percentT = (double)Tcount/crispr.numRepeats();

			if ( (percentA >= thresholdVal) || (percentC >= thresholdVal) || (percentG >= thresholdVal) || (percentT >= thresholdVal) )
			{	rightExtensionLength++;
				Acount = Ccount = Tcount = Gcount = 0;
			} else {	
				done = true;
			}
		}
		rightExtensionLength--;


 		int leftExtensionLength = 0;
      	Acount = Ccount = Tcount = Gcount = 0;
     	done = false;
		int maxLeftExtensionLength = shortestRepeatSpacing - CRISPRArraysFinder.minSpacerLength - rightExtensionLength;

		//(from the left side) extends the length of the repeat to the left as long as the first base of all repeats is at least threshold
		while (!done && (leftExtensionLength <= maxLeftExtensionLength) && (firstRposition - leftExtensionLength >= 0) )
		{ 	for (int k = 0; k < crispr.numRepeats(); k++ ) {       
				currRepeatStartIndex = crispr.repeatAt(k);
				char firstChar = dnaSequence.getNucleotide(currRepeatStartIndex - leftExtensionLength);

					if (firstChar == 'A')    Acount++;
					if (firstChar == 'C')    Ccount++;
					if (firstChar == 'G')    Gcount++;
					if (firstChar == 'T')    Tcount++;
			}

			double percentA = (double)Acount/crispr.numRepeats();
			double percentC = (double)Ccount/crispr.numRepeats();
			double percentG = (double)Gcount/crispr.numRepeats();
			double percentT = (double)Tcount/crispr.numRepeats();

			if ( (percentA >= thresholdVal) || (percentC >= thresholdVal) || (percentG >= thresholdVal) || (percentT >= thresholdVal)  )
			{       leftExtensionLength++;
					Acount = Ccount = Tcount = Gcount = 0;
			} else {       
				done = true;
			}
		}
		leftExtensionLength--;

		Vector newPositions = (Vector)(crispr.repeats()).clone();

		for (int m = 0; m < newPositions.size(); m++)
		{	int newValue = crispr.repeatAt(m) - leftExtensionLength;
 			newPositions.setElementAt(new Integer(newValue), m);
		}

		int actualPatternLength = rightExtensionLength + leftExtensionLength;


		return new CRISPRArray(newPositions, actualPatternLength);

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
