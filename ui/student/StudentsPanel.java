package ui.student;

import backend.dao.EtudiantDAOImpl;
import model.Etudiant;
import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class StudentsPanel extends JPanel {
    private JTable tableEtudiants;
    private DefaultTableModel modelEtudiants;

    public StudentsPanel() {
        // Configuration du panneau
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.CONTENT_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ColorPalette.TEXT_PRIMARY);

        JButton btnAjouter = new JButton("+ Nouvel Étudiant");
        btnAjouter.setBackground(ColorPalette.PRIMARY_BLUE);
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAjouter.setFocusPainted(false);
        btnAjouter.setBorderPainted(false);
        btnAjouter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Action pour le bouton ajouter
        btnAjouter.addActionListener(e -> {
            // Future action: ouvrir StudentFormDialog
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnAjouter, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tableau dans carte arrondie
        RoundedPanel cardTableau = new RoundedPanel(12, ColorPalette.CARD_BG, new BorderLayout());
        cardTableau.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Colonnes
        String[] colonnes = {"ID", "Nom & Prénom", "Email", "Classe", "Actions"};
        modelEtudiants = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Seule la colonne actions est interactive
            }
        };

        tableEtudiants = new JTable(modelEtudiants);
        styleTableau(tableEtudiants);

        // Chargement des données
        actualiserDonnees();

        JScrollPane scrollPane = new JScrollPane(tableEtudiants);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        cardTableau.add(scrollPane, BorderLayout.CENTER);
        add(cardTableau, BorderLayout.CENTER);
    }

    private void styleTableau(JTable table) {
        table.setRowHeight(55); 
        table.setShowVerticalLines(false);
        table.setGridColor(ColorPalette.BORDER_COLOR);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(ColorPalette.PRIMARY_BLUE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(ColorPalette.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));
    }

    public void actualiserDonnees() {
        modelEtudiants.setRowCount(0); 
        EtudiantDAOImpl dao = new EtudiantDAOImpl();
        try {
            List<Etudiant> liste = dao.getAll();

            for (Etudiant e : liste) {
                Object[] ligne = {
                    e.getIdEtudiant(),
                    e.getNom() + " " + e.getPrenom(),
                    e.getEmail(),
                    e.getClasse(),
                    "⚙ Modifier | 🗑 Supprimer"
                };
                modelEtudiants.addRow(ligne);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }
}
