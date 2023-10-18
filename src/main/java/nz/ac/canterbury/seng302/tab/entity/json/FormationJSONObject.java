package nz.ac.canterbury.seng302.tab.entity.json;

import nz.ac.canterbury.seng302.tab.entity.Formation;

import java.util.List;

/**
 * A JSON response object for formations
 */
public record FormationJSONObject(Long teamId,
                                  List<ResponsePlayerObject> teamPlayerList,
                                  List<Formation> teamFormationList) {
}