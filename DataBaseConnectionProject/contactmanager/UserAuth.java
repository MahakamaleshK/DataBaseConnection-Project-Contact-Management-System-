package contactmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserAuth {

    private static final Logger logger = LogManager.getLogger(UserAuth.class);

    private UserDAO userDAO;

    public UserAuth() {
        this.userDAO = new UserDAOImpl();
    }
    
    String encryptedPassword;
    private String PasswordEncrypt(String p) {
    	encryptedPassword = "";
    	String password = p;
        for(int i=0; i<password.length(); i++) {
        	char originalCharacter=password.charAt(i);
        	char newCharacter = (char) (originalCharacter + 3);
        	encryptedPassword += newCharacter;
        }
		return encryptedPassword;
    }
    
    public String signUp(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }

        username = username.trim();
        password = PasswordEncrypt(password.trim());

        if (userDAO.usernameExists(username)) {
            return "Username already exists. Choose another.";
        }
        
        User user = new User(username, password);
//        int rows = userDAO.createUser(username, password);
        int rows = userDAO.createUser(user);

        if (rows > 0) {
            logger.info("New user registered");
            return "User registered successfully.";
        } else {
            logger.error("Failed to register user");
            return "Error registering user. Please try again.";
        }
    }

    public int signIn(String username, String password) {
        return userDAO.validateUserAndGetId(username, PasswordEncrypt(password));
    }
}
