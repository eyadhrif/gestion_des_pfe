/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;

import model.Etudiant;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class EtudiantDAOImpl implements EtudiantDAO {

    @Override
    public Etudiant get(int id) throws SQLException {
        String sql = "SELECT idEtudiant, nom, prenom, email, classe FROM Etudiant WHERE idEtudiant = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Etudiant(
                        rs.getInt("idEtudiant"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("classe")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Etudiant> getAll() throws SQLException {
        String sql = "SELECT idEtudiant, nom, prenom, email, classe FROM Etudiant";
        List<Etudiant> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Etudiant(
                    rs.getInt("idEtudiant"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("classe")
                ));
            }
        }
        return list;
    }

    @Override
    public int insert(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO etudiant (nom, prenom, email, classe) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getClasse());
            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE etudiant SET nom = ?, prenom = ?, email = ?, classe = ? WHERE idEtudiant = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getClasse());
            ps.setInt(5, etudiant.getIdEtudiant());
            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Etudiant etudiant) throws SQLException {
        String sql = "DELETE FROM etudiant WHERE idEtudiant = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, etudiant.getIdEtudiant());
            return ps.executeUpdate();
        }
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM etudiant WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM etudiant";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    @Override
    public Map<String, Integer> countByClasse() throws SQLException {
        String sql = "SELECT classe, COUNT(*) FROM etudiant GROUP BY classe";
        Map<String, Integer> stats = new HashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString(1), rs.getInt(2));
            }
        }
        return stats;
    }
}

