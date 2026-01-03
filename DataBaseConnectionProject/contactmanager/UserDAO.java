package contactmanager;

public interface UserDAO {
    boolean usernameExists(String username);
//    int createUser(String username, String password);
    int createUser(User user);
    int validateUserAndGetId(String username, String password);
}
