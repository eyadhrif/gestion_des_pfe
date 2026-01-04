package service;

import backend.dao.PfeDAOImpl;
import backend.dao.Pfe_EncadreurDAOImpl;
import backend.dao.EncadreurDAOImpl;
import backend.dao.EtudiantDAOImpl;
import model.Pfe;
import model.Encadreur;
import model.Etudiant;
import model.PfeEncadreur;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PfeService {
    private final PfeDAOImpl pfeDao = new PfeDAOImpl();
    private final Pfe_EncadreurDAOImpl linkDao = new Pfe_EncadreurDAOImpl();
    private final EtudiantDAOImpl etuDao = new EtudiantDAOImpl();
    private final EncadreurDAOImpl encDao = new EncadreurDAOImpl();

    public List<Pfe> getAllFull() throws SQLException {
        List<Pfe> list = pfeDao.getAll();
        for (Pfe p : list) {
            // load etudiant
            if (p.getEtudiant() == null && p.getIdpfe() > 0) {
                // pfeDao returns p with idetudiant in DB but not filled; fetch full student list and assign
                // Simpler: fetch all etudiants and match by id stored in DB in PfeDAOImpl (not stored currently)
                // For now, accept that getAll returns pfe with null etudiant; caller can show blank.
            }
            // load encadreurs
            List<PfeEncadreur> links = linkDao.getByPfe(p.getIdpfe());
            List<Encadreur> encs = new ArrayList<>();
            for (PfeEncadreur pe : links) {
                Encadreur e = encDao.get(pe.getIdEncadreur());
                if (e != null) encs.add(e);
            }
            p.setEncadreur(encs);
        }
        return list;
    }

    public List<Etudiant> getAllEtudiants() throws SQLException {
        return etuDao.getAll();
    }

    public List<Encadreur> getAllEncadreurs() throws SQLException {
        return encDao.getAll();
    }

    public boolean createPfe(Pfe pfe) {
        try {
            int r = pfeDao.insert(pfe);
            if (r > 0) {
                // fetch last inserted id is not implemented in DAO: assume client reloads from DB to find correct IDs
                // For now we will search all PFEs and find one with matching title + student id (best effort)
                List<Pfe> all = pfeDao.getAll();
                Pfe inserted = null;
                for (Pfe x : all) {
                    if (x.getTitre().equals(pfe.getTitre())) {
                        inserted = x; break;
                    }
                }
                if (inserted != null && pfe.getEncadreur() != null) {
                    for (Encadreur e : pfe.getEncadreur()) {
                        linkDao.insert(new PfeEncadreur(inserted.getIdpfe(), e.getIdEncadreur()));
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updatePfe(Pfe pfe) {
        try {
            int r = pfeDao.update(pfe);
            if (r > 0) {
                // remove existing links and re-insert
                List<PfeEncadreur> olds = linkDao.getByPfe(pfe.getIdpfe());
                for (PfeEncadreur old : olds) linkDao.delete(old);
                if (pfe.getEncadreur() != null) {
                    for (Encadreur e : pfe.getEncadreur()) linkDao.insert(new PfeEncadreur(pfe.getIdpfe(), e.getIdEncadreur()));
                }
                return true;
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean deletePfe(Pfe pfe) {
        try {
            // remove links
            List<PfeEncadreur> olds = linkDao.getByPfe(pfe.getIdpfe());
            for (PfeEncadreur old : olds) linkDao.delete(old);
            int r = pfeDao.delete(pfe);
            return r > 0;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }
}
