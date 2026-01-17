/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types; // Import nécessaire pour setNull
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Pfe;
import util.DBConnection;

/**
 *
 * @author ASUS
 */
public class PfeDAOImpl implements PfeDAO {

    @Override
    public Pfe get(int id) throws SQLException {
        Pfe pfe = null;
        String sql = "SELECT idpfe, titre, description, etat, dateSoutenance, idetudiant FROM pfe WHERE idpfe = ?";

        // Utilisation de try-with-resources pour fermer la connexion automatiquement
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Note: Le DateSoutenance peut être null en base, il faut gérer ce cas
                    Date dateSql = rs.getDate("dateSoutenance");
                    java.time.LocalDate dateLoc = (dateSql != null) ? dateSql.toLocalDate() : null;

                    pfe = new Pfe(
                            rs.getInt("idpfe"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getString("etat"),
                            dateLoc,
                            null, // Etudiant est null ici, à charger si besoin via EtudiantDAO
                            null  // Encadrant
                    );
                }
            }
        }
        return pfe;
    }

    @Override
    public List<Pfe> getAll() throws SQLException {
        String sql = "SELECT idpfe, titre, description, etat, dateSoutenance, idetudiant FROM pfe";
        List<Pfe> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date dateSql = rs.getDate("dateSoutenance");
                java.time.LocalDate dateLoc = (dateSql != null) ? dateSql.toLocalDate() : null;

                list.add(new Pfe(
                        rs.getInt("idpfe"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("etat"),
                        dateLoc,
                        null,
                        null));
            }
        }
        return list;
    }

    @Override
    public int insert(Pfe pfe) throws SQLException {
        // Validation simple
        if (pfe.getEtudiant() == null) {
            // On peut autoriser un PFE sans étudiant au début, sinon décommentez l'erreur
            // throw new SQLException("PFE must have an Etudiant");
        }

        String sql = "INSERT INTO pfe (titre, description, etat, dateSoutenance, idetudiant) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pfe.getTitre());
            ps.setString(2, pfe.getDescription());
            ps.setString(3, pfe.getEtat());
            
            if (pfe.getDateSoutenance() != null) {
                ps.setDate(4, Date.valueOf(pfe.getDateSoutenance()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (pfe.getEtudiant() != null) {
                ps.setInt(5, pfe.getEtudiant().getIdEtudiant());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Pfe pfe) throws SQLException {
        String sql = "UPDATE pfe SET titre = ?, description = ?, etat = ?, dateSoutenance = ? WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pfe.getTitre());
            ps.setString(2, pfe.getDescription());
            ps.setString(3, pfe.getEtat());
            
            if (pfe.getDateSoutenance() != null) {
                ps.setDate(4, Date.valueOf(pfe.getDateSoutenance()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            ps.setInt(5, pfe.getIdpfe());

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Pfe pfe) throws SQLException {
        String sql = "DELETE FROM pfe WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pfe.getIdpfe());
            return ps.executeUpdate();
        }
    }

    @Override
    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pfe";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    @Override
    public int countByEtat(String etat) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pfe WHERE etat = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }
    }

    // --- NOUVELLE MÉTHODE OBLIGATOIRE POUR LE DASHBOARD ---
    @Override
    public List<Map<String, Object>> getLateProjects() throws SQLException {
        // Cette requête joint PFE et ETUDIANT pour récupérer le nom
        // On cherche les PFE dont la date de soutenance est passée mais qui ne sont pas "Soutenue"
        String sql = "SELECT e.nom, e.prenom, p.titre, DATEDIFF(CURDATE(), p.dateSoutenance) as diff " +
                     "FROM pfe p " +
                     "JOIN etudiant e ON p.idetudiant = e.idetudiant " +
                     "WHERE p.dateSoutenance < CURDATE() AND p.etat NOT IN ('Soutenue', 'Validé') " +
                     "LIMIT 5";

        List<Map<String, Object>> list = new ArrayList<>();
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                
                // 1. Nom de l'étudiant
                String fullName = rs.getString("nom") + " " + rs.getString("prenom");
                row.put("student", fullName);
                
                // 2. Titre du projet (raccourci si trop long)
                String titre = rs.getString("titre");
                if (titre != null && titre.length() > 20) {
                    titre = titre.substring(0, 17) + "...";
                }
                row.put("project", titre);
                
                // 3. Calcul réel du retard
                int retard = rs.getInt("diff"); 
                row.put("delay", retard + " jrs");
                
                list.add(row);
            }
        }
        return list;
    }

    @Override
    public Map<String, Integer> getPfesByMonth() throws SQLException {
        Map<String, Integer> map = new java.util.LinkedHashMap<>();
        // On groupe par mois (Année-Mois) sur la date de soutenance
        // Seuls les PFEs avec une date définie sont pris en compte
        String sql = "SELECT DATE_FORMAT(dateSoutenance, '%Y-%m') as mois, COUNT(*) as total " +
                     "FROM pfe " +
                     "WHERE dateSoutenance IS NOT NULL " +
                     "GROUP BY mois " +
                     "ORDER BY mois DESC " +
                     "LIMIT 6";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while(rs.next()){
                map.put(rs.getString("mois"), rs.getInt("total"));
            }
        }
        return map;
    }
}
