package nz.ac.canterbury.seng302.tab.entity.json;

public record ResponseActivityObject (Long activityId,
                                      String activityType,
                                      Long teamId,
                                      String teamName,
                                      String teamImg,
                                      Long oppositionId,
                                      String oppositionName,
                                      String oppositionImg){
}
