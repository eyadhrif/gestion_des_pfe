package ui.etudiant;

import model.Etudiant;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for Etudiant entities.
 * Provides data for JTable with proper column definitions.
 */
public class EtudiantTableModel extends AbstractTableModel {
    private final String[] colonnes = {"ID", "Nom", "Prénom", "Email", "Classe"};
    private List<Etudiant> listeEtudiants = new ArrayList<>();

    public void setListeEtudiants(List<Etudiant> liste) {
        this.listeEtudiants = liste != null ? liste : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public List<Etudiant> getListeEtudiants() {
        return listeEtudiants;
    }
    
    public Etudiant getEtudiantAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < listeEtudiants.size()) {
            return listeEtudiants.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() { 
        return listeEtudiants.size(); 
    }

    @Override
    public int getColumnCount() { 
        return colonnes.length; 
    }

    @Override
    public String getColumnName(int column) { 
        return colonnes[column]; 
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= listeEtudiants.size()) {
            return null;
        }
        
        Etudiant e = listeEtudiants.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getIdEtudiant();
            case 1 -> e.getNom();
            case 2 -> e.getPrenom();
            case 3 -> e.getEmail();
            case 4 -> e.getClasse();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) { 
        return false; // Editing is done through dialogs
    }
    
    /**
     * Clear all data from the table
     */
    public void clear() {
        listeEtudiants.clear();
        fireTableDataChanged();
    }
    
    /**
     * Add a single etudiant to the table
     */
    public void addEtudiant(Etudiant etudiant) {
        listeEtudiants.add(etudiant);
        fireTableRowsInserted(listeEtudiants.size() - 1, listeEtudiants.size() - 1);
    }
    
    /**
     * Remove a single etudiant from the table
     */
    public void removeEtudiant(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < listeEtudiants.size()) {
            listeEtudiants.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    /**
     * Update a single etudiant in the table
     */
    public void updateEtudiant(int rowIndex, Etudiant etudiant) {
        if (rowIndex >= 0 && rowIndex < listeEtudiants.size()) {
            listeEtudiants.set(rowIndex, etudiant);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
}