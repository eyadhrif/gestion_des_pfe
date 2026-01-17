/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;

import model.Encadreur;
import java.sql.SQLException;
import java.util.Map; // Import nécessaire

/**
 *
 * @author ASUS
 */
public interface EncadreurDAO extends DAO<Encadreur> {
    
    // Nouvelle méthode pour le graphique "Supervision & Débordement"
    // Retourne par exemple : {"OK": 10, "Débordé": 2}
    Map<String, Integer> getSupervisionStats() throws SQLException;
}