import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.LinkedBag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {

    private final SAP sap;
    private final RedBlackBST<Integer, String> wordID;
    private final RedBlackBST<String, LinkedBag<Integer>> nounSet;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("WordNet: Arguments can't be null");
        }
        wordID = new RedBlackBST<>();
        In syn = new In(synsets);
        nounSet = new RedBlackBST<>();
        int verticeNum = 0;
        while (!syn.isEmpty()) {
            String content = syn.readLine();
            String[] elements = content.split(",", 3);
            int id = Integer.parseInt(elements[0]);
            wordID.put(id, elements[1]);
            String[] nouns = elements[1].split(" ");
            LinkedBag<Integer> bag;
            for (String noun : nouns) {
                if (!nounSet.contains(noun)) {
                    bag = new LinkedBag<>();
                } else {
                    bag = nounSet.get(noun);
                }
                bag.add(verticeNum);
                nounSet.put(noun, bag);
            }
            verticeNum += 1;
        }

        /* Generate a new DiGraph */
        Digraph g = new Digraph(verticeNum);

        In hyp = new In(hypernyms);
        int currentPlace = 0;
        int rootNum = 0;
        while (!hyp.isEmpty()) {
            String content = hyp.readLine();
            String[] element = content.split(",");
            int v = Integer.parseInt(element[0]);
            while (v > currentPlace) {
                rootNum += 1;
                if (rootNum > 1)
                    throw new IllegalArgumentException("Only a single root is needed");
                currentPlace += 1;
            }
            for (int p = 1; p < element.length; p++) {
                int m = Integer.parseInt(element[p]);
                g.addEdge(v, m);
            }
            currentPlace++;
        }
        while (currentPlace < verticeNum) {
            rootNum += 1;
            if (rootNum > 1)
                throw new IllegalArgumentException("Only a single root is needed");
            currentPlace += 1;
        }

        DirectedCycle gcycle = new DirectedCycle(g);
        if (gcycle.hasCycle())
            throw new IllegalArgumentException("WordNet shouldn't have any cycle");
        sap = new SAP(g);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounSet.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nounSet.contains(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        LinkedBag<Integer> vSet = nounSet.get(nounA);
        LinkedBag<Integer> wSet = nounSet.get(nounB);
        return sap.length(vSet, wSet);
    }

    /* a synset (second field of synsets.txt) that is the common ancestor
     * of nounA and nounB in a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        LinkedBag<Integer> vSet = nounSet.get(nounA);
        LinkedBag<Integer> wSet = nounSet.get(nounB);
        int nodeNum = sap.ancestor(vSet, wSet);
        return wordID.get(nodeNum);
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        int length = wordnet.distance("Europe", "cat");
        StdOut.printf("Distance is %d\n", length);
    }
}
