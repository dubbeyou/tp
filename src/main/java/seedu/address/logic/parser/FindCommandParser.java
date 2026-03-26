package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.time.LocalDate;
import java.util.Arrays;

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

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG,
                        PREFIX_DATE, PREFIX_START_DATE, PREFIX_END_DATE);

        // reject having text before the prefix (Preamble)
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // reject duplicate prefixes (eg. n/Alice n/Bob)
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_TAG,
                PREFIX_DATE, PREFIX_START_DATE, PREFIX_END_DATE);

        boolean hasName = argMultimap.getValue(PREFIX_NAME).isPresent();
        boolean hasTag = argMultimap.getValue(PREFIX_TAG).isPresent();
        boolean hasDate = argMultimap.getValue(PREFIX_DATE).isPresent();
        boolean hasStartDate = argMultimap.getValue(PREFIX_START_DATE).isPresent();
        boolean hasEndDate = argMultimap.getValue(PREFIX_END_DATE).isPresent();

        // sd/ and ed/ must appear as a pair
        if (hasStartDate ^ hasEndDate) {
            throw new ParseException(MESSAGE_MISSING_DATE_RANGE_PAIR);
        }

        boolean hasRange = hasStartDate && hasEndDate;

        // only allow exactly one search mode
        int modeCount = 0;
        if (hasName) { modeCount++; }
        if (hasTag) { modeCount++; }
        if (hasDate) { modeCount++; }
        if (hasRange) { modeCount++; }

        // reject for multiple modes are used or no valid mode
        if (modeCount > 1) {
            throw new ParseException(MESSAGE_ONLY_ONE_SEARCH_TYPE);
        }

        if (modeCount == 0) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // branch logic to return the relevant find command
        if (hasName) {
            String keyword = argMultimap.getValue(PREFIX_NAME).get();
            return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(keyword.split("\\s+"))));

        } else if (hasTag) {
            String tag = argMultimap.getValue(PREFIX_TAG).get();
            return new FindCommand(new TagContainsPredicate(tag));

        } else if (hasDate) {
            String dateValue = argMultimap.getValue(PREFIX_DATE).get().trim();
            LocalDate targetDate = dateValue.equalsIgnoreCase(KEYWORD_TODAY)
                    ? LocalDate.now()
                    : ParserUtil.parseDate(dateValue);
            return new FindCommand(new VisitContainsDatePredicate(targetDate, targetDate));

        } else {
            LocalDate startDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_START_DATE).get());
            LocalDate endDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_END_DATE).get());

            if (startDate.isAfter(endDate)) {
                throw new ParseException(MESSAGE_INVALID_DATE_RANGE);
            }
            return new FindCommand(new VisitContainsDatePredicate(startDate, endDate));
        }
    }

}
