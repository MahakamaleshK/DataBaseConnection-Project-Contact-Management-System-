package contactmanager;

import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAOImpl.class);

    public UserDAOImpl() { }

    @Override
    public boolean usernameExists(String username) {
        try{
        	Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(QueryConstants.CHECK_USERNAME);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            logger.error("Error checking username exists", e);
        }
        return false;
    }

//    @Override
//    public int createUser(String username, String password) {
//        try{
//        	Connection conn = DBConnection.getInstance().getConnection();
//            PreparedStatement ps = conn.prepareStatement(QueryConstants.CREATE_USER);
//            ps.setString(1, username);
//            ps.setString(2, password);
//            return ps.executeUpdate();
//        } catch (SQLException e) {
//            logger.error("Error creating user", e);
//            return 0;
//        }
//    }
    
    @Override
    public int createUser(User u) {
        try{
        	Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(QueryConstants.CREATE_USER);
            ps.setString(1, u.userName);
            ps.setString(2, u.password);
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error creating user", e);
            return 0;
        }
    }

    @Override
    public int validateUserAndGetId(String username, String password) {

        try{
        	Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(QueryConstants.VALIDATE_USER_WITH_ID);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("userId");
            }

        } catch (SQLException e) {
            logger.error("Error validating user", e);
        }

        return -1;
    }

}
