package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_RANGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_TOKEN;
import static seedu.address.logic.Messages.MESSAGE_RANGE_INDEX_LARGE;
import static seedu.address.logic.Messages.MESSAGE_RANGE_TOO_LARGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Utility class for parsing bulk index inputs (e.g. "1 3 5-7").
 */
public final class BulkIndexParserUtil {

    private static final int MAX_RANGE_SIZE = 100;
    private static final String RANGE_SEPARATOR = "-";

    private BulkIndexParserUtil() {}

    /**
     * Parses a string of indices and ranges into a list of {@code Index}.
     *
     * @param args user input arguments containing indices and/or ranges
     * @param usageMessage the usage message used for format-related errors
     * @return an unmodifiable list of unique {@code Index} objects sorted in ascending order
     * @throws ParseException if the input is empty or contains invalid tokens (format errors),
     *                        or contains invalid indices or ranges
     */
    public static List<Index> parseBulkIndexes(String args, String usageMessage) throws ParseException {
        String[] tokens = tokenizeOrThrow(args, usageMessage);
        Set<Integer> indexSet = collectOneBasedIndexes(tokens);
        return toSortedUnmodifiableIndexList(indexSet);
    }

    /**
     * Tokenizes the input string into individual index or range tokens.
     *
     * @param args raw user input
     * @return array of tokens split by whitespace
     * @throws ParseException if the input is null or empty
     */
    private static String[] tokenizeOrThrow(String args, String usageMessage) throws ParseException {
        if (args == null || args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usageMessage));
        }
        return args.trim().split("\\s+");
    }

    /**
     * Parses all tokens and collects valid one-based indices into a set.
     *
     * @param tokens array of input tokens representing indices or ranges
     * @return a set of unique one-based indices
     * @throws ParseException if any token is invalid
     */
    private static Set<Integer> collectOneBasedIndexes(String[] tokens) throws ParseException {
        Set<Integer> indexSet = new HashSet<>();

        for (String token : tokens) {
            parseTokenIntoSet(token, indexSet);
        }

        return indexSet;
    }

    /**
     * Parses a single token and adds the resulting indices into the given set.
     * The token may represent either a single index or a range.
     *
     * @param token the input token to parse
     * @param indexSet the set to store parsed indices
     * @throws ParseException if the token is invalid
     */
    private static void parseTokenIntoSet(String token, Set<Integer> indexSet)
            throws ParseException {

        if (token.matches("-\\d+")) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        if (token.contains(RANGE_SEPARATOR)) {
            parseRangeToken(token, indexSet);
        } else {
            parseSingleToken(token, indexSet);
        }
    }

    /**
     * Converts a set of one-based integers into a sorted, unmodifiable list of {@code Index}.
     *
     * @param indexSet set of one-based indices
     * @return sorted unmodifiable list of {@code Index}
     */
    private static List<Index> toSortedUnmodifiableIndexList(Set<Integer> indexSet) {
        List<Integer> sorted = new ArrayList<>(indexSet);
        Collections.sort(sorted);

        List<Index> result = new ArrayList<>();
        for (int i : sorted) {
            result.add(Index.fromOneBased(i));
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * Parses a range token (e.g. "3-5") and adds all indices within the range to the set.
     *
     * @param token the range token to parse
     * @param indexSet the set to store parsed indices
     * @throws ParseException if the range format is invalid, contains non-numeric values,
     *                        or violates index constraints
     */
    private static void parseRangeToken(String token, Set<Integer> indexSet)
            throws ParseException {

        String[] parts = token.split(RANGE_SEPARATOR, -1);

        if (parts.length != 2) {
            throw new ParseException(MESSAGE_INVALID_TOKEN);
        }

        // Validate format first (no signs, only digits)
        if (!parts[0].matches("\\d+") || !parts[1].matches("\\d+")) {
            throw new ParseException(MESSAGE_INVALID_TOKEN);
        }

        int start;
        int end;

        try {
            start = Integer.parseInt(parts[0]);
            end = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            // Only possible case now = overflow
            throw new ParseException(MESSAGE_RANGE_INDEX_LARGE);
        }

        if (start <= 0 || end <= 0) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        if (start > end) {
            throw new ParseException(MESSAGE_INVALID_RANGE);
        }

        if (end - start + 1 > MAX_RANGE_SIZE) {
            throw new ParseException(MESSAGE_RANGE_TOO_LARGE);
        }

        for (long i = start; i <= end; i++) {
            indexSet.add((int) i);
        }
    }

    /**
     * Parses a single index token and adds it to the set.
     *
     * @param token the input token representing a single index
     * @param indexSet the set to store parsed indices
     * @throws ParseException if the token is not a valid positive integer
     */
    private static void parseSingleToken(String token, Set<Integer> indexSet)
            throws ParseException {

        // Validate format first (digit only)
        if (!token.matches("\\d+")) {
            throw new ParseException(MESSAGE_INVALID_TOKEN);
        }

        int value;

        try {
            value = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            // Only possible case now = overflow
            throw new ParseException(MESSAGE_INDEX_TOO_LARGE);
        }

        if (value <= 0) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        indexSet.add(value);
    }
}
