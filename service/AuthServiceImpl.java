package service;

import backend.dao.UserDAO;
import backend.dao.UserDAOImpl;
import model.User;
import java.security.MessageDigest;
import java.util.Base64;

public class AuthServiceImpl implements AuthService {
    private User authenticated;
    private final UserDAO userDao = new UserDAOImpl();

    @Override
    public boolean authenticate(String email, String password) {
        // Hardcoded admin credentials for quick access
        if ("admin@example.com".equals(email) && "admin123".equals(password)) {
            authenticated = new User(0, "admin@example.com", "admin");
            return true;
        }
        
        try {
            User u = userDao.findByEmail(email);
            if (u == null) return false;
            String hashed = hash(password);
            boolean ok = hashed.equals(u.getPasswordHash());
            if (ok) authenticated = u;
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getAuthenticatedUser() { return authenticated; }

    private String hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(digest);
    }
}
