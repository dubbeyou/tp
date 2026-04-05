package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;


public class TagCommandParserTest {

    private TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, " at/highRisk",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        // no tags specified, only index
        assertParseFailure(parser, "1", TagCommand.MESSAGE_NOT_EDITED);

        // no index and no tags
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5 at/highRisk",
                String.format(MESSAGE_INVALID_INDEX));

        // zero index
        assertParseFailure(parser, "0 at/highRisk",
                String.format(MESSAGE_INVALID_INDEX));

        // invalid arguments
        assertParseFailure(parser, "some random string at/highRisk",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = " " + targetIndex.getOneBased() + " at/highRisk dt/friend";

        Set<Tag> addTags = new HashSet<>();
        addTags.add(new Tag("highRisk"));

        Set<Tag> deleteTags = new HashSet<>();
        deleteTags.add(new Tag("friend"));

        TagCommand expectedCommand = new TagCommand(targetIndex, addTags, deleteTags);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleTags_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        // test multiple 'at/' and 'dt/' prefixes
        String userInput = " " + targetIndex.getOneBased() + " at/diabetes at/highRisk dt/friend dt/colleague";

        Set<Tag> addTags = new HashSet<>();
        addTags.add(new Tag("diabetes"));
        addTags.add(new Tag("highRisk"));

        Set<Tag> deleteTags = new HashSet<>();
        deleteTags.add(new Tag("friend"));
        deleteTags.add(new Tag("colleague"));

        TagCommand expectedCommand = new TagCommand(targetIndex, addTags, deleteTags);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_onlyAddTags_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = " " + targetIndex.getOneBased() + " at/urgent";

        Set<Tag> addTags = new HashSet<>();
        addTags.add(new Tag("urgent"));

        TagCommand expectedCommand = new TagCommand(targetIndex, addTags, new HashSet<>());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_onlyDeleteTags_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = " " + targetIndex.getOneBased() + " dt/social";

        Set<Tag> deleteTags = new HashSet<>();
        deleteTags.add(new Tag("social"));

        TagCommand expectedCommand = new TagCommand(targetIndex, new HashSet<>(), deleteTags);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
