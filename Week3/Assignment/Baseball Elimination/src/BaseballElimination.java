import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BST;

public class BaseballElimination {

    private final int teamNumber;
    private final TeamInfo[] teamsInfo;
    private final BST<String, Integer> teamIndex;
    private final int[][] gamesInfo;

    public BaseballElimination(String filename) {
        In inputStream = new In(filename);
        this.teamNumber = inputStream.readInt();
        teamsInfo = new TeamInfo[teamNumber];
        teamIndex = new BST<>();
        gamesInfo = new int[teamNumber][teamNumber];

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

    }

    public int numberOfTeams() {
        return teamNumber;
    }

    public Iterable<String> teams() {
        return teamIndex.keys();
    }

    public int wins(String team) {
        return teamsInfo[teamIndex.get(team)].wins;
    }

    public int losses(String team) {
        return teamsInfo[teamIndex.get(team)].wins;
    }

    public int remaining(String team) {
        return teamsInfo[teamIndex.get(team)].wins;
    }

    public int against(String team1, String team2) {
        return gamesInfo[teamIndex.get(team1)][teamIndex.get(team2)];
    }
    public boolean isEliminated(String team) {
        return false;
    }

    public Iterable<String> certificateOfElimination(String team) {
        return null;
    }

    private class TeamInfo {
        String teamName;
        int wins;
        int losses;
        int remaining;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            StdOut.printf("Team: %s\n", team);
//            if (division.isEliminated(team)) {
//                StdOut.print(team + " is eliminated by the subset R = { ");
//                for (String t : division.certificateOfElimination(team)) {
//                    StdOut.print(t + " ");
//                }
//                StdOut.println("}");
//            }
//            else {
//                StdOut.println(team + " is not eliminated");
//            }
        }
    }
}