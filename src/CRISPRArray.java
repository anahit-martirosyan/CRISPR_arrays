import java.util.Vector;

public class CRISPRArray {
    private DNASequence dnaSequence;
    private Vector<Integer> repeatIndices;
    private int repeatsLength;
    private int numRepeats;

    CRISPRArray(DNASequence dnaSequence, int firstRepeatIndex, int secondRepeatIndex, int length) {
        this.dnaSequence = dnaSequence;
        repeatIndices = new Vector<>();
        numRepeats = 0;
        addRepeat(firstRepeatIndex);
        addRepeat(secondRepeatIndex);
        setRepeatLength(length);
    }

    public void addRepeat(int index) {
        repeatIndices.add(index);
        ++numRepeats;
    }

    public void setRepeatLength(int len) {
       repeatsLength = len;
    }

    public int getRepeatIndex(int index) {
        return repeatIndices.get(index);
    }

    public int getNumRepeats() {
        return numRepeats;
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < repeatIndices.size(); ++i) {
            int repeatIndex = repeatIndices.get(i);
            int nextRepeatIndex = (i < repeatIndices.size() - 1) ? repeatIndices.get(i + 1) : -1;
            int spacerIndex = repeatIndex + repeatsLength + 1;

            String repeat = dnaSequence.subSequence(repeatIndex, spacerIndex);
            String spacer = (nextRepeatIndex > 0) ? dnaSequence.subSequence(spacerIndex, nextRepeatIndex) : "";
            result += (repeatIndex + "\t" + repeat + "\t" + spacerIndex + "\t" + spacer + "\n");
        }
        return result;
    }

	public CRISPRArray()
	{	repeatIndices = new Vector();
		repeatsLength = 0;
	}

	public CRISPRArray(Vector positions, int length)
	{	repeatIndices = positions;
		repeatsLength = length;
	}

	public Vector repeats()
	{	return repeatIndices;
	}

	public int repeatLength()
	{	return repeatsLength;
	}

	public int repeatAt(int i)
	{	return ((Integer)repeatIndices.elementAt(i)).intValue();
	}

	public int numRepeats()
	{	return repeatIndices.size();
	}

}
