package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AddressTest {

    private static final String VALID_MAX_LENGTH_ADDRESS = "A".repeat(Address.MAX_LENGTH);
    private static final String INVALID_OVER_MAX_LENGTH_ADDRESS = "A".repeat(Address.MAX_LENGTH + 1);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void constructor_invalidAddress_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Address(invalidAddress));
    }

    @Test
    public void isValidAddress() {
        // null address
        assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // Equivalent Partitioning (invalid): blank, whitespace-only, disallowed character sets
        assertFalse(Address.isValidAddress("")); // empty string
        assertFalse(Address.isValidAddress(" ")); // spaces only
        assertFalse(Address.isValidAddress("12/34 Main St")); // slash is not allowed

        // Boundary Value Analysis: max length + 1 should be rejected
        assertFalse(Address.isValidAddress(INVALID_OVER_MAX_LENGTH_ADDRESS)); // exceeds max length

        // Equivalent Partitioning (valid): common real-world address formats
        assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355"));
        assertTrue(Address.isValidAddress("#01-355, Blk 456, Den Road")); // starts with allowed special character
        assertTrue(Address.isValidAddress("Leng Inc, 1234 Market St, San Francisco CA 2349879")); // long address
        assertTrue(Address.isValidAddress("One George Street (Tower A), #15-01"));

        // Boundary Value Analysis: exactly max length should be accepted
        assertTrue(Address.isValidAddress(VALID_MAX_LENGTH_ADDRESS));
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address");

        // same values -> returns true
        assertTrue(address.equals(new Address("Valid Address")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("Other Valid Address")));
    }
}
