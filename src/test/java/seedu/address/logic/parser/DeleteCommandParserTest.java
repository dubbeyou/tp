package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_EMPTY_INPUT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_RANGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_TOKEN;
import static seedu.address.logic.Messages.MESSAGE_RANGE_TOO_LARGE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;


/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private static final String MESSAGE_USAGE =
            DeleteCommand.MESSAGE_USAGE;

    private final DeleteCommandParser parser = new DeleteCommandParser();


    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1",
                new DeleteCommand(List.of(INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                MESSAGE_INVALID_TOKEN + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_multipleIndexes_success() {
        assertParseSuccess(parser, "1 2 3",
                new DeleteCommand(List.of(
                        Index.fromOneBased(1),
                        Index.fromOneBased(2),
                        Index.fromOneBased(3)
                )));
    }

    @Test
    public void parse_range_success() {
        assertParseSuccess(parser, "2-4",
                new DeleteCommand(List.of(
                        Index.fromOneBased(2),
                        Index.fromOneBased(3),
                        Index.fromOneBased(4)
                )));
    }

    @Test
    public void parse_mixedIndexesAndRange_success() {
        assertParseSuccess(parser, "1 3-5",
                new DeleteCommand(List.of(
                        Index.fromOneBased(1),
                        Index.fromOneBased(3),
                        Index.fromOneBased(4),
                        Index.fromOneBased(5)
                )));
    }

    @Test
    public void parse_duplicateIndexes_success() {
        assertParseSuccess(parser, "1 1 1",
                new DeleteCommand(List.of(
                        Index.fromOneBased(1)
                )));
    }

    @Test
    public void parse_overlappingRangeAndIndexes_success() {
        assertParseSuccess(parser, "1 2-4 3",
                new DeleteCommand(List.of(
                        Index.fromOneBased(1),
                        Index.fromOneBased(2),
                        Index.fromOneBased(3),
                        Index.fromOneBased(4)
                )));
    }

    @Test
    public void parse_invalidNegativeIndex_failure() {
        assertParseFailure(parser, "-1",
                MESSAGE_INVALID_INDEX + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_zeroIndex_failure() {
        assertParseFailure(parser, "0",
                MESSAGE_INVALID_INDEX + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidRange_failure() {
        assertParseFailure(parser, "5-3",
                MESSAGE_INVALID_RANGE + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidFormat_failure() {
        assertParseFailure(parser, "1,2",
                MESSAGE_INVALID_TOKEN + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyInput_failure() {
        assertParseFailure(parser, "",
                MESSAGE_EMPTY_INPUT + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_largeRange_failure() {
        assertParseFailure(parser, "1-1000",
                MESSAGE_RANGE_TOO_LARGE + "\n" + MESSAGE_USAGE);
    }
}

