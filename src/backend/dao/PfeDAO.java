/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;

import model.Pfe;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface PfeDAO extends DAO<Pfe> {
    
    int countTotal() throws SQLException;
    
    int countByEtat(String etat) throws SQLException;
    
    // On garde cette méthode car elle est implémentée dans PfeDAOImpl
    // même si on ne l'utilise plus sur le Dashboard principal pour l'instant.
    List<Map<String, Object>> getLateProjects() throws SQLException;
    
    Map<String, Integer> getPfesByMonth() throws SQLException;
}