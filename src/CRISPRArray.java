import java.security.PublicKey;
import java.util.Vector;

import static java.lang.Math.*;

public class CRISPRArray {
    private DNASequence dnaSequence;
    private Vector<Integer> repeatIndices;
    private int repeatLength;

    CRISPRArray(DNASequence dnaSequence, int firstRepeatIndex, int secondRepeatIndex, int length) {
        this.dnaSequence = dnaSequence;
        repeatIndices = new Vector<>();
        addRepeat(firstRepeatIndex);
        addRepeat(secondRepeatIndex);
        setRepeatLength(length);
    }

    public void addRepeat(int index) {
        repeatIndices.add(index);
    }

    public int getSpacerLength(int index) {
        return getRepeatPosition(index + 1) - (getRepeatPosition(index) + repeatLength);
    }

    public int getAverageSpacerLength() {
        int totalSpacerLength = 0;
        for (int i = 0; i < getNumSpacers(); ++i) {
            totalSpacerLength += getSpacerLength(i);
        }
        return totalSpacerLength / getNumSpacers();
    }

    public int getRepeatLength() {
        return repeatLength;
    }
    
    public void setRepeatLength(int len) {
       repeatLength = len;
    }

    public int getRepeatPosition(int index) {
        return repeatIndices.get(index);
    }

    public int getNumRepeats() {
        return repeatIndices.size();
    }

    public int getNumSpacers() {
        return repeatIndices.size() - 1;
    }

    public int getShortestRepeatSpacing() {
        int shortestRepeatSpacing = repeatIndices.get(1) - repeatIndices.get(0);
        for (int i = 0; i < getNumRepeats() - 1; i++) {
            int currRepeatIndex = repeatIndices.get(i);
            int nextRepeatIndex = repeatIndices.get(i + 1);
            int currRepeatSpacing = nextRepeatIndex - currRepeatIndex;
            if (currRepeatSpacing < shortestRepeatSpacing)
                shortestRepeatSpacing = currRepeatSpacing;
        }
        return shortestRepeatSpacing;
    }

    /**
     * Extends all repeats to the left by given size.
     * */
    void extendLeft(int size) {
        for (int i = 0; i < repeatIndices.size(); ++i) {
            repeatIndices.setElementAt(repeatIndices.get(i) - size, i);
        }
        setRepeatLength(repeatLength + size);
    }

    /**
     * Extends all repeats to the right by given size.
     * */
    void extendRight(int size) {
        setRepeatLength(repeatLength + size);
    }

    public int getStartIndex() {
        return repeatIndices.get(0);
    }

    public int getEndIndex() {
        return repeatIndices.get(getNumRepeats() - 1) + repeatLength;
    }

    private String getRepeat(int i) {
        int repeatStart = getRepeatPosition(i);
        return dnaSequence.subSequence(repeatStart, repeatStart + repeatLength);
    }

    private String getSpacer(int i) {
        return dnaSequence.subSequence(getRepeatPosition(i) + repeatLength, getRepeatPosition(i + 1));
    }

    /**
     * Checks if given two sequences are more similar than similarityThreshold
     * */
    public static boolean areSimilar(String s1, String s2, double similarityThreshold)
    {	int maxLength = max(s1.length(), s2.length());
        double similarity = 1.0 - (double)LevenshteinDistance.getLevenshteinDistance(s1, s2)/maxLength;

        return similarity > similarityThreshold;
    }

    /**
     * Checks if spacers are no more similar than given similarityThreshold.
     * Checks if spacers are no more similar with repeats than given similarityThreshold.
     * Checks maximum 5 spacers.
     * */
    public boolean hasNonRepeatingSpacers(double similarityThreshold) {
        final int maxNumSpacersToCheck = 5;
        int numSpacerToCheck = min(maxNumSpacersToCheck, getNumRepeats() - 1);

        if (numSpacerToCheck == 1) {
            String curSpacer = getSpacer(0);
            String firstRepeat = getRepeat(0);
            String secondRepeat = getRepeat(1);
            
            return !areSimilar(curSpacer, firstRepeat, similarityThreshold)
                    && !areSimilar(curSpacer, secondRepeat, similarityThreshold);
        }
        
        for (int i = 0; i < numSpacerToCheck - 1; ++i) {
            String curSpacer = getSpacer(i);
            String repeat = getRepeat(i);
            if (areSimilar(curSpacer, repeat, similarityThreshold)) {
                return false;
            }
            
            for (int j = i + 1; j < numSpacerToCheck; ++j) {
                repeat = getRepeat(j);
                String spacer = getSpacer(j);

                if (areSimilar(curSpacer, spacer, similarityThreshold)
                        || areSimilar(curSpacer, repeat, similarityThreshold)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if spacers' lengths differ no more than spacerToSpacerLengthDiff.
     * Checks if spacers' lengths differ from repeats' length no more than spacerToRepeatLengthDiff.
     * */
    public boolean hasSimilarlySizedSpacers(int spacerToSpacerLengthDiff, int spacerToRepeatLengthDiff) {
        int numSpacers = getNumSpacers();
        for (int i = 0; i < numSpacers; ++i) {
            int currentSpacerLength = getSpacerLength(i);
            if (abs(currentSpacerLength - repeatLength) > spacerToRepeatLengthDiff) {
                return false;
            }

            for (int j = i + 1; j < numSpacers; ++j) {
                if (abs(currentSpacerLength - getSpacerLength(j)) > spacerToSpacerLengthDiff) {
                    return false;
                }
            }
        }
        
        return true;
    }

    public String toString() {
//        String result = ("POSITION" + "\t" + "REPEAT" + "\t" + "POSITION" + "\t" + "SPACER" + "\n");
        String result = "";
        result += "Range: " + getStartIndex() + "-" + getEndIndex() + "\n";

        result += "---------------------------------------------------------------------------------------------\n";
        for (int i = 0; i < repeatIndices.size(); ++i) {
            int repeatIndex = repeatIndices.get(i);
            int nextRepeatIndex = (i < repeatIndices.size() - 1) ? repeatIndices.get(i + 1) : -1;
            int spacerIndex = repeatIndex + repeatLength + 1;

            String repeat = dnaSequence.subSequence(repeatIndex, spacerIndex);
            String spacer = (nextRepeatIndex > 0) ? dnaSequence.subSequence(spacerIndex, nextRepeatIndex) : "";
            result += (repeatIndex + "\t" + repeat + "\t" + spacerIndex + "\t" + spacer + "\n");
        }
        result += "---------------------------------------------------------------------------------------------\n";
        result += "Repeats:" + getNumRepeats() + "\t" + "Average Repeat Length: " + getRepeatLength() + "\t"
                + "Average Spacer Length: " + getAverageSpacerLength() + "\n";
        return result;
    }
}
