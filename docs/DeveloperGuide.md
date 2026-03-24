---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

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
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

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

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

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

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

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
| `* *` | user | reuse past commands | execute commands faster |
| `* *` | user | undo commands | undo any wrong changes  |
| `* *` | expert user | add aliases to commands | execute commands faster |
| `* *` | new user | see sample commands | know valid command formats |
| `*` | user | add a specific visit location | quickly reference where the visit takes place |
| `*` | user | set visit type (e.g. consultation, remote) | understand the nature of the visit |
| `*` | user | specify the visit duration | plan my time realistically |
| `*` | user | view completed visits | understand my visit history |
| `*` | user | mark a visit as planned, completed, or cancelled | track visit status and outcomes |
| `*` | user | be sure that visit records remains unchanged | be held accountable for the visit |
| `*` | new user | view a list of common commands when opening the app | refer to instructions without using an external manual |
| `*` | new user | transfer my contact details from my phone’s contact list to CareSync | transition to using CareSync easily |


*{More to be added}*

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

*{More to be added}*

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

*{More to be added}*

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

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
