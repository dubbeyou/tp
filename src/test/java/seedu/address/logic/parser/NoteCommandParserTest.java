package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.model.person.Note;

public class NoteCommandParserTest {
    private NoteCommandParser parser = new NoteCommandParser();
    private final String nonEmptyNote = "Note.";

    @Test
    public void parse_indexSpecified_success() {
        // have note
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_NOTE + nonEmptyNote;
        NoteCommand expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(nonEmptyNote));
        assertParseSuccess(parser, userInput, expectedCommand);

        // no note
        userInput = targetIndex.getOneBased() + " " + PREFIX_NOTE;
        expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(""));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE);
        Index targetIndex = INDEX_FIRST_PERSON;

        // no parameters
        assertParseFailure(parser, NoteCommand.COMMAND_WORD, expectedMessage);

        // no index
        assertParseFailure(parser, NoteCommand.COMMAND_WORD + " " + PREFIX_NOTE + nonEmptyNote, expectedMessage);

        // no note prefix
        assertParseFailure(parser, String.valueOf(targetIndex.getOneBased()), expectedMessage);
    }

    @Test
    public void parse_duplicateNotePrefix_failure() {
        Index targetIndex = INDEX_FIRST_PERSON;

        assertParseFailure(parser,
                targetIndex.getOneBased() + " " + PREFIX_NOTE + "first " + PREFIX_NOTE + "second",
                getErrorMessageForDuplicatePrefixes(PREFIX_NOTE));

        assertParseFailure(parser,
                targetIndex.getOneBased() + " " + PREFIX_NOTE + " " + PREFIX_NOTE,
                getErrorMessageForDuplicatePrefixes(PREFIX_NOTE));
    }
}
