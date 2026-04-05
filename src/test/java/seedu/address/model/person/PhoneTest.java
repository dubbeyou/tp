package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

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
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // Equivalent Partitioning (invalid): wrong length, invalid prefix, non-digit, malformed spacing
        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // too short
        assertFalse(Phone.isValidPhone("71234567")); // does not start with 6, 8, or 9
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("93 121534")); // malformed local spacing
        assertFalse(Phone.isValidPhone("93-121534")); // malformed local hyphenation
        assertFalse(Phone.isValidPhone("1800 12 3456")); // malformed toll-free group sizes
        assertFalse(Phone.isValidPhone("1800 1234567")); // malformed toll-free spacing

        // Equivalent Partitioning (valid): local, spaced local, landline, emergency, and toll-free formats
        // valid phone numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("81234567"));
        assertTrue(Phone.isValidPhone("61234567"));
        assertTrue(Phone.isValidPhone("9123 4567"));
        assertTrue(Phone.isValidPhone("6123 4567"));
        assertTrue(Phone.isValidPhone("995"));
        assertTrue(Phone.isValidPhone("9123-4567"));
        assertTrue(Phone.isValidPhone("999"));
        assertTrue(Phone.isValidPhone("6123-4567"));
        assertTrue(Phone.isValidPhone("1700"));
        assertTrue(Phone.isValidPhone("1800 123 4567"));
        assertTrue(Phone.isValidPhone("1800-123-4567"));
        assertTrue(Phone.isValidPhone("18001234567"));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("91234567");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("91234567")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("81234567")));
    }
}
