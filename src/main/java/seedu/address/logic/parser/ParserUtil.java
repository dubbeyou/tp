package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.person.VisitDateTime;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer)
     *         or if the value exceeds the allowable integer range.
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();

        // First check format (digits only)
        if (!trimmedIndex.matches("\\d+")) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        int value;
        try {
            value = Integer.parseInt(trimmedIndex);
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INDEX_TOO_LARGE);
        }

        if (value <= 0) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        return Index.fromOneBased(value);
    }

    /**
     * Parses {@code rawInput} into a single {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * Ensures that the input contains exactly one token representing an index. Inputs that are empty, contain multiple
     * tokens, or are not numeric will result in a {@code ParseException} with the provided {@code usageMessage}.
     * Delegates to {@link #parseIndex(String)} for validation of the index value.
     *
     * @throws ParseException if the input format is invalid or does not represent a valid index.
     */
    public static Index parseSingleIndexOrThrow(String rawInput, String usageMessage)
            throws ParseException {

        String trimmed = rawInput.trim();

        // Case 1: missing index
        if (trimmed.isEmpty()) {
            throw new ParseException(usageMessage);
        }

        String[] parts = trimmed.split("\\s+");

        // Case 2: more than one token
        if (parts.length != 1) {
            throw new ParseException(usageMessage);
        }

        String token = parts[0];

        // Case 3: not number-like (reject abc, -, etc.)
        // Accepts optional leading '-' to allow negative input (e.g. "-1").
        // Actual validation (rejecting negatives/zero) is handled in parseIndex().
        // This lets us return a specific "invalid index" error instead of a generic format error.
        if (!token.matches("-?\\d+")) {
            throw new ParseException(usageMessage);
        }

        return parseIndex(token);
    }

    /**
     * Generic helper to reduce duplication for simple parsing logic.
     */
    private static <T> T parseField(String input, Predicate<String> validator,
                                    String errorMsg, Function<String, T> constructor) throws ParseException {
        requireNonNull(input);
        String trimmed = input.trim();
        if (!validator.test(trimmed)) {
            throw new ParseException(errorMsg);
        }
        return constructor.apply(trimmed);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     */
    public static Name parseName(String name) throws ParseException {
        return parseField(name, Name::isValidName, Name.MESSAGE_CONSTRAINTS, Name::new);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        return parseField(phone, Phone::isValidPhone, Phone.MESSAGE_CONSTRAINTS, Phone::new);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     */
    public static Address parseAddress(String address) throws ParseException {
        return parseField(address, Address::isValidAddress, Address.MESSAGE_CONSTRAINTS, Address::new);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     */
    public static Email parseEmail(String email) throws ParseException {
        return parseField(email, Email::isValidEmail, Email.MESSAGE_CONSTRAINTS, Email::new);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     */
    public static Tag parseTag(String tag) throws ParseException {
        return parseField(tag, Tag::isValidTagName, Tag.MESSAGE_CONSTRAINTS, Tag::new);
    }

    /**
     * Parses a {@code String note} into a {@code Note}.
     */
    public static Note parseNote(String note) throws ParseException {
        return parseField(note, Note::isValidNote, Note.MESSAGE_CONSTRAINTS, Note::new);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String visitDateTime} into a {@code VisitDateTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code visitDateTime} is invalid.
     */
    public static VisitDateTime parseVisitDateTime(String visitDateTime) throws ParseException {
        requireNonNull(visitDateTime);
        String trimmedVisitDateTime = visitDateTime.trim();
        if (trimmedVisitDateTime.isEmpty()) {
            return new VisitDateTime(); // Return empty if not provided
        }
        if (!VisitDateTime.isValidVisitDateTime(trimmedVisitDateTime)) {
            throw new ParseException(VisitDateTime.MESSAGE_CONSTRAINTS);
        }
        return new VisitDateTime(trimmedVisitDateTime);
    }

    /**
     * Parses a {@code String date} into a {@code LocalDate}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @param date The date string to be parsed (Expected format: YYYY-MM-DD).
     * @return The parsed LocalDate object.
     * @throws ParseException if the given {@code date} is invalid or not in the correct format.
     */
    public static LocalDate parseDate(String date) throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();
        try {
            return LocalDate.parse(trimmedDate);
        } catch (DateTimeParseException e) {
            throw new ParseException(VisitDateTime.MESSAGE_DATE_CONSTRAINTS);
        }
    }
}
