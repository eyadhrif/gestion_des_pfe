package service;

import model.User;

public interface AuthService {
    boolean authenticate(String email, String password);
    User getAuthenticatedUser();
}
