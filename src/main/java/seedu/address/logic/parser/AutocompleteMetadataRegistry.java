package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_VISIT;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * Parser-side source of truth for autocomplete command metadata.
 */
public final class AutocompleteMetadataRegistry {

    private static final List<String> NO_PREFIXES = List.of();
    private static final Set<String> NO_REPEATABLE_PREFIXES = Set.of();

    private static final String FIND_NAME_PREFIX = PREFIX_NAME.getPrefix();
    private static final String FIND_TAG_PREFIX = PREFIX_TAG.getPrefix();
    private static final String FIND_DATE_PREFIX = PREFIX_DATE.getPrefix();
    private static final String FIND_START_DATE_PREFIX = PREFIX_START_DATE.getPrefix();
    private static final String FIND_END_DATE_PREFIX = PREFIX_END_DATE.getPrefix();

    private static final List<String> FIND_PREFIXES = List.of(
            FIND_NAME_PREFIX, FIND_TAG_PREFIX, FIND_DATE_PREFIX, FIND_START_DATE_PREFIX, FIND_END_DATE_PREFIX);

    private static final List<String> COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD,
            ArchiveCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ListArchiveCommand.COMMAND_WORD,
            NoteCommand.COMMAND_WORD,
            TagCommand.COMMAND_WORD,
            UnarchiveCommand.COMMAND_WORD
    );

    private static final Map<String, AutocompleteCommandMetadata> COMMAND_CONFIGS = Map.of(
            AddCommand.COMMAND_WORD, standard(false,
                    List.of(PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(),
                            PREFIX_ADDRESS.getPrefix(), PREFIX_NOTE.getPrefix(), PREFIX_VISIT.getPrefix(),
                            PREFIX_TAG.getPrefix()),
                    Set.of(PREFIX_TAG.getPrefix())),
            EditCommand.COMMAND_WORD, standard(true,
                    List.of(PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(),
                            PREFIX_ADDRESS.getPrefix(), PREFIX_NOTE.getPrefix(), PREFIX_VISIT.getPrefix(),
                            PREFIX_TAG.getPrefix()),
                    Set.of(PREFIX_TAG.getPrefix())),
            FindCommand.COMMAND_WORD, findExclusive(FIND_PREFIXES),
            ListCommand.COMMAND_WORD, standard(false,
                    List.of(PREFIX_SORT.getPrefix()), NO_REPEATABLE_PREFIXES),
            ArchiveCommand.COMMAND_WORD, standard(true, NO_PREFIXES, NO_REPEATABLE_PREFIXES),
            NoteCommand.COMMAND_WORD, standard(true,
                    List.of(PREFIX_NOTE.getPrefix()), NO_REPEATABLE_PREFIXES),
            TagCommand.COMMAND_WORD, standard(true,
                    List.of(PREFIX_ADD_TAG.getPrefix(), PREFIX_DELETE_TAG.getPrefix()),
                    Set.of(PREFIX_ADD_TAG.getPrefix(), PREFIX_DELETE_TAG.getPrefix())),
            UnarchiveCommand.COMMAND_WORD, standard(true, NO_PREFIXES, NO_REPEATABLE_PREFIXES)
    );

    private AutocompleteMetadataRegistry() {}

    public static List<String> commandWords() {
        return COMMAND_WORDS;
    }

    public static Optional<AutocompleteCommandMetadata> getMetadata(String commandWord) {
        return Optional.ofNullable(COMMAND_CONFIGS.get(commandWord));
    }

    public static String findNamePrefix() {
        return FIND_NAME_PREFIX;
    }

    public static String findTagPrefix() {
        return FIND_TAG_PREFIX;
    }

    public static String findDatePrefix() {
        return FIND_DATE_PREFIX;
    }

    public static String findStartDatePrefix() {
        return FIND_START_DATE_PREFIX;
    }

    public static String findEndDatePrefix() {
        return FIND_END_DATE_PREFIX;
    }

    private static AutocompleteCommandMetadata standard(
            boolean requiresIndex, List<String> prefixes, Set<String> repeatablePrefixes) {
        return new AutocompleteCommandMetadata(requiresIndex, prefixes, repeatablePrefixes, false);
    }

    private static AutocompleteCommandMetadata findExclusive(List<String> prefixes) {
        return new AutocompleteCommandMetadata(false, prefixes, NO_REPEATABLE_PREFIXES, true);
    }

    /**
     * Immutable parser-side autocomplete metadata for a command.
     */
    public record AutocompleteCommandMetadata(boolean requiresIndex,
                                              List<String> prefixes,
                                              Set<String> repeatablePrefixes,
                                              boolean isFindModeExclusive) {}
}
