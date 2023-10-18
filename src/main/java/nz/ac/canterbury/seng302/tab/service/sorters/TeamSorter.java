package nz.ac.canterbury.seng302.tab.service.sorters;

import nz.ac.canterbury.seng302.tab.entity.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Sorts the teams as specified.
 * 
 * @author  Daniel Lowe   
 * @version 1.0.0, April 23 
 */
public class TeamSorter {

    /**
     * Implements the TeamAlphaComparator class
     */
    static class TeamAlphaComparator implements Comparator<Team> {
        /**
         * Overrides the compare function to work with teams
         * @param lhs one of the teams to compare
         * @param rhs the other team to compare
         * @return an int of which team is sorted first
         */
        @Override
        public int compare(Team lhs, Team rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }


    /**
     * Implements the TeamLocationGroupComparator class
     */
    static class TeamLocationGroupComparator implements Comparator<TeamLocationGroup> {

        /** The team alpha comparator */
        private static final TeamAlphaComparator teamComp = new TeamAlphaComparator();

       /**
         * Overrides the compare function to work with teams
         * @param t1 one of the teams to compare
         * @param t2 the other team to compare
         * @return an int of which team is sorted first
         */
        @Override
        public int compare(TeamLocationGroup t1, TeamLocationGroup t2) {
            return teamComp.compare(t1.teams.get(0),t2.teams.get(0));
        }
    }

    /**
     * Implements the TeamLocationGroup class
     */
    static class TeamLocationGroup {
        /** The location */
        final String location;

        /** The list of teams */
        final ArrayList<Team> teams;

        /**
         * Constructor for the TeamLocationGroup class
         * @param team the team to add to the list of teams
         */
        TeamLocationGroup(Team team) {
            this.location = team.getLocation().toString();
            this.teams = new ArrayList<>();
            teams.add(team);
        }

        /**
         * Checks if two locations are equal
         * @param location the location to check against
         * @return boolean of whether two locations are the same
         */
        Boolean sameLocation(String location) {
            return this.location.equals(location);
        }

        /**
         * Adds a team to the array
         * @param team the team to add
         */
        void add(Team team) {
            teams.add(team);
        }

        /**
         * Sorts the given team
         */
        void sort() {
            teams.sort(new TeamAlphaComparator());
        }
    }

    /**
     * Custom sorter for teams
     * @param teams the list of teams to sort
     */
    public static void sort(List<Team> teams) {
        ArrayList<TeamLocationGroup> groups = new ArrayList<>();
        for (Team team: teams) {
            boolean notInGroup = true;
            for (TeamLocationGroup group: groups) {
                if (group.sameLocation(team.getLocation().toString())) {
                    notInGroup = false;
                    group.add(team);
                    break;
                }
            }
            if (notInGroup) {
                groups.add(new TeamLocationGroup(team));
            }
        }
        groups.sort(new TeamLocationGroupComparator());
        teams.clear();
        for (TeamLocationGroup group: groups) {
            group.sort();
            teams.addAll(group.teams);
        }
    }
}
