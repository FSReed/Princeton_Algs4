import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;

import java.util.Arrays;
import java.util.HashSet;


public class WordNet{

    private final Digraph G;
    private final RedBlackBST<Integer, String[]> wordID;
    private final HashSet<String> nounSet;
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("WordNet: Arguments can't be null");
        }
        In syn = new In(synsets);
        wordID = new RedBlackBST<>();
        nounSet = new HashSet<>();
        int verticeNum = 0;
        while (!syn.isEmpty()) {
            String content = syn.readLine();
            String[] elements = content.split(",", 3);
            int id = Integer.parseInt(elements[0]);
            String[] nouns = elements[1].split(" ");
            wordID.put(id, nouns);
            nounSet.addAll(Arrays.asList(nouns));
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
        return nounSet;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nounSet.contains(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        return 0;

    }

    /* a synset (second field of synsets.txt) that is the common ancestor
     * of nounA and nounB in a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        return null;
    }

    public static void main(String[] args) {
        WordNet newOne = new WordNet("synsets.txt", "hypernyms.txt");
        Iterable<String> target = newOne.nouns();
        System.out.println(newOne.isNoun("reed"));
    }
}
