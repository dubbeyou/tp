package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.TagContainsPredicate;
import seedu.address.model.person.VisitContainsDatePredicate;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    public static final String MESSAGE_ONLY_ONE_SEARCH_TYPE = "Only one search type allowed.";
    public static final String KEYWORD_TODAY = "today";
    public static final String MESSAGE_INVALID_DATE_RANGE = "Start date cannot be after end date!";
    public static final String MESSAGE_MISSING_DATE_RANGE_PAIR = "Both sd/ and ed/ must be provided together.";

    private enum SearchMode {
        NAME, TAG, DATE, DATE_RANGE
    }

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenizeFindArgs(args);
        validateStructure(argMultimap);

        SearchMode mode = resolveSearchMode(argMultimap);

        return switch (mode) {
        case NAME -> parseNameFind(argMultimap);
        case TAG -> parseTagFind(argMultimap);
        case DATE -> parseSingleDateFind(argMultimap);
        case DATE_RANGE -> parseDateRangeFind(argMultimap);
        };
    }

    private ArgumentMultimap tokenizeFindArgs(String args) {
        return ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_DATE,
                PREFIX_START_DATE, PREFIX_END_DATE);
    }

    private void validateStructure(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.getPreamble().isEmpty()) {
            throw invalidFindFormat();
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_TAG, PREFIX_DATE,
                PREFIX_START_DATE, PREFIX_END_DATE);
    }

    private SearchMode resolveSearchMode(ArgumentMultimap argMultimap) throws ParseException {
        boolean hasName = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasTag = argMultimap.getValue(PREFIX_TAG).isPresent();
        boolean hasDate = argMultimap.getValue(PREFIX_DATE).isPresent();
        boolean hasStartDate = argMultimap.getValue(PREFIX_START_DATE).isPresent();
        boolean hasEndDate = argMultimap.getValue(PREFIX_END_DATE).isPresent();

        if (hasStartDate ^ hasEndDate) {
            throw new ParseException(MESSAGE_MISSING_DATE_RANGE_PAIR);
        }

        boolean hasRange = hasStartDate && hasEndDate;

        int modeCount = (hasName ? 1 : 0) + (hasTag ? 1 : 0) + (hasDate ? 1 : 0) + (hasRange ? 1 : 0);

        if (modeCount == 0) {
            throw invalidFindFormat();
        }
        if (modeCount > 1) {
            throw new ParseException(MESSAGE_ONLY_ONE_SEARCH_TYPE);
        }

        if (hasName) {
            return SearchMode.NAME;
        }
        if (hasTag) {
            return SearchMode.TAG;
        }
        if (hasDate) {
            return SearchMode.DATE;
        }
        return SearchMode.DATE_RANGE;
    }

    private FindCommand parseNameFind(ArgumentMultimap argMultimap) throws ParseException {
        String rawName = getRequiredValue(argMultimap, PREFIX_NAME).trim();
        if (rawName.isEmpty()) {
            throw invalidFindFormat();
        }

        List<String> keywords = Arrays.stream(rawName.split("\\s+")).toList();
        return new FindCommand(new NameContainsKeywordsPredicate(keywords));
    }

    private FindCommand parseTagFind(ArgumentMultimap argMultimap) throws ParseException {
        String tag = getRequiredValue(argMultimap, PREFIX_TAG).trim();
        if (tag.isEmpty()) {
            throw invalidFindFormat();
        }

        return new FindCommand(new TagContainsPredicate(tag));
    }

    private FindCommand parseSingleDateFind(ArgumentMultimap argMultimap) throws ParseException {
        String dateValue = getRequiredValue(argMultimap, PREFIX_DATE).trim();
        if (dateValue.isEmpty()) {
            throw invalidFindFormat();
        }

        LocalDate targetDate = dateValue.equalsIgnoreCase(KEYWORD_TODAY)
                ? LocalDate.now()
                : ParserUtil.parseDate(dateValue);

        return new FindCommand(new VisitContainsDatePredicate(targetDate, targetDate));
    }

    private FindCommand parseDateRangeFind(ArgumentMultimap argMultimap) throws ParseException {
        LocalDate startDate = ParserUtil.parseDate(getRequiredValue(argMultimap, PREFIX_START_DATE).trim());
        LocalDate endDate = ParserUtil.parseDate(getRequiredValue(argMultimap, PREFIX_END_DATE).trim());

        if (startDate.isAfter(endDate)) {
            throw new ParseException(MESSAGE_INVALID_DATE_RANGE);
        }

        return new FindCommand(new VisitContainsDatePredicate(startDate, endDate));
    }

    private String getRequiredValue(ArgumentMultimap argMultimap, Prefix prefix) throws ParseException {
        return argMultimap.getValue(prefix).orElseThrow(this::invalidFindFormat);
    }

    private ParseException invalidFindFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

}
