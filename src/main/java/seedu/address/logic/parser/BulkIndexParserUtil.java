package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_EMPTY_INPUT;
import static seedu.address.logic.Messages.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_RANGE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_TOKEN;
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

    private BulkIndexParserUtil() {}

    /**
     * Parses a string of indices and ranges into a list of {@code Index}.
     *
     * @param args user input arguments
     * @return list of unique, sorted {@code Index}
     * @throws ParseException if input is invalid
     */
    public static List<Index> parseBulkIndexes(String args) throws ParseException {
        if (args == null || args.trim().isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_INPUT);
        }

        String[] tokens = args.trim().split("\\s+");
        Set<Integer> indexSet = new HashSet<>();

        for (String token : tokens) {
            if (token.matches("-\\d+")) {
                throw new ParseException(MESSAGE_INVALID_INDEX);
            }
            if (token.contains("-")) {
                parseRangeToken(token, indexSet);
            } else {
                parseSingleToken(token, indexSet);
            }
        }

        // Sort indices in ascending order
        List<Integer> sorted = new ArrayList<>(indexSet);
        Collections.sort(sorted);

        // Convert to Index objects
        List<Index> result = new ArrayList<>();
        for (int i : sorted) {
            result.add(Index.fromOneBased(i));
        }

        return Collections.unmodifiableList(result);
    }

    private static void parseRangeToken(String token, Set<Integer> indexSet)
            throws ParseException {

        String[] parts = token.split("-", -1);

        if (parts.length != 2) {
            throw new ParseException(MESSAGE_INVALID_TOKEN);
        }

        int start;
        int end;

        try {
            start = Integer.parseInt(parts[0]);
            end = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            if (!parts[0].matches("\\d+") || !parts[1].matches("\\d+")) {
                throw new ParseException(MESSAGE_INVALID_TOKEN);
            }
            throw new ParseException(MESSAGE_RANGE_TOO_LARGE);
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

        for (int i = start; i <= end; i++) {
            indexSet.add(i);
        }
    }

    private static void parseSingleToken(String token, Set<Integer> indexSet)
            throws ParseException {

        int value;

        try {
            value = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            if (!token.matches("\\d+")) {
                throw new ParseException(MESSAGE_INVALID_TOKEN);
            }
            throw new ParseException(MESSAGE_INDEX_TOO_LARGE);
        }

        if (value <= 0) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        indexSet.add(value);
    }
}
