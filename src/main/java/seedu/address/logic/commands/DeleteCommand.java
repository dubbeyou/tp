package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_NONEXISTENCE_INDEX;

import java.util.ArrayList;
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
            + "Parameters: INDEX or RANGE (must be a positive integer)\n"
            + "Example: delete 1\n"
            + "Example: delete 3-7\n"
            + "Example: delete 1 3-7 10";


    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted persons:\n%1$s";

    private final List<Index> targetIndexes;

    /**
     * Creates a DeleteCommand to delete one or more persons at the specified indexes.
     *
     * @param targetIndexes list of indexes of persons to be deleted
     */
    public DeleteCommand(List<Index> targetIndexes) {
        requireNonNull(targetIndexes);
        this.targetIndexes = List.copyOf(targetIndexes);
    }

    /**
     * Executes the delete command by removing the specified persons from the model.
     *
     * <p>All indices are validated first to ensure the operation is atomic (i.e. no partial deletion occurs).</p>
     *
     * @param model the model containing the filtered list of persons
     * @return a {@code CommandResult} containing the formatted details of deleted persons
     * @throws CommandException if any index is out of bounds
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        validateIndexesExist(lastShownList);

        assert targetIndexes.stream()
                .allMatch(i -> i.getZeroBased() >= 0
                        && i.getZeroBased() < lastShownList.size())
                : "All indexes should be valid after validation";

        List<Index> sortedIndexes = toDescendingIndexes(targetIndexes);

        String messageBody = deletePersonsAndFormat(model, lastShownList, sortedIndexes);

        return new CommandResult(
                String.format(MESSAGE_DELETE_PERSONS_SUCCESS, messageBody)
        );
    }

    /**
     * Validates that all target indexes exist within the current filtered person list.
     *
     * @param lastShownList the current filtered list of persons
     * @throws CommandException if any index is out of bounds
     */
    private void validateIndexesExist(List<Person> lastShownList) throws CommandException {
        List<Integer> invalidIndexes = new ArrayList<>();

        for (Index index : targetIndexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
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
    }

    /**
     * Returns a new list of indexes sorted in descending order of their zero-based values.
     *
     * <p>This prevents index shifting issues during deletion.</p>
     *
     * @param indexes the list of indexes to sort
     * @return a new list of indexes sorted in descending order
     */
    private List<Index> toDescendingIndexes(List<Index> indexes) {
        List<Index> sortedIndexes = new ArrayList<>(indexes);
        sortedIndexes.sort((a, b) -> Integer.compare(b.getZeroBased(), a.getZeroBased()));
        return sortedIndexes;
    }

    /**
     * Deletes the persons at the specified indexes and formats them into a result string.
     *
     * @param model the model used to perform deletions
     * @param lastShownList the current filtered list of persons
     * @param indexes the list of indexes to delete (must be valid and sorted in descending order)
     * @return a formatted string of deleted persons
     */
    private String deletePersonsAndFormat(Model model, List<Person> lastShownList, List<Index> indexes) {
        StringBuilder deletedPersonsMessage = new StringBuilder();

        for (Index index : indexes) {
            if (!deletedPersonsMessage.isEmpty()) {
                deletedPersonsMessage.append("\n");
            }

            assert index.getZeroBased() < lastShownList.size()
                    : "Index should be within bounds";

            Person personToDelete = lastShownList.get(index.getZeroBased());
            model.deletePerson(personToDelete);
            deletedPersonsMessage.append(Messages.format(personToDelete));
        }

        return deletedPersonsMessage.toString();
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

