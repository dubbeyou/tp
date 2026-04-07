package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AutocompleteProviderTest {

    // EP Group: Input Validity and Unknown Commands

    @Test
    public void suggestCompletion_invalidInputs_returnsNoSuggestion() {
        // EP: Invalid/unknown inputs return no suggestion
        assertTrue(AutocompleteProvider.suggestCompletion(null).isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("   ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("zz").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unknown ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unknown something").isEmpty());
    }

    // EP Group: Command Word Completion

    @Test
    public void suggestCompletion_partialCommand_returnsCompletedCommand() {
        // EP: Partial command prefix completes to matching command
        assertEquals("add", AutocompleteProvider.suggestCompletion("a").orElseThrow());
    }

    @Test
    public void suggestCompletion_multiCharacterPrefix_completesCorrectly() {
        // EP: Multi-character prefixes complete to appropriate commands
        assertEquals("add", AutocompleteProvider.suggestCompletion("ad").orElseThrow());
        assertEquals("delete", AutocompleteProvider.suggestCompletion("del").orElseThrow());
        assertEquals("archive", AutocompleteProvider.suggestCompletion("ar").orElseThrow());
        assertEquals("unarchive", AutocompleteProvider.suggestCompletion("una").orElseThrow());
    }

    @Test
    public void suggestCompletion_exactCommandCycles_toNextMatch() {
        // BV: Exact command cycles to next command with same prefix
        assertEquals("list-archive", AutocompleteProvider.suggestCompletion("list").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespace_preservedInSuggestion() {
        // EP: Leading whitespace is preserved through command completion
        assertEquals(" add", AutocompleteProvider.suggestCompletion(" a").orElseThrow());
        assertEquals("          add", AutocompleteProvider.suggestCompletion("          a").orElseThrow());
    }

    // EP Group: Prefix Completion

    @Test
    public void suggestCompletion_commandWithTrailingSpace_suggestsFirstPrefix() {
        // EP: Trailing space transitions to prefix suggestion mode
        assertEquals("add n/", AutocompleteProvider.suggestCompletion("add ").orElseThrow());
    }

    @Test
    public void suggestCompletion_partialPrefix_completesToFull() {
        // EP: Partial prefix token completes to full prefix
        assertEquals("add n/", AutocompleteProvider.suggestCompletion("add n").orElseThrow());
    }

    @Test
    public void suggestCompletion_afterFirstPrefix_suggestsNext() {
        // EP: After first prefix, next unused prefix is suggested
        assertEquals("add n/Amy p/", AutocompleteProvider.suggestCompletion("add n/Amy ").orElseThrow());
    }

    @Test
    public void suggestCompletion_allNonRepeatableUsed_suggestsRepeatable() {
        // EP: Repeatable prefix suggested after all non-repeatable prefixes used
        String input = "add n/Amy p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client ";
        assertEquals(input + "t/", AutocompleteProvider.suggestCompletion(input).orElseThrow());
    }

    @Test
    public void suggestCompletion_prefixWithoutTrailingSpace_noSuggestion() {
        // EP: Prefix without trailing space yields no suggestion
        assertTrue(AutocompleteProvider.suggestCompletion("add n/").isEmpty());
    }

    @Test
    public void suggestCompletion_whitespaceVariations_treatedSame() {
        // EP: Tab and multiple spaces treated same as single space
        assertEquals("add\t n/", AutocompleteProvider.suggestCompletion("add\t").orElseThrow());
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   ").orElseThrow());
    }

    @Test
    public void suggestCompletion_valueWithTrailingSpace_suggestsNext() {
        // EP: Trailing space after prefix value suggests next prefix
        assertEquals("add n/John  p/", AutocompleteProvider.suggestCompletion("add n/John  ").orElseThrow());
    }

    @Test
    public void suggestCompletion_noTrailingSpace_noSuggestion() {
        // EP: Input without trailing space yields no suggestion
        String input = "add n/Amy p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client";
        assertTrue(AutocompleteProvider.suggestCompletion(input).isEmpty());
    }

    @Test
    public void suggestCompletion_prefixesAnyOrder_suggestsUnused() {
        // EP: Prefixes can be entered in any order; next suggestion is next unused
        assertEquals("add p/", AutocompleteProvider.suggestCompletion("add p").orElseThrow());
        assertEquals("add p/12345 n/", AutocompleteProvider.suggestCompletion("add p/12345 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_repeatablePrefixMultipleTimes() {
        // EP: Repeatable prefixes can be suggested multiple times
        assertEquals("add t/client n/", AutocompleteProvider.suggestCompletion("add t/client ").orElseThrow());
    }

    @Test
    public void suggestCompletion_leadingWhitespacePreservedAll() {
        // EP: Leading whitespace preserved in all contexts
        assertEquals(" add n/", AutocompleteProvider.suggestCompletion(" add ").orElseThrow());
        assertEquals("          add n/", AutocompleteProvider.suggestCompletion("          add ").orElseThrow());
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   n").orElseThrow());
        assertEquals("          add         n/",
                AutocompleteProvider.suggestCompletion("          add         n").orElseThrow());
    }

    @Test
    public void suggestCompletion_invalidFreeTextBeforePrefix_preventsSuggestion() {
        // EP: Invalid text before first prefix prevents suggestion
        assertFalse(AutocompleteProvider.suggestCompletion("add abc ").isPresent());
        assertFalse(AutocompleteProvider.suggestCompletion("edit 1 abc ").isPresent());
        assertFalse(AutocompleteProvider.suggestCompletion("find abc ").isPresent());
    }

    @Test
    public void suggestCompletion_invalidTokensBeforePrefix_preventsSuggestion() {
        // EP: Invalid tokens before first prefix prevent suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("add f p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add invalid p/123 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add xyz n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add f invalid g p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add x y z n/John ").isEmpty());
    }

    @Test
    public void suggestCompletion_invalidPrefixToken_preventsSuggestion() {
        // EP: Standalone invalid prefix-like tokens prevent autocomplete progression
        assertTrue(AutocompleteProvider.suggestCompletion("add n/ x/ ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("add n/ x/a ").isEmpty());
    }

    @Test
    public void suggestCompletion_onlyWhitespaceAllowsSuggestion() {
        // EP: Only whitespace (no invalid tokens) allows prefix suggestions
        assertEquals("add  p/", AutocompleteProvider.suggestCompletion("add  p").orElseThrow());
        assertEquals("add   n/", AutocompleteProvider.suggestCompletion("add   n").orElseThrow());
    }

    @Test
    public void suggestCompletion_prefixAtStartOfArgs_recognized() {
        // BV: Prefix at very start of args (position 0) is recognized immediately
        assertEquals("add p/", AutocompleteProvider.suggestCompletion("add p").orElseThrow());
        assertEquals("add n/Amy p/", AutocompleteProvider.suggestCompletion("add n/Amy ").orElseThrow());
    }

    // EP Group: Find Command Modes

    @Test
    public void suggestCompletion_findPrefixCompletion_suggestsModes() {
        // EP: Find command prefixes (n/, t/, sd/, ed/) complete correctly
        assertEquals("find n/", AutocompleteProvider.suggestCompletion("find ").orElseThrow());
        assertEquals("find t/", AutocompleteProvider.suggestCompletion("find t").orElseThrow());
        assertEquals("find sd/", AutocompleteProvider.suggestCompletion("find s").orElseThrow());
        assertEquals("find ed/", AutocompleteProvider.suggestCompletion("find e").orElseThrow());
    }

    @Test
    public void suggestCompletion_findSingleModes_completeImmediately() {
        // EP: Single search modes (name, tag, date) complete search immediately
        assertTrue(AutocompleteProvider.suggestCompletion("find n/Amy ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find t/client ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find d/2026-01-01 ").isEmpty());
    }

    @Test
    public void suggestCompletion_findDateRangeRequiresPair() {
        // EP: Date range mode requires both sd/ and ed/ for completion
        assertEquals("find sd/2026-01-01 ed/",
                AutocompleteProvider.suggestCompletion("find sd/2026-01-01 ").orElseThrow());
        assertEquals("find ed/2026-12-31 sd/",
                AutocompleteProvider.suggestCompletion("find ed/2026-12-31 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_findCompleteDateRange_noSuggestion() {
        // EP: Completed date range pair stops all suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("find sd/2026-01-01 ed/2026-12-31 ").isEmpty());
    }

    @Test
    public void suggestCompletion_findRangePartialEntry_suggestsMissingComponent() {
        // EP: Incomplete date range suggests missing half of pair
        assertEquals("find sd/ ed/", AutocompleteProvider.suggestCompletion("find sd/ ").orElseThrow());
        assertEquals("find ed/ sd/", AutocompleteProvider.suggestCompletion("find ed/ ").orElseThrow());
        assertEquals("find sd/ ed/", AutocompleteProvider.suggestCompletion("find sd/ e").orElseThrow());
        assertEquals("find ed/ sd/", AutocompleteProvider.suggestCompletion("find ed/ s").orElseThrow());
    }

    @Test
    public void suggestCompletion_findRangeWithValue_suggestsMissingPair() {
        // EP: Date range with value in first component suggests missing component prefix
        assertEquals("find sd/2026-01-01 ed/",
                AutocompleteProvider.suggestCompletion("find sd/2026-01-01 e").orElseThrow());
        assertEquals("find ed/2026-12-31 sd/",
                AutocompleteProvider.suggestCompletion("find ed/2026-12-31 s").orElseThrow());
    }

    @Test
    public void suggestCompletion_findRangePartialTokenNoMatch_noSuggestion() {
        // EP: Partial token in range context that doesn't match expected prefix yields no suggestion
        assertTrue(AutocompleteProvider.suggestCompletion("find sd/2026-01-01 x").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find ed/2026-12-31 p").isEmpty());
    }

    @Test
    public void suggestCompletion_findSingleModeBlocksOtherModes() {
        // EP: Single search mode prevents other modes from being entered
        assertEquals("find n/", AutocompleteProvider.suggestCompletion("find n").orElseThrow());
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find n/John t/client ").isEmpty());
    }

    @Test
    public void suggestCompletion_findIncompatibleModes_noSuggestion() {
        // EP: Mixing non-range modes with date range modes creates conflict, no suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("find n/Alice sd/2026-01-01 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find t/client ed/2026-12-31 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find d/2026-06-15 sd/2026-01-01 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find sd/2026-01-01 n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find n/Alice sd/2026-01-01 e").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find t/client ed/2026-12-31 s").isEmpty());
    }

    @Test
    public void suggestCompletion_findInvalidTokens_preventsSuggestion() {
        // EP: Invalid tokens before first prefix prevent suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("find f n/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find invalid n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find xyz t/client ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find x y z n/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("find invalid more stuff t/client ").isEmpty());
    }

    @Test
    public void suggestCompletion_findOnlyWhitespace_allowsSuggestion() {
        // EP: Only whitespace (no invalid tokens) allows prefix suggestions in find
        assertEquals("find  n/", AutocompleteProvider.suggestCompletion("find  n").orElseThrow());
        assertEquals("find   t/", AutocompleteProvider.suggestCompletion("find   t").orElseThrow());
    }

    // EP Group: List, Edit, Note, Tag Commands

    @Test
    public void suggestCompletion_listOnlySortPrefix_completesSearch() {
        // EP: List command only supports single sort prefix
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list ").orElseThrow());
        assertEquals("list s/", AutocompleteProvider.suggestCompletion("list s").orElseThrow());
        assertTrue(AutocompleteProvider.suggestCompletion("list s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_listInvalidTokens_preventsSuggestion() {
        // EP: Invalid tokens before first prefix prevent suggestions in list
        assertTrue(AutocompleteProvider.suggestCompletion("list f s/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("list invalid s/name ").isEmpty());
    }

    @Test
    public void suggestCompletion_listOnlyWhitespace_suggestsPrefix() {
        // EP: Only whitespace allows prefix suggestions in list
        assertEquals("list  s/", AutocompleteProvider.suggestCompletion("list  s").orElseThrow());
        assertEquals("list   s/", AutocompleteProvider.suggestCompletion("list   s").orElseThrow());
    }

    @Test
    public void suggestCompletion_editRequiresValidIndex() {
        // EP: Edit requires valid index before any prefix suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("edit ").isEmpty());
    }

    @Test
    public void suggestCompletion_editWithValidIndex_suggestsPrefixes() {
        // BV: Minimum valid index is 1
        assertEquals("edit 1 n/", AutocompleteProvider.suggestCompletion("edit 1").orElseThrow());
        assertEquals("edit 1 p/", AutocompleteProvider.suggestCompletion("edit 1 p").orElseThrow());
        assertEquals("edit 1 n/Bob p/", AutocompleteProvider.suggestCompletion("edit 1 n/Bob ").orElseThrow());
    }

    @Test
    public void suggestCompletion_editAllPrefixes_suggestsRepeatable() {
        // EP: All non-repeatable prefixes used suggests repeatable prefix
        String allPrefixesInput =
                "edit 1 n/Bob p/91234567 e/test@example.com a/Blk 1 nt/note v/2026-12-01 14:00 t/client ";
        assertEquals(allPrefixesInput + "t/", AutocompleteProvider.suggestCompletion(allPrefixesInput).orElseThrow());
    }

    @Test
    public void suggestCompletion_editValueWithoutTrailingSpace_noSuggestion() {
        // EP: Prefix value without trailing space yields no suggestion
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 n/Bob").isEmpty());
    }

    @Test
    public void suggestCompletion_editInvalidTokens_preventsSuggestion() {
        // EP: Invalid tokens before first prefix prevent suggestions in edit
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 f p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 invalid p/123 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 xyz n/John ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 x y z p/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("edit 1 invalid more stuff n/Bob ").isEmpty());
    }

    @Test
    public void suggestCompletion_editOnlyWhitespace_suggestsPrefix() {
        // EP: Only whitespace allows prefix suggestions in edit
        assertEquals("edit 1  p/", AutocompleteProvider.suggestCompletion("edit 1  p").orElseThrow());
        assertEquals("edit 1   n/", AutocompleteProvider.suggestCompletion("edit 1   n").orElseThrow());
    }

    @Test
    public void suggestCompletion_noteRequiresValidIndex() {
        // EP: Note requires valid index before prefix suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("note ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteWithValidIndex_suggestsPrefixes() {
        // BV: Minimum valid index is 1
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1").orElseThrow());
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 nt").orElseThrow());
        assertEquals("note 1 nt/", AutocompleteProvider.suggestCompletion("note 1 ").orElseThrow());
    }

    @Test
    public void suggestCompletion_notePrefixValueWithoutTrailingSpace_noSuggestion() {
        // EP: Prefix value without trailing space yields no suggestion
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 nt/hello ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteInvalidTokens_preventsSuggestion() {
        // EP: Invalid tokens before first prefix prevent suggestions in note
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 f nt/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("note 1 invalid nt/hello ").isEmpty());
    }

    @Test
    public void suggestCompletion_noteOnlyWhitespace_suggestsPrefix() {
        // EP: Only whitespace allows prefix suggestions in note
        assertEquals("note 1  nt/", AutocompleteProvider.suggestCompletion("note 1  nt").orElseThrow());
        assertEquals("note 1   nt/", AutocompleteProvider.suggestCompletion("note 1   nt").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagRequiresValidIndex() {
        // EP: Tag requires valid index before prefix suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("tag ").isEmpty());
    }

    @Test
    public void suggestCompletion_tagWithValidIndex_suggestsPrefixes() {
        // BV: Minimum valid index is 1
        assertEquals("tag 1 at/", AutocompleteProvider.suggestCompletion("tag 1").orElseThrow());
        assertEquals("tag 1 dt/", AutocompleteProvider.suggestCompletion("tag 1 d").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagInvalidTokens_preventsSuggestion() {
        // EP: Invalid tokens before first prefix prevent suggestions in tag
        assertTrue(AutocompleteProvider.suggestCompletion("tag 1 f at/").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("tag 1 invalid at/client ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("tag 1 xyz dt/service ").isEmpty());
    }

    @Test
    public void suggestCompletion_tagOnlyWhitespace_suggestsPrefix() {
        // EP: Only whitespace allows prefix suggestions in tag
        assertEquals("tag 1  at/", AutocompleteProvider.suggestCompletion("tag 1  at").orElseThrow());
        assertEquals("tag 1   dt/", AutocompleteProvider.suggestCompletion("tag 1   dt").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagBothPrefixes_suggestsRepeatable() {
        // EP: After both non-repeatable prefixes, repeatable prefix is suggested
        assertEquals("tag 1 at/client dt/service at/",
                AutocompleteProvider.suggestCompletion("tag 1 at/client dt/service ").orElseThrow());
    }

    @Test
    public void suggestCompletion_tagPartialRepeatablePrefix_completes() {
        // EP: Partial repeatable prefix completes deterministically
        assertEquals("tag 1 at/client dt/service dt/",
                AutocompleteProvider.suggestCompletion("tag 1 at/client dt/service d").orElseThrow());
    }

    // EP Group: Commands Without Arguments

    @Test
    public void suggestCompletion_noArgumentCommands_noSuggestion() {
        // EP: Commands with no arguments (clear, exit, help) never suggest anything
        assertTrue(AutocompleteProvider.suggestCompletion("clear").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("clear ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("exit").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("exit ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("help").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("help ").isEmpty());
    }

    @Test
    public void suggestCompletion_deleteWithInvalidIndex_noSuggestion() {
        // EP: Delete requires valid index; invalid input yields no suggestion
        assertTrue(AutocompleteProvider.suggestCompletion("delete ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("delete abc").isEmpty());
    }

    @Test
    public void suggestCompletion_archiveCommands_requireValidIndex() {
        // EP: Archive/unarchive require index; no prefix suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("archive ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unarchive ").isEmpty());
    }

    @Test
    public void suggestCompletion_listArchiveCommand_noSuggestion() {
        // EP: List-archive takes no arguments or prefixes
        assertTrue(AutocompleteProvider.suggestCompletion("list-archive ").isEmpty());
    }

    // Boundary Value Tests: Index Boundaries

    @Test
    public void suggestCompletion_indexZero_invalidBoundary() {
        // BV: Index = 0 is invalid lower boundary
        assertTrue(AutocompleteProvider.suggestCompletion("edit 0").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("archive 0").isEmpty());
    }

    @Test
    public void suggestCompletion_indexNegative_invalid() {
        // BV: Negative index is invalid
        assertTrue(AutocompleteProvider.suggestCompletion("edit -1").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("archive -1").isEmpty());
    }

    @Test
    public void suggestCompletion_indexOne_minimumValid() {
        // BV: Index = 1 is minimum valid value
        assertEquals("edit 1 n/", AutocompleteProvider.suggestCompletion("edit 1").orElseThrow());
    }

    @Test
    public void suggestCompletion_indexLarge_validBoundary() {
        // BV: Large index values are valid
        assertEquals("edit 999 n/", AutocompleteProvider.suggestCompletion("edit 999").orElseThrow());
    }

    @Test
    public void suggestCompletion_archiveIndexOnlyCommand_noPrefixSuggestion() {
        // EP: Archive/unarchive accept index but offer no prefix suggestions
        assertTrue(AutocompleteProvider.suggestCompletion("archive 1").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unarchive 1").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("archive 1 ").isEmpty());
        assertTrue(AutocompleteProvider.suggestCompletion("unarchive 1 ").isEmpty());
    }
}
