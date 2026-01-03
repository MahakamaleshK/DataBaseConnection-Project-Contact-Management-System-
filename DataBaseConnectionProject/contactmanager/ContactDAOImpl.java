package contactmanager;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContactDAOImpl implements ContactDAO {

    private static final Logger logger = LogManager.getLogger(ContactDAOImpl.class);


    public ContactDAOImpl() {}

    /* ===================== Validation ===================== */

    @Override
    public boolean mobileExists(int userId, String mobile) {
        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.CHECK_MOBILE);
            ps.setInt(1, userId);
            ps.setString(2, mobile);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            logger.error("mobileExists error (user={})",userId, e);
        }
        return false;
    }

    @Override
    public boolean nameExists(int userId, String firstName, String lastName) {
        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.CHECK_NAME);
            ps.setInt(1, userId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            logger.error("nameExists error (user={})",userId, e);
        }
        return false;
    }

    /* ===================== Insert ===================== */

    @Override
    public int addContact(Contact c) {
        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.ADD_CONTACT);
            ps.setInt(1, c.userId);
            ps.setString(2, c.firstName);
            ps.setString(3, c.lastName);
            ps.setString(4, c.mobile);
            ps.setString(5, c.email);

            if (c.dob == null || c.dob.isEmpty())
                ps.setNull(6, Types.DATE);
            else
                ps.setDate(6, Date.valueOf(c.dob));

            ps.setString(7, c.secondNumber);
            ps.setString(8, c.instagram);
            ps.setString(9, c.memory);
            ps.setString(10, c.status);
            ps.setBoolean(11, c.isEmergency);
            ps.setString(12, c.group);

            return ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("addContact error (user={})",c.userId, e);
        }
        return 0;
    }

    /* ===================== Update ===================== */

    @Override
    public int updateContact(int id, String newMobile, String newEmail) {
        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.UPDATE_CONTACT);
            ps.setString(1, newMobile);
            ps.setString(2, newEmail);
            ps.setInt(3, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("updateContact error", e);
        }
        return 0;
    }

    @Override
    public int updateAdditionalDetails(int id, String dob,
                                       String secondNumber, String instagram, String memory) {
        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.UPDATE_ADDITIONAL_DETAILS);
            if (dob == null || dob.isEmpty())
                ps.setNull(1, Types.DATE);
            else
                ps.setDate(1, Date.valueOf(dob));

            ps.setString(2, secondNumber);
            ps.setString(3, instagram);
            ps.setString(4, memory);
            ps.setInt(5, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("updateAdditionalDetails error", e);
        }
        return 0;
    }

    @Override
    public boolean updateEmergency(int contactId, boolean isEmergency) {

        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.UPDATE_EMERGENCY);
            ps.setBoolean(1, isEmergency);
            ps.setInt(2, contactId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.error("Error updating emergency flag", e);
            return false;
        }
    }
    
    @Override
    public boolean updateGroup(int contactId, String group) {

        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.UPDATE_GROUP);
            ps.setString(1, group);
            ps.setInt(2, contactId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.error("Error updating group", e);
            return false;
        }
    }
    
    /* ===================== Fetch ===================== */

    @Override
    public List<Contact> getContactsByOwner(int userId) {
        List<Contact> list = new ArrayList<>();

        try {
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.GET_CONTACTS_BY_OWNER);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
                list.add(map(rs));

        } catch (SQLException e) {
            logger.error("getContactsByOwner error", e);
        }
        return list;
    }

    /* ===================== Index Based ===================== */


    @Override
    public boolean mobileExistsExcludingId(int userId, String mobile, int id) {
        try {
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.MOBILE_EXISTS_EXCLUDING_ID);
            ps.setInt(1, userId);
            ps.setString(2, mobile);
            ps.setInt(3, id);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            logger.error("mobileExistsExcludingId error", e);
        }
        return false;
    }

    /* ===================== Trash ===================== */

    @Override
    public boolean moveToTrash(Contact c) {
        Connection con = DBConnection.getInstance().getConnection();
//		System.out.println(1);

		try{
			PreparedStatement ps1 = con.prepareStatement(QueryConstants.MOVE_TO_TRASH_INSERT_TRASH);
//			System.out.println(2);
		    PreparedStatement ps2 = con.prepareStatement(QueryConstants.MOVE_TO_TRASH_SOFT_DELETE);
//		    System.out.println(3);
		    ps1.setInt(1, c.contactId);
//		    System.out.println(4);
		    ps1.setInt(2, c.userId);
		    ps1.executeUpdate();
//		    System.out.println(5);
		    ps2.setInt(1, c.contactId);
//		    System.out.println(6);
		    ps2.executeUpdate();
//		    System.out.println(7);
		    return true;
		} catch (SQLException e) {
//			System.out.println(8);
			logger.error("moveToTrash error", e);
		}
        return false;
    }

    @Override
    public Map<Integer, List<Contact>> getDeletedContactsGroupedByOwner() {
        Map<Integer, List<Contact>> map = new HashMap<>();

        try{
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.GET_DELETED_CONTACTS_GROUPED_BY_OWNER);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contact c = map(rs);
                map.computeIfAbsent(c.userId, k -> new ArrayList<>()).add(c);
            }

        } catch (SQLException e) {
            logger.error("getDeletedContactsGroupedByOwner error", e);
        }
        return map;
    }

    @Override
    public Contact getDeletedContactByGlobalIndex(int position) {

        try {
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QueryConstants.GET_DELETED_CONTACTS_BY_GLOBAL_INDEX);
            ps.setInt(1, position-1);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return map(rs);

        } catch (SQLException e) {
            logger.error("getDeletedContactByGlobalIndex error", e);
        }
        return null;
    }

    @Override
    public boolean recoverDeletedContact(Contact c) {
        try {
        	Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps1 = con.prepareStatement(QueryConstants.RECOVER_DELETED_CONTACT1);
       	 	PreparedStatement ps2 = con.prepareStatement(QueryConstants.RECOVER_DELETED_CONTACT2);
            ps1.setInt(1, c.contactId);
            ps1.executeUpdate();
            ps2.setInt(1, c.contactId);
            return ps2.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("recoverDeletedContact error", e);
        }
        return false;
    }

    private Contact map(ResultSet rs) throws SQLException {
        Contact c = new Contact(
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("mobileNumber"),
                rs.getString("email"),
                rs.getString("dob"),
                rs.getString("secondNumber"),
                rs.getString("insta_id"),
                rs.getString("memory"),
                rs.getString("status"),
                rs.getBoolean("isEmergency"),
                rs.getString("group")
        );
        c.contactId = rs.getInt("contactId");
        return c;
    }
    
}
