import java.util.Vector;

public class CRISPRArray {
    private Vector<Integer> repeatIndices;
    private int repeatsLength;

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
