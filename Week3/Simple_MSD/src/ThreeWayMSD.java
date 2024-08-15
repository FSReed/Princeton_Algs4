import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class ThreeWayMSD {

    private final String[] aux;
    public ThreeWayMSD(String[] s) {
        aux = new String[s.length];
    }

    private int charAt(String s, int position) {
        if (position == s.length())
            return -1;
        else
            return s.charAt(position) - 'a';
    }

    public void sort(String[] a) {
        sort(a, 0, a.length - 1, 0);
    }

    private void sort(String[] a, int low, int high, int position) {
        if (low >= high) return;

        int[] count = new int[3];
        int mid = charAt(a[low], position);

        for (int i = low; i <= high; i++) {
            int index = charAt(a[i], position);
            if (index < mid) count[1]++;
            else if (index == mid) count[2]++;
        }

        count[2] += count[1];

        for (int i = low; i <= high; i++) {
            int index = charAt(a[i], position);
            if (index < mid) {
                aux[low + count[0]] = a[i];
                count[0]++;
            } else if (index == mid) {
                aux[low + count[1]] = a[i];
                count[1]++;
            } else {
                aux[low + count[2]] = a[i];
                count[2]++;
            }
        }

        for (int i = low; i <= high; i++) {
            a[i] = aux[i];
        }

        sort(a, low,  low + count[0] - 1, position);
        sort(a, low + count[0], low + count[1] - 1, position + 1);
        sort(a, low + count[1], high, position);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] test = in.readAllStrings();
        ThreeWayMSD triMsd = new ThreeWayMSD(test);
        triMsd.sort(test);
        for (String s : test) {
            StdOut.printf("%s\n", s);
        }
    }
}
