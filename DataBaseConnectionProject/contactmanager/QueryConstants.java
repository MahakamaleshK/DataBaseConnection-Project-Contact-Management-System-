package contactmanager;

public class QueryConstants {

    // ===== User Queries =====
    public static final String CHECK_USERNAME = "SELECT COUNT(*) FROM users WHERE username = ?";
    public static final String CREATE_USER = "INSERT INTO users (username, password) VALUES (?, ?)";   
    public static final String VALIDATE_USER_WITH_ID = "SELECT userId FROM users WHERE username = ? AND password = ?";

    // ===== Contact Queries =====
    public static final String ADD_CONTACT =
            "INSERT INTO contacts (userId, firstName, lastName, mobileNumber, email, dob, secondNumber, insta_id, memory, status, isEmergency, `group`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String CHECK_MOBILE =
            "SELECT COUNT(*) FROM contacts WHERE userId = ? AND mobileNumber = ? AND status='active'";

    public static final String CHECK_NAME =
            "SELECT COUNT(*) FROM contacts WHERE userId = ? AND firstName = ? AND lastName = ? AND status='active'";

    public static final String MOBILE_EXISTS_EXCLUDING_ID =
            "SELECT COUNT(*) FROM contacts WHERE userId = ? AND mobileNumber = ? AND contactId != ? AND status='active'";

    public static final String UPDATE_CONTACT =
            "UPDATE contacts SET mobileNumber = ?, email = ? WHERE contactId = ?";

    public static final String UPDATE_ADDITIONAL_DETAILS =
            "UPDATE contacts SET dob = ?, secondNumber = ?, insta_id = ?, memory = ? WHERE contactId = ?";

    public static final String GET_CONTACTS_BY_OWNER =
            "SELECT * FROM contacts WHERE userId = ? AND status='active' ORDER BY firstName";
    
    public static final String UPDATE_EMERGENCY =
    	    "UPDATE contacts SET isEmergency = ? WHERE contactId = ?";

    public static final String UPDATE_GROUP =
    		"UPDATE contacts SET `group` = ? WHERE contactId = ?";
    
    public static final String MOVE_TO_TRASH_INSERT_TRASH = 
    		"INSERT INTO trash (contactId, userId) VALUES (?, ?)";
    
    public static final String MOVE_TO_TRASH_SOFT_DELETE = 
    		"UPDATE contacts SET status = 'inactive' WHERE contactId = ?";
    
    public static final String GET_DELETED_CONTACTS_GROUPED_BY_OWNER =
    		"SELECT c.* FROM contacts c JOIN trash t ON c.contactId = t.contactId ORDER BY c.userId";
    
    public static final String GET_DELETED_CONTACTS_BY_GLOBAL_INDEX = 
    		"SELECT c.* FROM trash t JOIN contacts c USING (contactId) WHERE c.status = 'inactive' ORDER BY t.deletedAt LIMIT ?, 1";
    
    public static final String RECOVER_DELETED_CONTACT1 =
            "DELETE FROM trash WHERE contactId = ?";
    
    public static final String RECOVER_DELETED_CONTACT2 =
            "UPDATE contacts SET status = 'active' WHERE contactId = ? ";
}
