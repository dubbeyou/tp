package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListArchiveCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.UnarchiveCommand;

/**
 * Tests for {@link HelpContentProvider}.
 * Tests the public API using real command specs and verifying parsed output.
 */
public class HelpContentProviderTest {

    @Test
    public void getHelpSections_returnsAllThirteenCommands() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();

        assertEquals(13, sections.size());
    }

    @Test
    public void getHelpSections_firstSectionIsAddCommand() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection firstSection = sections.get(0);

        assertEquals(AddCommand.COMMAND_WORD, firstSection.commandWord());
    }

    @Test
    public void getHelpSections_addCommandParsedWithDescriptionUsageExamples() {
        // EP: command usage that has description + Parameters + Example blocks.
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection addSection = sections.get(0);

        assertEquals("Adds a person to the address book.", addSection.description());
        assertTrue(addSection.usage().startsWith("Parameters:"));
        assertTrue(addSection.examples().startsWith("Example:"));
    }

    @Test
    public void getHelpSections_listCommandInThirdPosition() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection listSection = sections.get(3);

        assertEquals(ListCommand.COMMAND_WORD, listSection.commandWord());
    }

    @Test
    public void getHelpSections_exitCommandIsLast() {
        // BVA: verify the last valid index in the command section list.
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        int lastIndex = sections.size() - 1;
        HelpContentProvider.HelpSection lastSection = sections.get(lastIndex);

        assertEquals(ExitCommand.COMMAND_WORD, lastSection.commandWord());
    }

    @Test
    public void getHelpSections_clearCommandHasSimpleUsage() {
        // EP: clear command should hide usage and keep example only.
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection clearSection = sections.get(10);

        assertEquals(ClearCommand.COMMAND_WORD, clearSection.commandWord());
        assertEquals("Clears all entries from the address book.", clearSection.description());
        assertEquals("", clearSection.usage());
        assertTrue(clearSection.examples().startsWith("Example:"));
    }

    @Test
    public void getHelpSections_addCommandIncludesVisitDateTimeParameter() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection addSection = sections.get(0);

        assertTrue(addSection.usage().contains("VISIT_DATE_TIME"));
    }

    @Test
    public void getHelpSections_addCommandIncludesNoteParameter() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection addSection = sections.get(0);

        assertTrue(addSection.usage().contains("nt/"));
    }

    @Test
    public void getHelpSections_descriptionHasNoNewlines() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();

        for (HelpContentProvider.HelpSection section : sections) {
            assertFalse(section.description().contains("\n"),
                    "Description for " + section.commandWord() + " should not contain newlines");
        }
    }

    @Test
    public void getHelpSections_allNonHiddenSectionsHaveUsage() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();

        for (HelpContentProvider.HelpSection section : sections) {
            if (section.commandWord().equals(ClearCommand.COMMAND_WORD)
                    || section.commandWord().equals(ListArchiveCommand.COMMAND_WORD)
                    || section.commandWord().equals(HelpCommand.COMMAND_WORD)
                    || section.commandWord().equals(ExitCommand.COMMAND_WORD)) {
                continue;
            }
            assertFalse(section.usage().isEmpty(),
                    "Usage for " + section.commandWord() + " should not be empty");
        }
    }

    @Test
    public void getHelpSections_allSectionsHaveNonNullExamples() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();

        for (HelpContentProvider.HelpSection section : sections) {
            assertNotEquals(null, section.examples(),
                    "Examples for " + section.commandWord() + " should not be null");
        }
    }

    @Test
    public void helpCommandSpec_storesCommandAndUsageText() {
        String command = "test";
        String usage = "Some usage text\nExample: example";
        HelpContentProvider.HelpCommandSpec spec = new HelpContentProvider.HelpCommandSpec(command, usage);

        assertEquals(command, spec.commandWord());
        assertEquals(usage, spec.usageAndExamples());
    }

    @Test
    public void helpSection_providesAllFourFields() {
        String command = "test";
        String description = "Test description";
        String usage = "test";
        String examples = "Example: test command";
        HelpContentProvider.HelpSection section = new HelpContentProvider.HelpSection(
                command, description, usage, examples);

        assertEquals(command, section.commandWord());
        assertEquals(description, section.description());
        assertEquals(usage, section.usage());
        assertEquals(examples, section.examples());
    }

    @Test
    public void parsedHelpText_recordBehavesAsValueType() {
        HelpContentProvider.ParsedHelpText first =
                new HelpContentProvider.ParsedHelpText("desc1", "usage1", "example1");
        HelpContentProvider.ParsedHelpText same =
                new HelpContentProvider.ParsedHelpText("desc1", "usage1", "example1");
        HelpContentProvider.ParsedHelpText different =
                new HelpContentProvider.ParsedHelpText("desc2", "usage1", "example1");

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
        assertNotEquals(first.hashCode(), different.hashCode());
    }

    @Test
    public void helpMessage_matchesUserGuideUrl() {
        assertEquals("Refer to the user guide: " + HelpWindow.USERGUIDE_URL, HelpWindow.HELP_MESSAGE);
    }

    @Test
    public void getHelpSections_orderedDeclaratively() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();

        // Verify order matches COMMAND_SPECS declaration
        assertEquals(AddCommand.COMMAND_WORD, sections.get(0).commandWord());
        assertEquals(ListCommand.COMMAND_WORD, sections.get(3).commandWord());
        assertEquals(ExitCommand.COMMAND_WORD, sections.get(sections.size() - 1).commandWord());
    }

    @Test
    public void parseHelpText_lowercaseParametersHeader_parsedCorrectly() throws Exception {
        // EP: case-insensitive marker detection for Parameters section.
        HelpContentProvider.ParsedHelpText parsed = invokeParseHelpText(
                "Adds a person to the address book.\n"
                        + "parameters: NAME PHONE\n"
                        + "Example: add n/Alice p/91234567");

        assertEquals("Adds a person to the address book.", parsed.description());
        assertTrue(parsed.usage().startsWith("parameters:"));
        assertTrue(parsed.examples().startsWith("Example:"));
    }

    @Test
    public void parseHelpText_lowercaseExampleHeader_parsedCorrectly() throws Exception {
        // EP: case-insensitive marker detection for Example section.
        HelpContentProvider.ParsedHelpText parsed = invokeParseHelpText(
                "Adds a person to the address book.\n"
                        + "Parameters: NAME PHONE\n"
                        + "example: add n/Alice p/91234567");

        assertEquals("Adds a person to the address book.", parsed.description());
        assertTrue(parsed.usage().startsWith("Parameters:"));
        assertTrue(parsed.examples().startsWith("example:"));
    }

    @Test
    public void extractDescription_colonWithTrailingText_returnsTrimmedSuffix() throws Exception {
        // Covers line 80 (if colon exists) and line 81 (has chars after colon)
        String result = invokeExtractDescription("add:   description text   ");
        assertEquals("description text", result);
    }

    @Test
    public void extractDescription_colonAtEnd_returnsEmptyString() throws Exception {
        // Covers line 80 (if colon exists) and line 84 (colon at end branch)
        String result = invokeExtractDescription("add:");
        assertEquals("", result);
    }

    @Test
    public void extractDescription_singleColonOnly_returnsEmptyString() throws Exception {
        String result = invokeExtractDescription(":");
        assertEquals("", result);
    }

    @Test
    public void extractDescription_withoutColon_returnsTrimmedInput() throws Exception {
        // Covers the no-colon path for line 84 in HelpContentProvider.extractDescription
        String result = invokeExtractDescription("   Adds a person to the address book.   ");
        assertEquals("Adds a person to the address book.", result);
    }

    @Test
    public void extractDescription_multipleColons_keepsTextAfterFirstColon() throws Exception {
        String result = invokeExtractDescription("add: one: two");
        assertEquals("one: two", result);
    }

    @Test
    public void parseHelpText_compactCommandDescriptionFormat_parsedCorrectly() throws Exception {
        // EP: compact format "command: description".
        HelpContentProvider.ParsedHelpText parsed = invokeParseHelpText(
                "clear: Clears all entries from the address book.");

        assertEquals("Clears all entries from the address book.", parsed.description());
        assertEquals("clear", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void parseHelpText_usageHeaderAtStart_treatedAsCompactFormat() throws Exception {
        // BVA: compact format starting with colon-token "Usage: clear" treated as compact.
        HelpContentProvider.ParsedHelpText parsed = invokeParseHelpText("Usage: clear");
        assertEquals("clear", parsed.description());
        assertEquals("Usage", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void getHelpSections_archiveCommandInEightPosition() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection archiveSection = sections.get(7);

        assertEquals(ArchiveCommand.COMMAND_WORD, archiveSection.commandWord());
        assertTrue(archiveSection.usage().contains("Parameters:"));
    }

    @Test
    public void getHelpSections_unarchiveCommandInNinthPosition() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection unarchiveSection = sections.get(8);

        assertEquals(UnarchiveCommand.COMMAND_WORD, unarchiveSection.commandWord());
        assertTrue(unarchiveSection.usage().contains("Parameters:"));
    }

    @Test
    public void getHelpSections_listArchiveCommandInTenthPosition() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection listArchiveSection = sections.get(9);

        assertEquals(ListArchiveCommand.COMMAND_WORD, listArchiveSection.commandWord());
        assertEquals("", listArchiveSection.usage());
        assertTrue(listArchiveSection.examples().startsWith("Example:"));
    }

    @Test
    public void getHelpSections_helpAndExitHideUsageAndShowExamples() {
        List<HelpContentProvider.HelpSection> sections = HelpContentProvider.getHelpSections();
        HelpContentProvider.HelpSection helpSection = sections.get(11);
        HelpContentProvider.HelpSection exitSection = sections.get(12);

        assertEquals(HelpCommand.COMMAND_WORD, helpSection.commandWord());
        assertEquals("", helpSection.usage());
        assertTrue(helpSection.examples().startsWith("Example:"));

        assertEquals(ExitCommand.COMMAND_WORD, exitSection.commandWord());
        assertEquals("", exitSection.usage());
        assertTrue(exitSection.examples().startsWith("Example:"));
    }

    @Test
    public void constructor_privateConstructor_throwsAssertionError() throws Exception {
        var ctor = HelpContentProvider.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        InvocationTargetException ex =
                assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertTrue(ex.getCause() instanceof AssertionError);
    }

    @Test
    public void parseHelpText_compactFormatWithExample_parsedCorrectly() throws Exception {
        // EP: compact "command: description" with example - the standard MESSAGE_USAGE format.
        HelpContentProvider.ParsedHelpText parsed = invokeParseHelpText(
                "clear: Clears all entries from the address book.\n"
                        + "Example: clear");

        assertEquals("Clears all entries from the address book.", parsed.description());
        assertEquals("clear", parsed.usage());
        assertEquals("Example: clear", parsed.examples());
    }

    @Test
    public void parseHelpText_noExampleAndNoDescription_usage() throws Exception {
        // Edge case: plain command-only MESSAGE_USAGE (like old format)
        HelpContentProvider.ParsedHelpText parsed = invokeParseHelpText("clear");
        assertEquals("", parsed.description());
        assertEquals("clear", parsed.usage());
        assertEquals("", parsed.examples());
    }

    private static String invokeExtractDescription(String input) throws Exception {
        Method method = HelpContentProvider.class.getDeclaredMethod("extractDescription", String.class);
        method.setAccessible(true);
        return (String) method.invoke(null, input);
    }

    private static HelpContentProvider.ParsedHelpText invokeParseHelpText(String input) throws Exception {
        Method method = HelpContentProvider.class.getDeclaredMethod("parseHelpText", String.class);
        method.setAccessible(true);
        return (HelpContentProvider.ParsedHelpText) method.invoke(null, input);
    }
}
