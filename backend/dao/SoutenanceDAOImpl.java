/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Soutenance;
import util.DBConnection;
import java.time.LocalDate;

/**
 *
 * @author ASUS
 */
public class SoutenanceDAOImpl implements SoutenanceDAO {
     @Override
    public Soutenance get(int id) throws SQLException {
        Soutenance soutenance = null;

        String sql = "SELECT idsoutenance, date, salle, note FROM soutenance WHERE idsoutenance = ?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int idsoutenance = rs.getInt("idsoutenance");
            LocalDate date = rs.getDate("date").toLocalDate();
            String salle = rs.getString("salle");
            double note = rs.getDouble("note");

            soutenance = new Soutenance(idsoutenance, date, salle, note, null);
        }

        return soutenance;
    }

    @Override
    public List<Soutenance> getAll() throws SQLException {
        String sql = "SELECT idsoutenance, date, salle, note FROM soutenance";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<Soutenance> list = new ArrayList<>();

        while (rs.next()) {
            int idsoutenance = rs.getInt("idsoutenance");
            LocalDate date = rs.getDate("date").toLocalDate();
            String salle = rs.getString("salle");
            double note = rs.getDouble("note");

            list.add(new Soutenance(idsoutenance, date, salle, note, null));
        }

        return list;
    }

    @Override
    public int insert(Soutenance soutenance) throws SQLException {
        String sql = "INSERT INTO soutenance (date, salle, note, idpfe) VALUES (?, ?, ?,?)";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setDate(1, java.sql.Date.valueOf(soutenance.getDate()));
        ps.setString(2, soutenance.getSalle());
        ps.setDouble(3, soutenance.getNote());
        if (soutenance.getPfe() != null)
            ps.setInt(4, soutenance.getPfe().getIdpfe());
        else
            ps.setNull(4, java.sql.Types.INTEGER);

        return ps.executeUpdate();
    }

    @Override
    public int update(Soutenance soutenance) throws SQLException {
        String sql = "UPDATE soutenance SET date = ?, salle = ?, note = ? WHERE idsoutenance = ?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setDate(1, java.sql.Date.valueOf(soutenance.getDate()));
        ps.setString(2, soutenance.getSalle());
        ps.setDouble(3, soutenance.getNote());
        ps.setInt(4, soutenance.getIdsoutenance());

        return ps.executeUpdate();
    }

    @Override
    public int delete(Soutenance soutenance) throws SQLException {
        String sql = "DELETE FROM soutenance WHERE idsoutenance = ?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, soutenance.getIdsoutenance());
        
        return ps.executeUpdate();
    }
    
}
