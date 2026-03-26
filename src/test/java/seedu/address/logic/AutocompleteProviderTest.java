package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AutocompleteProviderTest {

    @Test
    public void suggestCompletion_partialCommand_returnsCompletedCommand() {
        assertEquals("add", AutocompleteProvider.suggestCompletion("a").orElseThrow());
    }

    @Test
    public void suggestCompletion_exactCommandWithExpectedPrefix_returnsPrefixSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("add").isEmpty());
    }

    @Test
    public void suggestCompletion_commandWithSpace_returnsPrefixSuggestion() {
        assertEquals("add n/", AutocompleteProvider.suggestCompletion("add ").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialPrefix_returnsCompletedPrefix() {
        assertEquals("add n/", AutocompleteProvider.suggestCompletion("add n").orElseThrow());
    }

    @Test
    public void suggestCompletion_addAfterNamePrefixSuggestsNextPrefix() {
        assertEquals("add n/Amy p/", AutocompleteProvider.suggestCompletion("add n/Amy ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addAfterAllPrefixesSuggestsRepeatableTag() {
        String input = "add n/Amy p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client ";
        assertEquals(input + "t/", AutocompleteProvider.suggestCompletion(input).orElseThrow());
    }

    @Test
    public void suggestCompletion_completedPrefix_returnsNoSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("add n/").isEmpty());
    }

    @Test
    public void suggestCompletion_unknownInput_returnsNoSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("zz").isEmpty());
    }

    @Test
    public void suggestCompletion_nullInput_returnsNoSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion(null).isEmpty());
    }

    @Test
    public void suggestCompletion_whitespaceOnlyInput_returnsNoSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("   ").isEmpty());
    }

    @Test
    public void suggestCompletion_findCommandPrefixSuggestions_work() {
        assertEquals("find n/", AutocompleteProvider.suggestCompletion("find ").orElseThrow());
        assertEquals("find t/", AutocompleteProvider.suggestCompletion("find t").orElseThrow());
    }

    @Test
    public void suggestCompletion_listCommandPrefixSuggestions_work() {
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list ").orElseThrow());
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list s").orElseThrow());
        assertTrue(AutocompleteProvider.suggestCompletion("list s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_edit_requiresIndexBeforePrefixSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("edit ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1").isPresent());
        assertEquals("edit 1 n/", AutocompleteProvider.suggestCompletion("edit 1").orElseThrow());
        assertEquals("edit 1 p/", AutocompleteProvider.suggestCompletion("edit 1 p").orElseThrow());
        assertEquals("edit 1 n/Bob p/", AutocompleteProvider.suggestCompletion("edit 1 n/Bob ").orElseThrow());

        String allPrefixesInput =
                "edit 1 n/Bob p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client ";
        assertEquals(allPrefixesInput + "t/", AutocompleteProvider.suggestCompletion(allPrefixesInput).orElseThrow());
    }

    @Test
    public void suggestCompletion_note_requiresIndexBeforePrefixSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("note ").isEmpty());
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1").orElseThrow());
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 nt").orElseThrow());
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 nt/hello ").isEmpty());
    }

    @Test
    public void suggestCompletion_tag_requiresIndexBeforePrefixSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("tag ").isEmpty());
        assertEquals("tag 1 at/", AutocompleteProvider.suggestCompletion("tag 1").orElseThrow());
        assertEquals("tag 1 dt/", AutocompleteProvider.suggestCompletion("tag 1 d").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespace_stillSuggestsCommand() {
        assertEquals(" add", AutocompleteProvider.suggestCompletion(" a").orElseThrow());
        assertEquals("          add", AutocompleteProvider.suggestCompletion("          a").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespaceBeforeCommand_returnsPrefixSuggestion() {
        assertEquals(" add n/", AutocompleteProvider.suggestCompletion(" add ").orElseThrow());
        assertEquals("          add n/", AutocompleteProvider.suggestCompletion("          add ").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespaceBeforePrefix_returnsCompletedPrefix() {
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   n").orElseThrow());
        assertEquals("          add         n/",
                AutocompleteProvider.suggestCompletion("          add         n").orElseThrow());
    }

    @Test
    public void suggestCompletion_clearCommand_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("clear").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("clear ").isEmpty());
    }

    @Test
    public void suggestCompletion_exitCommand_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("exit").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("exit ").isEmpty());
    }

    @Test
    public void suggestCompletion_helpCommand_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("help").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("help ").isEmpty());
    }

    @Test
    public void suggestCompletion_deleteCommand_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("delete ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("delete abc").isEmpty());
    }

    @Test
    public void suggestCompletion_addWithTabWhitespace_suggestsPrefix() {
        assertEquals("add\t n/", AutocompleteProvider.suggestCompletion("add\t").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithMultipleSpaces_suggestsPrefix() {
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addNameWithSpaceNoPrefix_suggestsNextPrefix() {
        assertEquals("add n/John  p/", AutocompleteProvider.suggestCompletion("add n/John  ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithAllPrefixesNoTrailingSpace_noSuggestion() {
        String input = "add n/Amy p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client";
        assertTrue(AutocompleteProvider.suggestCompletion(input).isEmpty());
    }

    @Test
    public void suggestCompletion_editWithValidIndex_suggestsFirstPrefix() {
        assertEquals("edit 1 n/", AutocompleteProvider.suggestCompletion("edit 1").orElseThrow());
    }

    @Test
    public void suggestCompletion_editWithIndexAndSpace_suggestsFirstPrefix() {
        assertEquals("edit 1 n/", AutocompleteProvider.suggestCompletion("edit 1 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_editAfterPartialPhonePrefix_suggestsPhonePrefix() {
        assertEquals("edit 1 p/", AutocompleteProvider.suggestCompletion("edit 1 p").orElseThrow());
    }

    @Test
    public void suggestCompletion_editWithNameAndNoSpace_suggestsSpace() {
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 n/Bob").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithPartialNamePrefix_suggestsNamePrefix() {
        assertEquals("find n/", AutocompleteProvider.suggestCompletion("find n").orElseThrow());
    }

    @Test
    public void suggestCompletion_findWithNameThenTag_suggestsTagPrefix() {
        assertEquals("find n/John t/", AutocompleteProvider.suggestCompletion("find n/John ").orElseThrow());
    }

    @Test
    public void suggestCompletion_findWithNameAndTagComplete_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John t/client ").isEmpty());
    }

    @Test
    public void suggestCompletion_listCommand_suggestsSortPrefix() {
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list ").orElseThrow());
    }

    @Test
    public void suggestCompletion_listWithSortValue_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("list s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteWithoutIndex_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("note ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteWithIndexAndSpace_suggestNotePrefix() {
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_noteWithIndexPartialPrefix_suggestNotePrefix() {
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 n").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithoutIndex_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("tag ").isEmpty());
    }

    @Test
    public void suggestCompletion_tagWithIndexAndSpace_suggestAddTagPrefix() {
        assertEquals("tag 1 at/", AutocompleteProvider.suggestCompletion("tag 1 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithPartialDeletePrefix_suggestDeleteTagPrefix() {
        assertEquals("tag 1 dt/", AutocompleteProvider.suggestCompletion("tag 1 dt").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithBothPrefixes_noSuggestion() {
        assertEquals("tag 1 at/client dt/service at/",
                AutocompleteProvider.suggestCompletion("tag 1 at/client dt/service ").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithBothPrefixesAndPartialRepeatablePrefix_suggestsMatchingRepeatablePrefix() {
        assertEquals("tag 1 at/client dt/service dt/",
                AutocompleteProvider.suggestCompletion("tag 1 at/client dt/service d").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialCommandTwoLetters_returnsCompletion() {
        assertEquals("add", AutocompleteProvider.suggestCompletion("ad").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialCommandSimilarStart_returnsFirstMatch() {
        assertEquals("delete", AutocompleteProvider.suggestCompletion("del").orElseThrow());
    }

    @Test
    public void suggestCompletion_invalidCommandWord_returnsEmpty() {
        assertTrue(AutocompleteProvider.suggestCompletion("unknown ").isEmpty());
    }

    @Test
    public void suggestCompletion_unknownCommandWithArgs_returnsEmpty() {
        assertTrue(AutocompleteProvider.suggestCompletion("unknown something").isEmpty());
    }

    @Test
    public void suggestCompletion_repeatablePrefixFullyTyped_returnsNoSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("add t/").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithZeroIndex_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("edit 0").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithNegativeIndex_noSuggestion() {
        assertTrue(AutocompleteProvider.suggestCompletion("edit -1").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithLongIndex_suggestsPrefix() {
        assertEquals("edit 999 n/", AutocompleteProvider.suggestCompletion("edit 999").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithOnlyPhoneNoName_suggestsPhonePrefix() {
        assertEquals("add p/", AutocompleteProvider.suggestCompletion("add p").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithEmailAfterPhone_suggestsEmailPrefix() {
        assertEquals("add p/12345 n/", AutocompleteProvider.suggestCompletion("add p/12345 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithRepeatedTagPrefix_suggestsTagAgain() {
        assertEquals("add t/client n/", AutocompleteProvider.suggestCompletion("add t/client ").orElseThrow());
    }
}
