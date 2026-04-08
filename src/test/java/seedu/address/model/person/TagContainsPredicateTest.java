package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class TagContainsPredicateTest {

    @Test
    public void equals() {
        TagContainsPredicate firstPredicate = new TagContainsPredicate("friends");
        TagContainsPredicate secondPredicate = new TagContainsPredicate("colleagues");

        // same object
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values
        TagContainsPredicate firstPredicateCopy = new TagContainsPredicate("friends");
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types
        assertFalse(firstPredicate.equals(1));

        // null
        assertFalse(firstPredicate.equals(null));

        // different tag
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagMatches_returnsTrue() {
        TagContainsPredicate predicate = new TagContainsPredicate("friends");

        Person person = new PersonBuilder()
                .withTags("friends", "family")
                .build();

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_tagDoesNotMatch_returnsFalse() {
        TagContainsPredicate predicate = new TagContainsPredicate("friends");

        Person person = new PersonBuilder()
                .withTags("family", "colleagues")
                .build();

        assertFalse(predicate.test(person));
    }

    @Test
    public void test_tagMatchesIgnoreCase_returnsTrue() {
        TagContainsPredicate predicate = new TagContainsPredicate("FRIENDS");

        Person person = new PersonBuilder()
                .withTags("friends")
                .build();

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_multipleTagsOneMatches_returnsTrue() {
        TagContainsPredicate predicate = new TagContainsPredicate("friends");

        Person person = new PersonBuilder()
                .withTags("family", "friends", "work")
                .build();

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_emptyTag_returnsFalse() {
        TagContainsPredicate predicate = new TagContainsPredicate("  ");
        assertFalse(predicate.test(new PersonBuilder().withTags("Friends").build()));
    }

    @Test
    public void test_tagStartsWith_returnsTrue() {
        TagContainsPredicate predicate = new TagContainsPredicate("frie");
        assertTrue(predicate.test(new PersonBuilder().withTags("Friends").build()));
    }

    @Test
    public void test_mixedCaseTrailingSpaces_returnsTrue() {
        TagContainsPredicate predicate = new TagContainsPredicate("  FriENds  ");
        assertTrue(predicate.test(new PersonBuilder().withTags("Friends").build()));
    }
}
