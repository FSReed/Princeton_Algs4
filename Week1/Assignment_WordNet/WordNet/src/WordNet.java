import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.LinkedBag;


public class WordNet{

    private final Digraph G;
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
            wordID.put(verticeNum, elements[1]);
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
        G = new Digraph(verticeNum);

        In hyp = new In(hypernyms);
        while (!hyp.isEmpty()) {
            String content = hyp.readLine();
            String[] element = content.split(",");
            int v = Integer.parseInt(element[0]);
            for (int p = 1; p <element.length; p++) {
                int m = Integer.parseInt(element[p]);
                G.addEdge(v, m);
            }
        }
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
        SAP sap = new SAP(G);
        return sap.length(vSet, wSet);
    }

    /* a synset (second field of synsets.txt) that is the common ancestor
     * of nounA and nounB in a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        LinkedBag<Integer> vSet = nounSet.get(nounA);
        LinkedBag<Integer> wSet = nounSet.get(nounB);
        SAP sap = new SAP(G);
        int nodeNum = sap.ancestor(vSet, wSet);
        return wordID.get(nodeNum);
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        int length = wordnet.distance("horse", "cat");
        System.out.println(length);
    }
}
