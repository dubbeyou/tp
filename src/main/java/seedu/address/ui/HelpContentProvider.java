package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";
    private static final String COLON = ":";
    private static final String NEWLINE_REGEX = "\\R";
    private static final int NOT_FOUND = -1;
    private static final int ZERO_POSITION = 0;
    private static final Set<String> COMMANDS_WITH_HIDDEN_USAGE = Set.of(
            ListArchiveCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD
    );

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
            new HelpCommandSpec(ListArchiveCommand.COMMAND_WORD, ListArchiveCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE),
            new HelpCommandSpec(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE),
            new HelpCommandSpec(ExitCommand.COMMAND_WORD, ExitCommand.MESSAGE_USAGE)
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
        String usageText = COMMANDS_WITH_HIDDEN_USAGE.contains(commandSpec.commandWord())
                ? EMPTY_STRING : parsed.usage();
        return new HelpSection(commandSpec.commandWord(), parsed.description(), usageText, parsed.examples());
    }

    private static ParsedHelpText parseHelpText(String usageAndExamples) {
        ParsedHelpText baseText = splitExamples(usageAndExamples);
        int parametersIndex = indexOfIgnoreCase(baseText.usage(), PARAMETERS_SECTION_HEADER);

        if (parametersIndex < 0) {
            return parseCompactFormat(baseText);
        }

        return parseStandardFormat(baseText, parametersIndex);
    }

    private static ParsedHelpText parseCompactFormat(ParsedHelpText baseText) {
        int colonIndex = baseText.usage().indexOf(COLON);
        if (colonIndex > ZERO_POSITION) {
            // Support compact format: "command: description".
            String descriptionText = extractDescription(baseText.usage());
            String commandWord = baseText.usage().substring(ZERO_POSITION, colonIndex).trim();
            return new ParsedHelpText(descriptionText, commandWord, baseText.examples());
        }
        return baseText;
    }

    private static ParsedHelpText parseStandardFormat(ParsedHelpText baseText, int parametersIndex) {
        String descriptionPrefix = baseText.usage().substring(ZERO_POSITION, parametersIndex).trim();
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
        if (firstExampleIndex == NOT_FOUND) {
            return new ParsedHelpText(EMPTY_STRING, usageAndExamples.trim(), EMPTY_STRING);
        }

        String usageWithoutExamples = usageAndExamples.substring(ZERO_POSITION, firstExampleIndex).trim();
        String examplesText = usageAndExamples.substring(firstExampleIndex + 1).trim();
        return new ParsedHelpText(EMPTY_STRING, usageWithoutExamples, examplesText);
    }

    private static String extractDescription(String descriptionPrefix) {
        requireNonNull(descriptionPrefix);
        int colonIndex = descriptionPrefix.indexOf(COLON);
        if (colonIndex >= ZERO_POSITION) {
            if (colonIndex + 1 < descriptionPrefix.length()) {
                descriptionPrefix = descriptionPrefix.substring(colonIndex + 1).trim();
            } else {
                descriptionPrefix = EMPTY_STRING;
            }
        }
        return descriptionPrefix.replaceAll(NEWLINE_REGEX, SPACE).trim();
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
