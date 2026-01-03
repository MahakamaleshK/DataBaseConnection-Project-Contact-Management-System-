package contactmanager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Operations {
	private static final Logger logger = LogManager.getLogger(Operations.class);

    private static ContactDAO contactDAO = null;
    private final int userId;

    public Operations(int userId) {
        this.userId = userId;
        Operations.contactDAO = new ContactDAOImpl();
    }
    
    public String addContact(String firstName, String lastName, String mobile, String email) {

        firstName = (firstName == null) ? "" : firstName.trim();
        lastName  = (lastName == null)  ? "" : lastName.trim();
        mobile    = (mobile == null)    ? "" : mobile.trim();
        email     = (email == null)     ? "" : email.trim();

        /* ---------- Mandatory fields ---------- */
        if (firstName.isEmpty() || lastName.isEmpty()) {
            logger.warn("Empty name attempted by user {}", userId);
            return "First name and last name cannot be empty.";
        }

        /* ---------- Mobile validation ---------- */
        if (!mobile.matches("\\d{10}")) {
            logger.warn("Invalid mobile '{}' by user {}", mobile, userId);
            return "Invalid mobile number! It must contain exactly 10 digits.";
        }

        /* ---------- Email validation ---------- */
        if (!email.isEmpty() &&
            !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|in)$")) {
            logger.warn("Invalid email '{}' by user {}", email, userId);
            return "Invalid email format! (example: name@gmail.com)";
        }

        /* ---------- Duplicate checks ---------- */
        if (contactDAO.mobileExists(userId, mobile)) {
            logger.warn("Duplicate mobile '{}' by user {}", mobile, userId);
            return "This mobile number already exists in your contacts.";
        }

        if (contactDAO.nameExists(userId, firstName, lastName)) {
            logger.warn("Duplicate name '{}' '{}' by user {}", firstName, lastName, userId);
            return "A contact with the same first and last name already exists.";
        }

        /* ---------- Create contact ---------- */
        Contact contact = new Contact(
                userId,
                firstName,
                lastName,
                mobile,
                email.isEmpty() ? null : email,
                null,   // dob
                null,   // secondNumber
                null,   // instagram
                null,   // memory
                "active",
                false,
                null    // group
        );

        int rows = contactDAO.addContact(contact);

        if (rows > 0) {
            logger.info("Contact added successfully (userId={}, mobile={})", userId, mobile);
            return "Contact added successfully.";
        } else {
            logger.error("Failed to add contact (userId={}, mobile={})", userId, mobile);
            return "Failed to add contact. Please try again.";
        }
    }
    
    public String editContactByIndex(int index, String newMobile, String newEmail) {

        Contact contact = contactDAO.getContactsByOwner(userId).stream().skip(index - 1).findFirst().orElse(null);

        if (contact == null) {
            return "Invalid position.";
        }

        int contactId = contact.contactId;

        newMobile = (newMobile == null) ? "" : newMobile.trim();
        newEmail  = (newEmail == null)  ? "" : newEmail.trim();

        boolean mobileChanged = false;
        boolean emailChanged  = false;

        /* ---------- Mobile validation ---------- */
        if (!newMobile.isEmpty()) {
            if (!newMobile.matches("\\d{10}")) {
                return "Invalid mobile number! It must contain 10 digits.";
            }

            if (contactDAO.mobileExistsExcludingId(userId, newMobile, contactId)) {
                return "This mobile number already exists in your contacts.";
            }

            mobileChanged = true;
        } else {
            newMobile = contact.mobile; //keep old
        }

        /* ---------- Email validation ---------- */
        if (!newEmail.isEmpty()) {
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|in)$")) {
                return "Invalid email format!";
            }
            emailChanged = true;
        } else {
            newEmail = contact.email; // keep old
        }

        /* ---------- Nothing to update ---------- */
        if (!mobileChanged && !emailChanged) {
            return "No changes detected.";
        }

        /* ---------- Update DB ---------- */
        int rows = contactDAO.updateContact(contactId, newMobile, newEmail);

        if (rows > 0) {
            logger.info("Contact updated (id={}, userId={})", contactId, userId);
            return "Contact edited successfully.";
        } else {
            logger.error("Failed to update contact id {}", contactId);
            return "Failed to edit contact.";
        }
    }
    
    public void assignGroup(Scanner input) {

        List<Contact> contacts =
                contactDAO.getContactsByOwner(userId);

        if (contacts.isEmpty()) {
            System.out.println("No contacts available.");
            return;
        }

        for (int i = 0; i < contacts.size(); i++) {
            System.out.println((i + 1) + ". " +
                    contacts.get(i).firstName + " " +
                    contacts.get(i).lastName);
        }

        System.out.print("Enter contact position: ");
        int pos = Integer.parseInt(input.nextLine());

        if (pos < 1 || pos > contacts.size()) {
            System.out.println("Invalid position.");
            return;
        }

        System.out.print("Enter group name: ");
        String group = input.nextLine().trim();

        Contact c = contacts.get(pos - 1);
        contactDAO.updateGroup(c.contactId, group);

        System.out.println("Group assigned successfully.");
    }

    public void viewGroupedContacts() {

        List<Contact> contacts =
                contactDAO.getContactsByOwner(userId);

        if (contacts.isEmpty()) {
            System.out.println("No contacts available.");
            return;
        }

        contacts.stream()
                .collect(Collectors.groupingBy(
                        c -> c.group == null ? "Ungrouped" : c.group))
                .forEach((g, list) -> {
                    System.out.println("\n" + g + ":");
                    list.forEach(c ->
                        System.out.println("  " +
                            c.firstName + " " +
                            c.lastName + " | " +
                            c.mobile));
                });
    }
    
    public void markEmergency(Scanner input) {

        List<Contact> contacts =
                contactDAO.getContactsByOwner(userId);

        if (contacts.isEmpty()) {
            System.out.println("No contacts available.");
            return;
        }

        for (int i = 0; i < contacts.size(); i++) {
            System.out.println((i + 1) + ". " +
                    contacts.get(i).firstName + " " +
                    contacts.get(i).lastName);
        }

        System.out.print("Enter position: ");
        int pos = Integer.parseInt(input.nextLine());

        if (pos < 1 || pos > contacts.size()) {
            System.out.println("Invalid position.");
            return;
        }

        Contact c = contacts.get(pos - 1);
        contactDAO.updateEmergency(c.contactId, true);

        System.out.println("Marked as emergency contact.");
    }

    public void viewEmergencyContacts() {

        List<Contact> contacts =
                contactDAO.getContactsByOwner(userId);

        contacts.stream()
                .filter(c -> c.isEmergency).forEach(c -> System.out.println(c.firstName + " " + c.lastName + " | " + c.mobile));
    }
    
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    public String updateContactByIndex(
            int index,
            String dob,
            String secondNumber,
            String instagram,
            String memory) {

        Contact contact = contactDAO.getContactsByOwner(userId).stream().skip(index - 1).findFirst().orElse(null);

        if (contact == null) {
            return "Invalid position.";
        }

        int contactId = contact.contactId;

        dob = (dob == null) ? "" : dob.trim();
        secondNumber = (secondNumber == null) ? "" : secondNumber.trim();
        instagram = (instagram == null) ? "" : instagram.trim();
        memory = (memory == null) ? "" : memory.trim();

        boolean changed = false;

        /* ---------- DOB ---------- */
        String dbDob = contact.dob;
        if (!dob.isEmpty()) {
            try {
                LocalDate parsed = LocalDate.parse(dob, INPUT_FMT);
                dbDob = parsed.toString(); // yyyy-MM-dd
                changed = true;
            } catch (DateTimeParseException e) {
                return "Invalid DOB format! Use dd-mm-yyyy.";
            }
        }

        /* ---------- Second number ---------- */
        String dbSecond = contact.secondNumber;
        if (!secondNumber.isEmpty()) {
            if (!secondNumber.matches("\\d{10}")) {
                return "Invalid second number! Must be 10 digits.";
            }
            dbSecond = secondNumber;
            changed = true;
        }

        /* ---------- Instagram ---------- */
        String dbInstagram = contact.instagram;
        if (!instagram.isEmpty()) {
            dbInstagram = instagram;
            changed = true;
        }

        /* ---------- Memory ---------- */
        String dbMemory = contact.memory;
        if (!memory.isEmpty()) {
            dbMemory = memory;
            changed = true;
        }

        if (!changed) {
            return "No changes detected.";
        }

        /* ---------- Update DB ---------- */
        int rows = contactDAO.updateAdditionalDetails(
                contactId, dbDob, dbSecond, dbInstagram, dbMemory);

        if (rows > 0) {
            logger.info("Additional details updated (id={}, userId={})", contactId, userId);
            return "Contact updated successfully.";
        } else {
            logger.error("Failed to update contact id {}", contactId);
            return "Failed to update contact.";
        }
    }
    
    public String viewContacts() {

        List<Contact> contacts = contactDAO.getContactsByOwner(userId);

        if (contacts == null || contacts.isEmpty()) {
            logger.info("No contacts found for user {}", userId);
            return "No contacts found.";
        }

        StringBuilder output = new StringBuilder();
        output.append("\n--- Contact List ---\n");

        for (int i = 0; i < contacts.size(); i++) {
            output.append(i + 1)
                  .append(". ")
                  .append(contacts.get(i).toString())
                  .append("\n");
        }

        logger.info("Displayed {} contacts for user {}", contacts.size(), userId);
        return output.toString();
    }
    
    public static List<Contact> searchByFirstName(int userId, String text) {

        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        String key = text.toLowerCase();

        return contactDAO.getContactsByOwner(userId).stream().filter(c -> c.firstName != null && c.firstName.toLowerCase().contains(key)).collect(Collectors.toList());
    }
    
    public String deleteContactByIndex(int index) {
//    	System.out.println("from delete1");
        Contact contact = contactDAO.getContactsByOwner(userId)
                                    .stream()
                                    .skip(index - 1)
                                    .findFirst()
                                    .orElse(null);
//        System.out.println("from delete2");
        if (contact == null) {
            return "Invalid position.";
        }
//        System.out.println("from delete3");
        System.out.println(contact);
        boolean deleted = contactDAO.moveToTrash(contact);
//        System.out.println("from delete4");
        if (deleted) {
            logger.info("Contact id {} deleted by user {}", contact.contactId, userId);
            return "Contact deleted successfully.";
        } else {
            logger.error("Failed to delete contact id {}", contact.contactId);
            return "Failed to delete contact.";
        }
    }
}
