package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose attributes contain any of "
            + "the specified keywords or dates and displays them as a list with index numbers.\n"
            + "Parameters: \n"
            + "1. " + PREFIX_NAME + "NAME [ADDITIONAL NAMES] \n"
            + "2. " + PREFIX_TAG + "TAG \n"
            + "3. " + PREFIX_DATE + "today OR DATE (Find visits for today or a specific date YYYY-MM-DD) \n"
            + "4. " + PREFIX_START_DATE + "START_DATE " + PREFIX_END_DATE + "END_DATE (Find visits within a range) \n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "NAME \n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_TAG + "TAG \n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_DATE + "today OR 2026-12-01 \n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_START_DATE + "2026-01-01 " + PREFIX_END_DATE + "2026-12-31";

    private final Predicate<Person> predicate;


    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
