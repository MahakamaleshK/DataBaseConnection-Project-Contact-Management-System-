package contactmanager;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Admin {

    private static final Logger logger = LogManager.getLogger(Admin.class);

    private ContactDAO contactDAO;

    public Admin() {
        this.contactDAO = new ContactDAOImpl();
    }

    public String viewAllDeletedGrouped() {

        Map<Integer, List<Contact>> groupedTrash =
                contactDAO.getDeletedContactsGroupedByOwner();

        if (groupedTrash == null || groupedTrash.isEmpty()) {
            return "Trash is empty.";
        }

        StringBuilder output = new StringBuilder();
        output.append("\n--- Deleted Contacts (Grouped by User) ---\n");

        int globalIndex = 1;

        for (Integer owner : groupedTrash.keySet()) {
            output.append("\nUser: ").append(owner).append("\n");

            List<Contact> contacts = groupedTrash.get(owner);
            for (Contact c : contacts) {
                output.append("  [")
                      .append(globalIndex)
                      .append("] ")
                      .append(c.firstName)
                      .append(" ")
                      .append(c.lastName)
                      .append(" | ")
                      .append(c.mobile)
                      .append("\n");
                globalIndex++;
            }
        }

        return output.toString();
    }

    public String recoverContactByPosition(int position) {

        if (position <= 0) {
            return "Invalid position number.";
        }

        Contact deletedContact =
                contactDAO.getDeletedContactByGlobalIndex(position);

        if (deletedContact == null) {
            return "Invalid position number.";
        }

        boolean recovered = contactDAO.recoverDeletedContact(deletedContact);

        if (recovered) {
            logger.info("Recovered contact {} {} for user {}",
                    deletedContact.firstName,
                    deletedContact.lastName,
                    deletedContact.userId);

            return "Recovered contact '"
                    + deletedContact.firstName + " "
                    + deletedContact.lastName
                    + "' to userId "
                    + deletedContact.userId + ".";
        } else {
            logger.error("Failed to recover contact id {}", deletedContact.userId);
            return "Failed to recover contact.";
        }
    }
}
