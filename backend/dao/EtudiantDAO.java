/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;
import model.Etudiant;
/**
 *
 * @author ASUS
 */
public interface EtudiantDAO extends DAO<Etudiant>{
    boolean existsByEmail(String email) throws java.sql.SQLException;
    int countTotal() throws java.sql.SQLException;
    java.util.Map<String, Integer> countByClasse() throws java.sql.SQLException;
}
