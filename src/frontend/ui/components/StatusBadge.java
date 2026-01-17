package ui.components;

import ui.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;

public class StatusBadge extends JPanel {
    public StatusBadge(String text, Color baseColor) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(baseColor);
        
        add(label);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fond avec opacité réduite (15%) pour l'effet Badge
        Color bg = getForeground(); 
        g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 35));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
    }
}