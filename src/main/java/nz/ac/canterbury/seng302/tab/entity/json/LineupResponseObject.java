package nz.ac.canterbury.seng302.tab.entity.json;

import java.util.List;

public record LineupResponseObject(
    String pitchString,
    String formationPlayerIds,
    String formationString,
    List<ResponsePlayerObject> players
) {
}
