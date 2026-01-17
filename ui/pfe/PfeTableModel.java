package ui.pfe;

import model.Pfe;
import model.Encadreur;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PfeTableModel extends AbstractTableModel {
    private final String[] colonnes = {"ID", "Titre", "Étudiant", "Encadreur(s)", "État", "Soutenance"};
    private List<Pfe> pfes = new ArrayList<>();

    public void setPfes(List<Pfe> pfes) {
        this.pfes = pfes;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() { return pfes.size(); }

    @Override
    public int getColumnCount() { return colonnes.length; }

    @Override
    public String getColumnName(int col) { return colonnes[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Pfe p = pfes.get(row);
        return switch (col) {
            case 0 -> p.getIdpfe();
            case 1 -> p.getTitre();
            case 2 -> p.getEtudiant() != null ? p.getEtudiant().getNom() + " " + p.getEtudiant().getPrenom() : "N/A";
            case 3 -> formatEncadreurs(p.getEncadreur());
            case 4 -> p.getEtat();
            case 5 -> p.getDateSoutenance() != null ? p.getDateSoutenance().toString() : "À définir";
            default -> null;
        };
    }

    public Pfe getAt(int row) { return pfes.get(row); }

    private String formatEncadreurs(List<Encadreur> list) {
        if (list == null || list.isEmpty()) return "Aucun";
        return list.stream()
                   .map(Encadreur::getNom)
                   .collect(Collectors.joining(", "));
    }
}