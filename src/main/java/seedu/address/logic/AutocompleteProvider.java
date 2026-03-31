package seedu.address.logic;

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
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
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
 * Provides context-aware command line autocompletion suggestions.
 */
public final class AutocompleteProvider {

    private static final Logger logger = LogsCenter.getLogger(AutocompleteProvider.class);
    private static final String EMPTY_STRING = "";
    private static final String SINGLE_SPACE = " ";

    private static final List<String> NO_PREFIXES = List.of();
    private static final Set<String> NO_REPEATABLE_PREFIXES = Set.of();

    private static final String FIND_NAME_PREFIX = PREFIX_NAME.getPrefix();
    private static final String FIND_TAG_PREFIX = PREFIX_TAG.getPrefix();
    private static final String FIND_DATE_PREFIX = PREFIX_DATE.getPrefix();
    private static final String FIND_START_DATE_PREFIX = PREFIX_START_DATE.getPrefix();
    private static final String FIND_END_DATE_PREFIX = PREFIX_END_DATE.getPrefix();

    private static final List<String> FIND_PREFIXES = List.of(
            FIND_NAME_PREFIX, FIND_TAG_PREFIX, FIND_DATE_PREFIX, FIND_START_DATE_PREFIX, FIND_END_DATE_PREFIX);

        private static final List<String> COMMAND_WORDS = createCommandWords();

        private static final Map<String, AutocompletePrefixConfig> AUTOCOMPLETE_PREFIX_CONFIGS = createPrefixConfigs();

    private AutocompleteProvider() {}

    private record AutocompletePrefixConfig(boolean requiresIndex,
            List<String> prefixes, Set<String> repeatablePrefixes) {}

    private record FindPrefixState(boolean hasName, boolean hasTag, boolean hasDate,
                                   boolean hasStartDate, boolean hasEndDate) {
        boolean hasIncompleteDateRangePair() {
            return hasStartDate ^ hasEndDate;
        }

        boolean hasCompletedMode() {
            return hasName || hasTag || hasDate || (hasStartDate && hasEndDate);
        }
    }

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
        boolean isExactCommand = COMMAND_WORDS.stream().anyMatch(command -> command.equals(input));
        if (isExactCommand) {
            return COMMAND_WORDS.stream()
                    .filter(command -> command.startsWith(input))
                    .filter(command -> !command.equals(input))
                    .findFirst();
        }

        return completeCurrentToken(input, input, COMMAND_WORDS, NO_REPEATABLE_PREFIXES, EMPTY_STRING);
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
        Optional<String> targetArgsOptional = extractTargetArgs(args, config.requiresIndex());
        if (targetArgsOptional.isEmpty()) {
            logger.fine("Autocomplete withheld because required index token is missing");
            return Optional.empty();
        }

        String targetArgs = targetArgsOptional.get();

        if (targetArgs.isEmpty()) {
            return suggestFirstPrefix(input, config.prefixes());
        }

        String lastToken = lastToken(targetArgs);
        if (FindCommand.COMMAND_WORD.equals(commandWord)) {
            return suggestFindPrefixCompletion(input, targetArgs, lastToken, config.prefixes());
        }

        if (lastToken.isEmpty()) {
            if (hasInvalidFreeTextArgs(targetArgs, config.prefixes())) {
                return Optional.empty();
            }

            Optional<String> nextUnusedPrefix = nextUnusedPrefix(
                    config.prefixes(), config.repeatablePrefixes(), targetArgs);
            if (nextUnusedPrefix.isPresent()) {
                return Optional.of(input + nextUnusedPrefix.get());
            }

            Optional<String> repeatablePrefix = nextRepeatablePrefix(config.repeatablePrefixes());
            return repeatablePrefix.map(prefix -> input + prefix);
        }

        return completeCurrentToken(input, lastToken,
                config.prefixes(), config.repeatablePrefixes(), targetArgs);
    }

    private static Optional<String> suggestFindPrefixCompletion(
            String input, String args, String lastToken, List<String> prefixes) {
        FindPrefixState findState = buildFindPrefixState(args);

        if (findState.hasIncompleteDateRangePair()) {
            return suggestMissingFindRangePair(input, args, lastToken, findState);
        }

        if (findState.hasCompletedMode()) {
            return Optional.empty();
        }

        if (lastToken.isEmpty()) {
            if (hasInvalidFreeTextArgs(args, prefixes)) {
                return Optional.empty();
            }

            return suggestFirstPrefix(input, prefixes);
        }

        return completeCurrentToken(input, lastToken, prefixes, NO_REPEATABLE_PREFIXES, EMPTY_STRING);
    }

    private static FindPrefixState buildFindPrefixState(String args) {
        return new FindPrefixState(
                containsPrefixToken(args, FIND_NAME_PREFIX),
                containsPrefixToken(args, FIND_TAG_PREFIX),
                containsPrefixToken(args, FIND_DATE_PREFIX),
                containsPrefixToken(args, FIND_START_DATE_PREFIX),
                containsPrefixToken(args, FIND_END_DATE_PREFIX)
        );
    }

    private static Optional<String> suggestMissingFindRangePair(
            String input, String args, String lastToken, FindPrefixState findState) {
        String requiredPairPrefix = findState.hasStartDate() ? FIND_END_DATE_PREFIX : FIND_START_DATE_PREFIX;

        if (lastToken.isEmpty()) {
            return Optional.of(input + requiredPairPrefix);
        }

        if (!containsPrefixToken(args, requiredPairPrefix) && requiredPairPrefix.startsWith(lastToken)) {
            return Optional.of(input + requiredPairPrefix.substring(lastToken.length()));
        }

        return Optional.empty();
    }

    private static Optional<String> extractTargetArgs(String args, boolean requiresIndex) {
        if (!requiresIndex) {
            return Optional.of(args);
        }

        if (!hasIndexToken(args)) {
            return Optional.empty();
        }

        return Optional.of(removeIndexToken(args));
    }

    private static Optional<String> suggestFirstPrefix(String input, List<String> prefixes) {
        if (prefixes.isEmpty()) {
            return Optional.empty();
        }

        String firstPrefix = prefixes.get(0);
        return Optional.of((input.endsWith(SINGLE_SPACE) ? input : input + SINGLE_SPACE) + firstPrefix);
    }

    private static boolean hasInvalidFreeTextArgs(String args, List<String> prefixes) {
        return !args.isBlank() && !containsAnyPrefixToken(args, prefixes);
    }

    private static Optional<String> completeCurrentToken(
            String input, String currentToken, List<String> candidates,
            Set<String> repeatablePrefixes, String existingArgs) {
        Optional<String> firstMatch = candidates.stream()
                .filter(candidate -> candidate.startsWith(currentToken))
                .filter(candidate -> isEligibleCandidate(candidate, repeatablePrefixes, existingArgs))
                .findFirst();

        if (firstMatch.isEmpty()) {
            return Optional.empty();
        }

        String match = firstMatch.get();
        if (match.equals(currentToken)) {
            return Optional.empty();
        }

        return Optional.of(input + match.substring(currentToken.length()));
    }

    private static boolean isEligibleCandidate(
            String candidate, Set<String> repeatablePrefixes, String existingArgs) {
        if (existingArgs.isEmpty()) {
            return true;
        }

        return !containsPrefixToken(existingArgs, candidate)
                || repeatablePrefixes.contains(candidate);
    }

    private static boolean containsAnyPrefixToken(String args, List<String> prefixes) {
        for (String prefix : prefixes) {
            if (containsPrefixToken(args, prefix)) {
                return true;
            }
        }

        return false;
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

    private static List<String> createCommandWords() {
    return List.of(
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
    }

    private static Map<String, AutocompletePrefixConfig> createPrefixConfigs() {
    return Map.of(
        AddCommand.COMMAND_WORD, config(false,
            List.of(PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(),
                PREFIX_ADDRESS.getPrefix(), PREFIX_NOTE.getPrefix(), PREFIX_VISIT.getPrefix(),
                PREFIX_TAG.getPrefix()),
            Set.of(PREFIX_TAG.getPrefix())),
        EditCommand.COMMAND_WORD, config(true,
            List.of(PREFIX_NAME.getPrefix(), PREFIX_PHONE.getPrefix(), PREFIX_EMAIL.getPrefix(),
                PREFIX_ADDRESS.getPrefix(), PREFIX_NOTE.getPrefix(), PREFIX_VISIT.getPrefix(),
                PREFIX_TAG.getPrefix()),
            Set.of(PREFIX_TAG.getPrefix())),
        FindCommand.COMMAND_WORD, config(false, FIND_PREFIXES, NO_REPEATABLE_PREFIXES),
        ListCommand.COMMAND_WORD, config(false,
            List.of(PREFIX_SORT.getPrefix()), NO_REPEATABLE_PREFIXES),
        ArchiveCommand.COMMAND_WORD, config(true, NO_PREFIXES, NO_REPEATABLE_PREFIXES),
        NoteCommand.COMMAND_WORD, config(true,
            List.of(PREFIX_NOTE.getPrefix()), NO_REPEATABLE_PREFIXES),
        TagCommand.COMMAND_WORD, config(true,
            List.of(PREFIX_ADD_TAG.getPrefix(), PREFIX_DELETE_TAG.getPrefix()),
            Set.of(PREFIX_ADD_TAG.getPrefix(), PREFIX_DELETE_TAG.getPrefix())),
        UnarchiveCommand.COMMAND_WORD, config(true, NO_PREFIXES, NO_REPEATABLE_PREFIXES)
    );
    }

    private static AutocompletePrefixConfig config(
        boolean requiresIndex, List<String> prefixes, Set<String> repeatablePrefixes) {
    return new AutocompletePrefixConfig(requiresIndex, prefixes, repeatablePrefixes);
    }
}
