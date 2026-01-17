package ui.dashboard;

import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatsCard extends JPanel {

    private String title;
    private int value;
    private Color accentColor;
    private String icon;
    private int changePercent;

    public StatsCard(String title, int value, Color accentColor, String icon, int changePercent, boolean isPositive) {
        this.title = title;
        this.value = value;
        this.accentColor = accentColor;
        this.icon = icon;
        this.changePercent = changePercent;

        // Configuration du Panel
        setOpaque(false); // Indispensable pour voir les arrondis personnalisés
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 25, 20, 20)); // Marge interne
        setPreferredSize(new Dimension(260, 140));

        // --- PARTIE HAUTE (Titre + Icône) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ColorPalette.TEXT_SECONDARY);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(iconLabel, BorderLayout.EAST);

        // --- PARTIE CENTRALE (Valeur) ---
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(ColorPalette.TEXT_PRIMARY);

        // --- PARTIE BASSE (Evolution) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footerPanel.setOpaque(false);

        if (changePercent > 0) {
            String trend = isPositive ? "↑ " : "↓ ";
            JLabel changeLabel = new JLabel(trend + changePercent + "% depuis le mois dernier");
            changeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            changeLabel.setForeground(isPositive ? ColorPalette.SUCCESS_GREEN : Color.RED);
            footerPanel.add(changeLabel);
        }

        // Assemblage
        add(headerPanel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Dessiner le fond blanc arrondi
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        // 2. Dessiner l'accent de couleur sur le bord GAUCHE (ton idée originale)
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 6, getHeight(), 15, 15);
        // On rectifie pour que seul le bord gauche soit arrondi
        g2.fillRect(3, 0, 3, getHeight()); 

        // 3. Dessiner une bordure subtile
        g2.setColor(ColorPalette.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

        g2.dispose();
        super.paintComponent(g);
    }
}