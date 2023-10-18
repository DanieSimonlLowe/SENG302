//package nz.ac.canterbury.seng302.tab.integration;
//
//import jakarta.annotation.Resource;
//import jakarta.transaction.Transactional;
//import nz.ac.canterbury.seng302.tab.entity.Location;
//import nz.ac.canterbury.seng302.tab.entity.Sport;
//import nz.ac.canterbury.seng302.tab.entity.User;
//import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
//import nz.ac.canterbury.seng302.tab.repository.UserRepository;
//import nz.ac.canterbury.seng302.tab.service.SportService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
//
//@SpringBootTest
//@Transactional
//@WithMockUser(username = "morgan.english@hotmail.com")
//class UserSearchTest {
//    private MockMvc mockMvc;
//    @Resource
//    private UserRepository userRepository;
//
//    @Resource
//    private LocationRepository locationRepository;
//
//    @Resource
//    private SportService sportService;
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    @BeforeEach
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("morgan.english@hotmail.com", null));
//    }
//
//    @Test
//    void searchUserInsufficientQueryLength() throws Exception {
//
//        String searchQuery = "Me";
//
//        mockMvc.perform(post("/search").param("search", searchQuery))
//                .andExpect(flash().attribute("searchError", "Invalid search. Can only contain characters present in a name and search must contain at least 3 characters."));
//    }
//
//    @Test
//    void searchUser() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//        Location location = new Location("", "", "", "", "Memphis", "United States of America");
//        locationRepository.save(location);
//
//        User user = new User("new@email.com", "Unique", "Name", LocalDate.now(), location, "passwordHash");
//
//        userRepository.save(user);
//        expectedUsers.add(user);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//        mockMvc.perform(get("/allProfiles").param("search", "Unique"))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchMultipleUsers() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//        Location location = new Location("", "", "", "", "Memphis", "United States of America");
//        locationRepository.save(location);
//
//        User user1 = new User("newOne@email.com", "FirstNameOne", "LastNameOne", LocalDate.now(), location, "passwordHash1");
//        User user2 = new User("newTwo@email.com", "FirstNameTwo", "LastNameTwo", LocalDate.now(), location, "passwordHash2");
//        User user3 = new User("newTwo@email.com", "DifferentFirst", "LastNameThree", LocalDate.now(), location, "passwordHash3");
//
//        userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
//        expectedUsers.add(user1);
//        expectedUsers.add(user2);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//        mockMvc.perform(get("/allProfiles").param("search", "FirstName"))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchNonExistentUser() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location = new Location("", "", "", "", "Memphis", "United States of America");
//        locationRepository.save(location);
//
//        User user1 = new User("new@email.com", "Unique", "Name", LocalDate.now(), location, "passwordHash1");
//
//        userRepository.save(user1);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles").param("search", "UnknownName"))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchingUser_bySingleCity_validOutput() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
//
//        locationRepository.save(location1);
//        locationRepository.save(location2);
//
//        User expectedUser = new User("johnSmith@email.com", "John", "Smith", LocalDate.now(), location1, "passwordHash1");
//        User notExpectedUser = new User("alex@email.com", "Alex", "Smith", LocalDate.now(), location2, "passwordHash1");
//
//        userRepository.save(expectedUser);
//        userRepository.save(notExpectedUser);
//
//        expectedUsers.add(expectedUser);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles").param("selectedCities", expectedUser.getCity().toUpperCase()))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//
//    }
//
//    @Test
//    void searchingTeam_bySingleSport_validOutput() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        locationRepository.save(location1);
//
//        List<Sport> sports = new ArrayList<>();
//        Sport basketball = sportService.findSportByName("Basketball");
//        sports.add(basketball);
//
//        User expectedUser = new User("johnSmith@email.com", "John", "Smith", LocalDate.now(), location1, "passwordHash1");
//        expectedUser.setSports(sports);
//        User notExpectedUser = new User("alex@email.com", "Alex", "Smith", LocalDate.now(), location1, "passwordHash1");
//
//        userRepository.save(expectedUser);
//        userRepository.save(notExpectedUser);
//
//        expectedUsers.add(expectedUser);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles").param("selectedSports", basketball.getId().toString()))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchingTeam_byMultipleCities_validOutput() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
//        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");
//
//        locationRepository.save(location1);
//        locationRepository.save(location2);
//        locationRepository.save(location3);
//
//        User expectedUser1 = new User("johnSmith@email.com", "John", "Smith", LocalDate.now(), location1, "passwordHash1");
//        User expectedUser2 = new User("alex@email.com", "Alex", "Smith", LocalDate.now(), location2, "passwordHash1");
//        User notExpectedUser = new User("josh@email.com", "Josh", "Smith", LocalDate.now(), location3, "passwordHash1");
//
//        userRepository.save(expectedUser1);
//        userRepository.save(expectedUser2);
//        userRepository.save(notExpectedUser);
//
//        expectedUsers.add(expectedUser2);
//        expectedUsers.add(expectedUser1);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles")
//                        .param("selectedCities", expectedUser1.getCity().toUpperCase())
//                        .param("selectedCities", expectedUser2.getCity().toUpperCase()))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchingUser_byMultipleSports_validOutput() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        locationRepository.save(location1);
//
//        List<Sport> sports = new ArrayList<>();
//        Sport basketball = sportService.findSportByName("Basketball");
//        Sport baseball = sportService.findSportByName("Baseball");
//        sports.add(basketball);
//        sports.add(baseball);
//
//        User expectedUser1 = new User("johnSmith@email.com", "John", "Smith", LocalDate.now(), location1, "passwordHash1");
//        expectedUser1.setSports(sports);
//        User expectedUser2 = new User("alex@email.com", "Alex", "Smith", LocalDate.now(), location1, "passwordHash1");
//        expectedUser2.setSports(sports);
//        User notExpectedUser = new User("josh@email.com", "Josh", "Smith", LocalDate.now(), location1, "passwordHash1");
//
//        userRepository.save(expectedUser1);
//        userRepository.save(expectedUser2);
//        userRepository.save(notExpectedUser);
//
//        expectedUsers.add(expectedUser2);
//        expectedUsers.add(expectedUser1);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles")
//                        .param("selectedSports", basketball.getId().toString())
//                        .param("selectedSports", baseball.getId().toString()))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchingUser_byMultipleCitiesAndKeyword_validOutput() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
//        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");
//
//        locationRepository.save(location1);
//        locationRepository.save(location2);
//        locationRepository.save(location3);
//
//        List<Sport> sports = new ArrayList<>();
//        Sport basketball = sportService.findSportByName("Basketball");
//        sports.add(basketball);
//
//        User expectedUser1 = new User("johnSmith@email.com", "John", "Smith", LocalDate.now(), location1, "passwordHash1");
//        expectedUser1.setSports(sports);
//        User expectedUser2 = new User("alex@email.com", "Alex", "Smith", LocalDate.now(), location2, "passwordHash1");
//        expectedUser2.setSports(sports);
//        User notExpectedUser1 = new User("joshEmmett@email.com", "Josh", "Emmett", LocalDate.now(), location1, "passwordHash1");
//        User notExpectedUser2 = new User("josh@email.com", "Josh", "Smith", LocalDate.now(), location3, "passwordHash1");
//
//        userRepository.save(expectedUser1);
//        userRepository.save(expectedUser2);
//        userRepository.save(notExpectedUser1);
//        userRepository.save(notExpectedUser2);
//
//        expectedUsers.add(expectedUser2);
//        expectedUsers.add(expectedUser1);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles")
//                        .param("search", "Smith")
//                        .param("selectedCities", expectedUser1.getCity().toUpperCase())
//                        .param("selectedCities", expectedUser2.getCity().toUpperCase()))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//
//    @Test
//    void searchingUser_byMultipleCitiesAndMultipleSportsAndKeyword_validOutput() throws Exception {
//        List<User> expectedUsers = new ArrayList<>();
//
//        Location location1 = new Location("", "", "", "", "Portland", "United States of America");
//        Location location2 = new Location("", "", "", "", "Miami", "United States of America");
//        Location location3 = new Location("", "", "", "", "Chicago", "United States of America");
//
//        locationRepository.save(location1);
//        locationRepository.save(location2);
//        locationRepository.save(location3);
//
//        List<Sport> expectedUser1Sports = new ArrayList<>();
//        List<Sport> expectedUser2Sports = new ArrayList<>();
//        List<Sport> notExpectedUser3Sports = new ArrayList<>();
//        Sport basketball = sportService.findSportByName("Basketball");
//        Sport baseball = sportService.findSportByName("Baseball");
//        Sport golf = sportService.findSportByName("Golf");
//        expectedUser1Sports.add(basketball);
//        expectedUser2Sports.add(baseball);
//        notExpectedUser3Sports.add(golf);
//
//        User expectedUser1 = new User("johnSmith@email.com", "John", "Smith", LocalDate.now(), location1, "passwordHash1");
//        expectedUser1.setSports(expectedUser1Sports);
//        User expectedUser2 = new User("alex@email.com", "Alex", "Smith", LocalDate.now(), location2, "passwordHash1");
//        expectedUser2.setSports(expectedUser2Sports);
//        User notExpectedUser1 = new User("joshEmmett@email.com", "Josh", "Emmett", LocalDate.now(), location1, "passwordHash1");
//        User notExpectedUser2 = new User("josh@email.com", "Josh", "Smith", LocalDate.now(), location3, "passwordHash1");
//        User notExpectedUser3 = new User("joseph@email.com", "joseph", "Smith", LocalDate.now(), location1, "passwordHash1");
//        notExpectedUser3.setSports(notExpectedUser3Sports);
//
//        userRepository.save(expectedUser1);
//        userRepository.save(expectedUser2);
//        userRepository.save(notExpectedUser1);
//        userRepository.save(notExpectedUser2);
//        userRepository.save(notExpectedUser3);
//
//        expectedUsers.add(expectedUser2);
//        expectedUsers.add(expectedUser1);
//
//        Page<User> expectedUsersPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10),0);
//
//        mockMvc.perform(get("/allProfiles")
//                        .param("search", "Smith")
//                        .param("selectedCities", expectedUser1.getCity().toUpperCase())
//                        .param("selectedCities", expectedUser2.getCity().toUpperCase())
//                        .param("selectedSports", basketball.getId().toString())
//                        .param("selectedSports", baseball.getId().toString()))
//                .andExpect(model().attribute("entries", expectedUsersPage));
//    }
//}