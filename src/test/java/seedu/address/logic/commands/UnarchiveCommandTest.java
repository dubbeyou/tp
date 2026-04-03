package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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
 * Contains integration tests and unit tests for UnarchiveCommand.
 */
public class UnarchiveCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getClonedTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_validIndexArchivedList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.archivePerson(firstPerson);
        model.updateFilteredPersonList(Person::isArchived);

        Person personToUnarchive = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnarchiveCommand unarchiveCommand = new UnarchiveCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(
                UnarchiveCommand.MESSAGE_UNARCHIVE_PERSON_SUCCESS,
                Messages.format(personToUnarchive));

        Model expectedModel = new ModelManager(getDeepCopiedAddressBook(model), new UserPrefs());
        expectedModel.updateFilteredPersonList(Person::isArchived);
        Person expectedPerson = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.unarchivePerson(expectedPerson);
        expectedModel.updateFilteredPersonList(Person::isArchived);

        assertCommandSuccess(unarchiveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleArchivedPersons_refreshShowsOnlyActivePersons() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        model.archivePerson(firstPerson);
        model.archivePerson(secondPerson);
        model.updateFilteredPersonList(Person::isArchived);

        UnarchiveCommand unarchiveCommand = new UnarchiveCommand(INDEX_FIRST_PERSON);

        Person personToUnarchive = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String expectedMessage = String.format(
                UnarchiveCommand.MESSAGE_UNARCHIVE_PERSON_SUCCESS,
                Messages.format(personToUnarchive));

        Model expectedModel = new ModelManager(getDeepCopiedAddressBook(model), new UserPrefs());
        expectedModel.updateFilteredPersonList(Person::isArchived);
        Person expectedPerson = expectedModel.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.unarchivePerson(expectedPerson);
        expectedModel.updateFilteredPersonList(Person::isArchived);

        assertCommandSuccess(unarchiveCommand, model, expectedMessage, expectedModel);
        assertFalse(model.getFilteredPersonList().stream().anyMatch(p -> !p.isArchived()));
    }

    @Test
    public void execute_invalidIndexArchivedList_throwsCommandException() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.archivePerson(firstPerson);
        model.updateFilteredPersonList(Person::isArchived);

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnarchiveCommand unarchiveCommand = new UnarchiveCommand(outOfBoundIndex);

        assertCommandFailure(unarchiveCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_personNotArchived_returnsUnsuccess() {
        // default filtered list is non-archived persons
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnarchiveCommand unarchiveCommand = new UnarchiveCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(
                UnarchiveCommand.MESSAGE_UNARCHIVE_PERSON_UNSUCCESS,
                person.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(unarchiveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        UnarchiveCommand firstCommand = new UnarchiveCommand(INDEX_FIRST_PERSON);
        UnarchiveCommand secondCommand = new UnarchiveCommand(INDEX_SECOND_PERSON);

        assertEquals(firstCommand, firstCommand);
        assertEquals(firstCommand, new UnarchiveCommand(INDEX_FIRST_PERSON));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
        assertNotEquals(firstCommand, secondCommand);
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        UnarchiveCommand unarchiveCommand = new UnarchiveCommand(targetIndex);
        String expected = UnarchiveCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, unarchiveCommand.toString());
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
    private AddressBook getDeepCopiedAddressBook(Model sourceModel) {
        AddressBook copied = new AddressBook();
        for (Person person : sourceModel.getAddressBook().getPersonList()) {
            copied.addPerson(new seedu.address.testutil.PersonBuilder(person).build());
        }
        return copied;
    }

}
