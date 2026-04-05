package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should be an 8-digit local number starting with 6, 8, or 9 (spaces or hyphens allowed "
                    + "as XXXX XXXX or XXXX-XXXX), a toll-free number in the format 1800 XXX XXXX, 1800-XXX-XXXX, "
                    + "or 1800XXXXXXX, or a valid emergency number (995, 999, 1700).";

    /*
     * Regex breakdown (alternatives separated by '|'):
     * - [689]\d{7}               : 8-digit local number starting with 6, 8, or 9
     * - [689]\d{3}[ -]\d{4}      : local number with one separator (space or hyphen), e.g. 9123 4567
     * - 1800[ -]\d{3}[ -]\d{4}   : toll-free with separators, e.g. 1800 123 4567 / 1800-123-4567
     * - 1800\d{7}                : compact toll-free, e.g. 18001234567
     * - 995|999|1700             : accepted emergency/service numbers
     */
    public static final String VALIDATION_REGEX =
            "(?:[689]\\d{7}|[689]\\d{3}[ -]\\d{4}|1800[ -]\\d{3}[ -]\\d{4}|1800\\d{7}|995|999|1700)";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
