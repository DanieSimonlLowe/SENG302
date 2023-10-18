package nz.ac.canterbury.seng302.tab.entity.json;

public record ResponseSubstitutionObject(Long playerOn,
                                         String playerOnPic,
                                         String playerOnName,
                                         Long playerOff,
                                         String playerOffPic,
                                         String playerOffName,
                                         int minute,
                                         Long team,
                                         String teamPic,
                                         Long id) {
}
