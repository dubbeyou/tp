package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    @Test
    public void sortFilteredPersonList_sortByName_success() {
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(BENSON)
                .withPerson(ALICE)
                .build();

        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        model.sortFilteredPersonList("name");

        assertEquals(ALICE, model.getFilteredPersonList().get(0));
        assertEquals(BENSON, model.getFilteredPersonList().get(1));
    }

    @Test
    public void resetSort_afterSorting_restoresOriginalOrder() {
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(BENSON)
                .withPerson(ALICE)
                .build();

        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        model.sortFilteredPersonList("name");
        model.resetSort();

        assertEquals(BENSON, model.getFilteredPersonList().get(0));
        assertEquals(ALICE, model.getFilteredPersonList().get(1));
    }

    @Test
    public void sortFilteredPersonList_sortByVisit_success() {
        Person personWithLaterVisit = new PersonBuilder(ALICE)
                .withVisitDateTime("2026-01-01 10:00")
                .build();

        Person personWithEarlierVisit = new PersonBuilder(BENSON)
                .withVisitDateTime("2025-01-01 10:00")
                .build();

        Person personWithoutVisit = new PersonBuilder()
                .withName("No Visit")
                .build();

        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(personWithLaterVisit)
                .withPerson(personWithoutVisit)
                .withPerson(personWithEarlierVisit)
                .build();

        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        model.sortFilteredPersonList("visit");

        assertEquals(personWithEarlierVisit, model.getFilteredPersonList().get(0));
        assertEquals(personWithLaterVisit, model.getFilteredPersonList().get(1));
        assertEquals(personWithoutVisit, model.getFilteredPersonList().get(2));
    }

    @Test
    public void constructor_withArchivedPerson_filtersOutArchivedPerson() {
        Person activePerson = new PersonBuilder(ALICE).build();
        Person archivedPerson = new PersonBuilder(BENSON).build();
        archivedPerson.setArchived(true);

        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(activePerson)
                .withPerson(archivedPerson)
                .build();

        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(activePerson, model.getFilteredPersonList().get(0));
    }

    @Test
    public void archivePerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.archivePerson(null));
    }

    @Test
    public void archivePerson_validPerson_setsArchivedTrue() {
        Person person = new PersonBuilder(ALICE).build();
        modelManager.addPerson(person);

        modelManager.archivePerson(person);

        assertTrue(person.isArchived());
    }

    @Test
    public void unarchivePerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.unarchivePerson(null));
    }

    @Test
    public void unarchivePerson_validPerson_setsArchivedFalse() {
        Person person = new PersonBuilder(ALICE).build();
        person.setArchived(true);

        AddressBook addressBook = new AddressBookBuilder().withPerson(person).build();
        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        model.unarchivePerson(person);

        assertFalse(person.isArchived());
    }

    @Test
    public void predicateShowAllPersons_filtersArchivedPersons() {
        Person active = new PersonBuilder(ALICE).build();
        Person archived = new PersonBuilder(BENSON).build();
        archived.setArchived(true);

        assertTrue(PREDICATE_SHOW_ALL_PERSONS.test(active));
        assertFalse(PREDICATE_SHOW_ALL_PERSONS.test(archived));
    }

}

