import java.util.Vector;

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

    public int getRepeatLength() {
        return repeatLength;
    }
    
    public void setRepeatLength(int len) {
       repeatLength = len;
    }

    public int getRepeatIndex(int index) {
        return repeatIndices.get(index);
    }

    public int getNumRepeats() {
        return repeatIndices.size();
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

    void extendLeft(int size) {
        for (int i = 0; i < repeatIndices.size(); ++i) {
            repeatIndices.setElementAt(repeatIndices.get(i) - size, i);
        }
        setRepeatLength(repeatLength + size);
    }

    void extendRight(int size) {
        setRepeatLength(repeatLength + size);
    }

    public int getEndIndex() {
        return repeatIndices.get(getNumRepeats() - 1) + repeatLength;
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < repeatIndices.size(); ++i) {
            int repeatIndex = repeatIndices.get(i);
            int nextRepeatIndex = (i < repeatIndices.size() - 1) ? repeatIndices.get(i + 1) : -1;
            int spacerIndex = repeatIndex + repeatLength + 1;

            String repeat = dnaSequence.subSequence(repeatIndex, spacerIndex);
            String spacer = (nextRepeatIndex > 0) ? dnaSequence.subSequence(spacerIndex, nextRepeatIndex) : "";
            result += (repeatIndex + "\t" + repeat + "\t" + spacerIndex + "\t" + spacer + "\n");
        }
        return result;
    }
}
