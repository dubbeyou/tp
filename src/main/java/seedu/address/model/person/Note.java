package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's note in the address book.
 */
public class Note {

    public static final String MESSAGE_CONSTRAINTS = "Notes should be up to 150 characters "
        + "and contain only alphanumeric characters, spaces, commas, and full stops";

    /*
     * Allows up to 150 characters consisting of letters, numbers, spaces, commas, and full stops.
     */
    public static final String VALIDATION_REGEX = "^[a-zA-Z0-9,. ]{0,150}$";
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
        return test.matches(VALIDATION_REGEX);
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
