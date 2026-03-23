package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.TagCommand;

/**
 * Provides render-ready help content for {@link HelpWindow}.
 */
final class HelpContentProvider {

    private static final List<HelpCommandSpec> COMMAND_SPECS = List.of(
            new HelpCommandSpec(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE),
            new HelpCommandSpec(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE),
            new HelpCommandSpec(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE),
            new HelpCommandSpec(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE),
            new HelpCommandSpec(TagCommand.COMMAND_WORD, TagCommand.MESSAGE_USAGE),
            new HelpCommandSpec(NoteCommand.COMMAND_WORD, NoteCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ClearCommand.COMMAND_WORD, "Usage: " + ClearCommand.COMMAND_WORD),
            new HelpCommandSpec(HelpCommand.COMMAND_WORD, "Usage: " + HelpCommand.COMMAND_WORD),
            new HelpCommandSpec(ExitCommand.COMMAND_WORD, "Usage: " + ExitCommand.COMMAND_WORD)
    );

    List<HelpSection> getHelpSections() {
        return COMMAND_SPECS.stream()
                .map(HelpContentProvider::toHelpSection)
                .toList();
    }

    private static HelpSection toHelpSection(HelpCommandSpec commandSpec) {
        ParsedHelpText parsed = parseHelpText(commandSpec.usageAndExamples());
        return new HelpSection(commandSpec.commandWord(), parsed.description(), parsed.usage(), parsed.examples());
    }

    static ParsedHelpText parseHelpText(String usageAndExamples) {
        ParsedHelpText baseText = splitExamples(usageAndExamples);
        int parametersIndex = baseText.usage().indexOf("Parameters:");

        if (parametersIndex < 0) {
            return baseText;
        }

        String descriptionPrefix = baseText.usage().substring(0, parametersIndex).trim();
        String usageText = baseText.usage().substring(parametersIndex).trim();
        String descriptionText = extractDescription(descriptionPrefix);

        return new ParsedHelpText(descriptionText, usageText, baseText.examples());
    }

    static ParsedHelpText splitExamples(String usageAndExamples) {
        requireNonNull(usageAndExamples);
        int firstExampleIndex = usageAndExamples.indexOf("\nExample:");
        if (firstExampleIndex < 0) {
            return new ParsedHelpText("", usageAndExamples.trim(), "");
        }

        String usageWithoutExamples = usageAndExamples.substring(0, firstExampleIndex).trim();
        String examplesText = usageAndExamples.substring(firstExampleIndex + 1).trim();
        return new ParsedHelpText("", usageWithoutExamples, examplesText);
    }

    static String extractDescription(String descriptionPrefix) {
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

