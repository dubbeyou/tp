package seedu.address.model.person;

import java.util.function.Predicate;

/**
 * Tests that a {@code Person}'s {@code Tag} matches the keyword given.
 * Supports partial matching using startsWith().
 */
public class TagContainsPredicate implements Predicate<Person> {

    private String tag;

    public TagContainsPredicate(String tag) {
        this.tag = tag.trim().toLowerCase();
    }

    @Override
    public boolean test(Person person) {
        if (tag.isEmpty()) {
            return false;
        }

        return person.getTags().stream()
                .anyMatch(t -> t.tagName.toLowerCase().startsWith(this.tag));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagContainsPredicate)) {
            return false;
        }

        TagContainsPredicate otherPredicate = (TagContainsPredicate) other;
        return tag.equals(otherPredicate.tag);
    }
}
