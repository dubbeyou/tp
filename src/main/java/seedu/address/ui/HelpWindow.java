package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s2-cs2103-f11-1.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;
    private static final String COPY_BUTTON_DEFAULT_TEXT = "Copy URL";
    private static final String COPY_BUTTON_COPIED_TEXT = "Copied!";

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private VBox commandContainer;

    private final HelpContentProvider helpContentProvider;
    private final PauseTransition copyFeedbackReset = new PauseTransition(Duration.seconds(1.2));

    /**
     * Creates a new HelpWindow with a specified Stage.
     * Delegates to the fully-parameterized constructor with a default HelpContentProvider.
     * Use this constructor for normal application usage.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        this(root, new HelpContentProvider());
    }

    /**
     * Internal constructor that fully initializes the HelpWindow.
     * This constructor performs the actual initialization and is package-private
     * to allow for dependency injection during testing (e.g., injecting a mock HelpContentProvider).
     *
     * @param root Stage to use as the root of the HelpWindow.
     * @param helpContentProvider Provider for help content sections. Must not be null.
     */
    HelpWindow(Stage root, HelpContentProvider helpContentProvider) {
        super(FXML, root);
        this.helpContentProvider = requireNonNull(helpContentProvider);
        helpMessage.setText(HELP_MESSAGE);
        copyButton.setText(COPY_BUTTON_DEFAULT_TEXT);
        copyButton.setOnAction(event -> copyUrl());
        copyFeedbackReset.setOnFinished(event -> copyButton.setText(COPY_BUTTON_DEFAULT_TEXT));
        renderHelpSections();
    }

    /**
     * Creates a new HelpWindow with default Stage and HelpContentProvider.
     * This is a convenience constructor for simple cases where no specific Stage is required.
     * Delegates to the Stage-based constructor with a newly created Stage.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);

        copyButton.setText(COPY_BUTTON_COPIED_TEXT);
        copyFeedbackReset.stop();
        copyFeedbackReset.playFromStart();
    }

    /**
     * Populates the command help section with styled command information.
     */
    private void renderHelpSections() {
        commandContainer.getChildren().clear();
        commandContainer.setFillWidth(true);

        for (HelpContentProvider.HelpSection helpSection : helpContentProvider.getHelpSections()) {
            addCommandSection(helpSection);
        }
    }

    /**
     * Adds a command section to the help container.
     *
     * @param helpSection Render-ready content for one command.
     */
    private void addCommandSection(HelpContentProvider.HelpSection helpSection) {
        Label nameLabel = createCommandNameLabel(helpSection.commandWord());
        Label descriptionLabel = createDescriptionLabel(helpSection.description());
        Label usageLabel = createUsageBlockLabel(helpSection.usage());
        Label examplesLabel = createExampleBlockLabel(helpSection.examples());

        VBox commandBox = createCommandBox(nameLabel, descriptionLabel, usageLabel, examplesLabel);
        commandContainer.getChildren().add(commandBox);
    }

    private Label createCommandNameLabel(String commandName) {
        Label nameLabel = new Label(commandName.toUpperCase());
        nameLabel.getStyleClass().add("help-command-name");
        return nameLabel;
    }

    private Label createDescriptionLabel(String description) {
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("help-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setManaged(!description.isEmpty());
        descriptionLabel.setVisible(!description.isEmpty());
        return descriptionLabel;
    }

    private Label createUsageBlockLabel(String text) {
        return createCodeBlockLabel(text, "help-code-block-usage");
    }

    private Label createExampleBlockLabel(String text) {
        return createCodeBlockLabel(text, "help-code-block-example");
    }

    private Label createCodeBlockLabel(String text, String variantStyleClass) {
        Label codeBlockLabel = new Label(text);
        codeBlockLabel.getStyleClass().add("help-code-block");
        codeBlockLabel.getStyleClass().add(variantStyleClass);
        codeBlockLabel.setWrapText(true);
        codeBlockLabel.setMaxWidth(Double.MAX_VALUE);
        codeBlockLabel.setManaged(!text.isEmpty());
        codeBlockLabel.setVisible(!text.isEmpty());
        return codeBlockLabel;
    }

    private VBox createCommandBox(Label nameLabel, Label descriptionLabel, Label usageLabel, Label examplesLabel) {
        VBox commandBox = new VBox(5);
        commandBox.getStyleClass().add("help-command-box");
        commandBox.setMaxWidth(Double.MAX_VALUE);
        commandBox.getChildren().addAll(nameLabel, descriptionLabel, usageLabel, examplesLabel);
        return commandBox;
    }
}
