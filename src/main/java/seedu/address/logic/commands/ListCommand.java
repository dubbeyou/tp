package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

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
            + "Optionally sorts the list.\n"
            + "Parameters: s/FIELD (FIELD: name, phone, email)\n"
            + "Example: list\n"
            + "Example: list s/name";

    private final String sortField;

    public ListCommand(String sortField) {
        this.sortField = sortField;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        if (!sortField.isEmpty()) {
            model.sortFilteredPersonList(sortField);
            return new CommandResult(String.format(MESSAGE_SORT_SUCCESS, sortField));

        } else {
            model.resetSort();
            return new CommandResult(MESSAGE_SUCCESS);
        }
    }
}
