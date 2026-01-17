package ui.components;

import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SearchField extends JTextField {
    public SearchField(String placeholder) {
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(ColorPalette.TEXT_TERTIARY);
        setText(placeholder);
        setBorder(new EmptyBorder(5, 35, 5, 10)); // Marge à gauche pour l'icône
        setPreferredSize(new Dimension(250, 35));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond arrondi
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
        
        // Bordure
        g2.setColor(ColorPalette.BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
        
        // Dessin de la loupe 🔍
        g2.setColor(ColorPalette.TEXT_TERTIARY);
        g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        g2.drawString("🔍", 12, getHeight() / 2 + 5);
        
        g2.dispose();
        super.paintComponent(g);
    }
}