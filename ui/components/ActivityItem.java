package ui.components;

import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActivityItem extends JPanel {

    public ActivityItem(String icon, String title, String description, String time, Color iconColor) {
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(5, 0, 5, 0));

        // Cercle pour l'icône
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 30)); // Version transparente
        iconLabel.setForeground(iconColor);

        // Textes (Titre et Description)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(ColorPalette.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(ColorPalette.TEXT_SECONDARY);

        textPanel.add(lblTitle);
        textPanel.add(lblDesc);

        // Temps à droite
        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTime.setForeground(Color.GRAY);

        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(lblTime, BorderLayout.EAST);
    }
}