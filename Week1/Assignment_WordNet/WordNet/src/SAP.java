import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


public class SAP {

    private final Digraph G;
    private final boolean[] vMarked;
    private final boolean[] wMarked;
    private final int[] vdistTo;
    private final int[] wdistTo;
    private Queue<Integer> vQueue;
    private Queue<Integer> wQueue;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("argument is null");
        this.G = new Digraph(G);
        vMarked = new boolean[G.V()];
        wMarked = new boolean[G.V()];
        vdistTo = new int[G.V()];
        wdistTo = new int[G.V()];
        initializeArray();
    }

    private void initializeArray() {
        for (int i = 0; i < G.V(); i++) {
            vMarked[i] = false;
            wMarked[i] = false;
            vdistTo[i] = Integer.MAX_VALUE;
            wdistTo[i] = Integer.MAX_VALUE;
        }
    }

    private void initializeWithArgument(int v, int w) {
        argCheck(v);
        argCheck(w);
        initializeArray();
        vQueue = new Queue<>();
        wQueue = new Queue<>();

        vInitialize(v);
        wInitialize(w);
    }

    private void initializeWithArgument(Iterable<Integer> v, Iterable<Integer> w) {
        initializeArray();
        vQueue = new Queue<>();
        wQueue = new Queue<>();
        for (Integer vNode : v) {
            argCheck(vNode);
            vInitialize(vNode);
        }
        for (Integer wNode : w) {
            argCheck(wNode);
            wInitialize(wNode);
        }
    }

    private void argCheck(Integer node) {
        if (node == null || node < 0 || node >= G.V())
            throw new IllegalArgumentException("Invalid node number");
    }

    private void vInitialize(Integer vNode) {
        vMarked[vNode] = true;
        vdistTo[vNode] = 0;
        vQueue.enqueue(vNode);
    }

    private void wInitialize(Integer wNode) {
        wMarked[wNode] = true;
        wdistTo[wNode] = 0;
        wQueue.enqueue(wNode);
    }

    // length of shortest ancestral path between v and w; -1 if no such path.
    public int length(int v, int w) {
        initializeWithArgument(v, w);
        int[] result = new int[2];
        sapHelper(result);
        return result[0];
    }

    /* a common ancestor of v and w that participates in a shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(int v, int w) {
        initializeWithArgument(v, w);
        int[] result = new int[2];
        sapHelper(result);
        return result[1];
    }

    /* length of shortest ancestral path between any vertex in v and any vertex in w;
     * -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Arguments can't be null");
        initializeWithArgument(v, w);
        if (vQueue.isEmpty() || wQueue.isEmpty())
            return -1;
        int[] result = new int[2];
        sapHelper(result);
        return result[0];
    }

    /* a common ancestor that participates in shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Arguments can't be null");
        initializeWithArgument(v, w);
        if (vQueue.isEmpty() || wQueue.isEmpty())
            return -1;
        int[] result = new int[2];
        sapHelper(result);
        return result[1];
    }

    private void sapHelper(int[] result) {
        if (result.length != 2) throw new RuntimeException("I only need integer array with 2 elements.");
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
