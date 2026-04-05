package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // Equivalent Partitioning (EP): valid inputs
        assertTrue(Tag.isValidTagName("friend"));
        assertTrue(Tag.isValidTagName("abc123"));

        // Equivalent Partitioning (EP): invalid inputs
        assertFalse(Tag.isValidTagName(""));
        assertFalse(Tag.isValidTagName("friend!"));

        // Boundary Value Analysis (BVA): length around MAX_LENGTH
        String tagAtMaxLength = "a".repeat(Tag.MAX_LENGTH);
        String tagAboveMaxLength = "a".repeat(Tag.MAX_LENGTH + 1);
        assertTrue(Tag.isValidTagName(tagAtMaxLength));
        assertFalse(Tag.isValidTagName(tagAboveMaxLength));
    }

}
