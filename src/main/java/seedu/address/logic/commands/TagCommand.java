package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE_TAG;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Add or delete tags for an existing person in the address book.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits tag(s) of the contact identified "
            + "by the index number used in the displayed contact list. "
            + "Cannot add duplicate tag or delete non-existent tag.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_ADD_TAG + "TAG_TO_ADD]... "
            + "[" + PREFIX_DELETE_TAG + "TAG_TO_DELETE]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_ADD_TAG + "neighbour "
            + PREFIX_DELETE_TAG + "friend";

    public static final String MESSAGE_TAG_PERSON_SUCCESS = "Updated tags for Contact: %1$s";
    public static final String MESSAGE_NOT_EDITED = "Tag to add or delete must be provided.";

    private final Index index;
    private final Set<Tag> tagsToAdd;
    private final Set<Tag> tagsToDelete;

    /**
     * @param index of the person to be edited on the tag
     * @param tagsToAdd tags to be added to the person
     * @param tagsToDelete tags to be deleted from the person
     */
    public TagCommand(Index index, Set<Tag> tagsToAdd, Set<Tag> tagsToDelete) {
        requireNonNull(index);
        requireNonNull(tagsToAdd);
        requireNonNull(tagsToDelete);

        this.index = index;
        this.tagsToAdd = tagsToAdd;
        this.tagsToDelete = tagsToDelete;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Set<Tag> currentTags = personToEdit.getTags();
        Set<Tag> updatedTags = new HashSet<>(currentTags);

        // check if new add tag already exist
        for (Tag tag : tagsToAdd) {
            if (currentTags.contains(tag)) {
                throw new CommandException("The tag [" + tag.tagName + "] already exists for this person.");
            }
        }

        // check if tag to delete exists
        for (Tag tag : tagsToDelete) {
            if (!currentTags.contains(tag)) {
                throw new CommandException("The tag [" + tag.tagName + "] does not exist, cannot be deleted.");
            }
        }

        updatedTags.addAll(tagsToAdd);
        updatedTags.removeAll(tagsToDelete);

        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getNote(), updatedTags, personToEdit.getVisitDateTime(),
                personToEdit.isArchived());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(model.getCurrentPredicate());
        return new CommandResult(String.format(MESSAGE_TAG_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCommand)) {
            return false;
        }

        TagCommand otherTagCommand = (TagCommand) other;
        return index.equals(otherTagCommand.index)
                && tagsToAdd.equals(otherTagCommand.tagsToAdd)
                && tagsToDelete.equals(otherTagCommand.tagsToDelete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, tagsToAdd, tagsToDelete);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("tagsToAdd", tagsToAdd)
                .add("tagsToDelete", tagsToDelete)
                .toString();
    }
}
