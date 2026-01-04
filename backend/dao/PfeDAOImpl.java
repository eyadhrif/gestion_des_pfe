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
import java.time.LocalDate;  
import java.util.ArrayList;
import java.util.List;
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

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            pfe = new Pfe(
                    rs.getInt("idpfe"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("etat"),
                    rs.getDate("dateSoutenance").toLocalDate(),
                    null,
                    null);
        }

        return pfe;
    }

    @Override
    public List<Pfe> getAll() throws SQLException {
        String sql = "SELECT idpfe, titre, description, etat, dateSoutenance, idetudiant FROM pfe";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<Pfe> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new Pfe(
                    rs.getInt("idpfe"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("etat"),
                    rs.getDate("dateSoutenance").toLocalDate(),
                    null,
                    null));
        }

        return list;
    }

    @Override
    public int insert(Pfe pfe) throws SQLException {
        if (pfe.getEtudiant() == null) {
            throw new SQLException("PFE must have an Etudiant");
        }

        String sql = "INSERT INTO pfe (titre, description, etat, dateSoutenance, idetudiant) VALUES (?, ?, ?, ?, ?)";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, pfe.getTitre());
        ps.setString(2, pfe.getDescription());
        ps.setString(3, pfe.getEtat());
        ps.setDate(4, Date.valueOf(pfe.getDateSoutenance()));
        ps.setInt(5, pfe.getEtudiant().getIdEtudiant());

        return ps.executeUpdate();
    }

    @Override
    public int update(Pfe pfe) throws SQLException {
        String sql = "UPDATE pfe SET titre = ?, description = ?, etat = ?, dateSoutenance = ? WHERE idpfe = ?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, pfe.getTitre());
        ps.setString(2, pfe.getDescription());
        ps.setString(3, pfe.getEtat());
        ps.setDate(4, Date.valueOf(pfe.getDateSoutenance()));
        ps.setInt(5, pfe.getIdpfe());

        return ps.executeUpdate();
    }

    @Override
    public int delete(Pfe pfe) throws SQLException {
        String sql = "DELETE FROM pfe WHERE idpfe = ?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, pfe.getIdpfe());

        return ps.executeUpdate();
    }
}

