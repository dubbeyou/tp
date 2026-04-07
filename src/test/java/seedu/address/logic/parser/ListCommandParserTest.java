package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_FIELDS;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_SORT_FIELD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.SortField;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    // VALID INPUTS

    // EP: no sort (empty input)
    @Test
    public void parse_noSort_success() {
        assertParseSuccess(parser,
                "",
                new ListCommand(null));
    }

    // EP: valid sort field (name)
    @Test
    public void parse_validSortName_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "name",
                new ListCommand(SortField.NAME));
    }

    // EP: valid sort field (visit)
    @Test
    public void parse_validSortVisit_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "visit",
                new ListCommand(SortField.VISIT));
    }

    // EP: case-insensitive sort (name)
    @Test
    public void parse_caseInsensitiveSort_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "Name",
                new ListCommand(SortField.NAME));
    }

    // EP: case-insensitive sort (visit)
    @Test
    public void parse_caseInsensitiveSortVisit_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT + "Visit",
                new ListCommand(SortField.VISIT));
    }

    // EP: whitespace only (treated as no sort)
    @Test
    public void parse_whitespaceOnly_success() throws Exception {
        ListCommandParser parser = new ListCommandParser();

        ListCommand command = parser.parse("   ");

        assertEquals(new ListCommand(null), command);
    }

    // INVALID INPUTS

    // EP: invalid sort value
    @Test
    public void parse_invalidSort_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "invalid",
                MESSAGE_INVALID_SORT_FIELD);
    }

    // EP: another invalid sort value
    @Test
    public void parse_invalidSortField_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "date",
                MESSAGE_INVALID_SORT_FIELD);
    }

    // EP: extra preamble before prefix
    @Test
    public void parse_extraPreamble_throwsParseException() {
        ListCommandParser parser = new ListCommandParser();

        assertThrows(ParseException.class, () ->
                parser.parse("randomText s/name"));
    }

    // EP: only garbage input
    @Test
    public void parse_onlyGarbage_throwsParseException() {
        ListCommandParser parser = new ListCommandParser();

        assertThrows(ParseException.class, () ->
                parser.parse("hello"));
    }

    // EP: extra text after prefix value
    @Test
    public void parse_extraTextAfterPrefix_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "name extra",
                MESSAGE_INVALID_SORT_FIELD);
    }

    // EP: preamble with spaces before prefix
    @Test
    public void parse_preambleWithSpaces_failure() {
        assertParseFailure(parser,
                "   hello   " + PREFIX_SORT + "name",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    // EP: text after valid prefix token
    @Test
    public void parse_textBetweenPrefixes_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "name junk",
                MESSAGE_INVALID_SORT_FIELD);
    }

    // EDGE / HEURISTIC CASES
    // Heuristic: duplicate prefix should fail
    @Test
    public void parse_duplicatePrefix_failure() {
        assertParseFailure(parser,
                " " + PREFIX_SORT + "name " + PREFIX_SORT + "visit",
                MESSAGE_DUPLICATE_FIELDS + PREFIX_SORT);
    }

    // Heuristic: empty value after prefix treated as no sort
    @Test
    public void parse_emptySortValue_success() {
        assertParseSuccess(parser,
                " " + PREFIX_SORT,
                new ListCommand(null));
    }
}


