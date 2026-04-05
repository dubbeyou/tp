package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.SortField;
import seedu.address.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";
    public static final String MESSAGE_SORT_SUCCESS = "Listed all persons sorted by %s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons in the address book.\n"
            + "Optionally sorts the list by a field.\n"
            + "Parameters: [" + PREFIX_SORT + "FIELD]\n"
            + "Valid fields: name, visit\n"
            + "Example: " + COMMAND_WORD + "\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_SORT + "name\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_SORT + "visit";

    private final SortField sortField;

    public ListCommand() {
        this(null);
    }

    public ListCommand(SortField sortField) {
        this.sortField = sortField;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        if (sortField != null) {
            model.sortFilteredPersonList(sortField);
            return new CommandResult(
                    String.format(MESSAGE_SORT_SUCCESS, sortField.name().toLowerCase())
            );
        } else {
            model.resetSort();
            return new CommandResult(MESSAGE_SUCCESS);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommand otherCommand)) {
            return false;
        }

        return sortField == otherCommand.sortField;
    }
}

