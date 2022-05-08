public class DNASequence {
    // TODO remove hardcoded value
    private String sequence = "GTTTTAGAGCTATGCTGTTTTGAATGGTCCCAAAACTGCGCTGGTTGATTTCTTCTTGCGCTTTTTGTTTTAGAGCTATGCTGTTTTGAATGGTCCCAAAACTTATATGAACATAACTCAATTTGTAAAAAAGTTTTAGAGCTATGCTGTTTTGAATGGTCCCAAAACAGGAATATCCGCAATAATTAATTGCGCTCTGTTTTAGAGCTATGCTGTTTTGAATGGTCCCAAAACAGTGCCGAGGAAAAATTAGGTGCGCTTGGCGTTTTAGAGCTATGCTGTTTTGAATGGTCCCAAAACTAAATTTGTTTAGCAGGTAAACCGTGCTTTGTTTTAGAGCTATGCTGTTTTGAATGGTCCCAAAACTTCAGCACACTGAGACTTGTTGAGTTCCATGTTTTAGAGCTATGCTGTTTTGAATGGTCTCCATTC";;
    public static DNASequence build(String sequenceFile) {
        // read sequence file
        // set sequence
        return new DNASequence();
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
