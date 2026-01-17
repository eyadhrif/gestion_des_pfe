/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;

import model.Soutenance;
import java.sql.SQLException;
import java.util.List; // Import nécessaire
import java.util.Map;  // Import nécessaire

/**
 *
 * @author ASUS
 */
public interface SoutenanceDAO extends DAO<Soutenance> {
    
    // Compte total des soutenances (pour les stats)
    int countTotal() throws SQLException;
    
    // Compte les soutenances futures (date >= aujourd'hui)
    int countUpcoming() throws SQLException;
    
    // Récupère les détails (Nom étudiant, Date, Salle) pour le tableau du Dashboard
    List<Map<String, Object>> getUpcomingDetails() throws SQLException;
}