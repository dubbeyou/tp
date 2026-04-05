package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.SortField;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listSortedByName_success() {
        ListCommand command = new ListCommand(SortField.NAME);

        expectedModel.sortFilteredPersonList(SortField.NAME);

        assertCommandSuccess(command, model,
                String.format(ListCommand.MESSAGE_SORT_SUCCESS, "name"),
                expectedModel);
    }

    @Test
    public void execute_listSortedByVisit_success() {
        ListCommand command = new ListCommand(SortField.VISIT);

        expectedModel.sortFilteredPersonList(SortField.VISIT);

        assertCommandSuccess(command, model,
                String.format(ListCommand.MESSAGE_SORT_SUCCESS, "visit"),
                expectedModel);
    }

    @Test
    public void execute_noSort_resetsSort() {
        model.sortFilteredPersonList(SortField.NAME);

        ListCommand command = new ListCommand(null);

        expectedModel.resetSort();

        assertCommandSuccess(command, model,
                ListCommand.MESSAGE_SUCCESS,
                expectedModel);
    }

}
