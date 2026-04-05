package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.person.VisitDateTime;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_LONG_NAME = "A".repeat(Name.MAX_LENGTH + 1);
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_ADDRESS_WITH_SLASH = "12/34 Main Street";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_LONG_EMAIL = "a".repeat(Email.MAX_LENGTH - "@example.com".length() + 1)
            + "@example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_VISIT_DATE_TIME = "2026-13-40 25:99";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "91234567";
    private static final String VALID_SPACED_PHONE = "9123 4567";
    private static final String VALID_HYPHENATED_PHONE = "9123-4567";
    private static final String VALID_LANDLINE_PHONE = "61234567";
    private static final String VALID_EMERGENCY_PHONE = "995";
    private static final String VALID_TOLL_FREE_PHONE = "1800 123 4567";
    private static final String VALID_TOLL_FREE_HYPHENATED_PHONE = "1800-123-4567";
    private static final String VALID_TOLL_FREE_PHONE_NO_SPACE = "18001234567";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_NOTE = "Meet client at lobby.";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_VISIT_DATE_TIME = "2026-03-15 14:30";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        // Equivalent Partitioning (invalid): mixed numeric and non-numeric token
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        // Boundary Value Analysis: Integer.MAX_VALUE + 1 is just beyond supported range
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        // Equivalent Partitioning (invalid): bad character set
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));

        // Boundary Value Analysis: exceeds max name length by 1
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_LONG_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone("93-121534"));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));

        Phone expectedSpacedPhone = new Phone(VALID_SPACED_PHONE);
        assertEquals(expectedSpacedPhone, ParserUtil.parsePhone(VALID_SPACED_PHONE));

        Phone expectedHyphenatedPhone = new Phone(VALID_HYPHENATED_PHONE);
        assertEquals(expectedHyphenatedPhone, ParserUtil.parsePhone(VALID_HYPHENATED_PHONE));

        Phone expectedLandlinePhone = new Phone(VALID_LANDLINE_PHONE);
        assertEquals(expectedLandlinePhone, ParserUtil.parsePhone(VALID_LANDLINE_PHONE));

        Phone expectedEmergencyPhone = new Phone(VALID_EMERGENCY_PHONE);
        assertEquals(expectedEmergencyPhone, ParserUtil.parsePhone(VALID_EMERGENCY_PHONE));

        Phone expectedTollFree = new Phone(VALID_TOLL_FREE_PHONE);
        assertEquals(expectedTollFree, ParserUtil.parsePhone(VALID_TOLL_FREE_PHONE));

        Phone expectedTollFreeHyphenatedPhone = new Phone(VALID_TOLL_FREE_HYPHENATED_PHONE);
        assertEquals(expectedTollFreeHyphenatedPhone,
                ParserUtil.parsePhone(VALID_TOLL_FREE_HYPHENATED_PHONE));

        Phone expectedTollFreeNoSpace = new Phone(VALID_TOLL_FREE_PHONE_NO_SPACE);
        assertEquals(expectedTollFreeNoSpace, ParserUtil.parsePhone(VALID_TOLL_FREE_PHONE_NO_SPACE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        // Equivalent Partitioning (invalid): blank and disallowed character set
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS_WITH_SLASH));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        // Equivalent Partitioning (valid): standard address format
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        // Equivalent Partitioning (valid): valid address with surrounding whitespace
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_LONG_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test public void parseNote_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNote(null));
    }

    @Test public void parseNote_validValueWithoutWhitespace_returnsNote() throws Exception {
        Note expectedNote = new Note(VALID_NOTE);
        assertEquals(expectedNote, ParserUtil.parseNote(VALID_NOTE));
    }

    @Test public void parseNote_validValueWithWhitespace_returnsTrimmedNote() throws Exception {
        String noteWithWhitespace = WHITESPACE + VALID_NOTE + WHITESPACE;
        Note expectedNote = new Note(VALID_NOTE);
        assertEquals(expectedNote, ParserUtil.parseNote(noteWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseVisitDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseVisitDateTime(null));
    }

    @Test
    public void parseVisitDateTime_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseVisitDateTime(INVALID_VISIT_DATE_TIME));
    }

    @Test
    public void parseVisitDateTime_validValueWithoutWhitespace_returnsVisitDateTime() throws Exception {
        VisitDateTime expectedVisitDateTime = new VisitDateTime(VALID_VISIT_DATE_TIME);
        assertEquals(expectedVisitDateTime, ParserUtil.parseVisitDateTime(VALID_VISIT_DATE_TIME));
    }

    @Test
    public void parseVisitDateTime_validValueWithWhitespace_returnsTrimmedVisitDateTime() throws Exception {
        String visitDateTimeWithWhitespace = WHITESPACE + VALID_VISIT_DATE_TIME + WHITESPACE;
        VisitDateTime expectedVisitDateTime = new VisitDateTime(VALID_VISIT_DATE_TIME);
        assertEquals(expectedVisitDateTime, ParserUtil.parseVisitDateTime(visitDateTimeWithWhitespace));
    }

    @Test
    public void parseVisitDateTime_blankAfterTrim_returnsEmptyVisitDateTime() throws Exception {
        VisitDateTime visitDateTime = ParserUtil.parseVisitDateTime("   ");
        assertFalse(visitDateTime.isPresent());
        assertEquals("", visitDateTime.toString());
    }

    @Test
    public void parseVisitDateTime_validInput_success() throws Exception {
        VisitDateTime visitDateTime = ParserUtil.parseVisitDateTime("2026-12-01 14:00");
        assertTrue(visitDateTime.isPresent());
    }

    @Test
    public void parseVisitDateTime_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseVisitDateTime("invalid input"));
    }

    @Test
    public void parseVisitDateTime_emptyString_returnsEmptyVisitDateTime() throws Exception {
        VisitDateTime expected = new VisitDateTime();
        assertEquals(expected, ParserUtil.parseVisitDateTime(""));
    }

    @Test
    public void parseVisitDateTime_whitespaceOnly_returnsEmptyVisitDateTime() throws Exception {
        VisitDateTime expected = new VisitDateTime();
        assertEquals(expected, ParserUtil.parseVisitDateTime("   "));
    }

    @Test
    public void parseDate_validValue_returnsDate() throws Exception {
        LocalDate expectedDate = LocalDate.of(2026, 12, 31);
        assertEquals(expectedDate, ParserUtil.parseDate("2026-12-31"));
    }

    @Test
    public void parseDate_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseDate("31-12-2026"));
    }

    @Test
    public void parseSingleIndexOrThrow_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ParserUtil.parseSingleIndexOrThrow(null, "usage"));
    }

    @Test
    public void parseSingleIndexOrThrow_emptyInput_throwsParseException() {
        assertThrows(ParseException.class, "usage", () ->
                ParserUtil.parseSingleIndexOrThrow("", "usage"));
    }

    @Test
    public void parseSingleIndexOrThrow_whitespaceOnly_throwsParseException() {
        assertThrows(ParseException.class, "usage", () ->
                ParserUtil.parseSingleIndexOrThrow("   ", "usage"));
    }

    @Test
    public void parseSingleIndexOrThrow_multipleTokens_throwsParseException() {
        assertThrows(ParseException.class, "usage", () ->
                ParserUtil.parseSingleIndexOrThrow("1 2", "usage"));
    }

    @Test
    public void parseSingleIndexOrThrow_nonNumeric_throwsParseException() {
        assertThrows(ParseException.class, "usage", () ->
                ParserUtil.parseSingleIndexOrThrow("abc", "usage"));
    }

    @Test
    public void parseIndex_zero_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                ParserUtil.parseIndex("0"));
    }

    @Test
    public void parseIndex_negative_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                ParserUtil.parseIndex("-1"));
    }

    @Test
    public void parseIndex_indexTooLarge_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INDEX_TOO_LARGE, () ->
                ParserUtil.parseIndex("999999999999999999999999"));
    }
}
