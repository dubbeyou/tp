package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ArchiveCommand}.
 */
public class ArchiveCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getClonedTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToArchive = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ArchiveCommand archiveCommand = new ArchiveCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_PERSON_SUCCESS,
                Messages.format(personToArchive));

        ModelManager expectedModel = getExpectedModelCopy();
        Person expectedPersonToArchive = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.archivePerson(expectedPersonToArchive);
        expectedModel.updateFilteredPersonList(p -> !p.isArchived());

        assertCommandSuccess(archiveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ArchiveCommand archiveCommand = new ArchiveCommand(outOfBoundIndex);

        assertCommandFailure(archiveCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToArchive = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ArchiveCommand archiveCommand = new ArchiveCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_PERSON_SUCCESS,
                Messages.format(personToArchive));

        Model expectedModel = getExpectedModelCopy();
        Person expectedPersonToArchive = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.archivePerson(expectedPersonToArchive);
        expectedModel.updateFilteredPersonList(p -> !p.isArchived());

        assertCommandSuccess(archiveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ArchiveCommand archiveCommand = new ArchiveCommand(outOfBoundIndex);

        assertCommandFailure(archiveCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ArchiveCommand archiveFirstCommand = new ArchiveCommand(INDEX_FIRST_PERSON);
        ArchiveCommand archiveSecondCommand = new ArchiveCommand(INDEX_SECOND_PERSON);

        assertTrue(archiveFirstCommand.equals(archiveFirstCommand));

        ArchiveCommand archiveFirstCommandCopy = new ArchiveCommand(INDEX_FIRST_PERSON);
        assertTrue(archiveFirstCommand.equals(archiveFirstCommandCopy));

        assertFalse(archiveFirstCommand.equals(1));
        assertFalse(archiveFirstCommand.equals(null));
        assertFalse(archiveFirstCommand.equals(archiveSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ArchiveCommand archiveCommand = new ArchiveCommand(targetIndex);
        String expected = ArchiveCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, archiveCommand.toString());
    }

    /**
     * Returns a fresh AddressBook with cloned typical persons to avoid mutating shared test fixtures.
     */
    private AddressBook getClonedTypicalAddressBook() {
        AddressBook clonedAddressBook = new AddressBook();
        for (Person person : getTypicalPersons()) {
            clonedAddressBook.addPerson(new PersonBuilder(person).build());
        }
        return clonedAddressBook;
    }

    /**
     * Returns a deep copy of the current model state for expected-model assertions.
     */
    private ModelManager getExpectedModelCopy() {
        AddressBook copiedAddressBook = new AddressBook();
        for (Person person : model.getAddressBook().getPersonList()) {
            copiedAddressBook.addPerson(new PersonBuilder(person).build());
        }
        ModelManager expectedModel = new ModelManager(copiedAddressBook, new UserPrefs());
        expectedModel.updateFilteredPersonList(model.getFilteredPersonList()::contains);
        return expectedModel;
    }
}