package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    private static final String SORT_BY_NAME = "name";
    private static final String SORT_BY_VISIT = "visit";

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public ListCommand parse(String args) throws ParseException {

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_SORT);

        String sortField = argMultimap.getValue(PREFIX_SORT)
                .map(String::trim)
                .map(String::toLowerCase)
                .orElse("");

        if (!sortField.isEmpty()
                && !SORT_BY_NAME.equals(sortField)
                && !SORT_BY_VISIT.equals(sortField)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        String preamble = argMultimap.getPreamble();

        if (!preamble.trim().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }
        return new ListCommand(sortField);
    }
}



