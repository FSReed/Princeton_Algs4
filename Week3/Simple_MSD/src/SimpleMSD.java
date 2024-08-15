import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class SimpleMSD {

    private final int R;
    private final String[] aux;
    public SimpleMSD(int size, int R) {
        this.R = R;
        aux = new String[size];
    }

    public void sort(String[] a) {
        sort(a, 0, a.length - 1, 0);
    }

    private int charAt(String s, int d) {
        if (d == s.length())
            return -1;
        else
            return s.charAt(d) - 'a';
    }

    public void sort(String[] a, int low, int high, int position) {
        if (low >= high) return;

        int[] count = new int[R + 2];

        for (int i = low; i <= high; i++) {
            int index = charAt(a[i], position);
            count[index + 2]++;
        }

        for (int r = 1; r < R + 2; r++) {
            count[r] += count[r - 1];
        }

        for (int i = low; i <= high; i++) {
            int index = charAt(a[i], position);
            aux[low + count[index + 1]] = a[i];
            count[index + 1]++;
        }

        for (int i = low; i <= high; i++) {
            a[i] = aux[i];
        }

        for (int r = 0; r < R; r++) {
            sort(a, low + count[r], low + count[r + 1] - 1, position + 1);
        }
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] test = in.readAllStrings();
        SimpleMSD msd = new SimpleMSD(test.length, 26);
        msd.sort(test);
        for (String s : test) {
            StdOut.printf("%s\n", s);
        }
    }
}