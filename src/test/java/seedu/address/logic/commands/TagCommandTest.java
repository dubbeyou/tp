package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class TagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addAndRemoveTags_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Tag tagToDelete = firstPerson.getTags().iterator().next();
        Set<Tag> tagsToDelete = new HashSet<>();
        tagsToDelete.add(tagToDelete);

        Tag tagToAdd = new Tag("NewTag");
        Set<Tag> tagsToAdd = new HashSet<>();
        tagsToAdd.add(tagToAdd);

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, tagsToDelete);

        // Update the tags manually
        Set<Tag> expectedTags = new HashSet<>(firstPerson.getTags());
        expectedTags.remove(tagToDelete);
        expectedTags.add(tagToAdd);

        // Use the constructor directly to avoid PersonBuilder string conversion issues
        Person editedPerson = new Person(
                firstPerson.getName(), firstPerson.getPhone(), firstPerson.getEmail(),
                firstPerson.getAddress(), firstPerson.getNote(), expectedTags, firstPerson.getVisitDateTime(),
                firstPerson.isArchived());

        String expectedMessage = String.format(TagCommand.MESSAGE_TAG_PERSON_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(tagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateTagAdd_throwsCommandException() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag existingTag = firstPerson.getTags().iterator().next();

        Set<Tag> tagsToAdd = new HashSet<>();
        tagsToAdd.add(existingTag);

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, new HashSet<>());
        String expectedMessage = "The tag [" + existingTag.tagName + "] already exists for this person.";

        assertCommandFailure(tagCommand, model, expectedMessage);
    }

    @Test
    public void execute_deleteNonExistentTag_throwsCommandException() {
        String tagName = "nonexistent";
        Tag nonExistentTag = new Tag(tagName);
        Set<Tag> tagsToDelete = new HashSet<>();
        tagsToDelete.add(nonExistentTag);

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, new HashSet<>(), tagsToDelete);

        String expectedMessage = "The tag [" + tagName + "] not exist, cannot be deleted.";

        assertCommandFailure(tagCommand, model, expectedMessage);
    }

    @Test
    public void execute_tagAlreadyExists_throwsCommandException() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag existingTag = firstPerson.getTags().iterator().next();

        Set<Tag> tagsToAdd = new HashSet<>();
        tagsToAdd.add(existingTag);

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, new HashSet<>());
        String expectedMessage = "The tag [" + existingTag.tagName + "] already exists for this person.";

        assertCommandFailure(tagCommand, model, expectedMessage);
    }

    @Test
    public void hashCodeMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Set<Tag> addTags = new HashSet<>();
        addTags.add(new Tag("Tag1"));
        Set<Tag> deleteTags = new HashSet<>();
        deleteTags.add(new Tag("Tag2"));

        TagCommand tagCommand = new TagCommand(targetIndex, addTags, deleteTags);

        // same values should have same hash code
        TagCommand sameCommand = new TagCommand(targetIndex, addTags, deleteTags);
        assertEquals(tagCommand.hashCode(), sameCommand.hashCode());

        TagCommand differentIndexCommand = new TagCommand(Index.fromOneBased(2), addTags, deleteTags);
        assertFalse(tagCommand.hashCode() == differentIndexCommand.hashCode());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        TagCommand tagCommand = new TagCommand(outOfBoundIndex, new HashSet<>(), new HashSet<>());

        assertCommandFailure(tagCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Set<Tag> addTags = new HashSet<>();
        addTags.add(new Tag("T1"));
        Set<Tag> deleteTags = new HashSet<>();
        deleteTags.add(new Tag("T2"));

        final TagCommand standardCommand = new TagCommand(INDEX_FIRST_PERSON, addTags, deleteTags);

        // same values -> returns true
        TagCommand commandWithSameValues = new TagCommand(INDEX_FIRST_PERSON, addTags, deleteTags);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new TagCommand(INDEX_SECOND_PERSON, addTags, deleteTags)));

        // different addTags -> returns false
        Set<Tag> differentAddTags = new HashSet<>();
        differentAddTags.add(new Tag("T3"));
        assertFalse(standardCommand.equals(new TagCommand(INDEX_FIRST_PERSON, differentAddTags, deleteTags)));

        // different deleteTags -> returns false
        Set<Tag> differentDeleteTags = new HashSet<>();
        differentDeleteTags.add(new Tag("T4"));
        assertFalse(standardCommand.equals(new TagCommand(INDEX_FIRST_PERSON, addTags, differentDeleteTags)));
    }

    @Test
    public void toStringMethod() {
        Set<Tag> addTags = new HashSet<>();
        Set<Tag> deleteTags = new HashSet<>();
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, addTags, deleteTags);
        String expected = TagCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", tagsToAdd=" + addTags + ", tagsToDelete=" + deleteTags + "}";
        assertEquals(expected, tagCommand.toString());
    }
}
