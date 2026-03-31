package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListArchiveCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UnarchiveCommand;

/**
 * Provides render-ready help content for {@link HelpWindow}.
 */
final class HelpContentProvider {

    private static final String PARAMETERS_SECTION_HEADER = "Parameters:";
    private static final String EXAMPLES_SECTION_HEADER = "\nExample:";

    private static final List<HelpCommandSpec> COMMAND_SPECS = List.of(
            new HelpCommandSpec(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE),
            new HelpCommandSpec(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE),
            new HelpCommandSpec(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE),
            new HelpCommandSpec(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE),
            new HelpCommandSpec(TagCommand.COMMAND_WORD, TagCommand.MESSAGE_USAGE),
            new HelpCommandSpec(NoteCommand.COMMAND_WORD, NoteCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ArchiveCommand.COMMAND_WORD, ArchiveCommand.MESSAGE_USAGE),
            new HelpCommandSpec(UnarchiveCommand.COMMAND_WORD, UnarchiveCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ListArchiveCommand.COMMAND_WORD, "Usage: " + ListArchiveCommand.COMMAND_WORD),
            new HelpCommandSpec(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE),
            new HelpCommandSpec(HelpCommand.COMMAND_WORD, "Usage: " + HelpCommand.COMMAND_WORD),
            new HelpCommandSpec(ExitCommand.COMMAND_WORD, "Usage: " + ExitCommand.COMMAND_WORD)
    );

    private HelpContentProvider() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    static List<HelpSection> getHelpSections() {
        return COMMAND_SPECS.stream()
                .map(HelpContentProvider::toHelpSection)
                .toList();
    }

    private static HelpSection toHelpSection(HelpCommandSpec commandSpec) {
        ParsedHelpText parsed = parseHelpText(commandSpec.usageAndExamples());
        return new HelpSection(commandSpec.commandWord(), parsed.description(), parsed.usage(), parsed.examples());
    }

    private static ParsedHelpText parseHelpText(String usageAndExamples) {
        ParsedHelpText baseText = splitExamples(usageAndExamples);
        int parametersIndex = indexOfIgnoreCase(baseText.usage(), PARAMETERS_SECTION_HEADER);

        if (parametersIndex < 0) {
            int usageIndex = indexOfIgnoreCase(baseText.usage(), "Usage:");
            if (usageIndex >= 0) {
                // For simple commands (no Parameters section), keep description and usage separate.
                String descriptionPrefix = baseText.usage().substring(0, usageIndex).trim();
                String usageText = baseText.usage().substring(usageIndex).trim();
                String descriptionText = extractDescription(descriptionPrefix);
                return new ParsedHelpText(descriptionText, usageText, baseText.examples());
            }

            int colonIndex = baseText.usage().indexOf(':');
            if (colonIndex > 0) {
                // Support compact format: "command: description".
                String descriptionText = extractDescription(baseText.usage());
                String commandWord = baseText.usage().substring(0, colonIndex).trim();
                String usageText = "Usage: " + commandWord;
                return new ParsedHelpText(descriptionText, usageText, baseText.examples());
            }

            return baseText;
        }

        String descriptionPrefix = baseText.usage().substring(0, parametersIndex).trim();
        String usageText = baseText.usage().substring(parametersIndex).trim();
        String descriptionText = extractDescription(descriptionPrefix);

        return new ParsedHelpText(descriptionText, usageText, baseText.examples());
    }

    private static int indexOfIgnoreCase(String source, String target) {
        requireNonNull(source);
        requireNonNull(target);
        return source.toLowerCase(Locale.ROOT).indexOf(target.toLowerCase(Locale.ROOT));
    }

    private static ParsedHelpText splitExamples(String usageAndExamples) {
        requireNonNull(usageAndExamples);
        int firstExampleIndex = indexOfIgnoreCase(usageAndExamples, EXAMPLES_SECTION_HEADER);
        if (firstExampleIndex < 0) {
            return new ParsedHelpText("", usageAndExamples.trim(), "");
        }

        String usageWithoutExamples = usageAndExamples.substring(0, firstExampleIndex).trim();
        String examplesText = usageAndExamples.substring(firstExampleIndex + 1).trim();
        return new ParsedHelpText("", usageWithoutExamples, examplesText);
    }

    private static String extractDescription(String descriptionPrefix) {
        requireNonNull(descriptionPrefix);
        int colonIndex = descriptionPrefix.indexOf(':');
        if (colonIndex >= 0) {
            if (colonIndex + 1 < descriptionPrefix.length()) {
                descriptionPrefix = descriptionPrefix.substring(colonIndex + 1).trim();
            } else {
                descriptionPrefix = "";
            }
        }
        return descriptionPrefix.replaceAll("\\R", " ").trim();
    }

    record HelpCommandSpec(String commandWord, String usageAndExamples) {
        HelpCommandSpec {
            requireNonNull(commandWord);
            requireNonNull(usageAndExamples);
        }
    }

    record ParsedHelpText(String description, String usage, String examples) {}

    record HelpSection(String commandWord, String description, String usage, String examples) {}
}
