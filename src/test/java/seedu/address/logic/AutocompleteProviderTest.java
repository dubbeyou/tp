package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AutocompleteProviderTest {

    // EP Group: Input Validity and Unknown Commands

    @Test
    public void suggestCompletion_nullInput_returnsNoSuggestion() {
        // EP: null input
        assertTrue(AutocompleteProvider.suggestCompletion(null).isEmpty());
    }

    @Test
    public void suggestCompletion_whitespaceOnlyInput_returnsNoSuggestion() {
        // EP: empty string (all whitespace)
        assertTrue(AutocompleteProvider.suggestCompletion("   ").isEmpty());
    }

    @Test
    public void suggestCompletion_unknownInput_returnsNoSuggestion() {
        // EP: unknown command fragment
        assertTrue(AutocompleteProvider.suggestCompletion("zz").isEmpty());
    }

    @Test
    public void suggestCompletion_invalidCommandWord_returnsEmpty() {
        // EP: unknown command with whitespace
        assertTrue(AutocompleteProvider.suggestCompletion("unknown ").isEmpty());
    }

    @Test
    public void suggestCompletion_unknownCommandWithArgs_returnsEmpty() {
        // EP: unknown command with trailing arguments
        assertTrue(AutocompleteProvider.suggestCompletion("unknown something").isEmpty());
    }

    // EP Group: Command Word Completion

    @Test
    public void suggestCompletion_partialCommand_returnsCompletedCommand() {
        // EP: valid single-character command prefix
        assertEquals("add", AutocompleteProvider.suggestCompletion("a").orElseThrow());
        // Boundary Value: exact command should cycle to next longer command with same prefix
        assertEquals("list-archive", AutocompleteProvider.suggestCompletion("list").orElseThrow());
    }

    @Test
    public void suggestCompletion_exactCommandWithExpectedPrefix_returnsPrefixSuggestion() {
        // EP: exact command without trailing whitespace should not suggest arguments
        assertTrue(AutocompleteProvider.suggestCompletion("add").isEmpty());
    }

    @Test
    public void suggestCompletion_partialCommandTwoLetters_returnsCompletion() {
        // EP: valid multi-character command prefix
        assertEquals("add", AutocompleteProvider.suggestCompletion("ad").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialCommandSimilarStart_returnsFirstMatch() {
        // EP: unambiguous command prefix among similar commands
        assertEquals("delete", AutocompleteProvider.suggestCompletion("del").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialArchiveCommands_returnsCompletion() {
        // EP: partial command prefixes for newly added command words
        assertEquals("archive", AutocompleteProvider.suggestCompletion("ar").orElseThrow());
        assertEquals("unarchive", AutocompleteProvider.suggestCompletion("una").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespace_stillSuggestsCommand() {
        // EP: leading whitespace should be preserved while completing command words
        assertEquals(" add", AutocompleteProvider.suggestCompletion(" a").orElseThrow());
        assertEquals("          add", AutocompleteProvider.suggestCompletion("          a").orElseThrow());
    }

    // EP Group: Whitespace and Prefix Token Completion

    @Test
    public void suggestCompletion_commandWithSpace_returnsPrefixSuggestion() {
        // EP: exact command with trailing space suggests first argument prefix
        assertEquals("add n/", AutocompleteProvider.suggestCompletion("add ").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialPrefix_returnsCompletedPrefix() {
        // EP: partial prefix token should autocomplete to full prefix
        assertEquals("add n/", AutocompleteProvider.suggestCompletion("add n").orElseThrow());
    }

    @Test
    public void suggestCompletion_addAfterNamePrefixSuggestsNextPrefix() {
        // EP: after one non-repeatable prefix, suggest next unused prefix
        assertEquals("add n/Amy p/", AutocompleteProvider.suggestCompletion("add n/Amy ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addAfterAllPrefixesSuggestsRepeatableTag() {
        // EP: all non-repeatable prefixes used, only repeatable prefix remains
        String input = "add n/Amy p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client ";
        assertEquals(input + "t/", AutocompleteProvider.suggestCompletion(input).orElseThrow());
    }

    @Test
    public void suggestCompletion_completedPrefix_returnsNoSuggestion() {
        // EP: completed prefix token should not be suggested again immediately
        assertTrue(AutocompleteProvider.suggestCompletion("add n/").isEmpty());
    }

    @Test
    public void suggestCompletion_addWithTabWhitespace_suggestsPrefix() {
        // EP: tab is treated as whitespace separator
        assertEquals("add\t n/", AutocompleteProvider.suggestCompletion("add\t").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithMultipleSpaces_suggestsPrefix() {
        // EP: multiple spaces should still trigger first-prefix suggestion
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addNameWithSpaceNoPrefix_suggestsNextPrefix() {
        // EP: completed name value with trailing spaces should suggest next prefix
        assertEquals("add n/John  p/", AutocompleteProvider.suggestCompletion("add n/John  ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithAllPrefixesNoTrailingSpace_noSuggestion() {
        // EP: no trailing whitespace means no next-prefix trigger
        String input = "add n/Amy p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client";
        assertTrue(AutocompleteProvider.suggestCompletion(input).isEmpty());
    }

    @Test
    public void suggestCompletion_addWithOnlyPhoneNoName_suggestsPhonePrefix() {
        // EP: add command allows prefixes in entered order
        assertEquals("add p/", AutocompleteProvider.suggestCompletion("add p").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithEmailAfterPhone_suggestsEmailPrefix() {
        // EP: next unused non-repeatable prefix suggestion
        assertEquals("add p/12345 n/", AutocompleteProvider.suggestCompletion("add p/12345 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithRepeatedTagPrefix_suggestsTagAgain() {
        // EP: repeatable tag prefix can continue to be suggested
        assertEquals("add t/client n/", AutocompleteProvider.suggestCompletion("add t/client ").orElseThrow());
    }

    @Test
    public void suggestCompletion_repeatablePrefixFullyTyped_returnsNoSuggestion() {
        // EP: fully typed prefix token should not duplicate immediately
        assertTrue(AutocompleteProvider.suggestCompletion("add t/").isEmpty());
    }

    @Test
    public void suggestCompletion_leadingWhitespaceBeforeCommand_returnsPrefixSuggestion() {
        // EP: leading whitespace plus command+space still suggests first prefix
        assertEquals(" add n/", AutocompleteProvider.suggestCompletion(" add ").orElseThrow());
        assertEquals("          add n/", AutocompleteProvider.suggestCompletion("          add ").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespaceBeforePrefix_returnsCompletedPrefix() {
        // EP: extra internal spacing does not block prefix completion
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   n").orElseThrow());
        assertEquals("          add         n/",
                AutocompleteProvider.suggestCompletion("          add         n").orElseThrow());
    }

    @Test
    public void suggestCompletion_invalidFreeTextBeforePrefixes_returnsNoSuggestion() {
        // EP: invalid preamble text for prefix-based command
        assertFalse(AutocompleteProvider.suggestCompletion("add abc ").isPresent());
        assertFalse(AutocompleteProvider.suggestCompletion("edit 1 abc ").isPresent());
        assertFalse(AutocompleteProvider.suggestCompletion("find abc ").isPresent());
    }

    @Test
    public void suggestCompletion_addWithInvalidTokenBeforeValidPrefix_returnsNoSuggestion() {
        // EP: invalid non-prefix token appearing before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("add f p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add invalid p/123 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add xyz n/John ").isEmpty());
    }

    @Test
    public void suggestCompletion_addWithMultipleInvalidTokensBeforePrefixes_returnsNoSuggestion() {
        // EP: multiple invalid tokens before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("add f invalid g p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add x y z n/John ").isEmpty());
    }

    @Test
    public void suggestCompletion_addWithOnlyWhitespaceBeforePrefixes_suggestsPrefix() {
        // EP: only whitespace (no invalid tokens) before first prefix
        assertEquals("add  p/", AutocompleteProvider.suggestCompletion("add  p").orElseThrow());
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   n").orElseThrow());
    }

    @Test
    public void suggestCompletion_addWithPrefixAtStart_suggestsNextPrefix() {
        // Boundary Value: prefix token at very start of args (position 0)
        assertEquals("add p/", AutocompleteProvider.suggestCompletion("add p").orElseThrow());
        assertEquals("add n/Amy p/", AutocompleteProvider.suggestCompletion("add n/Amy ").orElseThrow());
    }

    // EP Group: Find Command Modes

    @Test
    public void suggestCompletion_findCommandPrefixSuggestions_work() {
        // EP: valid find prefix mode (name)
        assertEquals("find n/", AutocompleteProvider.suggestCompletion("find ").orElseThrow());
        assertEquals("find t/", AutocompleteProvider.suggestCompletion("find t").orElseThrow());
        assertEquals("find sd/", AutocompleteProvider.suggestCompletion("find s").orElseThrow());
        assertEquals("find ed/", AutocompleteProvider.suggestCompletion("find e").orElseThrow());
    }

    @Test
    public void suggestCompletion_findAllowsOnlyOneModeExceptDateRangePair() {
        // EP: one non-range mode already chosen -> stop suggesting
        assertTrue(AutocompleteProvider.suggestCompletion("find n/Amy ").isEmpty());

        // EP: only name mode chosen (hasName=true, others false, so hasCompletedMode=true)
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John ").isEmpty());

        // EP: only tag mode chosen (hasTag=true, others false, so hasCompletedMode=true)
        assertTrue(AutocompleteProvider.suggestCompletion("find t/client ").isEmpty());

        // EP: only date mode chosen (hasDate=true, others false, so hasCompletedMode=true)
        assertTrue(AutocompleteProvider.suggestCompletion("find d/2026-01-01 ").isEmpty());

        // EP: range mode uses required pair -> suggest missing counterpart
        assertEquals("find sd/2026-01-01 ed/",
                AutocompleteProvider.suggestCompletion("find sd/2026-01-01 ").orElseThrow());
        assertEquals("find ed/2026-12-31 sd/",
                AutocompleteProvider.suggestCompletion("find ed/2026-12-31 ").orElseThrow());

        // EP: range pair completed -> stop suggesting
        assertTrue(AutocompleteProvider.suggestCompletion("find sd/2026-01-01 ed/2026-12-31 ").isEmpty());
    }

    @Test
    public void suggestCompletion_findRangePrefixWithoutValue_suggestsMissingPair() {
        // EP: sd/ without value should still suggest missing ed/ on trailing whitespace
        assertEquals("find sd/ ed/", AutocompleteProvider.suggestCompletion("find sd/ ").orElseThrow());

        // EP: ed/ without value should still suggest missing sd/ on trailing whitespace
        assertEquals("find ed/ sd/", AutocompleteProvider.suggestCompletion("find ed/ ").orElseThrow());

        // EP: partial typing of missing pair prefix should autocomplete
        assertEquals("find sd/ ed/", AutocompleteProvider.suggestCompletion("find sd/ e").orElseThrow());
        assertEquals("find ed/ sd/", AutocompleteProvider.suggestCompletion("find ed/ s").orElseThrow());
    }

    @Test
    public void suggestCompletion_findRangePrefixPartialAutocompleteMissingPair_suggestsCompletion() {
        // EP: partial typing of range pair with non-blank last token should autocomplete to complete pair
        assertEquals("find sd/2026-01-01 ed/",
                AutocompleteProvider.suggestCompletion("find sd/2026-01-01 e").orElseThrow());
        assertEquals("find ed/2026-12-31 sd/",
                AutocompleteProvider.suggestCompletion("find ed/2026-12-31 s").orElseThrow());
    }

    @Test
    public void suggestCompletion_findRangePartialTypingNonMatchingToken_noSuggestion() {
        // EP: partial typing that doesn't match any expected prefix in range context
        assertTrue(AutocompleteProvider.suggestCompletion("find sd/2026-01-01 x").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find ed/2026-12-31 p").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithPartialNamePrefix_suggestsNamePrefix() {
        // EP: partial find name prefix
        assertEquals("find n/", AutocompleteProvider.suggestCompletion("find n").orElseThrow());
    }

    @Test
    public void suggestCompletion_findWithNameThenTag_suggestsTagPrefix() {
        // EP: find only allows one non-range mode
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithNameAndTagComplete_noSuggestion() {
        // EP: multiple find modes are invalid; no autocomplete should proceed
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John t/client ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithMixedDateRangeAndName_noSuggestion() {
        // EP: mixing name mode with incomplete date range should not suggest ed/
        assertTrue(AutocompleteProvider.suggestCompletion("find n/Alice sd/2026-01-01 ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithMixedDateRangeAndTag_noSuggestion() {
        // EP: mixing tag mode with incomplete date range should not suggest sd/
        assertTrue(AutocompleteProvider.suggestCompletion("find t/client ed/2026-12-31 ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithMixedDateRangeAndDate_noSuggestion() {
        // EP: mixing single date mode with incomplete date range should not suggest ed/
        assertTrue(AutocompleteProvider.suggestCompletion("find d/2026-06-15 sd/2026-01-01 ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithReversedMixedDateRange_noSuggestion() {
        // EP: mixing date range with name mode (name entered after date range start)
        assertTrue(AutocompleteProvider.suggestCompletion("find sd/2026-01-01 n/John ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithPartialModeConflict_noSuggestion() {
        // EP: partial typing of conflicting mode should still reject suggestion
        assertTrue(AutocompleteProvider.suggestCompletion("find n/Alice sd/2026-01-01 e").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find t/client ed/2026-12-31 s").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithInvalidTokenBeforeValidPrefix_returnsNoSuggestion() {
        // EP: invalid non-prefix token appearing before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("find f n/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find invalid n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find xyz t/client ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithMultipleInvalidTokensBeforePrefixes_returnsNoSuggestion() {
        // EP: multiple invalid tokens before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("find x y z n/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find invalid more stuff t/client ").isEmpty());
    }

    @Test
    public void suggestCompletion_findWithOnlyWhitespaceBeforePrefixes_suggestsPrefix() {
        // EP: only whitespace (no invalid tokens) before first prefix
        assertEquals("find  n/", AutocompleteProvider.suggestCompletion("find  n").orElseThrow());
        assertEquals("find   t/", AutocompleteProvider.suggestCompletion("find   t").orElseThrow());
    }

    // EP Group: List, Note, Tag, Edit Prefix Behavior

    @Test
    public void suggestCompletion_listCommandPrefixSuggestions_work() {
        // EP: list supports only sort prefix
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list ").orElseThrow());
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list s").orElseThrow());
        assertTrue(AutocompleteProvider.suggestCompletion("list s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_listCommand_suggestsSortPrefix() {
        // EP: list command accepts sort prefix suggestion
        // overlap with suggestCompletion_listCommandPrefixSuggestions_work()
        // to emphasize the "first prefix suggestion" partition
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list ").orElseThrow());
    }

    @Test
    public void suggestCompletion_listWithSortValue_noSuggestion() {
        // EP: sort value already provided
        // overlap with suggestCompletion_listCommandPrefixSuggestions_work()
        // to emphasize the "sort value already supplied" partition
        assertTrue(AutocompleteProvider.suggestCompletion("list s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_listWithInvalidTokenBeforeValidPrefix_returnsNoSuggestion() {
        // EP: invalid non-prefix token appearing before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("list f s/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("list invalid s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_listWithOnlyWhitespaceBeforePrefixes_suggestsPrefix() {
        // EP: only whitespace (no invalid tokens) before first prefix
        assertEquals("list  s/", AutocompleteProvider.suggestCompletion("list  s").orElseThrow());
        assertEquals("list   s/", AutocompleteProvider.suggestCompletion("list   s").orElseThrow());
    }

    @Test
    public void suggestCompletion_edit_requiresIndexBeforePrefixSuggestion() {
        // EP: index-required command blocks prefix suggestions without a valid index
        assertTrue(AutocompleteProvider.suggestCompletion("edit ").isEmpty());
        // Boundary Value: minimum valid index is 1
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1").isPresent());
        assertEquals("edit 1 n/", AutocompleteProvider.suggestCompletion("edit 1").orElseThrow());
        assertEquals("edit 1 p/", AutocompleteProvider.suggestCompletion("edit 1 p").orElseThrow());
        assertEquals("edit 1 n/Bob p/", AutocompleteProvider.suggestCompletion("edit 1 n/Bob ").orElseThrow());

        String allPrefixesInput =
                "edit 1 n/Bob p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client ";
        assertEquals(allPrefixesInput + "t/", AutocompleteProvider.suggestCompletion(allPrefixesInput).orElseThrow());
    }

    @Test
    public void suggestCompletion_editAfterPartialPhonePrefix_suggestsPhonePrefix() {
        // EP: partial phone prefix for edit
        assertEquals("edit 1 p/", AutocompleteProvider.suggestCompletion("edit 1 p").orElseThrow());
    }

    @Test
    public void suggestCompletion_editWithNameAndNoSpace_suggestsSpace() {
        // EP: value token still being typed (no trailing whitespace)
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 n/Bob").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithInvalidTokenBeforeValidPrefix_returnsNoSuggestion() {
        // EP: invalid non-prefix token appearing before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 f p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 invalid p/123 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 xyz n/John ").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithMultipleInvalidTokensBeforePrefixes_returnsNoSuggestion() {
        // EP: multiple invalid tokens before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 x y z p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 invalid more stuff n/Bob ").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithOnlyWhitespaceBeforePrefixes_suggestsPrefix() {
        // EP: only whitespace (no invalid tokens) before first prefix
        assertEquals("edit 1  p/", AutocompleteProvider.suggestCompletion("edit 1  p").orElseThrow());
        assertEquals("edit 1   n/", AutocompleteProvider.suggestCompletion("edit 1   n").orElseThrow());
    }

    @Test
    public void suggestCompletion_note_requiresIndexBeforePrefixSuggestion() {
        // EP: note requires a valid index before nt/ can be suggested
        assertTrue(AutocompleteProvider.suggestCompletion("note ").isEmpty());
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1").orElseThrow());
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 nt").orElseThrow());
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 nt/hello ").isEmpty());
    }

    @Test
    public void suggestCompletion_tag_requiresIndexBeforePrefixSuggestion() {
        // EP: tag requires a valid index before at/ or dt/
        assertTrue(AutocompleteProvider.suggestCompletion("tag ").isEmpty());
        assertEquals("tag 1 at/", AutocompleteProvider.suggestCompletion("tag 1").orElseThrow());
        assertEquals("tag 1 dt/", AutocompleteProvider.suggestCompletion("tag 1 d").orElseThrow());
    }

    @Test
    public void suggestCompletion_noteWithoutIndex_noSuggestion() {
        // EP: missing required index
        // overlap with suggestCompletion_note_requiresIndexBeforePrefixSuggestion()
        // to emphasize the "missing index" invalid partition
        assertTrue(AutocompleteProvider.suggestCompletion("note ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteWithIndexAndSpace_suggestNotePrefix() {
        // Boundary Value: minimum valid index for note
        // overlap with suggestCompletion_note_requiresIndexBeforePrefixSuggestion()
        // to emphasize the "trailing space after valid index" partition
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_noteWithIndexPartialPrefix_suggestNotePrefix() {
        // EP: partial nt/ prefix completion
        // overlap with suggestCompletion_note_requiresIndexBeforePrefixSuggestion()
        // to emphasize the "partial prefix after valid index" partition
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 n").orElseThrow());
    }

    @Test
    public void suggestCompletion_noteWithInvalidTokenBeforeValidPrefix_returnsNoSuggestion() {
        // EP: invalid non-prefix token appearing before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 f nt/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 invalid nt/hello ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteWithOnlyWhitespaceBeforePrefixes_suggestsPrefix() {
        // EP: only whitespace (no invalid tokens) before first prefix
        assertEquals("note 1  nt/", AutocompleteProvider.suggestCompletion("note 1  nt").orElseThrow());
        assertEquals("note 1   nt/", AutocompleteProvider.suggestCompletion("note 1   nt").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithoutIndex_noSuggestion() {
        // EP: missing required index
        // overlap with suggestCompletion_tag_requiresIndexBeforePrefixSuggestion()
        // to emphasize the "missing index" invalid partition
        assertTrue(AutocompleteProvider.suggestCompletion("tag ").isEmpty());
    }

    @Test
    public void suggestCompletion_tagWithIndexAndSpace_suggestAddTagPrefix() {
        // Boundary Value: minimum valid index for tag
        // overlap with suggestCompletion_tag_requiresIndexBeforePrefixSuggestion()
        // to emphasize the "trailing space after valid index" partition
        assertEquals("tag 1 at/", AutocompleteProvider.suggestCompletion("tag 1 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithPartialDeletePrefix_suggestDeleteTagPrefix() {
        // EP: partial delete-tag prefix
        // overlap with suggestCompletion_tag_requiresIndexBeforePrefixSuggestion()
        // to emphasize the "partial prefix after valid index" partition
        assertEquals("tag 1 dt/", AutocompleteProvider.suggestCompletion("tag 1 dt").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithBothPrefixes_noSuggestion() {
        // EP: both repeatable tag prefixes exist; deterministic repeatable suggestion
        assertEquals("tag 1 at/client dt/service at/",
                AutocompleteProvider.suggestCompletion("tag 1 at/client dt/service ").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithBothPrefixesAndPartialRepeatablePrefix_suggestsMatchingRepeatablePrefix() {
        // EP: partial repeatable prefix should complete deterministically
        assertEquals("tag 1 at/client dt/service dt/",
                AutocompleteProvider.suggestCompletion("tag 1 at/client dt/service d").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagWithInvalidTokenBeforeValidPrefix_returnsNoSuggestion() {
        // EP: invalid non-prefix token appearing before first valid prefix
        assertTrue(AutocompleteProvider.suggestCompletion("tag 1 f at/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("tag 1 invalid at/client ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("tag 1 xyz dt/service ").isEmpty());
    }

    @Test
    public void suggestCompletion_tagWithOnlyWhitespaceBeforePrefixes_suggestsFirstPrefix() {
        // EP: only whitespace (no invalid tokens) before first prefix
        assertEquals("tag 1  at/", AutocompleteProvider.suggestCompletion("tag 1  at").orElseThrow());
        assertEquals("tag 1   dt/", AutocompleteProvider.suggestCompletion("tag 1   dt").orElseThrow());
    }

    // EP Group: Commands Without Prefix Suggestions

    @Test
    public void suggestCompletion_clearCommand_noSuggestion() {
        // EP: no-argument command should not suggest anything
        assertTrue(AutocompleteProvider.suggestCompletion("clear").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("clear ").isEmpty());
    }

    @Test
    public void suggestCompletion_exitCommand_noSuggestion() {
        // EP: no-argument command should not suggest anything
        assertTrue(AutocompleteProvider.suggestCompletion("exit").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("exit ").isEmpty());
    }

    @Test
    public void suggestCompletion_helpCommand_noSuggestion() {
        // EP: no-argument command should not suggest anything
        assertTrue(AutocompleteProvider.suggestCompletion("help").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("help ").isEmpty());
    }

    @Test
    public void suggestCompletion_deleteCommand_noSuggestion() {
        // EP: not a number
        assertTrue(AutocompleteProvider.suggestCompletion("delete ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("delete abc").isEmpty());
    }

    @Test
    public void suggestCompletion_newArchiveCommands_work() {
        // EP: archive/unarchive require index
        assertTrue(AutocompleteProvider.suggestCompletion("archive ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unarchive ").isEmpty());

        // EP: list-archive takes no prefixes
        assertTrue(AutocompleteProvider.suggestCompletion("list-archive ").isEmpty());
    }

    // Boundary Value Group: Index Parsing

    @Test
    public void suggestCompletion_editWithZeroIndex_noSuggestion() {
        // Boundary Value: index = 0 (invalid lower boundary)
        assertTrue(AutocompleteProvider.suggestCompletion("edit 0").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithNegativeIndex_noSuggestion() {
        // Boundary Value: index < 0
        assertTrue(AutocompleteProvider.suggestCompletion("edit -1").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithLongIndex_suggestsPrefix() {
        // Boundary Value: large but valid index
        assertEquals("edit 999 n/", AutocompleteProvider.suggestCompletion("edit 999").orElseThrow());
    }

    @Test
    public void suggestCompletion_archiveWithInvalidIndexes_noSuggestion() {
        // Boundary Value: index = 0 is invalid
        assertTrue(AutocompleteProvider.suggestCompletion("archive 0").isEmpty());

        // Boundary Value: negative index is invalid
        assertTrue(AutocompleteProvider.suggestCompletion("archive -1").isEmpty());
    }

    @Test
    public void suggestCompletion_archiveCommandsWithValidIndex_noPrefixSuggestion() {
        // EP: archive/unarchive are index-only commands with no argument prefixes
        assertTrue(AutocompleteProvider.suggestCompletion("archive 1").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unarchive 1").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("archive 1 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unarchive 1 ").isEmpty());
    }
}
