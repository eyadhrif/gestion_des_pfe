package backend.dao;

import model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO extends DAO<User> {
    User findByEmail(String email) throws SQLException;
}
