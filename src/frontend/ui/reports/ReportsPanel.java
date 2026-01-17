package ui.reports;

import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportsPanel extends JPanel {

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.CONTENT_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Rapports et Statistiques");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ColorPalette.TEXT_PRIMARY);
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Grid of charts
        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setBackground(ColorPalette.CONTENT_BG);
        grid.setOpaque(false);
        
        grid.add(new ChartsReportsPanel("Taux de Réussite"));
        grid.add(new ChartsReportsPanel("Répartition par Encadreur"));
        grid.add(new ChartsReportsPanel("Soutenances par Mois"));
        grid.add(new ChartsReportsPanel("Evolution des Inscriptions"));

        add(grid, BorderLayout.CENTER);
    }
}