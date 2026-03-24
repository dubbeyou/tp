package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests for ListArchiveCommand.
 */
public class ListArchiveCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getClonedTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_noArchivedPersons_showsEmptyList() {
        Model expectedModel = new ModelManager(getDeepCopiedAddressBook(model), new UserPrefs());
        expectedModel.updateFilteredPersonList(Person::isArchived);

        assertCommandSuccess(new ListArchiveCommand(), model, ListArchiveCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_hasArchivedPersons_showsOnlyArchivedPersons() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.archivePerson(firstPerson);

        Model expectedModel = new ModelManager(getDeepCopiedAddressBook(model), new UserPrefs());
        expectedModel.updateFilteredPersonList(Person::isArchived);

        assertCommandSuccess(new ListArchiveCommand(), model, ListArchiveCommand.MESSAGE_SUCCESS, expectedModel);
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
            copied.addPerson(new PersonBuilder(person).build());
        }
        return copied;
    }
}
