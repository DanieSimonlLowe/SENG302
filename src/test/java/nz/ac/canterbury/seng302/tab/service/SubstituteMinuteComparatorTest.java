package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.stats.Substituted;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SubstituteMinuteComparatorTest {
    @Test
    void compare_lessThen() {
        Substituted s1 = Mockito.mock(Substituted.class);
        Substituted s2 = Mockito.mock(Substituted.class);
        Mockito.when(s1.getSubMinute()).thenReturn(1);
        Mockito.when(s2.getSubMinute()).thenReturn(2);

        SubstituteMinuteComparator substituteMinuteComparator = new SubstituteMinuteComparator();
        Assertions.assertEquals(-1,substituteMinuteComparator.compare(s1,s2));
    }


    @Test
    void compare_same() {
        Substituted s1 = Mockito.mock(Substituted.class);
        Substituted s2 = Mockito.mock(Substituted.class);
        Mockito.when(s1.getSubMinute()).thenReturn(1);
        Mockito.when(s2.getSubMinute()).thenReturn(1);

        SubstituteMinuteComparator substituteMinuteComparator = new SubstituteMinuteComparator();
        Assertions.assertEquals(0,substituteMinuteComparator.compare(s1,s2));
    }


    @Test
    void compare_moreThen() {
        Substituted s1 = Mockito.mock(Substituted.class);
        Substituted s2 = Mockito.mock(Substituted.class);
        Mockito.when(s1.getSubMinute()).thenReturn(3);
        Mockito.when(s2.getSubMinute()).thenReturn(2);

        SubstituteMinuteComparator substituteMinuteComparator = new SubstituteMinuteComparator();
        Assertions.assertEquals(1,substituteMinuteComparator.compare(s1,s2));
    }
}
