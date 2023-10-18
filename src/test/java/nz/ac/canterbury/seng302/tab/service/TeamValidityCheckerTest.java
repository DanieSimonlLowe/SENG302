package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.exceptions.InvalidTeamException;
import nz.ac.canterbury.seng302.tab.exceptions.NotFoundException;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamMemberRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.checkers.TeamValidityChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.stream.Stream;

public class TeamValidityCheckerTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamMemberRepository memberRepository;



    @Mock
    private UserService userService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    public TeamService teamService;

    @Mock
    private final TeamValidityChecker teamValidityChecker = new TeamValidityChecker(teamMemberService);

    @BeforeEach
    void BeforeEach() {
        MockitoAnnotations.openMocks(this);
    }


    @ValueSource(strings = {"ああ, ああ", "a, nz", "123 lane, USA", "123-lane, uas", "mals st., CAT", "dan's lane, DA"}) // six numbers
    private static Stream<Arguments> provideLocationPartsForDiffValidLocation() {
        return Stream.of(
                Arguments.of("", "", "", "", "ああ", "ああ"),
                Arguments.of("", "", "", "", "a", "nz"),
                Arguments.of("123 Lane", "", "", "", "christchurch", "new zealand"),
                Arguments.of("8 ああ", "Apt 23", "", "", "Xi-an", "China"),
                Arguments.of("8 ciff-main st", "", "Upper riccarton", "XFGY-123", "Mount Gambier", "Australia")
        );
    }

    private static Stream<Arguments> provideLocationPartsForDiffInvalidLocation() {
        return Stream.of(
                Arguments.of("", "", "", "", "あ2あ", "ああ"),
                Arguments.of("", "", "", "", "a", "n2z"),
                Arguments.of("123 Lane!", "", "", "", "christchurch", "new zealand"),
                Arguments.of("8 ああ", "Apt 23!", "", "", "Xi-an", "China"),
                Arguments.of("8 ciff-main st", "", "@Upper riccarton", "XFGY-123*", "Mount Gambier", "Australia"),
                Arguments.of("aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS"
                        ,"aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS",
                        "aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS",
                        "aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS",
                        "aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS",
                        "aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"a","{a}", "a.", "a a", "z" , "AZaz BDS", "asb 123 321 asb", "ああ.", "123"}) // six numbers
    void isValidName_valid(String value) {
        Assertions.assertTrue(TeamValidityChecker.isValidName(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a'","{a}[", "a.=", "  ", "( )", "+21390321 jADHudb", "", "aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS"}) // six numbers
    void isValidName_invalid(String value) {
        Assertions.assertFalse(TeamValidityChecker.isValidName(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a","abc", "a's", "a a", "z" , "AZaz BDS", "asBasb", "ああ-test"}) // six numbers
    void isValidSport_valid(String value) {
        Assertions.assertTrue(TeamValidityChecker.isValidSport(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a!","{a}[", "a.=", "  ", "( )", "+21390321 jADHudb", "", "aaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaAaaaaaaBAS"}) // six numbers
    void isValidSport_invalid(String value) {
        Assertions.assertFalse(TeamValidityChecker.isValidSport(value));
    }



    @Test
    void canRemoveOnlyManager() throws NotFoundException, InvalidTeamException {
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        Team team = new Team(
                "Grizzles",
                location,
                "Basketball"
        );
        teamRepository.save(team);
        LocalDate date = LocalDate.now();
        User newUser = new User("newUser@email.com", "testFirstName", "testLastName", date, location, "testHash");
        userService.save(newUser);

        String token = team.getTeamToken();

        TeamMember manager = teamMemberService.addTeamMember(newUser, token);
        teamMemberService.changeRole(manager, Role.MANAGER);
        Assertions.assertFalse(teamValidityChecker.canRemoveManager(team));

    }

    @Test
    void canAddFourthManager() throws NotFoundException, InvalidTeamException {
        Location location = new Location("", "", "", "", "Memphis", "United States of America");
        Team team = new Team(
                "Grizzles",
                location,
                "Basketball"
        );
        teamRepository.save(team);
        LocalDate date = LocalDate.now();
        User newUser = new User("newUser@email.com", "testFirstName", "testLastName", date, location, "testHash");
        User newUser2 = new User("newUser2@email.com", "testFirstName", "testLastName", date, location, "testHash");
        User newUser3 = new User("newUser3@email.com", "testFirstName", "testLastName", date, location, "testHash");
        User newUser4 = new User("newUser4@email.com", "testFirstName", "testLastName", date, location, "testHash");
        userService.save(newUser);
        userService.save(newUser2);
        userService.save(newUser3);
        userService.save(newUser4);

        String token = team.getTeamToken();

        TeamMember manager = teamMemberService.addTeamMember(newUser, token);
        manager = teamMemberService.changeRole(manager, Role.MANAGER);
        TeamMember manager2 = teamMemberService.addTeamMember(newUser2, token);
        manager2 = teamMemberService.changeRole(manager2, Role.MANAGER);
        TeamMember manager3 = teamMemberService.addTeamMember(newUser3, token);
        manager3 = teamMemberService.changeRole(manager3, Role.MANAGER);
        TeamMember manager4 = teamMemberService.addTeamMember(newUser4, token);
        manager4 = teamMemberService.changeRole(manager4, Role.MANAGER);

        Assertions.assertFalse(teamValidityChecker.canAddManager(team));
    }
}
