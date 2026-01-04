package ui.soutenance;

import model.Soutenance;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class SoutenanceTableModel extends AbstractTableModel {
    private final String[] colonnes = {"ID", "Date", "Salle", "Projet PFE", "Note", "Statut"};
    private List<Soutenance> soutenances = new ArrayList<>();

    public void setSoutenances(List<Soutenance> soutenances) {
        this.soutenances = soutenances != null ? soutenances : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public List<Soutenance> getSoutenances() {
        return soutenances;
    }
    
    public Soutenance getSoutenanceAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < soutenances.size()) {
            return soutenances.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() { return soutenances.size(); }

    @Override
    public int getColumnCount() { return colonnes.length; }

    @Override
    public String getColumnName(int col) { return colonnes[col]; }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            case 1 -> LocalDate.class;
            case 4 -> Double.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int col) {
        Soutenance s = soutenances.get(row);
        return switch (col) {
            case 0 -> s.getIdsoutenance();
            case 1 -> s.getDate();
            case 2 -> s.getSalle();
            case 3 -> s.getPfe() != null ? s.getPfe().getTitre() : "Non assigné";
            case 4 -> s.getNote();
            case 5 -> getStatus(s);
            default -> null;
        };
    }
    
    private String getStatus(Soutenance s) {
        if (s.getDate().isBefore(LocalDate.now()) && s.getNote() > 0) return "Terminé";
        if (s.getDate().isBefore(LocalDate.now())) return "En attente de note";
        return "Programmé";
    }
}