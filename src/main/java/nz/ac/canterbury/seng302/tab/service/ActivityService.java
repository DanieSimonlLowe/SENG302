package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The service for saving and querying activity's to and from the database.
 * */
@Service
public class ActivityService {

    final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * saves the activity into the database.
     * @param activity the Activity being saved.
     * */
    public void save(Activity activity) {
        activityRepository.save(activity);
    }

    /**
     * Gets an activity from persistence
     * @param id of activity to retrieve
     * @return activity with matching id or null if none exists
     */
    public Activity getActivity(long id) throws NotFoundException {
        return activityRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    /**
     * Gets all activities from persistence that relate to the given user
     * @param userId of the user that requires the activities
     * @return the matching activities
     */
    public List<Activity> getMyTeamActivities(Long userId) { return activityRepository.findTeamActivitiesThatHasUser(userId); }


    /**
     * Gets all team activities from persistence that relate to the given user
     * @param userId of the user that requires the activities
     * @return the matching activities
     */
    public List<Activity> getMyPersonalActivities(Long userId) { return activityRepository.findPersonalActivitiesThatHasUser(userId); }


    public Activity getByIdCanEdit(long id, User user) {
        int[] roles = {Role.MANAGER.ordinal(), Role.COACH.ordinal()};
        List<Activity> activities = activityRepository.findByIdWithTheUserOfRoleOrIsPersonal(id,user.getId(),roles);
        if (activities.isEmpty()) {
            return null;
        } else {
            return activities.get(0);
        }
    }

    public List<Activity> getActivitiesByTeamId(Long teamId) {
        return activityRepository.findAllByTeamId(teamId);
    }

    /**
     * Checks whether the given user was the person who created the given activity
     * @param id   the id of the activity
     * @param user the user to check
     * @return a list of activities
     */
    public Activity checkCanEdit(long id, User user) {
        return activityRepository.findByCreatorUserId(id, user.getId());
    }

    /**
     * Generates a colour given the name of a team
     * @param teamName The string the colour is generated from
     * @return a hex value for the generated colour as a string
     */

    public String generateHexColour(long teamName) {
        String[] teamColourList = new String[]{"#7faac8", "#ffbf00", "#aa86ee", "#8db600",
        "#20a8b6", "#ef7f32", "#ace1af", "#fbec5d", "#ffdab9", "#e5aa70", "#addfad", "#afeeee"
        , "#ccccff", "#9eb9d4", "#00cccc", "#ffcc00", "#d8bfd8", "#f5deb3", "#ebc2af", "#5fbeaf",
        "#ff6666", "#ffcc99", "#ffccff", "#ccffcc", "#ccffff", "#99ccff", "#cc99ff", "#ff99cc",
        "#dc855c", "#f9e69b", "#b3b6b9", "#d3bbb0", "#b3a947", "#db8e2b"};
        return teamColourList[(int) (teamName % teamColourList.length)];
    }
}
