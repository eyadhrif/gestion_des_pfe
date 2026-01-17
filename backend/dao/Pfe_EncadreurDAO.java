/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package backend.dao;


import model.PfeEncadreur;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ASUS
 */
public interface Pfe_EncadreurDAO extends DAO<PfeEncadreur>{
    List<PfeEncadreur> getByPfe(int idPfe) throws SQLException;
    List<PfeEncadreur> getByEncadreur(int idEncadreur) throws SQLException;
}
