package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
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
    private boolean isArchived;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Note note, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, note, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.tags.addAll(tags);
        this.visitDateTime = new VisitDateTime(); // Empty by default
        this.isArchived = false; // False by default
    }

    /**
     * Constructor with optional visitDateTime field.
     */
    public Person(Name name, Phone phone, Email email, Address address, Note note, Set<Tag> tags,
                  VisitDateTime visitDateTime, boolean isArchived) {
        requireAllNonNull(name, phone, email, address, note, tags, visitDateTime);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.tags.addAll(tags);
        this.visitDateTime = visitDateTime;
        this.isArchived = isArchived;
    } // Edit here every new feature

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

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        this.isArchived = archived;
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

    public boolean hasVisitDateTime() {
        return getVisitDateTime().isPresent();
    }

    public LocalDateTime getVisitDateTimeValue() {
        return getVisitDateTime().getValue();
    }

    /**
     * Compares this person with another person by name, ignoring case.
     *
     * @param other The other person to compare with.
     * @return A negative integer, zero, or a positive integer as this person's name
     *         is lexicographically less than, equal to, or greater than the other person's name.
     */
    public int compareByName(Person other) {
        requireNonNull(other);
        return this.getName().compareToIgnoreCase(other.getName());
    }

    /**
     * Compares this person with another person by visit date-time, then by name if both
     * do not have a visit date-time.
     *
     * <p>Persons with a visit date-time are ordered before those without. If both persons
     * have a visit date-time, they are compared chronologically. If neither has a visit
     * date-time, they are compared by name (case-insensitive).</p>
     *
     * @param other The other person to compare with.
     * @return A negative integer, zero, or a positive integer as this person should be
     *         ordered before, equal to, or after the other person.
     */
    public int compareByVisitThenName(Person other) {
        requireNonNull(other);

        boolean thisHasVisit = this.hasVisitDateTime();
        boolean otherHasVisit = other.hasVisitDateTime();

        if (thisHasVisit && otherHasVisit) {
            return this.getVisitDateTimeValue()
                    .compareTo(other.getVisitDateTimeValue());
        }

        if (thisHasVisit) {
            return -1;
        }
        if (otherHasVisit) {
            return 1;
        }

        return this.compareByName(other);
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
                && visitDateTime.equals(otherPerson.visitDateTime)
                && isArchived == otherPerson.isArchived;
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, note, tags, visitDateTime, isArchived);
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
        sb.append(", isArchived=").append(isArchived);

        sb.append("}");
        return sb.toString();
    }

}
