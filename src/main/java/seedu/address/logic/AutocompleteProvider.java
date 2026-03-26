package seedu.address.logic;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_VISIT;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
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
 * Provides context-aware command line autocompletion suggestions.
 */
public final class AutocompleteProvider {

    private static final Logger logger = LogsCenter.getLogger(AutocompleteProvider.class);
    private static final String EMPTY_STRING = "";

    private static final List<String> COMMAND_WORDS = List.of(
            AddCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            NoteCommand.COMMAND_WORD,
            TagCommand.COMMAND_WORD
    );

    private static final Map<String, AutocompletePrefixConfig> AUTOCOMPLETE_PREFIX_CONFIGS = Map.of(
            AddCommand.COMMAND_WORD, new AutocompletePrefixConfig(false,
                    List.of(PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(),
                            PREFIX_ADDRESS.getPrefix(), PREFIX_NOTE.getPrefix(), PREFIX_VISIT.getPrefix(),
                            PREFIX_TAG.getPrefix()),
                    Set.of(PREFIX_TAG.getPrefix())),
            EditCommand.COMMAND_WORD, new AutocompletePrefixConfig(true,
                    List.of(PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(),
                            PREFIX_ADDRESS.getPrefix(), PREFIX_NOTE.getPrefix(), PREFIX_VISIT.getPrefix(),
                            PREFIX_TAG.getPrefix()),
                    Set.of(PREFIX_TAG.getPrefix())),
            FindCommand.COMMAND_WORD, new AutocompletePrefixConfig(false,
                    List.of(PREFIX_NAME.getPrefix(), PREFIX_TAG.getPrefix()),
                    Set.of()),
            ListCommand.COMMAND_WORD, new AutocompletePrefixConfig(false,
                    List.of(PREFIX_SORT.getPrefix()),
                    Set.of()),
            NoteCommand.COMMAND_WORD, new AutocompletePrefixConfig(true,
                    List.of(PREFIX_NOTE.getPrefix()),
                    Set.of()),
            TagCommand.COMMAND_WORD, new AutocompletePrefixConfig(true,
                    List.of(PREFIX_ADD_TAG.getPrefix(), PREFIX_DELETE_TAG.getPrefix()),
                    Set.of(PREFIX_ADD_TAG.getPrefix(), PREFIX_DELETE_TAG.getPrefix()))
    );

    private AutocompleteProvider() {}

    private record AutocompletePrefixConfig(boolean requiresIndex,
            List<String> prefixes, Set<String> repeatablePrefixes) {}

    /**
     * Returns a full completion suggestion for the current user input.
     *
     * This method analyses the user input and provides context-aware suggestions:
     *     If input contains no trailing whitespace, suggests command word completion (e.g., "a" -> "add")
     *     If input contains trailing whitespace, suggests argument/prefix completion (e.g., "add " -> "add n/")
     *     Preserves leading whitespace in the returned suggestion for UI alignment
     *
     * For prefix suggestions:
     *     Enforces index requirement for commands like edit, note, and tag
     *     Progressively suggests prefixes in order after previous prefixes are entered
     *     For repeatable prefixes (e.g., "t/"), continues suggesting after all other prefixes are complete
     *
     * @param userInput the user's current input string
     * @return an {@code Optional} containing the full completed suggestion if one exists,
     *         or an empty {@code Optional} if no suggestion is available
     */
    public static Optional<String> suggestCompletion(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return Optional.empty();
        }

        // Extract leading spaces to preserve them in the suggestion
        int leadingSpaceCount = userInput.length() - userInput.stripLeading().length();
        String leadingSpaces = userInput.substring(0, leadingSpaceCount);
        String trimmedInput = userInput.stripLeading();

        Optional<String> suggestion;
        if (containsWhitespace(trimmedInput)) {
            suggestion = suggestArgumentCompletion(trimmedInput);
        } else {
            suggestion = suggestCommandCompletion(trimmedInput);
        }

        // Prepend leading spaces back to the suggestion so it aligns with the input
        return suggestion.map(s -> leadingSpaces + s);
    }

    private static Optional<String> suggestCommandCompletion(String input) {
        return COMMAND_WORDS.stream()
                .filter(command -> command.startsWith(input))
                .findFirst()
                .filter(match -> !match.equals(input));
    }

    private static Optional<String> suggestArgumentCompletion(String input) {
        assert input != null : "suggestArgumentCompletion input must not be null";

        int firstWhitespaceIndex = firstWhitespaceIndex(input);
        assert firstWhitespaceIndex >= 0 : "suggestArgumentCompletion expects command + whitespace + args";
        String commandWord = input.substring(0, firstWhitespaceIndex);

        AutocompletePrefixConfig config = AUTOCOMPLETE_PREFIX_CONFIGS.get(commandWord);
        if (config == null) {
            logger.fine("No autocomplete config for command word: " + commandWord);
            return Optional.empty();
        }

        String args = input.substring(firstWhitespaceIndex).stripLeading();
        String targetArgs = args;
        if (config.requiresIndex()) {
            if (!hasIndexToken(args)) {
                logger.fine("Autocomplete withheld because required index token is missing");
                return Optional.empty();
            }
            targetArgs = removeIndexToken(args);
        }

        if (targetArgs.isEmpty()) {
            String firstPrefix = config.prefixes().get(0);
            return Optional.of((input.endsWith(" ") ? input : input + " ") + firstPrefix);
        }

        String lastToken = lastToken(targetArgs);
        if (lastToken.isEmpty()) {
            Optional<String> nextUnusedPrefix = nextUnusedPrefix(
                    config.prefixes(), config.repeatablePrefixes(), targetArgs);
            if (nextUnusedPrefix.isPresent()) {
                return Optional.of(input + nextUnusedPrefix.get());
            }

            Optional<String> repeatablePrefix = nextRepeatablePrefix(config.repeatablePrefixes());
            if (repeatablePrefix.isPresent()) {
                return Optional.of(input + repeatablePrefix.get());
            }

            return Optional.empty();
        }

        String filterArgs = targetArgs;
        Optional<String> firstMatch = config.prefixes().stream()
                .filter(prefix -> prefix.startsWith(lastToken))
                .filter(prefix -> !containsPrefixToken(filterArgs, prefix)
                || config.repeatablePrefixes().contains(prefix))
            .findFirst();

        if (firstMatch.isEmpty()) {
            return Optional.empty();
        }

        String match = firstMatch.get();
        if (match.equals(lastToken)) {
            return Optional.empty();
        }

        return Optional.of(input + match.substring(lastToken.length()));
    }

    private static Optional<String> nextUnusedPrefix(
            List<String> orderedPrefixes, Set<String> repeatablePrefixes, String args) {
        for (String prefix : orderedPrefixes) {
            if (repeatablePrefixes.contains(prefix)) {
                continue;
            }

            if (!containsPrefixToken(args, prefix)) {
                return Optional.of(prefix);
            }
        }

        return Optional.empty();
    }

    private static Optional<String> nextRepeatablePrefix(Set<String> repeatablePrefixes) {
        if (repeatablePrefixes.isEmpty()) {
            return Optional.empty();
        }

        // Deterministic behavior if more repeatable prefixes are added in the future.
        return repeatablePrefixes.stream().sorted().findFirst();
    }

    private static boolean containsPrefixToken(String value, String prefix) {
        if (value.startsWith(prefix)) {
            return true;
        }

        for (int i = 0; i < value.length() - 1; i++) {
            if (Character.isWhitespace(value.charAt(i)) && value.startsWith(prefix, i + 1)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasIndexToken(String args) {
        return StringUtil.isNonZeroUnsignedInteger(firstToken(args));
    }

    private static String removeIndexToken(String args) {
        assert args != null : "removeIndexToken args must not be null";
        String trimmed = args.stripLeading();
        int tokenEnd = tokenEndIndex(trimmed);
        if (tokenEnd >= trimmed.length()) {
            return EMPTY_STRING;
        }

        return trimmed.substring(tokenEnd).stripLeading();
    }

    private static String firstToken(String args) {
        assert args != null : "firstToken args must not be null";
        String trimmed = args.stripLeading();
        int tokenEnd = tokenEndIndex(trimmed);
        return trimmed.substring(0, tokenEnd);
    }

    private static int tokenEndIndex(String value) {
        assert value != null : "tokenEndIndex value must not be null";
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return i;
            }
        }

        return value.length();
    }

    private static boolean containsWhitespace(String value) {
        return firstWhitespaceIndex(value) >= 0;
    }

    private static int firstWhitespaceIndex(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    private static String lastToken(String value) {
        int lastWhitespace = -1;

        for (int i = value.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(value.charAt(i))) {
                lastWhitespace = i;
                break;
            }
        }

        return lastWhitespace < 0 ? value : value.substring(lastWhitespace + 1);
    }
}
