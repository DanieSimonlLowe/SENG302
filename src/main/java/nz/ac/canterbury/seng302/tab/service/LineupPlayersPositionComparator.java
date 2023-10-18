package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.LineupPlayer;

import java.util.Comparator;

public class LineupPlayersPositionComparator implements Comparator<LineupPlayer> {
    @Override
    public int compare(LineupPlayer t1, LineupPlayer t2) {
        return Integer.compare(t1.getPosition(), t2.getPosition());
    }
}
