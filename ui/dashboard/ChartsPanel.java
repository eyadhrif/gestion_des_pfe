package ui.dashboard;

import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ChartsPanel extends RoundedPanel {

    public ChartsPanel() {
        super(15, Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Titre et Légende
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Statistiques des Projets");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ColorPalette.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Répartition par état d'avancement");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);

        topPanel.add(titleGroup, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Zone de dessin du graphique
        JPanel chartCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSimpleBarChart(g);
            }
        };
        chartCanvas.setOpaque(false);
        add(chartCanvas, BorderLayout.CENTER);
    }

    private void drawSimpleBarChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 50;
        int height = getHeight() - 100;
        int barWidth = 40;
        int gap = 60;
        
        // Données fictives : [Valeur, Couleur, Label]
        Object[][] data = {
            {70, ColorPalette.PRIMARY_BLUE, "En cours"},
            {40, ColorPalette.WARNING_AMBER, "Attente"},
            {90, ColorPalette.SUCCESS_GREEN, "Terminés"}
        };

        int x = 30;
        for (Object[] d : data) {
            int val = (int) d[0];
            Color col = (Color) d[1];
            String label = (String) d[2];

            // Calcul de la hauteur de la barre
            int barHeight = (val * height) / 100;

            // Dessin de la barre avec bords arrondis en haut
            g2.setColor(col);
            g2.fill(new RoundRectangle2D.Float(x, height - barHeight + 40, barWidth, barHeight, 10, 10));

            // Label en dessous
            g2.setColor(ColorPalette.TEXT_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(label, x - 5, height + 60);
            
            // Pourcentage au dessus
            g2.setColor(ColorPalette.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(val + "%", x + 5, height - barHeight + 30);

            x += barWidth + gap;
        }
    }
}