---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CareSync Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

- This project is built upon [AddressBook Level-3 project by the SE-EDU initiative](https://github.com/se-edu/addressbook-level3).
- Gradle - for build automation and dependency management: https://gradle.org
- Jackson - JSON parser: https://github.com/FasterXML/jackson
- JavaFX - for UI development: https://openjfx.io
- JUnit5 - testing framework: https://github.com/junit-team/junit5
- Markbind - for website generation: https://markbind.org
- PlantUML - for diagram creation: https://plantuml.com

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1 3 6-9")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103-F11-1/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Archive Feature

#### Implementation

The archive mechanism is implemented using an archive flag stored inside each person.

- Each person has an `isArchived` state.
- Active views hide archived persons by default.
- The `archive` command marks a selected person as archived.
- The `unarchive` command restores a selected archived person.
- The `list-archive` command filters and shows only archived persons.

#### Command Flow

1. User enters `archive INDEX`.
2. Logic routes the command text to `ArchiveCommandParser`.
3. Parser validates input and creates `ArchiveCommand`.
4. `ArchiveCommand` checks that `INDEX` exists in the current filtered list.
5. Model marks the selected person as **archived**.
6. Model refreshes the current filtered list.
7. Logic saves the updated address book to `storage`.
8. UI receives and displays a success message.

The sequence diagram below illustrates the interactions within the Logic component when executing the command `archive 1`.

<puml src="diagrams/ArchiveSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the archive command" /> 

<box type="info" seamless>

**Note:** The lifeline for `ArchiveCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of the diagram.
</box>


Similarly, how an archive operation goes through the Model component is shown below:

<puml src="diagrams/ArchiveSequenceDiagram-Model.puml" width="420" />

The unarchive command does the opposite. It calls Model#unarchivePerson(personToUnarchive), which updates the selected person’s archive state back to false.

<box type="info" seamless>

Note: list-archive filtering is handled by the command layer via Model#updateFilteredPersonList(p -> p.isArchived()), not inside Model#archivePerson(...).

</box>

#### Why This Design

- Archiving is non-destructive, so no contact data is lost.
- It is simple to implement because no separate archived database is required.
- It keeps behavior predictable: archived contacts are hidden from the default list but can still be recovered.

#### Design Considerations

Aspect: How to represent archived contacts

1. Alternative 1 (current choice): Keep an archive flag in each person.
- Pros: Minimal structural changes, easy JSON persistence, low implementation overhead.
- Cons: Filtering logic must consistently exclude archived records in active views.

2. Alternative 2: Move archived contacts into a separate collection.
- Pros: Strong separation between active and archived data.
- Cons: Higher complexity for edit, find, delete, and synchronization logic.

#### Current Behavior Summary

- Archive does not delete contact data.
- Default list shows non-archived contacts.
- list-archive shows archived contacts.
- unarchive returns a contact to the default active list.
_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of client and service contacts
* frequently conducts home visits and requires quick access to visit details
* works under time pressure and must efficiently track last and upcoming visits
* needs to categorise contacts into different roles (e.g., Client, family members, government services)
* requires reminders for follow-ups and scheduled check-ins
* prefers a simple, distraction-free desktop application
* is comfortable using keyboard-driven interfaces and typing commands
* values speed and efficiency over complex graphical interfaces
* wants all visit-related information consolidated in one place

**Value proposition**: Currently, social workers rely on a combination of paper notebooks,
calendars and messaging apps to track who they are visiting, where it takes place and the
purpose or outcome of each visit. This fragmentation increases administrative workload,
raises the risk of missed follow-ups or incomplete records, and reduces overall efficiency.
CareSync helps social service workers keep track of their clients and service contact
details as well as allow them to easily track upcoming visits with the various families.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                    | So that I can…​                                           |
|----------|--------------------------------------------|---------------------------------|-----------------------------------------------------------|
| `* * *` | user | add a contact with basic details | quickly identify who is being visited |
| `* * *` | user | delete a contact | remove contacts that are no longer needed |
| `* * *` | user | store an address with each contact | know where to go for home visits |
| `* * *` | user | set a visit date | know when to go for home visits |
| `* * *` | user | see visit date and time clearly | manage daily schedule effectively |
| `* * *` | user | add tags to differentiate clients and services | retrieve relevant contact details quickly |
| `* * *` | user | add tags to differentiate case ID / client groups | retrieve relevant contact details quickly |
| `* * *` | user | filter contacts based on tags | quickly view related contacts based on tag |
| `* * *` | user | search for a name | reference contacts clearly |
| `* *` | user | see upcoming visits alongside past ones | plan follow-ups effectively |
| `* *` | user | have a clean and simple layout | find information quickly between visits |
| `* *` | user | sort contacts by name or ID | find contacts easily |
| `* *` | user | mark a contact’s status (e.g. stable, urgent) | prioritise my work |
| `* *` | user | add a visit note to a contact | remember the purpose of the visit |
| `* *` | user | pin contacts | identify important contacts easily |
| `* *` | user | reuse past commands | execute commands faster |
| `* *` | user | autocomplete commands | execute commands faster |
| `* *` | user | undo commands | undo any wrong changes |
| `* *` | user | archive contacts | hide irrelavant contacts without deleting them |
| `* *` | user | list archived contacts | view hidden contacts |
| `* *` | user | unarchive contacts | restore hidden contacts to the main contact list |
| `* *` | expert user | add aliases to commands | execute commands faster |
| `* *` | new user | see sample commands | know valid command formats |
| `*` | expert user | combine commands | execute commands efficiently |
| `*` | user | add a specific visit location | quickly reference where the visit takes place |
| `*` | user | set visit type (e.g. consultation, remote) | understand the nature of the visit |
| `*` | user | specify the visit duration | plan my time realistically |
| `*` | user | view completed visits | understand my visit history |
| `*` | user | mark a visit as planned, completed, or cancelled | track visit status and outcomes |
| `*` | user | be sure that visit records remains unchanged | be held accountable for the visit |
| `*` | new user | view a list of common commands when opening the app | refer to instructions without using an external manual |
| `*` | new user | transfer my contact details from my phone’s contact list to CareSync | transition to using CareSync easily |


*{More may be added in the future}*

### Use cases

(For all use cases below, the **System** is the `CareSync` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC1 - Add Contact**

**Guarantee:**
- If successful, the contact will be stored and visible in the contact list.
- A duplicate contact will never be added.
- Malformed contacts based on invalid command entry will never be added.

**MSS**

1.  User enters the required details to add a contact.
2.  CareSync validates the entered data.
3.  CareSync stores the new contact.
4.  CareSync displays a success message.

    Use case ends.

**Extensions**

* 2a. CareSync detects invalid input format.

   * 2a1. CareSync displays an error message.
   * 2a2. User re-enters data.

     Steps 2a1–2a2 are repeated until the data entered is valid.

     Use case resumes from step 3.


* 2b. CareSync detects existing contact.

    * 2b1. CareSync notifies the user that the contact already exists.
    * 2b2. User re-enters data.

      Steps 2b1–2b2 are repeated until the data entered is valid.

      Use case resumes from step 3.

**Use case: UC2 - Update Contact**

**Precondition:**
A contact exists in CareSync.

**Guarantee:**
If successful, the selected contact’s details will be updated and saved.

**MSS**

1.  CareSync displays contact(s).
2.  User specifies the ID of the contact and fields to be updated (e.g., phone number or address).
3.  CareSync validates the new data.
4.  CareSync updates the contact information.
5.  CareSync displays a success message.

    Use case ends.

**Extensions**

* 3a. No matching contact is found.

   * 3a1. CareSync informs the user that no match exists.

     Use case ends.


* 3b. CareSync detects invalid input format.

    * 3b1. CareSync displays an error message.
    * 3b2. User re-enters data.

      Steps 3b1–3b2 are repeated until the data entered is valid.

      Use case resumes from step 4.

**Use case: UC3 - Delete Contact**

**Precondition:**
A contact exists in CareSync.

**Guarantee:**
If successful, the contact will be permanently removed from CareSync.

**MSS**

1.  CareSync displays contact(s).
2.  User specifies the ID of the contact to be deleted.
3.  CareSync validates the contact’s existence.
4.  CareSync removes the contact from storage.
5.  CareSync displays a success message and an updated list.

    Use case ends.

**Extensions**

* 3a. User entered an invalid ID.

   * 3a1. CareSync displays an error message.
   * 3a2. User re-enters data.

     Steps 3a1–3a2 are repeated until the data entered is valid.

     Use case resumes from step 4.

**Use case: UC4 - Search Contact via Name**

**Guarantee:** Matching contact(s), if any, are displayed to the user.

**MSS**

1.  User specifies the name to be searched.
2.  CareSync validates the contact's existence.
3.  CareSync retrieves the matching contact(s).
4.  CareSync displays the search results.

    Use case ends.

**Extensions**

* 2a. No matching contact is found.

    * 2a1. CareSync displays a message indicating no results.

      Use case ends.

**Use case: UC5 - Search Contact via Tag**

**Guarantee:** Matching contact(s), if any, are displayed to the user.

**MSS**

1.  User specifies the tag to be searched.
2.  CareSync validates the contact's existence.
3.  CareSync retrieves the matching contact(s).
4.  CareSync displays the search results.

    Use case ends.

**Extensions**

* 2a. No matching contact is found.

    * 2a1. CareSync displays a message indicating no results.

      Use case ends.

**Use case: UC6 - Set Visit Date and Time**

**Precondition:** Contact exists in CareSync.

**Guarantee:** If successful, the next visit date and time for the contact is updated and saved.

**MSS**

1.  CareSync displays contact(s).
2.  User specifies the ID of the contact and the fields to be updated (e.g. visit date).
3.  CareSync validates the date format.
4.  CareSync updates the next visit date and time for the contact.
5.  CareSync displays a success message.

    Use case ends.

**Extensions**

* 3a. CareSync detects invalid input format.

    * 3a1. CareSync displays an error message.
    * 3a2. User re-enters correct data.

      Steps 3a1-3a2 are repeated until the data entered is valid.

      Use case resumes from step 4.

*{More may be added in the future}*

### Non-Functional Requirements

1. Should work on any mainstream OS as long as it has Java 17 or above installed.
2. A user with above average typing speed for regular English text (i.e. not code, not system
admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
3. The system should respond to user commands within 2 seconds under normal usage.
4. The system should support at least 20 contacts without noticeable performance degradation.
5. Filtering and search operations should complete within 2 seconds.
6. The system should be able to load stored data within 3 seconds upon startup.
7. The application should not crash upon receiving malformed data.
8. Error messages shall be clear and actionable (e.g., specify which field is invalid).
9. Command syntax shall be consistent and predictable across features.
10. The system shall automatically persist data after every valid modification.
11. Completed visits shall not be modified without explicit action.
12. Contact names shall not be empty.
13. Phone numbers shall follow a defined format.
14. Dates and times shall follow a consistent format.
15. Duplicate contacts shall not be allowed unless explicitly permitted.
14. The system architecture should allow future expansion of features (e.g., additional
commands or data fields) without requiring major restructuring of existing components.

*{More may be added in the future}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS.
* **Tag**: A label that can be assigned to a contact to categorise them.
* **ID**: The currently displayed list number of the specific contact.
* **Case ID**: A Case ID is a unique identifier assigned to a specific client
case or record within CareSync. It allows users to track and reference a particular
case efficiently, ensuring that all family members that are associated with the
clients are displayed.
* **Command**: An instruction entered by the user to execute a specific action.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

!!**Initial launch**!!

Steps:
1. Download the latest [jar file](https://github.com/AY2526S2-CS2103-F11-1/tp/releases) and copy into an empty folder.
2. Double-click the jar file.

Expected:
- Shows the GUI with a set of sample contacts. The window size may not be optimum.

!!**Saving window preferences**!!

Steps:
1. Resize the window to an optimum size.
2. Move the window to a different location.
3. Close the window.
4. Re-launch the app by double-clicking the jar file.

Expected:
- The most recent window size and location is retained.

### Viewing help

*Prerequisites:*
- CareSync is running.

!!**Positive Test Case 1: Run the help command**!!

Steps:
1. Run `help`

Expected:
- Help window opens.

!!**Positive Test Case 2: Run the help command with existing help window**!!

Steps:
1. Run `help`
2. Minimise the help window.
3. Run `help` again.

Expected:
- Exisiting help window is focused.

!!**Positive Test Case 3: Run the help command with unknown parameters**!!

Steps:
1. Run `help 123`

Expected:
- Help window opens.

### Adding a contact

*Prerequisites:*
- CareSync is running.
- List all contacts using the `list` command.

!!**Positive Test Case 1: Adding with only compulsory fields**!!

Steps:
1. Run `add n/Hugo p/96543218 e/hugo@example.com a/Hugo street, block 123, #01-01`

Expected:
- Message: `New contact added: Hugo; Phone: ...`
- Contact is added to the list.
- Contact information is as specified.
- List index increases accordingly.

!!**Positive Test Case 2: Adding with all fields**!!

Steps:
1. Run `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 nt/Needs financial support v/2026-12-01 14:00 t/caseid6`

Expected:
- Message: `New contact added: John Doe; Phone: ...`
- Contact is added to the list.
- Contact information is as specified.
- List index increases accordingly.

!!**Positive Test Case 3: Adding with duplicate fields (except name)**!!

Steps:
1. Run `add n/Alice1 p/88883333 e/alice@example.com a/Alice street, block 123, #01-01 nt/Needs financial support v/2026-12-01 14:00 t/caseid6`
2. Run `add n/Alice2 p/88883333 e/alice@example.com a/Alice street, block 123, #01-01 nt/Needs financial support v/2026-12-01 14:00 t/caseid6`

Expected:
- Message: `New contact added: Alice1; Phone: ...`
- `Alice1` is added to the list.
- Message: `New contact added: Alice2; Phone: ...`
- `Alice2` is added to the list.
- Contact information are as specified.
- List index increases accordingly.

!!**Negative Test Case 1: Adding with duplicate name**!!

Steps:
1. Run `add n/Alicia p/80015678 e/alicia@example.com a/Alicia street, block 123, #01-01`
1. Run the same command again.

Expected:
- Message: `This contact already exists in the address book`
- No contact is added.

!!**Negative Test Case 2: Adding with invalid name**!!

Steps:
1. Run `add n/Bob- p/92225430 e/bob@example.com a/Bob street, block 123, #01-01`

Expected:
- Message: `Names should only contain alphanumeric characters ...`
- No contact is added.

!!**Negative Test Case 3: Adding with invalid phone**!!

Steps:
1. Run `add n/Bob p/123 e/bob@example.com a/Bob street, block 123, #01-01`

Expected:
- Message: `Phone numbers should be an ...`
- No contact is added.

!!**Negative Test Case 4: Adding with invalid email**!!

Steps:
1. Run `add n/Bob p/91234567 e/bemail a/Bob street, block 123, #01-01`

Expected:
- Message: `Emails should be of the format ...`
- No contact is added.

!!**Negative Test Case 5: Adding with invalid address**!!

Steps:
1. Run `add n/Bob p/91234567 e/bob@example.com a/Bob street/block123`

Expected:
- Message: `Addresses should not be blank ...`
- No contact is added.

!!**Negative Test Case 6: Adding with invalid note**!!

Steps:
1. Run `add n/Bob p/91234567 e/bob@example.com a/Bob street, block 123, #01-01 nt/notes+-`

Expected:
- Message: `Notes should be up to ...`
- No contact is added.

!!**Negative Test Case 7: Adding with invalid visit date and time**!!

Steps:
1. Run `add n/Bob p/91234567 e/bob@example.com a/Bob street, block 123, #01-01 v/2026-12-32 12:00`

Expected:
- Message: `Visit date and time must be a valid ...`
- No contact is added.

!!**Negative Test Case 8: Adding with invalid tag**!!

Steps:
1. Run `add n/Bob p/91234567 e/bob@example.com a/Bob street, block 123, #01-01 t/fr!end`

Expected:
- Message: `Tag names should be alphanumeric ...`
- No contact is added.

### Saving data

!!**Positive Test Case 1: Missing data file**!!

Steps:
1. Navigate to the `data` folder.
2. Delete `addressbook.json` if it exists.
3. Launch CareSync with `java -jar CareSync.jar`

Expected:
- CareSync creates a new `addressbook.json` file with the default sample data.

!!**Positive Test Case 2: Corrupted data file**!!

Steps:
1. Navigate to the `data` folder.
2. Open `addressbook.json`
3. Modify the file such that the data file is now invalid.
4. Save the file.
5. Launch CareSync with `java -jar CareSync.jar`

Expected:
- CareSync discards all data and starts with an empty data file.

!!**Positive Test Case 3: Data saved after commands**!!

Steps:
1. Launch CareSync with `java -jar CareSync.jar`
2. Add, edit or delete a contact using the `add`, `edit`, `delete` commands respectively.
3. Close CareSync.

Expected:
- Changes are saved correctly in `addressbook.json`
