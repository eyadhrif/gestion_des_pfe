package ui.encadreur;

import model.Encadreur;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EncadreurTableModel extends AbstractTableModel {
    private final String[] colonnes = {"ID", "Nom & Prénom", "Grade", "Email", "PFEs Encadrés"};
    private List<Encadreur> listeEncadreurs = new ArrayList<>();

    public void setListeEncadreurs(List<Encadreur> liste) {
        this.listeEncadreurs = liste != null ? liste : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public Encadreur getEncadreurAt(int row) {
        if (row >= 0 && row < listeEncadreurs.size()) return listeEncadreurs.get(row);
        return null;
    }

    @Override
    public int getRowCount() { return listeEncadreurs.size(); }

    @Override
    public int getColumnCount() { return colonnes.length; }

    @Override
    public String getColumnName(int column) { return colonnes[column]; }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Integer.class : String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Encadreur e = listeEncadreurs.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getIdEncadreur();
            case 1 -> e.getNom() + " " + e.getPrenom();
            case 2 -> e.getGrade();
            case 3 -> e.getEmail();
            case 4 -> "Voir détails"; // Placeholder for count
            default -> null;
        };
    }
}