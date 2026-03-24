package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

public class ListArchiveCommand extends Command {

    public static final String COMMAND_WORD = "list-archive";
    public static final String MESSAGE_SUCCESS = "Listed all archived persons";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(p -> p.isArchived());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
