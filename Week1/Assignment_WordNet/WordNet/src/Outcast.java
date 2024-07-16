import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordnet;
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        int length;
        int maxLength = Integer.MAX_VALUE;
        String result = "No result";

        for (String noun : nouns) {
            length = 0;
            for (String anotherNoun : nouns) {
                int current = wordnet.distance(noun, anotherNoun);
                length += current;
//                StdOut.printf("%s to %s: %d\n", noun, anotherNoun, current);
            }
//            StdOut.printf("Prev Max: %d. And now: %d\n", maxLength, length);
            if (length < maxLength) {
                maxLength = length;
                result = noun;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
