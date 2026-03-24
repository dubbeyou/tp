package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NONEXISTENCE_INDEX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes one or more persons by index.\n"
            + "Parameters: INDEX [MORE INDEXES or RANGES]\n"
            + "Example: delete 1 3 6-9";

    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted persons:\n%1$s";

    private final List<Index> targetIndexes;

    /**
     * Creates a DeleteCommand to delete one or more persons at the specified indexes.
     *
     * @param targetIndexes list of indexes of persons to be deleted
     */
    public DeleteCommand(List<Index> targetIndexes) {
        requireNonNull(targetIndexes);
        this.targetIndexes = Collections.unmodifiableList(new ArrayList<>(targetIndexes));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        // Validate ALL indices first (atomic safety)
        List<Integer> invalidIndexes = new ArrayList<>();

        for (Index index : targetIndexes) {
            if (index.getZeroBased() < 0 || index.getZeroBased() >= lastShownList.size()) {
                invalidIndexes.add(index.getOneBased());
            }
        }

        if (!invalidIndexes.isEmpty()) {
            String joined = invalidIndexes.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            throw new CommandException(
                    String.format(MESSAGE_NONEXISTENCE_INDEX, joined)
            );
        }

        assert targetIndexes.stream()
                .allMatch(i -> i.getZeroBased() >= 0
                        && i.getZeroBased() < lastShownList.size())
                : "All indexes should be valid after validation";

        // Sort descending to prevent index shifting issues
        List<Index> sortedIndexes = new ArrayList<>(targetIndexes);
        sortedIndexes.sort((a, b) -> Integer.compare(b.getZeroBased(), a.getZeroBased()));

        StringBuilder deletedPersonsMessage = new StringBuilder();

        for (Index index : sortedIndexes) {
            if (deletedPersonsMessage.length() > 0) {
                deletedPersonsMessage.append("\n");
            }

            assert index.getZeroBased() < lastShownList.size()
                    : "Index should be within bounds";

            Person personToDelete = lastShownList.get(index.getZeroBased());
            model.deletePerson(personToDelete);
            deletedPersonsMessage.append(Messages.format(personToDelete));
        }

        return new CommandResult(
                String.format(MESSAGE_DELETE_PERSONS_SUCCESS, deletedPersonsMessage)
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetIndexes.equals(otherDeleteCommand.targetIndexes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .toString();
    }
}

