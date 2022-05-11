/**
 * source: https://www.baeldung.com/java-levenshtein-distance#:~:text=What%20Is%20the%20Levenshtein%20Distance,to%20transform%20x%20into%20y.
 */
public class LevenshteinDistance {
    public static int getLevenshteinDistance(String s1, String s2) {
        int a[][] = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    a[i][j] = j;
                }
                else if (j == 0) {
                    a[i][j] = i;
                }
                else {
                    a[i][j] = Minimum(a[i - 1][j - 1]
                                    + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            a[i - 1][j] + 1,
                            a[i][j - 1] + 1);
                }
            }
        }

        return a[s1.length()][s2.length()];
    }


    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
    private static int Minimum (int x, int y, int z)
    {
        int min;
        min = x;
        if (y < min)
            min = y;
        if (z < min)
            min = z;
        return min;

    }

}
