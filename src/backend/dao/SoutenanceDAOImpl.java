/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend.dao;

import java.sql.Connection;
import java.sql.Date; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap; // Ajouté
import java.util.List;
import java.util.Map;     // Ajouté
import model.Soutenance;
import util.DBConnection;

/**
 * Version Finale Corrigée : Gère le CRUD + Les KPI du Dashboard + Le Tableau du Dashboard
 */
public class SoutenanceDAOImpl implements SoutenanceDAO {

    @Override
    public Soutenance get(int id) throws SQLException {
        Soutenance soutenance = null;
        String sql = "SELECT idsoutenance, date, salle, note FROM soutenance WHERE idsoutenance = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idsoutenance = rs.getInt("idsoutenance");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    String salle = rs.getString("salle");
                    double note = rs.getDouble("note");
                    soutenance = new Soutenance(idsoutenance, date, salle, note, null);
                }
            }
        }
        return soutenance;
    }

    @Override
  
    public List<Soutenance> getAll() throws SQLException {
        // Jointure pour récupérer le titre du PFE associé à la soutenance
        String sql = "SELECT s.idsoutenance, s.date, s.salle, s.note, p.idpfe, p.titre " +
                     "FROM soutenance s " +
                     "LEFT JOIN pfe p ON s.idpfe = p.idpfe";

        List<Soutenance> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idsoutenance");
                java.sql.Date d = rs.getDate("date");
                LocalDate date = (d != null) ? d.toLocalDate() : null;
                String salle = rs.getString("salle");
                double note = rs.getDouble("note");

                // --- CORRECTION CRITIQUE ICI ---
                model.Pfe pfe = null;
                int idPfe = rs.getInt("idpfe");
                String titrePfe = rs.getString("titre");

                if (idPfe > 0) {
                    // Au lieu de new Pfe(), on utilise le constructeur complet en passant
                    // null ou "" pour les valeurs qu'on n'a pas dans cette requête.
                    // Ordre supposé : id, titre, description, etat, date, etudiant, encadreurs
                    pfe = new model.Pfe(
                        idPfe,          // ID
                        titrePfe,       // Titre
                        "",             // Description (vide)
                        "",             // Etat (vide)
                        null,           // Date Soutenance PFE (null)
                        null,           // Etudiant (null)
                        null            // Liste Encadreurs (null)
                    );
                }
                // -------------------------------

                list.add(new Soutenance(id, date, salle, note, pfe));
            }
        }
        return list;
    }

    @Override
    public int insert(Soutenance soutenance) throws SQLException {
        String sql = "INSERT INTO soutenance (date, salle, note, idpfe) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(soutenance.getDate()));
            ps.setString(2, soutenance.getSalle());
            ps.setDouble(3, soutenance.getNote());

            if (soutenance.getPfe() != null) {
                ps.setInt(4, soutenance.getPfe().getIdpfe());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Soutenance soutenance) throws SQLException {
        String sql = "UPDATE soutenance SET date = ?, salle = ?, note = ? WHERE idsoutenance = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(soutenance.getDate()));
            ps.setString(2, soutenance.getSalle());
            ps.setDouble(3, soutenance.getNote());
            ps.setInt(4, soutenance.getIdsoutenance());

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Soutenance soutenance) throws SQLException {
        String sql = "DELETE FROM soutenance WHERE idsoutenance = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, soutenance.getIdsoutenance());
            return ps.executeUpdate();
        }
    }

    @Override
    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM soutenance";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    @Override
    public int countUpcoming() throws SQLException {
        String sql = "SELECT COUNT(*) FROM soutenance WHERE date >= ?"; 

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            LocalDate today = LocalDate.now();
            ps.setDate(1, Date.valueOf(today));
            
            System.out.println("DEBUG DAO: Check date >= " + today);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // --- NOUVELLE MÉTHODE POUR LE TABLEAU DU DASHBOARD ---
    // Cette méthode récupère le Nom de l'étudiant via les jointures
    @Override
    public List<Map<String, Object>> getUpcomingDetails() throws SQLException {
        String sql = "SELECT e.nom, e.prenom, s.date, s.salle " +
                     "FROM soutenance s " +
                     "JOIN pfe p ON s.idpfe = p.idpfe " +
                     "JOIN etudiant e ON p.idetudiant = e.idetudiant " +
                     "WHERE s.date >= ? " +
                     "ORDER BY s.date ASC LIMIT 5";
        
        List<Map<String, Object>> list = new ArrayList<>();
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // On affiche seulement les soutenances futures
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    String nomComplet = rs.getString("nom") + " " + rs.getString("prenom");
                    
                    row.put("nom", nomComplet);
                    row.put("date", rs.getDate("date").toLocalDate());
                    row.put("heure", "---");
                    row.put("etat", "Planifiée");
                    
                    list.add(row);
                }
            }
        }
        return list;
    }
}