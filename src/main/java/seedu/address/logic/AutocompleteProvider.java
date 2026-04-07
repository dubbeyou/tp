package seedu.address.logic;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.AutocompleteMetadataRegistry;
import seedu.address.logic.parser.AutocompleteMetadataRegistry.AutocompleteCommandMetadata;

/**
 * Provides context-aware command line autocompletion suggestions.
 */
public final class AutocompleteProvider {

    private static final Logger logger = LogsCenter.getLogger(AutocompleteProvider.class);
    private static final String EMPTY_STRING = "";
    private static final String SINGLE_SPACE = " ";

    private static final Set<String> NO_REPEATABLE_PREFIXES = Set.of();

    private AutocompleteProvider() {}

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
        assert input != null : "suggestCommandCompletion input must not be null";

        List<String> commandWords = AutocompleteMetadataRegistry.commandWords();
        boolean isExactCommand = commandWords.stream().anyMatch(command -> command.equals(input));
        if (isExactCommand) {
            return commandWords.stream()
                    .filter(command -> command.startsWith(input))
                    .filter(command -> !command.equals(input))
                    .findFirst();
        }

        return completeCurrentToken(input, input, commandWords, NO_REPEATABLE_PREFIXES, EMPTY_STRING);
    }

    private static Optional<String> suggestArgumentCompletion(String input) {
        assert input != null : "suggestArgumentCompletion input must not be null";

        int firstWhitespaceIndex = firstWhitespaceIndex(input);
        assert firstWhitespaceIndex >= 0 : "suggestArgumentCompletion expects command + whitespace + args";
        String commandWord = input.substring(0, firstWhitespaceIndex);

        Optional<AutocompleteCommandMetadata> configOptional = AutocompleteMetadataRegistry.getMetadata(commandWord);
        if (configOptional.isEmpty()) {
            logger.fine("No autocomplete config for command word: " + commandWord);
            return Optional.empty();
        }

        AutocompleteCommandMetadata config = configOptional.get();

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
        if (config.isFindModeExclusive()) {
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
        assert input != null : "suggestFindPrefixCompletion input must not be null";
        assert args != null : "suggestFindPrefixCompletion args must not be null";
        assert lastToken != null : "suggestFindPrefixCompletion lastToken must not be null";
        assert prefixes != null : "suggestFindPrefixCompletion prefixes must not be null";

        FindPrefixState findState = buildFindPrefixState(args);

        if (hasFindModeConflict(findState)) {
            return Optional.empty();
        }

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

    private static boolean hasFindModeConflict(FindPrefixState findState) {
        assert findState != null : "hasFindModeConflict findState must not be null";

        if (findState.hasDate()) {
            return findState.hasName() || findState.hasTag() || findState.hasStartDate() || findState.hasEndDate();
        }

        if (findState.hasStartDate() || findState.hasEndDate()) {
            return findState.hasName() || findState.hasTag() || findState.hasDate();
        }

        return false;
    }

    private static FindPrefixState buildFindPrefixState(String args) {
        assert args != null : "buildFindPrefixState args must not be null";

        return new FindPrefixState(
                containsPrefixToken(args, AutocompleteMetadataRegistry.findNamePrefix()),
                containsPrefixToken(args, AutocompleteMetadataRegistry.findTagPrefix()),
                containsPrefixToken(args, AutocompleteMetadataRegistry.findDatePrefix()),
                containsPrefixToken(args, AutocompleteMetadataRegistry.findStartDatePrefix()),
                containsPrefixToken(args, AutocompleteMetadataRegistry.findEndDatePrefix())
        );
    }

    private static Optional<String> suggestMissingFindRangePair(
            String input, String args, String lastToken, FindPrefixState findState) {
        assert input != null : "suggestMissingFindRangePair input must not be null";
        assert args != null : "suggestMissingFindRangePair args must not be null";
        assert lastToken != null : "suggestMissingFindRangePair lastToken must not be null";
        assert findState != null : "suggestMissingFindRangePair findState must not be null";

        String requiredPairPrefix = findState.hasStartDate()
            ? AutocompleteMetadataRegistry.findEndDatePrefix()
            : AutocompleteMetadataRegistry.findStartDatePrefix();

        if (lastToken.isEmpty()) {
            return Optional.of(input + requiredPairPrefix);
        }

        if (!containsPrefixToken(args, requiredPairPrefix) && requiredPairPrefix.startsWith(lastToken)) {
            return Optional.of(input + requiredPairPrefix.substring(lastToken.length()));
        }

        return Optional.empty();
    }

    private static Optional<String> extractTargetArgs(String args, boolean requiresIndex) {
        assert args != null : "extractTargetArgs args must not be null";

        if (!requiresIndex) {
            return Optional.of(args);
        }

        if (!hasIndexToken(args)) {
            return Optional.empty();
        }

        return Optional.of(removeIndexToken(args));
    }

    private static Optional<String> suggestFirstPrefix(String input, List<String> prefixes) {
        assert input != null : "suggestFirstPrefix input must not be null";
        assert prefixes != null : "suggestFirstPrefix prefixes must not be null";

        if (prefixes.isEmpty()) {
            return Optional.empty();
        }

        String firstPrefix = prefixes.get(0);
        return Optional.of((input.endsWith(SINGLE_SPACE) ? input : input + SINGLE_SPACE) + firstPrefix);
    }

    private static boolean hasInvalidFreeTextArgs(String args, List<String> prefixes) {
        assert args != null : "hasInvalidFreeTextArgs args must not be null";
        assert prefixes != null : "hasInvalidFreeTextArgs prefixes must not be null";

        if (args.isBlank()) {
            return false;
        }

        if (containsInvalidPrefixToken(args, prefixes)) {
            return true;
        }

        if (!containsAnyPrefixToken(args, prefixes)) {
            return true;
        }

        int firstPrefixIndex = firstPrefixTokenStartIndex(args, prefixes);
        if (firstPrefixIndex < 0) {
            return true;
        }

        return !args.substring(0, firstPrefixIndex).isBlank();
    }

    private static boolean containsInvalidPrefixToken(String args, List<String> prefixes) {
        assert args != null : "containsInvalidPrefixToken args must not be null";
        assert prefixes != null : "containsInvalidPrefixToken prefixes must not be null";

        for (String token : args.split("\\s+")) {
            if (token.isBlank()) {
                continue;
            }

            if (token.contains("/") && prefixes.stream().noneMatch(token::startsWith)) {
                return true;
            }
        }

        return false;
    }

    private static int firstPrefixTokenStartIndex(String args, List<String> prefixes) {
        assert args != null : "firstPrefixTokenStartIndex args must not be null";
        assert prefixes != null : "firstPrefixTokenStartIndex prefixes must not be null";

        int firstIndex = Integer.MAX_VALUE;

        for (String prefix : prefixes) {
            if (args.startsWith(prefix)) {
                firstIndex = 0;
            }

            for (int i = 0; i < args.length() - 1; i++) {
                if (Character.isWhitespace(args.charAt(i)) && args.startsWith(prefix, i + 1)) {
                    int index = i + 1;
                    firstIndex = Math.min(firstIndex, index);
                }
            }
        }

        return firstIndex == Integer.MAX_VALUE ? -1 : firstIndex;
    }

    private static Optional<String> completeCurrentToken(
            String input, String currentToken, List<String> candidates,
            Set<String> repeatablePrefixes, String existingArgs) {
        assert input != null : "completeCurrentToken input must not be null";
        assert currentToken != null : "completeCurrentToken currentToken must not be null";
        assert candidates != null : "completeCurrentToken candidates must not be null";
        assert repeatablePrefixes != null : "completeCurrentToken repeatablePrefixes must not be null";
        assert existingArgs != null : "completeCurrentToken existingArgs must not be null";

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
        assert candidate != null : "isEligibleCandidate candidate must not be null";
        assert repeatablePrefixes != null : "isEligibleCandidate repeatablePrefixes must not be null";
        assert existingArgs != null : "isEligibleCandidate existingArgs must not be null";

        if (existingArgs.isEmpty()) {
            return true;
        }

        return !containsPrefixToken(existingArgs, candidate)
                || repeatablePrefixes.contains(candidate);
    }

    private static boolean containsAnyPrefixToken(String args, List<String> prefixes) {
        assert args != null : "containsAnyPrefixToken args must not be null";
        assert prefixes != null : "containsAnyPrefixToken prefixes must not be null";

        for (String prefix : prefixes) {
            if (containsPrefixToken(args, prefix)) {
                return true;
            }
        }

        return false;
    }

    private static Optional<String> nextUnusedPrefix(
            List<String> orderedPrefixes, Set<String> repeatablePrefixes, String args) {
        assert orderedPrefixes != null : "nextUnusedPrefix orderedPrefixes must not be null";
        assert repeatablePrefixes != null : "nextUnusedPrefix repeatablePrefixes must not be null";
        assert args != null : "nextUnusedPrefix args must not be null";

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
        assert repeatablePrefixes != null : "nextRepeatablePrefix repeatablePrefixes must not be null";

        if (repeatablePrefixes.isEmpty()) {
            return Optional.empty();
        }

        // Deterministic behavior if more repeatable prefixes are added in the future.
        return repeatablePrefixes.stream().sorted().findFirst();
    }

    private static boolean containsPrefixToken(String value, String prefix) {
        assert value != null : "containsPrefixToken value must not be null";
        assert prefix != null : "containsPrefixToken prefix must not be null";

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
        assert args != null : "hasIndexToken args must not be null";
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
        assert value != null : "containsWhitespace value must not be null";
        return firstWhitespaceIndex(value) >= 0;
    }

    private static int firstWhitespaceIndex(String value) {
        assert value != null : "firstWhitespaceIndex value must not be null";

        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    private static String lastToken(String value) {
        assert value != null : "lastToken value must not be null";

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
