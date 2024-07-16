import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


public class SAP {

    private final Digraph G;
    private boolean[] vMarked;
    private boolean[] wMarked;
    private int[] vdistTo;
    private int[] wdistTo;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("argument is null");
        this.G = G;
        initializeArray();
    }

    private void initializeArray() {
        vMarked = new boolean[G.V()];
        wMarked = new boolean[G.V()];
        vdistTo = new int[G.V()];
        wdistTo = new int[G.V()];
        for (int i = 0; i < G.V(); i++) {
            vdistTo[i] = Integer.MAX_VALUE;
            wdistTo[i] = Integer.MAX_VALUE;
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path.
    public int length(int v, int w) {
        int[] result = new int[2];
        sapHelper(v, w, result);
        return result[0];
    }

    /* a common ancestor of v and w that participates in a shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(int v, int w) {
        int[] result = new int[2];
        sapHelper(v, w, result);
        return result[1];
    }

    /* length of shortest ancestral path between any vertex in v and any vertex in w;
     * -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Arguments can't be null");
        int length = -1;
        int[] result = new int[2];
        for (Integer vNode : v) {
            for (Integer wNode : w) {
                if (vNode == null || wNode == null)
                    throw new IllegalArgumentException("Arguments can't be null");
                sapHelper(vNode, wNode, result);
                if (result[0] < length || length < 0) {
                    length = result[0];
                }
            }
        }
        return length;
    }

    /* a common ancestor that participates in shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Arguments can't be null");
        int length = -1;
        int ances = -1;
        int[] result = new int[2];

        for (Integer vNode : v) {
            for (Integer wNode : w) {
                if (vNode == null || wNode == null)
                    throw new IllegalArgumentException("Arguments can't be null");
                sapHelper(vNode, wNode, result);
                if (result[0] < length || length < 0) {
                    length = result[0];
                    ances = result[1];
                }
            }
        }
        return ances;
    }

    private void sapHelper(int v, int w, int[] result) {
        if (result.length != 2) throw new RuntimeException("I only need integer array with 2 elements.");
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V())
            throw new IllegalArgumentException("Index out of bounds");
        Queue<Integer> vQueue = new Queue<>();
        Queue<Integer> wQueue = new Queue<>();
        initializeArray();
        vdistTo[v] = 0;
        wdistTo[w] = 0;
        vMarked[v] = true;
        wMarked[w] = true;
        vQueue.enqueue(v);
        wQueue.enqueue(w);
        int vTemp = -1;
        int wTemp = -1;
        int length = -1;
        int ances = -1;

        while (!vQueue.isEmpty() || !wQueue.isEmpty()) {
            if (!vQueue.isEmpty()) {
                vTemp = vQueue.dequeue();
                for (Integer vAdj : G.adj(vTemp)) {
                    if (!vMarked[vAdj]) {
                        vQueue.enqueue(vAdj);
                        vMarked[vAdj] = true;
                        vdistTo[vAdj] = vdistTo[vTemp] + 1;
                    }
                }
            }
            if (!wQueue.isEmpty()) {
                wTemp = wQueue.dequeue();
                for (Integer wAdj : G.adj(wTemp)) {
                    if (!wMarked[wAdj]) {
                        wQueue.enqueue(wAdj);
                        wMarked[wAdj] = true;
                        wdistTo[wAdj] = wdistTo[wTemp] + 1;
                    }
                }
            }
            int tmpL;
            if (wMarked[vTemp]) {
                tmpL = vdistTo[vTemp] + wdistTo[vTemp];
                if (tmpL < length || length < 0) {
                    length = tmpL;
                    ances = vTemp;
                }
            }
            if (vMarked[wTemp]) {
                tmpL = vdistTo[wTemp] + wdistTo[wTemp];
                if (tmpL < length || length < 0) {
                    length = tmpL;
                    ances = wTemp;
                }
            }
        }
        result[0] = length;
        result[1] = ances;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
