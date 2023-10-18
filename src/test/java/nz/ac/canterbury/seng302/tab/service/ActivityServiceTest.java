package nz.ac.canterbury.seng302.tab.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@WithMockUser("morgan.english@hotmail.com")
public class ActivityServiceTest {
    ActivityRepository mockActivityRepository;


    ActivityService activityService;

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com",null));
        MockitoAnnotations.openMocks(this);
        mockActivityRepository = Mockito.mock(ActivityRepository.class);
        activityService = new ActivityService(mockActivityRepository);
    }

    @Test
    void test_addingActivity() {
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        Team team = new Team("name", location, "sport");
        User user = new User("email", "firstName", "lastName", LocalDate.now(), location, "passwordHash");
        Activity activity = new Activity(ActivityType.OTHER, team, null, "description", LocalDateTime.now(), LocalDateTime.now(), user, location);
        activityService.save(activity);
        Mockito.verify(mockActivityRepository).save(activity);
    }

    @Test
    void test_getUserTeamActivities() {
        activityService.getMyTeamActivities(1L);
        Mockito.verify(mockActivityRepository).findTeamActivitiesThatHasUser(1);

    }

    @Test
    void test_getUserActivities() {
        activityService.getMyPersonalActivities(1L);
        Mockito.verify(mockActivityRepository).findPersonalActivitiesThatHasUser(1);
    }

    @Test
    public void getByIdCanEdit_NoMatch() {
        User user = new User("a","","", LocalDate.now(),new Location("","","","","",""),"");
        user.setId(1);
        List<Activity> list = new ArrayList<>();
        int[] ints = {0,1};
        Mockito.when(mockActivityRepository.findByIdWithTheUserOfRoleOrIsPersonal(0,1,ints)).thenReturn(list);
        Assertions.assertNull(activityService.getByIdCanEdit(0,user));
    }

    @Test
    public void getByIdCanEdit_HasMatch() {
        Location location = new Location("","","","","","");
        User user = new User("a","","", LocalDate.now(),location,"");
        user.setId(2);
        List<Activity> list = new ArrayList<>();
        Activity activity = new Activity(ActivityType.OTHER,null, null,"", LocalDateTime.now(),LocalDateTime.now(),user, location);
        list.add(activity);
        int[] ints = {0,1};
        Mockito.when(mockActivityRepository.findByIdWithTheUserOfRoleOrIsPersonal(0,2,ints)).thenReturn(list);
        Assertions.assertEquals(activity,activityService.getByIdCanEdit(0,user));
    }

    @Test
    public void sameColour_IsEqual() {
        String colour1 = activityService.generateHexColour(1);
        String colour2 = activityService.generateHexColour(1);
        Assertions.assertEquals(colour1, colour2);
    }

    @Test
    public void diffColour_IsNotEqual() {
        String colour1 = activityService.generateHexColour(1);
        String colour2 = activityService.generateHexColour(2);
        Assertions.assertNotEquals(colour1, colour2);
    }

}
