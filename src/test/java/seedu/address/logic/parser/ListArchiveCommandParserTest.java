package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListArchiveCommand;

public class ListArchiveCommandParserTest {

    private final ListArchiveCommandParser parser = new ListArchiveCommandParser();

    @Test
    public void parse_emptyArgs_success() {
        assertParseSuccess(parser, "", new ListArchiveCommand());
    }

    @Test
    public void parse_whitespaceArgs_success() {
        assertParseSuccess(parser, "   ", new ListArchiveCommand());
    }

    @Test
    public void parse_nonEmptyArgs_failure() {
        assertParseFailure(parser, "extra arguments",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListArchiveCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_singleWordArg_failure() {
        assertParseFailure(parser, "archive",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListArchiveCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleArgs_failure() {
        assertParseFailure(parser, "sort name",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListArchiveCommand.MESSAGE_USAGE));
    }
}
