package ui.reports;

import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;

public class ChartsReportsPanel extends RoundedPanel {
    private String chartTitle;

    public ChartsReportsPanel(String chartTitle) {
        super(20, Color.WHITE);
        this.chartTitle = chartTitle;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel(chartTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblTitle, BorderLayout.NORTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Ici tu pourras dessiner tes graphiques avec Graphics2D
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(ColorPalette.BORDER_COLOR);
        g2.drawString("Graphique de " + chartTitle, 50, 100);
    }
}