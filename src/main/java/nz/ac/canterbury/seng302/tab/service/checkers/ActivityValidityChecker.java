package nz.ac.canterbury.seng302.tab.service.checkers;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ActivityValidityChecker {

    static final Logger logger = LoggerFactory.getLogger(ActivityValidityChecker.class);


    /**
     * Checks if the given type is valid or not
     * @param type the type of activity
     * @param team the team relating to the activity
     * @return a boolean of whether the type is valid or not
     */
    public static boolean isValidType (String type, Team team){
        type = type.toLowerCase();
        switch (type) {
            case "game", "friendly" -> {
                if (team == null) {
                    logger.info("ERROR Team is required");
                    return false;
                }
                return true;
            }
            case "training", "competition", "other" -> {
                return true;
            }
        }
        logger.info("ERROR Not valid activity type");
        return false;
    }

    /**
     * Checks if the given start and end dates are valid
     * @param start the start date of the activity
     * @param end the end date of the activity
     * @return a boolean of whether the given start and end dates are valid
     */
    public static boolean isValidStartEnd (LocalDateTime start, LocalDateTime end) {

        if (end.isAfter(start)) {
            return true;
        }
        logger.info("ERROR: Invalid period");
        return false;
    }

    /**
     * Checks if the given start and end dates are after the teams creation date
     * @param start the start date of the activity
     * @param end the end date of the activity
     * @param team the related team
     * @return a boolean of whether the given start and end dates are valid
     */
    public static boolean isValidTeamCreation (LocalDateTime start, LocalDateTime end, Team team) {
        if(team == null) {
            return true;
        }

        if(!(start.isAfter(team.getCreationDate()) && end.isAfter(team.getCreationDate()))) {
            logger.error("ERROR: Team was created on " + team.getCreationDate() + ", which is after the given start or end time");
            return false;
        }
        return true;
    }

    /**
     * Checks if the given description is valid
     * @param desc A description of the activity
     * @return a boolean of whether the description is valid
     */
    public static boolean isValidDescription (String desc) {
        if (!desc.matches("^(?!$|\\d+$|[^\\p{L}]+$|^.$).*$") ) {
            logger.info("ERROR Description invalid");
            return false;
        }

        if (desc.length() > 150 ) {
            logger.info("ERROR Description too long");
            return false;
        }

        return true;
    }

    public static boolean needsTeam(String type) {
        return switch (type) {
            case "game", "friendly" -> true;
            default -> false;
        };
    }
}
