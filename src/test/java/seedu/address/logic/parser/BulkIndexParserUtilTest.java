package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_RANGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_TOKEN;
import static seedu.address.logic.Messages.MESSAGE_RANGE_INDEX_LARGE;
import static seedu.address.logic.Messages.MESSAGE_RANGE_TOO_LARGE;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests for {@code BulkIndexParserUtil}.
 */
public class BulkIndexParserUtilTest {

    private static final String USAGE_MESSAGE = "test usage";
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, USAGE_MESSAGE);

    // Success Tests

    @Test
    public void parseBulkIndexes_singleIndex_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1", USAGE_MESSAGE);
        assertEquals(List.of(Index.fromOneBased(1)), result);
    }

    @Test
    public void parseBulkIndexes_multipleIndexes_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1 2 3", USAGE_MESSAGE);
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)
        ), result);
    }

    @Test
    public void parseBulkIndexes_range_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("2-4", USAGE_MESSAGE);
        assertEquals(List.of(
                Index.fromOneBased(2),
                Index.fromOneBased(3),
                Index.fromOneBased(4)
        ), result);
    }

    @Test
    public void parseBulkIndexes_mixed_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1 3-5", USAGE_MESSAGE);
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(3),
                Index.fromOneBased(4),
                Index.fromOneBased(5)
        ), result);
    }

    @Test
    public void parseBulkIndexes_duplicatesSuccess() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1 1 1 2", USAGE_MESSAGE);
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2)
        ), result);
    }

    @Test
    public void parseBulkIndexes_unsortedInputSuccess() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("5 1 3", USAGE_MESSAGE);
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(3),
                Index.fromOneBased(5)
        ), result);
    }

    // Failure Tests

    @Test
    public void parseBulkIndexes_whitespaceOnlyInput_throwsParseException() {
        assertThrows(ParseException.class, INVALID_FORMAT_MESSAGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("   ", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_nullInput_throwsParseException() {
        assertThrows(ParseException.class, INVALID_FORMAT_MESSAGE, () ->
                BulkIndexParserUtil.parseBulkIndexes(null, USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_emptyInput_throwsParseException() {
        assertThrows(ParseException.class, INVALID_FORMAT_MESSAGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_plusSignIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("+1", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_signedRange_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("+1-2", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_trailingDash_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-2-", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_invalidCharacterInNumber_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-10a", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_multipleDashes_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-2-3", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_invalidRangeLeftSide_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("a-1", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_invalidRangeRightSide_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-a", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_rangeWithZeroStart_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("0-5", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_rangeWithZeroEnd_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("5-0", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_rangeWithZero_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-0", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_malformedRangeToken_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1--3", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_rangeIndexTooLarge_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_RANGE_INDEX_LARGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("999999999999-999999999999", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_indexTooLarge_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INDEX_TOO_LARGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("999999999999999999999999", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_nonNumericRangeParts_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("a-b", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_invalidToken_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1,2", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_negativeIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("-1", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_zeroIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("0", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_invalidRange_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_RANGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("5-3", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_largeRange_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_RANGE_TOO_LARGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-200", USAGE_MESSAGE));
    }

    @Test
    public void parseBulkIndexes_invalidMixedInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1 a 3", USAGE_MESSAGE));
    }

    @Test
    public void parseSingleIndexOrThrow_multipleTokens_throwsParseException() {
        assertThrows(ParseException.class, "usage", () ->
                ParserUtil.parseSingleIndexOrThrow("1 2", "usage"));
    }

    @Test
    public void parseSingleIndexOrThrow_invalidFormat_throwsParseException() {
        assertThrows(ParseException.class, "usage", () ->
                ParserUtil.parseSingleIndexOrThrow("abc", "usage"));
    }
}
