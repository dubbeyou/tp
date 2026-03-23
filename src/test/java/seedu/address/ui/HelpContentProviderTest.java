package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ListCommand;

public class HelpContentProviderTest {

    @Test
    public void parseHelpText_withParametersAndExamples_splitsSectionsCorrectly() {
        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText(AddCommand.MESSAGE_USAGE);

        assertEquals("Adds a person to the address book.", parsed.description());
        assertTrue(parsed.usage().startsWith("Parameters:"));
        assertTrue(parsed.usage().contains("VISIT_DATE_TIME"));
        assertTrue(parsed.examples().startsWith("Example:"));
    }

    @Test
    public void parseHelpText_withoutParameters_keepsUsageAsIs() {
        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText("Usage: clear");

        assertEquals("", parsed.description());
        assertEquals("Usage: clear", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void splitExamples_withNoExample_returnsUsageOnly() {
        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.splitExamples("Usage: help");

        assertEquals("", parsed.description());
        assertEquals("Usage: help", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void extractDescription_withCommandPrefix_returnsCleanDescription() {
        String extracted = HelpContentProvider.extractDescription("add: Adds a person to the address book.");

        assertEquals("Adds a person to the address book.", extracted);
    }

    @Test
    public void parseHelpText_withoutCommandColon_keepsDescriptionPrefixAsDescription() {
        String input = "Adds entries quickly. Parameters: p/PARAM\nExample: sample";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText(input);

        assertEquals("Adds entries quickly.", parsed.description());
        assertEquals("Parameters: p/PARAM", parsed.usage());
        assertEquals("Example: sample", parsed.examples());
    }

    @Test
    public void splitExamples_multipleExampleLines_keepsAllExamples() {
        String input = "Usage: something\nExample: first\nExample: second";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.splitExamples(input);

        assertEquals("Usage: something", parsed.usage());
        assertEquals("Example: first\nExample: second", parsed.examples());
    }

    @Test
    public void splitExamples_lowercaseExample_doesNotSplit() {
        String input = "Usage: test\nexample: lowercase marker";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.splitExamples(input);

        assertEquals("Usage: test\nexample: lowercase marker", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void extractDescription_newlinesCollapsed_returnsSingleLineDescription() {
        String extracted = HelpContentProvider.extractDescription("cmd: first line\nsecond line");

        assertEquals("first line second line", extracted);
    }

    @Test
    public void parseHelpText_parametersAtStart_hasNoDescription() {
        String input = "Parameters: p/PHONE\nExample: add p/12345678";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText(input);

        assertEquals("", parsed.description());
        assertEquals("Parameters: p/PHONE", parsed.usage());
        assertEquals("Example: add p/12345678", parsed.examples());
    }

    @Test
    public void parseHelpText_withExamplesButNoParameters_keepsUsageAndExamples() {
        String input = "Usage: quick command\nExample: quick run";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText(input);

        assertEquals("", parsed.description());
        assertEquals("Usage: quick command", parsed.usage());
        assertEquals("Example: quick run", parsed.examples());
    }

    @Test
    public void splitExamples_trimsWhitespaceAroundSections() {
        String input = "  Usage: do thing  \nExample: do thing now  ";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.splitExamples(input);

        assertEquals("Usage: do thing", parsed.usage());
        assertEquals("Example: do thing now", parsed.examples());
    }

    @Test
    public void extractDescription_colonAtEnd_returnsEmptyDescription() {
        String extracted = HelpContentProvider.extractDescription("add:");

        assertEquals("", extracted);
    }

    @Test
    public void parseHelpText_emptyString_returnsEmptyParts() {
        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText("");

        assertEquals("", parsed.description());
        assertEquals("", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void parseHelpText_exampleWithoutNewlineMarker_doesNotSplitExamples() {
        String input = "Usage: quick Example: inline";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText(input);

        assertEquals("", parsed.description());
        assertEquals("Usage: quick Example: inline", parsed.usage());
        assertEquals("", parsed.examples());
    }

    @Test
    public void parseHelpText_parametersOnlyInExamples_keepsUsageUnchanged() {
        String input = "Usage: quick\nExample: Parameters: p/111";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.parseHelpText(input);

        assertEquals("", parsed.description());
        assertEquals("Usage: quick", parsed.usage());
        assertEquals("Example: Parameters: p/111", parsed.examples());
    }

    @Test
    public void splitExamples_exampleAtBeginning_returnsEmptyUsage() {
        String input = "\nExample: just example";

        HelpContentProvider.ParsedHelpText parsed = HelpContentProvider.splitExamples(input);

        assertEquals("", parsed.usage());
        assertEquals("Example: just example", parsed.examples());
    }

    @Test
    public void extractDescription_withLeadingColon_trimsAndReturnsRemainder() {
        String extracted = HelpContentProvider.extractDescription(": prefixed text");

        assertEquals("prefixed text", extracted);
    }

    @Test
    public void extractDescription_multipleColons_usesFirstColonAsSplitPoint() {
        String extracted = HelpContentProvider.extractDescription("add: first: second");

        assertEquals("first: second", extracted);
    }

    @Test
    public void extractDescription_withCarriageReturn_normalizesToSingleLine() {
        String extracted = HelpContentProvider.extractDescription("cmd: first line\r\nsecond line");

        assertEquals("first line second line", extracted);
    }

    @Test
    public void parseHelpText_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> HelpContentProvider.parseHelpText(null));
    }

    @Test
    public void helpMessage_matchesUrlComposition() {
        assertEquals("Refer to the user guide: " + HelpWindow.USERGUIDE_URL, HelpWindow.HELP_MESSAGE);
    }

    @Test
    public void parsedHelpText_recordEqualityAndHashCode_behavesAsValueType() {
        HelpContentProvider.ParsedHelpText first = new HelpContentProvider.ParsedHelpText("d", "u", "e");
        HelpContentProvider.ParsedHelpText same = new HelpContentProvider.ParsedHelpText("d", "u", "e");
        HelpContentProvider.ParsedHelpText different = new HelpContentProvider.ParsedHelpText("d2", "u", "e");

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    public void getHelpSections_includesExpectedCommandsInDeclarativeOrder() {
        HelpContentProvider provider = new HelpContentProvider();

        assertEquals(AddCommand.COMMAND_WORD, provider.getHelpSections().get(0).commandWord());
        assertEquals(ListCommand.COMMAND_WORD, provider.getHelpSections().get(3).commandWord());
        assertEquals(ExitCommand.COMMAND_WORD,
                provider.getHelpSections().get(provider.getHelpSections().size() - 1).commandWord());
    }

    @Test
    public void getHelpSections_addSectionHasParsedDescriptionAndExamples() {
        HelpContentProvider provider = new HelpContentProvider();
        HelpContentProvider.HelpSection addSection = provider.getHelpSections().get(0);

        assertEquals("Adds a person to the address book.", addSection.description());
        assertTrue(addSection.usage().startsWith("Parameters:"));
        assertTrue(addSection.examples().startsWith("Example:"));
    }
}

