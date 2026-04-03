package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Unarchives a person identified using its displayed index.
 */
public class UnarchiveCommand extends Command {

    public static final String COMMAND_WORD = "unarchive";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unarchives the person identified by the index number.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_UNARCHIVE_PERSON_SUCCESS = "Unarchived: %1$s";
    public static final String MESSAGE_UNARCHIVE_PERSON_UNSUCCESS = "%1$s is not archived";
    private static final Logger logger = LogsCenter.getLogger(UnarchiveCommand.class);
    private final Index targetIndex;

    public UnarchiveCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        logger.info("Executing unarchive command for index: " + targetIndex);
        assert targetIndex.getZeroBased() >= 0;
        requireNonNull(model);

        List<Person> archivedList = model.getFilteredPersonList();
        assert archivedList != null;

        if (targetIndex.getZeroBased() >= archivedList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUnarchive = archivedList.get(targetIndex.getZeroBased());
        logger.fine("Person to unarchive: " + personToUnarchive);

        if (!personToUnarchive.isArchived()) {
            logger.warning("Attempted to unarchive a non-archived person: " + personToUnarchive);
            return new CommandResult(
                    String.format(MESSAGE_UNARCHIVE_PERSON_UNSUCCESS,
                            personToUnarchive.getName())
            );
        }
        model.unarchivePerson(personToUnarchive);

        logger.info("Successfully unarchived person: " + personToUnarchive);

        // Refresh on current active list
        model.updateFilteredPersonList(model.getCurrentPredicate());

        return new CommandResult(
                String.format(MESSAGE_UNARCHIVE_PERSON_SUCCESS,
                        Messages.format(personToUnarchive)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnarchiveCommand otherUnarchiveCommand)) {
            return false;
        }

        return targetIndex.equals(otherUnarchiveCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
