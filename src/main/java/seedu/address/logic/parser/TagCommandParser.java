package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE_TAG;

import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object.
 */
public class TagCommandParser implements Parser<TagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the TagCommand
     * and returns a TagCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ADD_TAG, PREFIX_DELETE_TAG);

        Index index = ParserUtil.parseSingleIndexOrThrow(
                argMultimap.getPreamble(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE)
        );

        if (argMultimap.getAllValues(PREFIX_ADD_TAG).isEmpty()
                && argMultimap.getAllValues(PREFIX_DELETE_TAG).isEmpty()) {
            throw new ParseException(TagCommand.MESSAGE_NOT_EDITED);
        }

        Set<Tag> tagsToAdd = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_ADD_TAG));
        Set<Tag> tagsToDelete = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_DELETE_TAG));

        return new TagCommand(index, tagsToAdd, tagsToDelete);
    }
}
