import java.io.BufferedReader;
import java.io.FileReader;

public class DNASequence {
    // TODO remove hardcoded value
    private String sequence;
    private String header;
    private DNASequence(String header, String sequence) {
        this.header = header;
        this.sequence = sequence;
    }

    public static DNASequence build(String sequenceFile) {
        // read sequence file
        // set sequence

        BufferedReader inputFile = null;
        String sequence = "";
        String header = "";

        try {
            inputFile = new BufferedReader(new FileReader(sequenceFile));

            header = inputFile.readLine();

            if (header == null || !header.startsWith(">")) {
                return null;
            }

            StringBuffer sb = new StringBuffer();

            String currLine = inputFile.readLine();
            while(currLine != null)
            {	sb.append(currLine);
                currLine = inputFile.readLine();
            }

            sequence = sb.toString().toUpperCase();
        } catch (Exception e) {
            System.out.println("Exception while reading from file " + sequenceFile + ".");
            System.out.println(e);
        } finally {
            try {
                inputFile.close();
            } catch (Exception e)  {}
        }

        if (!sequence.isEmpty()) {
            return new DNASequence(header, sequence);
        }
        return null;
    }
    public String getHeader() {
        return header;
    }
	public int length()
	{
		return sequence.length();
	}

    public String subSequence(int beginIndex, int endIndex) {
        return sequence.substring(beginIndex, endIndex);
    }

    public char getNucleotide(int index) {
        return sequence.charAt(index);
    }
}
