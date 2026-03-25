package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_EMPTY_INPUT;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_RANGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_TOKEN;
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

    // Success Tests

    @Test
    public void parseBulkIndexes_singleIndex_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1");
        assertEquals(List.of(Index.fromOneBased(1)), result);
    }

    @Test
    public void parseBulkIndexes_multipleIndexes_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1 2 3");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3)
                ), result);
    }

    @Test
    public void parseBulkIndexes_range_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("2-4");
        assertEquals(List.of(
                Index.fromOneBased(2),
                Index.fromOneBased(3),
                Index.fromOneBased(4)
                ), result);
    }

    @Test
    public void parseBulkIndexes_mixed_success() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1 3-5");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(3),
                Index.fromOneBased(4),
                Index.fromOneBased(5)
                ), result);
    }

    @Test
    public void parseBulkIndexes_duplicatesSuccess() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("1 1 1 2");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2)
                ), result);
    }

    @Test
    public void parseBulkIndexes_unsortedInputSuccess() throws Exception {
        List<Index> result = BulkIndexParserUtil.parseBulkIndexes("5 1 3");
        assertEquals(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(3),
                Index.fromOneBased(5)
                ), result);
    }

    // Failure Tests

    @Test
    public void parseBulkIndexes_plusSignIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("+1"));
    }

    @Test
    public void parseBulkIndexes_signedRange_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("+1-2"));
    }

    @Test
    public void parseBulkIndexes_trailingDash_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-2-"));
    }

    @Test
    public void parseBulkIndexes_invalidCharacterInNumber_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-10a"));
    }

    @Test
    public void parseBulkIndexes_multipleDashes_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-2-3"));
    }

    @Test
    public void parseBulkIndexes_invalidRangeLeftSide_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("a-1"));
    }

    @Test
    public void parseBulkIndexes_invalidRangeRightSide_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-a"));
    }

    @Test
    public void parseBulkIndexes_rangeWithZeroStart_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("0-5"));
    }

    @Test
    public void parseBulkIndexes_rangeWithZeroEnd_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("5-0"));
    }

    @Test
    public void parseBulkIndexes_whitespaceOnlyInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_INPUT, () ->
                BulkIndexParserUtil.parseBulkIndexes("   "));
    }

    @Test
    public void parseBulkIndexes_rangeWithZero_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-0"));
    }

    @Test
    public void parseBulkIndexes_malformedRangeToken_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1--3"));
    }

    @Test
    public void parseBulkIndexes_nullInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_INPUT, () ->
                BulkIndexParserUtil.parseBulkIndexes(null));
    }

    @Test
    public void parseBulkIndexes_rangeTooLargeNumberFormat_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_RANGE_TOO_LARGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("999999999999-999999999999"));
    }

    @Test
    public void parseBulkIndexes_indexTooLarge_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INDEX_TOO_LARGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("999999999999999999999999"));
    }

    @Test
    public void parseBulkIndexes_nonNumericRangeParts_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("a-b"));
    }

    @Test
    public void parseBulkIndexes_emptyInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_INPUT, () ->
                BulkIndexParserUtil.parseBulkIndexes(""));
    }

    @Test
    public void parseBulkIndexes_invalidToken_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1,2"));
    }

    @Test
    public void parseBulkIndexes_negativeIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("-1"));
    }

    @Test
    public void parseBulkIndexes_zeroIndex_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () ->
                BulkIndexParserUtil.parseBulkIndexes("0"));
    }

    @Test
    public void parseBulkIndexes_invalidRange_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_RANGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("5-3"));
    }

    @Test
    public void parseBulkIndexes_largeRange_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_RANGE_TOO_LARGE, () ->
                BulkIndexParserUtil.parseBulkIndexes("1-200"));
    }

    @Test
    public void parseBulkIndexes_invalidMixedInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_TOKEN, () ->
                BulkIndexParserUtil.parseBulkIndexes("1 a 3"));
    }
}



