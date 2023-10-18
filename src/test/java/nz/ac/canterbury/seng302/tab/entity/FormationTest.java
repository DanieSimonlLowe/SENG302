package nz.ac.canterbury.seng302.tab.entity;

import nz.ac.canterbury.seng302.tab.exceptions.MismatchSumPlayersSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class FormationTest {

    private static Stream<Arguments> makeFormation_noProblems_test_values() {
        return Stream.of(
                Arguments.of("1-1"),
                Arguments.of("1-2-3-4-5"),
                Arguments.of("99-32-541"),
                Arguments.of("5-4-3-2-1"),
                Arguments.of("1-01")
        );
    }

    @ParameterizedTest
    @MethodSource("makeFormation_noProblems_test_values")
    void makeFormation_noProblems_test(String input) {
        Team team = Mockito.mock(Team.class);
        Executable executable = () -> {
            Formation formation = new Formation(input, "football_pitch", team);
        };
        Assertions.assertDoesNotThrow(executable);
    }

    private static Stream<Arguments> makeFormation_wrongCount_test_values() {
        return Stream.of(
                Arguments.of("1-1",3),
                Arguments.of("1-2-3-4-5",15231),
                Arguments.of("99-32-541",-672),
                Arguments.of("5-4-3-2-1",9),
                Arguments.of("1-1",0),
                Arguments.of("1-1",-2)
        );
    }

    private static Stream<Arguments> makeFormation_invalidInput_test_values() {
        return Stream.of(
                Arguments.of("1-",2),
                Arguments.of("1--3-4-5",15),
                Arguments.of("99-32-5a41",672),
                Arguments.of("5-4-3-0-1",15),
                Arguments.of("-1",2),
                Arguments.of("-",2),
                Arguments.of("",2),
                Arguments.of("32768",1)
        );
    }

    @ParameterizedTest
    @MethodSource("makeFormation_invalidInput_test_values")
    void makeFormation_invalidInput_test(String input) {
        Team team = Mockito.mock(Team.class);
        Executable executable = () -> {
            Formation formation = new Formation(input, "football_pitch", team);
        };

        Assertions.assertThrowsExactly(NumberFormatException.class, executable);
    }

    @Test
    void makeFormation_nullInput_test() {
        Team team = Mockito.mock(Team.class);
        Executable executable = () -> {
            Formation formation = new Formation(null, "football_pitch", team);
        };

        Assertions.assertThrowsExactly(NullPointerException.class, executable);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,15})
    void makeFormation_allNull(int count) {
        Team team = Mockito.mock(Team.class);
        Executable executable = () -> {
            Formation formation = new Formation(Integer.toString(count), "football_pitch", team);
        };
        Assertions.assertDoesNotThrow(executable);
    }

    @ParameterizedTest
    @ValueSource(strings = {"football_pitch", "hockey_pitch"})
    void makeFormation_validSport(String sport) {
        Team team = Mockito.mock(Team.class);
        Executable executable = () -> {
            Formation formation = new Formation("1", sport, team);
            Assertions.assertNotEquals("unknown", formation.getPitchString());
            Assertions.assertEquals(sport, formation.getPitchString());
        };
    }

    @ParameterizedTest
    @ValueSource(strings = {"lkdjlfkjds", "invalid_sport", ""})
    void makeFormation_invalidSport(String sport) {
        Team team = Mockito.mock(Team.class);
        Executable executable = () -> {
            Formation formation = new Formation("1", sport, team);
            Assertions.assertEquals("unknown", formation.getPitchString());
        };
    }
}
