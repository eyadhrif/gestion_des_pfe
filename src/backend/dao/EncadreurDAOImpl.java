package backend.dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Encadreur;

public class EncadreurDAOImpl implements EncadreurDAO {

    // --- C'EST ICI QUE LA MAGIE OPÈRE (CORRIGÉ POUR VOTRE BASE) ---
    @Override
    public Map<String, Integer> getSupervisionStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        
        // NOUVELLE REQUÊTE SQL CORRECTE :
        // On joint 'encadreur' avec la table de liaison 'pfe_encadreur'
        String sql = "SELECT e.nom, COUNT(pe.idpfe) as total " +
                     "FROM encadreur e " +
                     "LEFT JOIN pfe_encadreur pe ON pe.idencadreur = e.idEncadreur " + 
                     "GROUP BY e.idEncadreur, e.nom";

        int encadrementOK = 0;
        int deborde = 0;
        int disponible = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int count = rs.getInt("total");
                
                // Classification
                if (count == 0) {
                    disponible++; // 0 projet
                } else if (count >= 5) {
                    deborde++;    // 5+ projets
                } else {
                    encadrementOK++; // 1 à 4 projets
                }
            }
        }
        
        stats.put("Encadrement OK", encadrementOK);
        stats.put("Débordé (>5)", deborde);
        stats.put("Disponible", disponible);
        
        return stats;
    }

    // --- CRUD Standard (Inchangés) ---
    @Override
    public Encadreur get(int id) throws SQLException {
        String sql = "SELECT idEncadreur, nom, prenom, grade, email FROM encadreur WHERE idEncadreur = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Encadreur(rs.getInt("idEncadreur"), rs.getString("nom"), rs.getString("prenom"), rs.getString("grade"), rs.getString("email"));
            }
        }
        return null;
    }

    @Override
    public List<Encadreur> getAll() throws SQLException {
        String sql = "SELECT idEncadreur, nom, prenom, grade, email FROM encadreur";
        List<Encadreur> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Encadreur(rs.getInt("idEncadreur"), rs.getString("nom"), rs.getString("prenom"), rs.getString("grade"), rs.getString("email")));
        }
        return list;
    }

    @Override
    public int insert(Encadreur encadreur) throws SQLException {
        String sql = "INSERT INTO encadreur (nom, prenom, grade, email) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, encadreur.getNom()); ps.setString(2, encadreur.getPrenom()); ps.setString(3, encadreur.getGrade()); ps.setString(4, encadreur.getEmail());
            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Encadreur encadreur) throws SQLException {
        String sql = "UPDATE encadreur SET nom = ?, prenom = ?, grade = ?, email = ? WHERE idEncadreur = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, encadreur.getNom()); ps.setString(2, encadreur.getPrenom()); ps.setString(3, encadreur.getGrade()); ps.setString(4, encadreur.getEmail()); ps.setInt(5, encadreur.getIdEncadreur());
            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Encadreur encadreur) throws SQLException {
        String sql = "DELETE FROM encadreur WHERE idEncadreur = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, encadreur.getIdEncadreur());
            return ps.executeUpdate();
        }
    }
}