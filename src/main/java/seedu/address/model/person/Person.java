package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final Note note;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final VisitDateTime visitDateTime;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Note note, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.tags.addAll(tags);
        this.visitDateTime = new VisitDateTime(); // Empty by default
    }

    /**
     * Constructor with optional visitDateTime field.
     */
    public Person(Name name, Phone phone, Email email, Address address, Note note, Set<Tag> tags,
                  VisitDateTime visitDateTime) {
        requireAllNonNull(name, phone, email, address, note, tags, visitDateTime);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.tags.addAll(tags);
        this.visitDateTime = visitDateTime;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Note getNote() {
        return note;
    }

    public VisitDateTime getVisitDateTime() {
        return visitDateTime;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && note.equals(otherPerson.note)
                && tags.equals(otherPerson.tags)
                && visitDateTime.equals(otherPerson.visitDateTime);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, note, tags, visitDateTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getCanonicalName()).append("{");
        sb.append("name=").append(name);
        sb.append(", phone=").append(phone);
        sb.append(", email=").append(email);
        sb.append(", address=").append(address);
        sb.append(", note=").append(note);
        sb.append(", tags=").append(tags);

        if (visitDateTime.isPresent()) {
            sb.append(", visitDateTime=").append(visitDateTime);
        }

        sb.append("}");
        return sb.toString();
    }

}
