package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's note in the address book.
 */
public class Note {

    public static final int MAX_LENGTH = 150;

    public static final String MESSAGE_CONSTRAINTS = "Notes should be up to " + MAX_LENGTH + " characters "
        + "and contain only alphanumeric characters, spaces, commas, and full stops";

    /*
     * Regex breakdown:
     * - ^ and $            : match the whole note string
     * - [a-zA-Z0-9,. ]*    : zero or more letters, digits, commas, periods, or spaces
     */
    public static final String VALIDATION_REGEX = "^[a-zA-Z0-9,. ]*$";
    public final String value;

    /**
     * Constructs a {@code Note}.
     *
     * @param note A valid note.
     */
    public Note(String note) {
        requireNonNull(note);
        this.value = note;
    }

    /**
     * Returns true if a given string is a valid note.
     */
    public static boolean isValidNote(String test) {
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if this VisitDateTime has a value.
     */
    public boolean isPresent() {
        return !value.isEmpty();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Note // instanceof handles nulls
                && value.equals(((Note) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
