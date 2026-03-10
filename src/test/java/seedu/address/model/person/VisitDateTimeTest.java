package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class VisitDateTimeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new VisitDateTime(null));
    }

    @Test
    public void constructor_invalidVisitDateTime_throwsIllegalArgumentException() {
        String invalidVisitDateTime = "";
        assertThrows(IllegalArgumentException.class, () -> new VisitDateTime(invalidVisitDateTime));
    }

    @Test
    public void isValidVisitDateTime() {
        // null visit date time
        assertFalse(VisitDateTime.isValidVisitDateTime(null));

        // blank visit date time
        assertFalse(VisitDateTime.isValidVisitDateTime(""));
        assertFalse(VisitDateTime.isValidVisitDateTime(" "));

        // invalid visit date times
        assertFalse(VisitDateTime.isValidVisitDateTime("2026-13-01 14:30")); // invalid month
        assertFalse(VisitDateTime.isValidVisitDateTime("2026-12-32 14:30")); // invalid day
        assertFalse(VisitDateTime.isValidVisitDateTime("2026-12-01 25:00")); // invalid hour
        assertFalse(VisitDateTime.isValidVisitDateTime("2026-12-01 14:60")); // invalid minute
        assertFalse(VisitDateTime.isValidVisitDateTime("01-12-2026 14:30")); // wrong format
        assertFalse(VisitDateTime.isValidVisitDateTime("2026/12/01 14:30")); // wrong separator

        // valid visit date times
        assertTrue(VisitDateTime.isValidVisitDateTime("2026-03-15 14:30"));
        assertTrue(VisitDateTime.isValidVisitDateTime("2026-12-01 00:00"));
        assertTrue(VisitDateTime.isValidVisitDateTime("2026-06-20 23:59"));
    }

    @Test
    public void equals() {
        VisitDateTime visitDateTime = new VisitDateTime("2026-03-15 14:30");

        // same values -> returns true
        assertTrue(visitDateTime.equals(new VisitDateTime("2026-03-15 14:30")));

        // same object -> returns true
        assertTrue(visitDateTime.equals(visitDateTime));

        // null -> returns false
        assertFalse(visitDateTime.equals(null));

        // different types -> returns false
        assertFalse(visitDateTime.equals(5.0f));

        // different values -> returns false
        assertFalse(visitDateTime.equals(new VisitDateTime("2026-12-01 10:00")));
    }

    @Test
    public void testEmptyVisitDateTime() {
        VisitDateTime emptyVisit = new VisitDateTime();
        assertFalse(emptyVisit.isPresent());
        assertEquals("", emptyVisit.toString());
        assertEquals("", emptyVisit.getDisplayValue());
    }

    @Test
    public void testGetDisplayValue() {
        VisitDateTime visitDateTime = new VisitDateTime("2026-03-15 14:30");
        assertEquals("15 Mar 2026, 02:30 pm", visitDateTime.getDisplayValue());
    }
}

