import java.io.*;
import java.util.Vector;

public class CRISPRArraysFinder {

    // TODO remove hardcoded values
    public static DNASequence dnaSequence;
    public static String sequenceFile = "";
    public static String outputFile = "";

    public static int minNumberRepeats = 3;
    public static int minRepeatLength = 19;
    public static int maxRepeatLength = 38;
    public static int searchWindowLength = 8;
    public static int minSpacerLength = 19;
    public static int maxSpacerLength = 48;

    /**
     * Parses command line arguments and sets members of the class
     * */
    private static boolean parseArgs(String[] args) {
        boolean isInputFileSpecified = false;
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-inputFile":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Input file name is not specified. Exiting.");
                        return false;
                    }
                    sequenceFile = args[i];
                    isInputFileSpecified = true;
                    break;

                case "-outputFile":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Output file name is not specified.");
                        return false;
                    }
                    outputFile = args[i];
                    break;

                case "-minNumRepeats":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Minimum number of repeats is not specified. Using default value 3.");
                        --i;
                        continue;
                    }
                    minNumberRepeats = Integer.parseInt(args[i]);
                    break;

                case "-minRepeatLength":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Minimum repeat length is not specified. Using default value 19.");
                        --i;
                        continue;
                    }
                    minRepeatLength = Integer.parseInt(args[i]);
                    break;
                case "-maxRepeatLength":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Maximum repeat length is not specified. Using default value 38.");
                        --i;
                        continue;
                    }
                    maxRepeatLength = Integer.parseInt(args[i]);
                    break;


                case "-minSpacerLength":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Minimum spacer length is not specified. Using default value 19.");
                        --i;
                        continue;
                    }
                    minSpacerLength = Integer.parseInt(args[i]);
                    break;
                case "-maxSpacerLength":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Maximum spacer length is not specified. Using default value 48.");
                        --i;
                        continue;
                    }
                    maxSpacerLength = Integer.parseInt(args[i]);
                    break;
                case "-searchWindow":
                    ++i;
                    if (i >= args.length || args[i].startsWith("-")) {
                        System.out.println("Search window length is not specified. Using default value 8.");
                        --i;
                        continue;
                    }
                    searchWindowLength = Integer.parseInt(args[i]);
                    break;
                default:
                    System.out.println("Unknown feature " + args[0] + ". Ignoring.");

            }
        }
        if (!isInputFileSpecified) {
            System.out.println("Input file name is not specified. Exiting.");
            return false;
        }
        return true;
    }

    /**
     * Main function finding CRISPR arrays in DNA sequence
     * @param dnaSequence the DNA sequence provided by the user
     * @return vector of CRISPR arrays found in the sequence
     * */
    private static Vector<CRISPRArray> findCRIPSRs(DNASequence dnaSequence) {
        CRISPRSearchEngine searchEngine = new CRISPRSearchEngine(dnaSequence, minNumberRepeats, minRepeatLength, maxRepeatLength, searchWindowLength, minSpacerLength, maxSpacerLength);
        return searchEngine.findCRISPRs();
    }

    /**
     * Function visualizing program output
     * */
    private static void printCRISPRs(Vector<CRISPRArray> crisprs, PrintWriter writer) {
        if (writer != null) {
            writer.write("ORGANISM: " + dnaSequence.getHeader());
        } else {
            System.out.println("ORGANISM: " + dnaSequence.getHeader());
        }
        for (int i = 0; i < crisprs.size(); ++i) {
            CRISPRArray crispr = crisprs.get(i);
            if (writer != null) {
                writer.write("CRISPR " + i);
                writer.write(crispr.toString());
            } else {
                System.out.println("CRISPR " + i);
                System.out.println(crispr);
            }
        }
    }

    /**
     * Writes program output to outputFile.
     * */
    private static void writeOutput(Vector<CRISPRArray> crisprs) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFile, "UTF-8");
            printCRISPRs(crisprs, writer);
        }
        catch (IOException ex) {
            System.out.println("Exception while writing to file " + outputFile + ".");
            System.out.println(ex);
        }
        finally {
            writer.close();
        }
    }

    public static void main(String[] args) {

        if (!parseArgs(args)) {
            return;
        }

        dnaSequence = DNASequence.build(sequenceFile);
        if (dnaSequence == null) {
            System.out.println("Not a correct fasta file. Exiting.");
            return;
        }

        Vector<CRISPRArray> crisprs = findCRIPSRs(dnaSequence);
        printCRISPRs(crisprs, null);

        if (!outputFile.isEmpty()) {
            writeOutput(crisprs);
        }
    }
}
