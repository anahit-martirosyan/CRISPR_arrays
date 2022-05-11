/**
 * source: https://www.geeksforgeeks.org/boyer-moore-algorithm-for-pattern-searching/
 * */
public class BoyerMoore {

    static int NO_OF_CHARS = 256;

    /**
     * A utility function to get maximum of two integers.
     */
    static int max (int a, int b) { return (a > b)? a: b; }

    /**
     * The preprocessing function for Boyer Moore's bad character heuristic.
     */
    static void badCharHeuristic( char []str, int size,int badchar[])
    {

        // Initialize all occurrences as -1
        for (int i = 0; i < NO_OF_CHARS; i++)
            badchar[i] = -1;

        // Fill the actual value of last occurrence
        // of a character (indices of table are ascii and values are index of occurrence)
        for (int i = 0; i < size; i++)
            badchar[(int) str[i]] = i;
    }

    /**
     * The preprocessing function for Boyer Moore's good suffix heuristic.
     */
    static void preprocess_strong_suffix(int []shift, int []bpos,
                                         char []pat, int m)
    {
        // m is the length of pattern
        int i = m, j = m + 1;
        bpos[i] = j;

        while(i > 0)
        {
        /*if character at position i-1 is not
        equivalent to character at j-1, then
        continue searching to right of the
        pattern for border */
            while(j <= m && pat[i - 1] != pat[j - 1])
            {
            /* the character preceding the occurrence of t
            in pattern P is different than the mismatching
            character in P, we stop skipping the occurrences
            and shift the pattern from i to j */
                if (shift[j] == 0)
                    shift[j] = j - i;

                //Update the position of next border
                j = bpos[j];
            }
        /* p[i-1] matched with p[j-1], border is found.
        store the beginning position of border */
            i--; j--;
            bpos[i] = j;
        }
    }

    /**
     * The preprocessing function for Boyer Moore's good suffix heuristic (case 2).
     */
    static void process_case2(int []shift, int []bpos,
                              char []pat, int m)
    {
        int i, j;
        j = bpos[0];
        for(i = 0; i <= m; i++)
        {
        /* set the border position of the first character
        of the pattern to all indices in array shift
        having shift[i] = 0 */
            if(shift[i] == 0)
                shift[i] = j;

        /* suffix becomes shorter than bpos[0],
        use the position of next widest border
        as value of j */
            if (i == j)
                j = bpos[j];
        }
    }

    /**
     * Searches for a pattern in given text using Boyer Moore algorithm with Good suffix rule.
     * */
    static int search(String textStr, String patStr)
    {
        char[] text = textStr.toCharArray();
        char[] pat = patStr.toCharArray();
        // s is shift of the pattern
        // with respect to text
        int s = 0, j;
        int m = pat.length;
        int n = text.length;

        int[] badchar = new int[NO_OF_CHARS];

        badCharHeuristic(pat, m, badchar);

        int []bpos = new int[m + 1];
        int []shift = new int[m + 1];

        //initialize all occurrence of shift to 0
        for(int i = 0; i < m + 1; i++)
            shift[i] = 0;

        //do preprocessing
        preprocess_strong_suffix(shift, bpos, pat, m);
        process_case2(shift, bpos, pat, m);

        while(s <= n - m)
        {
            j = m - 1;

        /* Keep reducing index j of pattern while
        characters of pattern and text are matching
        at this shift s*/
            while(j >= 0 && pat[j] == text[s+j])
                j--;

        /* If the pattern is present at the current shift,
        then index j will become -1 after the above loop */
            if (j < 0)
            {
                return s;
            }
            else {
            /*pat[i] != pat[s+j] so shift the pattern
            shift[j+1] times */
                int badCharShift = (s + m < n) ? m - badchar[text[s + m]] : 1;
                s += max(shift[j + 1], badCharShift);
//                s += shift[j + 1];
            }
        }
        return -1;
    }
}
