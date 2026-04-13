package seedu.address.ui;

import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.AutocompleteProvider;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String EMPTY_TEXT = "";
    private static final double AUTOCOMPLETE_HINT_OFFSET = 12.0;
    private static final double AUTOCOMPLETE_HINT_VERTICAL_OFFSET = 7.5;
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final CommandHistory commandHistory = new CommandHistory();

    @FXML
    private TextField commandTextField;

    @FXML
    private TextFlow autocompleteHintFlow;

    @FXML
    private Text autocompleteHintPrefixText;

    @FXML
    private Text autocompleteHintSuffixText;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        autocompleteHintFlow.prefWidthProperty().bind(commandTextField.widthProperty());
        autocompleteHintFlow.setTranslateX(AUTOCOMPLETE_HINT_OFFSET);
        autocompleteHintFlow.setTranslateY(AUTOCOMPLETE_HINT_VERTICAL_OFFSET);
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> handleTextChanged());
        commandTextField.caretPositionProperty().addListener((unused1, unused2, unused3) -> updateAutocompleteHint());
        commandTextField.focusedProperty().addListener((unused1, unused2, unused3) -> updateAutocompleteHint());
        commandTextField.setOnKeyPressed(this::handleCommandBoxKeyPress);
        clearAutocompleteHint();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isEmpty()) {
            return;
        }

        commandHistory.add(commandText);

        try {
            commandExecutor.execute(commandText);
            setCommandText(EMPTY_TEXT);
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    private void handleCommandBoxKeyPress(KeyEvent event) {
        if (event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN && event.getCode() != KeyCode.TAB) {
            return;
        }

        if (event.getCode() == KeyCode.TAB) {
            handleTabKeyEvent(event);
        } else if (event.getCode() == KeyCode.UP) {
            handleUpKeyEvent(event);
        } else if (event.getCode() == KeyCode.DOWN) {
            handleDownKeyEvent(event);
        }
    }

    private void handleUpKeyEvent(KeyEvent event) {
        String recalled = commandHistory.navigateUp();
        fillRecalledCommand(recalled);
        event.consume();
    }

    private void handleDownKeyEvent(KeyEvent event) {
        String recalled = commandHistory.navigateDown();
        fillRecalledCommand(recalled);
        event.consume();
    }

    private void fillRecalledCommand(String recalled) {
        setCommandText(recalled);
        commandTextField.positionCaret(recalled.length());
    }

    private void handleTabKeyEvent(KeyEvent event) {
        if (!acceptAutocompleteSuggestion()) {
            return;
        }
        commandTextField.requestFocus();
        commandTextField.positionCaret(commandTextField.getText().length());
        event.consume();
    }

    private void handleTextChanged() {
        setStyleToDefault();
        updateAutocompleteHint();
    }

    private void updateAutocompleteHint() {
        if (!shouldComputeAutocompleteHint()) {
            clearAutocompleteHint();
            return;
        }

        String userInput = commandTextField.getText();
        getAutocompleteSuggestionIfExtends(userInput)
                .ifPresentOrElse(
                        suggestion -> showAutocompleteHint(userInput, suggestion),
                        this::clearAutocompleteHint);
    }

    private boolean acceptAutocompleteSuggestion() {
        String currentInput = commandTextField.getText();
        return getAutocompleteSuggestionIfExtends(currentInput)
                .map(suggestion -> {
                    logAutocompletionAccepted(currentInput, suggestion);
                    applyAutocompleteSuggestion(suggestion);
                    return true;
                })
                .orElse(false);
    }

    private void setCommandText(String text) {
        commandTextField.setText(text);
    }

    private void showAutocompleteHint(String userInput, String suggestion) {
        assert userInput != null : "showAutocompleteHint userInput must not be null";
        assert suggestion != null : "showAutocompleteHint suggestion must not be null";
        assert suggestion.startsWith(userInput) : "Autocomplete suggestion must extend user input";

        String suffix = suggestion.substring(userInput.length());
        setHintText(userInput, suffix);
    }

    private void clearAutocompleteHint() {
        setHintText(EMPTY_TEXT, EMPTY_TEXT);
    }

    private void setHintText(String prefix, String suffix) {
        autocompleteHintPrefixText.setText(prefix);
        autocompleteHintSuffixText.setText(suffix);
    }

    /**
     * Determines if autocomplete hint computation is appropriate based on
     * current text field state (focus and caret position).
     */
    private boolean shouldComputeAutocompleteHint() {
        if (!commandTextField.isFocused()) {
            return false;
        }
        String userInput = commandTextField.getText();
        return commandTextField.getCaretPosition() == userInput.length();
    }

    /**
     * Retrieves an autocomplete suggestion that extends the given input.
     * Returns an empty Optional if no suggestion extends the input.
     */
    private Optional<String> getAutocompleteSuggestionIfExtends(String input) {
        return AutocompleteProvider.suggestCompletion(input)
                .filter(suggestion -> suggestion.length() > input.length());
    }

    /**
     * Applies the accepted autocomplete suggestion to the command text field,
     * setting text, positioning caret, and clearing the hint.
     */
    private void applyAutocompleteSuggestion(String suggestion) {
        setCommandText(suggestion);
        commandTextField.positionCaret(suggestion.length());
        clearAutocompleteHint();
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    private void logAutocompletionAccepted(String input, String suggestion) {
        logger.fine("Accepted autocomplete suggestion. inputLength="
                + input.length() + ", suggestionLength=" + suggestion.length());
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
