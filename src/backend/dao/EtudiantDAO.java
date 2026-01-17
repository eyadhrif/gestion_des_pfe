/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;

import model.Etudiant;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface EtudiantDAO extends DAO<Etudiant> {
    
    // Vérifie si un email existe déjà (pour l'ajout/modif)
    boolean existsByEmail(String email) throws SQLException;
    
    // Compte total pour le Dashboard (Carte KPI Bleue)
    int countTotal() throws SQLException;
    
    // Répartition pour le Dashboard (Donut Chart)
    // Renommé pour correspondre à l'appel dans DashboardPanel
    Map<String, Integer> getRepartitionParClasse() throws SQLException;
}