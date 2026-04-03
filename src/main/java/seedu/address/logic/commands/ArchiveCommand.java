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
 * Archives a person identified using it's displayed index from the address book.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Archives the person identified by the index number. "
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_ARCHIVE_PERSON_SUCCESS = "Archived: %1$s";
    public static final String MESSAGE_PERSON_ALREADY_ARCHIVED = "%1$s is already archived";

    private static final Logger logger = LogsCenter.getLogger(ArchiveCommand.class);
    private final Index targetIndex;

    public ArchiveCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        logger.info("Executing archive command for index: " + targetIndex);

        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        assert lastShownList != null;

        assert targetIndex.getZeroBased() >= 0;

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToArchive = lastShownList.get(targetIndex.getZeroBased());
        logger.fine("Person to archive: " + personToArchive);

        if (personToArchive.isArchived()) {
            logger.warning("Attempted to archive an already archived person: " + personToArchive);
            return new CommandResult(
                    String.format(MESSAGE_PERSON_ALREADY_ARCHIVED,
                            personToArchive.getName()));
        }

        model.archivePerson(personToArchive);

        logger.info("Successfully archived person: " + personToArchive);

        model.updateFilteredPersonList(model.getCurrentPredicate());
        return new CommandResult(String.format(MESSAGE_ARCHIVE_PERSON_SUCCESS, Messages.format(personToArchive)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ArchiveCommand otherArchiveCommand)) {
            return false;
        }

        return targetIndex.equals(otherArchiveCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
