package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests and unit tests for {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                Messages.format(personToDelete)
        );

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyIndexList_success() {
        DeleteCommand command = new DeleteCommand(List.of());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, ""
        );

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(0);

        DeleteCommand command = new DeleteCommand(List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                Messages.format(personToDelete)
        );

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleIndexes_success() {
        DeleteCommand command = new DeleteCommand(
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)
        );

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        Person first = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person second = expectedModel.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Delete in descending order
        expectedModel.deletePerson(second);
        expectedModel.deletePerson(first);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                Messages.format(second) + "\n" + Messages.format(first)
        );

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_indexesOutOfOrder_correctDeletion() {
        DeleteCommand command = new DeleteCommand(
                List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON)
        );

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        Person first = expectedModel.getFilteredPersonList().get(0);
        Person second = expectedModel.getFilteredPersonList().get(1);

        expectedModel.deletePerson(second);
        expectedModel.deletePerson(first);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                Messages.format(second) + "\n" + Messages.format(first)
        );

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_rangeIndexes_success() {
        DeleteCommand command = new DeleteCommand(
                List.of(Index.fromOneBased(2), Index.fromOneBased(3), Index.fromOneBased(4))
        );

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        Person p2 = expectedModel.getFilteredPersonList().get(1);
        Person p3 = expectedModel.getFilteredPersonList().get(2);
        Person p4 = expectedModel.getFilteredPersonList().get(3);

        expectedModel.deletePerson(p4);
        expectedModel.deletePerson(p3);
        expectedModel.deletePerson(p2);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS,
                Messages.format(p4) + "\n"
                        + Messages.format(p3) + "\n"
                        + Messages.format(p2)
        );

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index invalid = Index.fromOneBased(999);

        DeleteCommand command = new DeleteCommand(List.of(invalid));

        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_NONEXISTENCE_INDEX, "999"));
    }

    @Test
    public void execute_multipleInvalidIndexes_throwsCommandException() {
        DeleteCommand command = new DeleteCommand(
                List.of(Index.fromOneBased(999), Index.fromOneBased(1000))
        );

        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_NONEXISTENCE_INDEX, "999, 1000"));
    }

    @Test
    public void execute_mixedValidAndInvalidIndexes_throwsCommandException() {
        DeleteCommand command = new DeleteCommand(
                List.of(INDEX_FIRST_PERSON, Index.fromOneBased(999))
        );

        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_NONEXISTENCE_INDEX, "999"));
    }

    @Test
    public void execute_partialInvalidIndexes_noDeletionOccurs() {
        Model originalModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        DeleteCommand command = new DeleteCommand(
                List.of(INDEX_FIRST_PERSON, Index.fromOneBased(999))
        );

        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_NONEXISTENCE_INDEX, "999"));

        // Ensure model unchanged (atomic behavior)
        assertEquals(originalModel, model);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON));
        DeleteCommand deleteSecondCommand = new DeleteCommand(List.of(INDEX_SECOND_PERSON));

        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(List.of(INDEX_FIRST_PERSON));
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        assertFalse(deleteFirstCommand.equals(1));
        assertFalse(deleteFirstCommand.equals(null));
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        List<Index> indexes = List.of(Index.fromOneBased(1));
        DeleteCommand command = new DeleteCommand(indexes);

        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetIndexes=" + indexes + "}";

        assertEquals(expected, command.toString());
    }

    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
