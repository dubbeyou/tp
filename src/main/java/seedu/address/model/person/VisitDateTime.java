package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a Person's next visit date and time in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidVisitDateTime(String)}
 */
public class VisitDateTime {

    public static final String MESSAGE_CONSTRAINTS =
            "Visit date and time should be in the format: yyyy-MM-dd HH:mm (e.g., 2026-03-15 14:30)";
    public static final String MESSAGE_DATE_CONSTRAINTS = "Dates must be in YYYY-MM-DD format!";

    public static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a",
            Locale.US);

    public final LocalDateTime value;
    private final String originalValue;

    /**
     * Constructs an empty {@code VisitDateTime}.
     */
    public VisitDateTime() {
        this.value = null;
        this.originalValue = "";
    }

    /**
     * Constructs a {@code VisitDateTime}.
     *
     * @param visitDateTime A valid visit date and time string.
     */
    public VisitDateTime(String visitDateTime) {
        requireNonNull(visitDateTime);
        checkArgument(isValidVisitDateTime(visitDateTime), MESSAGE_CONSTRAINTS);
        this.originalValue = visitDateTime.trim();
        this.value = LocalDateTime.parse(this.originalValue, INPUT_FORMATTER);
    }

    /**
     * Returns true if a given string is a valid visit date and time.
     */
    public static boolean isValidVisitDateTime(String test) {
        if (test == null || test.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(test.trim(), INPUT_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if this VisitDateTime has a value.
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Returns the formatted visit date and time for display.
     */
    public String getDisplayValue() {
        if (value == null) {
            return "";
        }
        return value.format(DISPLAY_FORMATTER);
    }

    @Override
    public String toString() {
        if (value == null) {
            return "";
        }
        return value.format(INPUT_FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VisitDateTime)) {
            return false;
        }
        VisitDateTime otherVisitDateTime = (VisitDateTime) other;
        if (value == null && otherVisitDateTime.value == null) {
            return true;
        }
        if (value == null || otherVisitDateTime.value == null) {
            return false;
        }
        return value.equals(otherVisitDateTime.value);
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }
}

