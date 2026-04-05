package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's address in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final int MAX_LENGTH = 120;
    public static final String MESSAGE_CONSTRAINTS =
            "Addresses should not be blank, must be at most " + MAX_LENGTH
                    + " characters, and may only contain alphanumeric characters, spaces, commas, periods, "
                    + "apostrophes, parentheses, hyphens, and hashtags";

    /*
     * Regex breakdown:
     * - [\p{Alnum}]                 : first character must be alphanumeric (prevents blank input)
     * - [\p{Alnum} ,.#'()\-]*       : remaining characters may include letters/digits, spaces, and
     *                                 common address punctuation: comma, period, hashtag, apostrophe,
     *                                 parentheses, and hyphen
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ,.#'()\\-]*";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = address;
    }

    /**
     * Returns true if a given string is a valid address.
     */
    public static boolean isValidAddress(String test) {
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Address)) {
            return false;
        }

        Address otherAddress = (Address) other;
        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
