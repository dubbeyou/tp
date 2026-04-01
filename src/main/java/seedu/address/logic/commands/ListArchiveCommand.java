package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;

import seedu.address.model.Model;

/**
 * Lists all archived persons in the address book to the user.
 */
public class ListArchiveCommand extends Command {

    public static final String COMMAND_WORD = "list-archive";
    public static final String MESSAGE_SUCCESS = "Listed all archived persons";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all archived persons in the address book.\n"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(p -> p.isArchived());
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ListArchiveCommand;
    }

    @Override
    public int hashCode() {
        return COMMAND_WORD.hashCode();
    }
}
