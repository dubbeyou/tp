![Java CI](https://github.com/AY2526S2-CS2103-F11-1/tp/actions/workflows/gradle.yml/badge.svg) [![codecov](https://codecov.io/gh/AY2526S2-CS2103-F11-1/tp/graph/badge.svg?token=CR2V7F145N)](https://codecov.io/gh/AY2526S2-CS2103-F11-1/tp)

![Ui](docs/images/Ui.png)

>This project is based on the [AddressBook Level-3](https://github.com/se-edu/addressbook-level3) project created by the [SE-EDU initiative](https://se-education.org).

---

# CareSync

**CareSync** is a desktop application designed for **Social Workers in Singapore** to manage client and support organization contact details, as well as track home visit schedules efficiently. It is optimized for those who prefer typing (Command Line Interface) but still want the benefits of a Graphical User Interface (GUI).

## Why CareSync?

Social workers like **Alice** (a community social service officer) often handle high volumes of low-income households and elderly clients. CareSync addresses these specific needs by providing:

* **Targeted Categorization:** Categorize contacts by their case identification number and roles such as `client`, `caregiver`, `hospital` etc.
* **Visit Tracking:** Quick access to next visit schedule without needing an external calendar.
* **Efficient Workflows:** Optimized for users with fast typing speeds and supports bulk operations (e.g., deleting multiple contacts at once).
* **Contact Organization:** Archive old or irrelevant contacts without deleting them.

## Key Features

### Contact Management
* Add and delete contacts.
* Store details such as contact name, phone number, email, address etc.
* Search contacts by name, tags or their visit dates.
* Sort contact list by name or their visit dates.
* Archive old contacts without deleting them.

### Visit Management
* Set specific dates and times for home visits.
* Add notes to each contact to record important details, reminders or outcome of a home visit.

### Advanced Categorization
* Tag family members under a unique Case ID for holistic case management.
* Use flexible tags to differentiate between clients, caregivers, and government agencies etc.

### Optimized for Power Users
* **CLI-First Design:** Accomplish tasks faster than a GUI with specialized commands.
* **Command History:** Reuse past commands to save time.
* **Autocompletion:** Autocomplete commands with the click of a button to increase efficiency.
* **Bulk Operations:** Delete multiple contacts with a single command.

## More Info

### Documentation

For more information about CareSync, the product website can be found [here](https://ay2526s2-cs2103-f11-1.github.io/tp/).

### Troubleshooting

If you have any issue, please check the [Known issues](https://ay2526s2-cs2103-f11-1.github.io/tp/UserGuide.html#known-issues) page.

### Technical Requirements

**Platform:** Cross-platform support (Windows, macOS, Linux) via **Java `17`**.

### Acknowledgements

Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)
