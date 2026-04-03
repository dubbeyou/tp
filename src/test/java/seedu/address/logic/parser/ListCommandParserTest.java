package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListCommandParserTest {

    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noSort_success() {
        assertParseSuccess(parser,
                "",
                new ListCommand(""));
    }

    @Test
    public void parse_validSortName_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "name",
                new ListCommand("name"));
    }

    @Test
    public void parse_invalidSort_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "invalid",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_caseInsensitiveSort_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "Name",
                new ListCommand("name"));
    }

    @Test
    public void parse_validSortVisit_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "visit",
                new ListCommand("visit"));
    }

    @Test
    public void parse_caseInsensitiveSortVisit_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "Visit",
                new ListCommand("visit"));
    }

    @Test
    public void parse_invalidSortField_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "date",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extraPreamble_throwsParseException() {
        ListCommandParser parser = new ListCommandParser();

        assertThrows(ParseException.class, () ->
                parser.parse("randomText s/name"));
    }

    @Test
    public void parse_onlyGarbage_throwsParseException() {
        ListCommandParser parser = new ListCommandParser();

        assertThrows(ParseException.class, () ->
                parser.parse("hello"));
    }

    @Test
    public void parse_whitespaceOnly_success() throws Exception {
        ListCommandParser parser = new ListCommandParser();

        ListCommand command = parser.parse("   ");

        assertEquals(new ListCommand(""), command);
    }

    @Test
    public void parse_extraTextAfterPrefix_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "name extra",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_preambleWithSpaces_failure() {
        assertParseFailure(parser,
                "   hello   " + PREFIX_SORT + "name",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_textBetweenPrefixes_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "name junk",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }
}


