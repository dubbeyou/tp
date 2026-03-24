package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void isValidNote() {
        // null note
        assertThrows(NullPointerException.class, () -> Note.isValidNote(null));

        // valid notes
        assertTrue(Note.isValidNote("")); // empty string
        assertTrue(Note.isValidNote("Likes baseball")); // alphabets with spaces
        assertTrue(Note.isValidNote("12345678")); // numbers
        assertTrue(Note.isValidNote("Note,with,fullstop.")); // comma and full stop
        assertTrue(Note.isValidNote("Has spaces")); // spaces are allowed

        // invalid notes
        assertFalse(Note.isValidNote("note!")); // unsupported punctuation
        assertFalse(Note.isValidNote("line\nbreak")); // line breaks are not allowed
    }

    @Test
    public void equals() {
        Note note = new Note("Hello");

        // same object -> returns true
        assertTrue(note.equals(note));

        // same values -> returns true
        assertTrue(note.equals(new Note("Hello")));

        // different types -> returns false
        assertFalse(note.equals(1));

        // null -> returns false
        assertFalse(note.equals(null));

        // different values -> returns false
        assertFalse(note.equals(new Note("Bye")));
    }

    @Test
    public void hashCodeMethod() {
        Note note = new Note("Test");
        assertEquals(note.hashCode(), new Note("Test").hashCode());
    }

    @Test
    public void isPresent() {
        assertFalse(new Note("").isPresent());
        assertTrue(new Note("Has note").isPresent());
    }
}
