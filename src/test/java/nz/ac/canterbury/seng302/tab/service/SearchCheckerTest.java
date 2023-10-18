package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.service.checkers.SearchChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchCheckerTest {
    @Test
    void normalSearch() {
        Assertions.assertTrue(SearchChecker.isValidSearch("abc"));
        Assertions.assertTrue(SearchChecker.isValidSearch("abcdefghijkcalkmnopoasdasdfasfsdfadsfagsasdgadsgadgssdgadsgasadgdsgsdgagdsagdsadgsgsdasdgdsgajhblfhjghjliuawhtjkbenwqlkjhlai"));
    }

    @Test
    void normalWithSpace() {
        Assertions.assertTrue(SearchChecker.isValidSearch("ab c"));
        Assertions.assertTrue(SearchChecker.isValidSearch("abcdefghijkcalkmnopoasdasdfasfsdfadsfagsasdgadsgadgssd gadsgasadgdsgsdgagdsagdsadgsgsdasdgdsgajhblfhjghjliuawhtjkbenwqlkjhlai"));
        Assertions.assertTrue(SearchChecker.isValidSearch("ab-c"));
    }

    @Test
    void weirdCharSearch() {
        Assertions.assertTrue(SearchChecker.isValidSearch("sdŒ"));
        Assertions.assertTrue(SearchChecker.isValidSearch("λωρρ"));
        Assertions.assertTrue(SearchChecker.isValidSearch("年Œω"));
    }

    @Test
    void toShortSearch() {
        Assertions.assertFalse(SearchChecker.isValidSearch("ab"));
        Assertions.assertFalse(SearchChecker.isValidSearch("dŒ"));
        Assertions.assertFalse(SearchChecker.isValidSearch("ωρ"));
        Assertions.assertFalse(SearchChecker.isValidSearch("年ω"));
        Assertions.assertFalse(SearchChecker.isValidSearch("月月"));
    }

    @Test
    void toShortSearchWithSpace() {
        Assertions.assertFalse(SearchChecker.isValidSearch("ab "));
        Assertions.assertFalse(SearchChecker.isValidSearch("dŒ "));
        Assertions.assertFalse(SearchChecker.isValidSearch("ω ρ"));
        Assertions.assertFalse(SearchChecker.isValidSearch("年 ω "));
        Assertions.assertFalse(SearchChecker.isValidSearch("月-月 "));
    }

    @Test
    void notLetters() {
        Assertions.assertFalse(SearchChecker.isValidSearch("abc 0"));
        Assertions.assertFalse(SearchChecker.isValidSearch("ab/"));
        Assertions.assertFalse(SearchChecker.isValidSearch("a=bc"));
        Assertions.assertFalse(SearchChecker.isValidSearch("   "));
    }
}
