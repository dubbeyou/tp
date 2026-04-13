package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    private static final String MIN_LENGTH_PHONE = "9".repeat(Phone.MIN_LENGTH);
    private static final String MIN_LENGTH_PLUS_PHONE = "+" + "9".repeat(Phone.MIN_LENGTH - 1);
    private static final String VALID_MAX_LENGTH_PHONE = "1".repeat(Phone.MAX_LENGTH);
    private static final String INVALID_OVER_MAX_LENGTH_PHONE = "1".repeat(Phone.MAX_LENGTH + 1);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // EP (invalid): blank, too short, wrong first character, over max length, disallowed chars, no digit.
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("9".repeat(Phone.MIN_LENGTH - 1))); // BVA: below minimum length
        assertFalse(Phone.isValidPhone("-91")); // EP (invalid): first character must be digit or plus
        assertFalse(Phone.isValidPhone("+--")); // allowed symbols only, but no digit
        assertFalse(Phone.isValidPhone(INVALID_OVER_MAX_LENGTH_PHONE)); // exceeds max length
        assertFalse(Phone.isValidPhone("+65(9123)4567")); // parentheses are not allowed
        assertFalse(Phone.isValidPhone("1234/5678")); // slash is not allowed

        // EP (valid): only allowed chars, with at least one digit, within min/max length and valid start character.
        assertTrue(Phone.isValidPhone(MIN_LENGTH_PHONE));
        assertTrue(Phone.isValidPhone(MIN_LENGTH_PLUS_PHONE));
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("+65 9123-4567"));
        assertTrue(Phone.isValidPhone("1800 123 4567"));

        // BVA: exactly max length should be accepted.
        assertTrue(Phone.isValidPhone(VALID_MAX_LENGTH_PHONE));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("91234567");

        // EP (valid): same values -> returns true.
        assertEquals(new Phone("91234567"), phone);

        // EP (invalid): null -> returns false.
        assertNotEquals(null, phone);

        // EP (invalid): different types -> returns false.
        assertNotEquals(5.0f, phone);

        // EP (invalid): different values -> returns false.
        assertNotEquals(new Phone("81234567"), phone);
    }
}
