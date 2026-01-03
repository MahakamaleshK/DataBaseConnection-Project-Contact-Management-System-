package contactmanager;

import java.util.List;
import java.util.Map;

public interface ContactDAO {

    boolean mobileExists(int userId, String mobile);
    boolean nameExists(int userId, String firstName, String lastName);
    int addContact(Contact contact);

    boolean mobileExistsExcludingId(int userId, String mobile, int id);
    int updateContact(int id, String newMobile, String newEmail);
    
    int updateAdditionalDetails(int id, String dob, String secondNumber, String instagram, String memory);

    boolean moveToTrash(Contact contact);

    List<Contact> getContactsByOwner(int userId);

    Map<Integer, List<Contact>> getDeletedContactsGroupedByOwner();
    Contact getDeletedContactByGlobalIndex(int position);

    boolean recoverDeletedContact(Contact contact);
    boolean updateEmergency(int contactId, boolean isEmergency);
    boolean updateGroup(int contactId, String group);

}