package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains tests for {@code Messages}.
 */
public class MessagesTest {

    @Test
    public void format_personWithoutVisitDateTime_success() {
        Person person = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong West Ave 6, #08-111")
                .withTags("friends")
                .build();

        String expected = "Alice Pauline; Phone: 94351253; Email: alice@example.com; Address: "
                + "123, Jurong West Ave 6, #08-111; Note: ; Tags: [friends]";

        assertEquals(expected, Messages.format(person));
    }

    @Test
    public void format_personWithVisitDateTime_success() {
        Person person = new PersonBuilder()
                .withName("Benson Meier")
                .withPhone("98765432")
                .withEmail("johnd@example.com")
                .withAddress("311, Clementi Ave 2, #02-25")
                .withVisitDateTime("2026-12-01 14:00")
                .withTags("friends")
                .build();

        String expected = "Benson Meier; Phone: 98765432; Email: johnd@example.com; Address: "
                + "311, Clementi Ave 2, #02-25; Note: ; Next Visit: 01 Dec 2026, 02:00 PM; Tags: [friends]";

        assertEquals(expected, Messages.format(person));
    }

    @Test
    public void getErrorMessageForDuplicatePrefixes_singlePrefix_success() {
        Prefix prefix = new Prefix("n/");
        String result = Messages.getErrorMessageForDuplicatePrefixes(prefix);
        assertEquals(Messages.MESSAGE_DUPLICATE_FIELDS + "n/", result);
    }

    @Test
    public void getErrorMessageForDuplicatePrefixes_multiplePrefixes_containsBothPrefixes() {
        Prefix prefix1 = new Prefix("n/");
        Prefix prefix2 = new Prefix("p/");
        String result = Messages.getErrorMessageForDuplicatePrefixes(prefix1, prefix2);
        assertTrue(result.startsWith(Messages.MESSAGE_DUPLICATE_FIELDS));
        assertTrue(result.contains("n/"));
        assertTrue(result.contains("p/"));
    }
}
