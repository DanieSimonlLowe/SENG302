package nz.ac.canterbury.seng302.tab.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ModerationResultTest {

    @Test
    void testConstructor() {
        boolean flagged = true;
        String flaggedReason = "flaggedReason";
        ModerationResult moderationResult = new ModerationResult(flagged, flaggedReason);
        Assertions.assertEquals(flagged, moderationResult.isFlagged());
        Assertions.assertEquals(flaggedReason, moderationResult.getFlaggedReason());
    }

    @Test
    void testSetFlagged() {
        ModerationResult moderationResult = new ModerationResult(false, "flaggedReason");
        moderationResult.setFlagged(true);
        Assertions.assertTrue(moderationResult.isFlagged());
    }

    @Test
    void testSetFlaggedReason() {
        ModerationResult moderationResult = new ModerationResult(false, "flaggedReason");
        moderationResult.setFlaggedReason("newFlaggedReason");
        Assertions.assertEquals("newFlaggedReason", moderationResult.getFlaggedReason());
    }
}
