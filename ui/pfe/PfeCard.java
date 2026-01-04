package ui.pfe;

import model.Pfe;
import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PfeCard extends RoundedPanel {
    public PfeCard(Pfe pfe) {
        super(15, Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(300, 150));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(pfe.getTitre());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(ColorPalette.PRIMARY_BLUE);

        JTextArea descLabel = new JTextArea(pfe.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setLineWrap(true);
        descLabel.setWrapStyleWord(true);
        descLabel.setEditable(false);
        descLabel.setOpaque(false);
        descLabel.setForeground(ColorPalette.TEXT_SECONDARY);

        JLabel statusLabel = new JLabel(pfe.getEtat());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(ColorPalette.SUCCESS_GREEN);

        add(titleLabel, BorderLayout.NORTH);
        add(descLabel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
}