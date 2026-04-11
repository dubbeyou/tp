package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a Person's next visit date and time in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidVisitDateTime(String)}
 */
public class VisitDateTime {

    public static final String MESSAGE_CONSTRAINTS = "Visit date and time must be a valid future date and time in "
                    + "the format: yyyy-MM-dd HH:mm (e.g., 2026-03-15 14:30)";
    public static final String MESSAGE_DATE_CONSTRAINTS = "Date must be a valid date in the format yyyy-MM-dd "
                    + "(e.g., 2026-03-15) or the keyword 'today'.";

    public static final DateTimeFormatter INPUT_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd HH:mm")
            .toFormatter();
    public static final DateTimeFormatter DATE_INPUT_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd")
            .toFormatter();
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a",
            Locale.US);

    private final LocalDateTime value;

    /**
     * Constructs an empty {@code VisitDateTime}.
     */
    public VisitDateTime() {
        this.value = null;
    }

    /**
     * Constructs a {@code VisitDateTime}.
     *
     * @param visitDateTime A valid visit date and time string.
     */
    public VisitDateTime(String visitDateTime) {
        this.value = parseVisitDateTime(visitDateTime);
    }

    /**
     * Parses a visit date-time string and returns the normalized value.
     */
    public static LocalDateTime parseVisitDateTime(String visitDateTime) {
        requireNonNull(visitDateTime);
        String trimmedVisitDateTime = visitDateTime.trim();
        checkArgument(!trimmedVisitDateTime.isEmpty(), MESSAGE_CONSTRAINTS);
        try {
            return LocalDateTime.parse(trimmedVisitDateTime, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS, e);
        }
    }

    /**
     * Parses a date string and returns the normalized value.
     */
    public static java.time.LocalDate parseDate(String date) {
        requireNonNull(date);
        return java.time.LocalDate.parse(date.trim(), DATE_INPUT_FORMATTER);
    }

    /**
     * Returns true if a given string is a valid visit date and time.
     */
    public static boolean isValidVisitDateTime(String test) {
        try {
            parseVisitDateTime(test);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
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

    /**
     * Returns the LocalDateTime value.
     */
    public LocalDateTime getValue() {
        return value;
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
        return Objects.equals(value, otherVisitDateTime.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
