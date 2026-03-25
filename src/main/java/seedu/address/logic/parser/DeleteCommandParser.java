package seedu.address.logic.parser;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            List<Index> indexes = BulkIndexParserUtil.parseBulkIndexes(args);
            return new DeleteCommand(indexes);
        } catch (ParseException pe) {
            throw new ParseException(
                    pe.getMessage() + "\n" + DeleteCommand.MESSAGE_USAGE);
        }
    }

}
