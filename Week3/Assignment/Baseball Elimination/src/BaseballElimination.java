import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BST;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.LinkedBag;

public class BaseballElimination {

    private final int teamNumber;
    private final TeamInfo[] teamsInfo;
    private final BST<String, Integer> teamIndex;
    private final int[][] gamesInfo;
    private final int teamVertexInit;
    private final int destVertex;
    private final int maxFlowFromSource;

    public BaseballElimination(String filename) {
        In inputStream = new In(filename);
        this.teamNumber = inputStream.readInt();
        teamsInfo = new TeamInfo[teamNumber];
        teamIndex = new BST<>();
        gamesInfo = new int[teamNumber][teamNumber];

        /* Create an empty flow network
         * The vertices represents:
         * [0]: s
         * [1] -> [n * (n - 1) / 2]: Games
         * [n * (n - 1) / 2 + 1] -> [n * (n + 1) / 2]: Teams;
         * n * (n + 1) / 2 + 1: t
         */
        teamVertexInit = teamNumber * (teamNumber - 1) / 2 + 1;
        destVertex = teamNumber * (teamNumber + 1) / 2 + 1;

        int currentTeam = 0;
        while (!inputStream.isEmpty()) {
            teamsInfo[currentTeam] = new TeamInfo();
            String teamName = inputStream.readString();
            teamsInfo[currentTeam].teamName = teamName;
            teamIndex.put(teamName, currentTeam);
            teamsInfo[currentTeam].wins = inputStream.readInt();        // Wins
            teamsInfo[currentTeam].losses = inputStream.readInt();      // Losses
            teamsInfo[currentTeam].remaining = inputStream.readInt();   // Games to play
            for (int opponent = 0; opponent < teamNumber; opponent++) {
                gamesInfo[currentTeam][opponent] = inputStream.readInt();
            }
            currentTeam += 1;
        }

        int maxFlow = 0;
        for (int t = 0; t < teamNumber - 1; t++) {
            for (int opponent = t + 1; opponent < teamNumber; opponent += 1) {
                maxFlow += gamesInfo[t][opponent];
            }
        }
        maxFlowFromSource = maxFlow;
    }


    public int numberOfTeams() {
        return teamNumber;
    }

    public Iterable<String> teams() {
        return teamIndex.keys();
    }

    public int wins(String team) {
        checkTeam(team);
        return teamsInfo[teamIndex.get(team)].wins;
    }

    public int losses(String team) {
        checkTeam(team);
        return teamsInfo[teamIndex.get(team)].losses;
    }

    public int remaining(String team) {
        checkTeam(team);
        return teamsInfo[teamIndex.get(team)].remaining;
    }

    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);
        return gamesInfo[teamIndex.get(team1)][teamIndex.get(team2)];
    }


    public boolean isEliminated(String team) {
        checkTeam(team);

        // First, do some simple check
        for (String currentTeam: teams()) {
            int currentIndex = teamIndex.get(currentTeam) + teamVertexInit;
            if (wins(team) + remaining(team) - wins(currentTeam) < 0)
                return true;
        }

        FlowNetwork net = initializeNet(team);
        FordFulkerson maxFlow = new FordFulkerson(net, 0, destVertex);
        return ((int) maxFlow.value()) == maxFlowFromSource;
    }

    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);

        FlowNetwork net = initializeNet(team);
        FordFulkerson maxFlow = new FordFulkerson(net, 0, destVertex);
        LinkedBag<String> result = new LinkedBag<>();
        for (String currentTeam : teams()) {
            if (wins(team) + remaining(team) - wins(currentTeam) < 0) {
                result.add(currentTeam);
                continue;
            }
            if (maxFlow.inCut(teamIndex.get(currentTeam) + teamVertexInit))
                result.add(currentTeam);
        }

        return result;
    }

    private class TeamInfo {
        String teamName;
        int wins;
        int losses;
        int remaining;
    }

    private FlowNetwork initializeNet(String team) {
        FlowNetwork result = new FlowNetwork(teamNumber * (teamNumber + 1) / 2 + 2);
        for (int t = 0; t < teamNumber - 1; t++) {
            for (int opponent = t + 1; opponent < teamNumber; opponent += 1) {
                /* Pure math computation
                 * Get the index in the graph that represents the game between t and opponent
                 */
                int gameIndex = (t * teamNumber) - t * (t + 1) / 2 + opponent;
                FlowEdge fromSource = new FlowEdge(0, gameIndex, gamesInfo[t][opponent]);

                // Add two edges from game to team with infinite capacity.
                FlowEdge toTeamV = new FlowEdge(gameIndex, t + teamVertexInit, Double.MAX_VALUE);
                FlowEdge toTeamW = new FlowEdge(gameIndex, opponent + teamVertexInit, Double.MAX_VALUE);

                result.addEdge(fromSource);
                result.addEdge(toTeamV);
                result.addEdge(toTeamW);
            }
        }

        // Insert edges from team to destination vertex t
        for (String currentTeam: teams()) {
            int currentIndex = teamIndex.get(currentTeam) + teamVertexInit;
            double capacity = wins(team) + remaining(team) - wins(currentTeam);
            capacity = (capacity > 0) ? capacity : 0;
            result.addEdge(new FlowEdge(currentIndex, destVertex, capacity));
        }
        return result;
    }

    // Check if the given team is valid
    private void checkTeam(String team) {
        if (team == null || !teamIndex.contains(team))
            throw new IllegalArgumentException("Invalid team");
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}