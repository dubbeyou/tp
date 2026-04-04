package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.VisitDateTime;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(
                    new Name("Tan Ah Seng"),
                    new Phone("91234567"),
                    new Email("ahseng@example.com"),
                    new Address("Blk 123 Ang Mo Kio Ave 3, #04-56"),
                    new Note("Elderly client. Needs weekly visits."),
                    getTagSet("caseid1", "client"),
                    new VisitDateTime("2026-04-12 10:30"),
                    false
            ),
            new Person(
                    new Name("Tan Mei Ling"),
                    new Phone("92345678"),
                    new Email("meiling@example.com"),
                    new Address("Blk 123 Ang Mo Kio Ave 3, #04-56"),
                    new Note(""),
                    getTagSet("caseid1", "caregiver", "family"),
                    new VisitDateTime(),
                    false
            ),
            new Person(
                    new Name("Ng Jun Kai"),
                    new Phone("93456789"),
                    new Email("junkai@example.com"),
                    new Address("Blk 78 Bedok North Street 4, #08-12"),
                    new Note("Youth client receiving financial assistance."),
                    getTagSet("caseid2", "client"),
                    new VisitDateTime("2026-04-13 09:00"),
                    false
            ),
            new Person(
                    new Name("Ng Li Wei"),
                    new Phone("94567890"),
                    new Email("liwei@example.com"),
                    new Address("Blk 78 Bedok North Street 4, #08-12"),
                    new Note("Mother and caregiver."),
                    getTagSet("caseid2", "caregiver", "family"),
                    new VisitDateTime(),
                    false
            ),
            new Person(
                    new Name("Bright Future School"),
                    new Phone("65556666"),
                    new Email("contact@bfschool.edu.sg"),
                    new Address("21 Jurong West Street 65"),
                    new Note("School contact for attendance monitoring."),
                    getTagSet("caseid2", "school"),
                    new VisitDateTime(),
                    false
            ),
            new Person(
                    new Name("Lim Siew Lan"),
                    new Phone("95678901"),
                    new Email("siewlan@example.com"),
                    new Address("Blk 45 Toa Payoh Lorong 5, #10-22"),
                    new Note("Client requiring medical follow-ups."),
                    getTagSet("caseid3", "client"),
                    new VisitDateTime("2026-04-15 14:00"),
                    false
            ),
            new Person(
                    new Name("Tan Kok Beng"),
                    new Phone("96789012"),
                    new Email("kokbeng@example.com"),
                    new Address("Blk 45 Toa Payoh Lorong 5, #10-22"),
                    new Note("Husband and caregiver."),
                    getTagSet("caseid3", "caregiver", "family"),
                    new VisitDateTime(),
                    false
            ),
            new Person(
                    new Name("SGH Medical Social Worker"),
                    new Phone("62223333"),
                    new Email("msw@sgh.com.sg"),
                    new Address("Outram Road"),
                    new Note("Hospital liaison for discharge planning."),
                    getTagSet("caseid3", "hospital"),
                    new VisitDateTime(),
                    false
            ),

            // ===== CASE 4 (ARCHIVED = true) =====
            new Person(
                    new Name("Rahim Bakar"),
                    new Phone("97890123"),
                    new Email("rahim@example.com"),
                    new Address("Blk 210 Jurong East Street 21, #02-11"),
                    new Note("Client awaiting housing relocation."),
                    getTagSet("caseid4", "client"),
                    new VisitDateTime("2026-04-20 11:00"),
                    true
            ),
            new Person(
                    new Name("HDB Officer Tan"),
                    new Phone("61234567"),
                    new Email("hdb@hdb.gov.sg"),
                    new Address("HDB Hub, Toa Payoh"),
                    new Note("Housing officer handling case."),
                    getTagSet("caseid4", "agency"),
                    new VisitDateTime(),
                    true
            ),

            // ===== CASE 5 =====
            new Person(
                    new Name("Siti Aisyah"),
                    new Phone("98901234"),
                    new Email("aisyah@example.com"),
                    new Address("Blk 55 Choa Chu Kang, #06-78"),
                    new Note("Single mother requiring support."),
                    getTagSet("caseid5", "client"),
                    new VisitDateTime("2026-04-18 16:00"),
                    false
            ),
            new Person(
                    new Name("Helping Hands Volunteer Group"),
                    new Phone("97778888"),
                    new Email("helpinghands@volunteer.org"),
                    new Address("10 Bukit Merah Central"),
                    new Note("Volunteer group assisting with visits."),
                    getTagSet("caseid5", "volunteer"),
                    new VisitDateTime(),
                    false
            )
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
