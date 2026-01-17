package ui.components;

import ui.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernProgressBar extends JPanel {

    private double progress;
    private Color progressColor;
    private String label;
    private boolean showPercentage;

    public ModernProgressBar(double progress, Color color, String label, boolean showPercentage) {
        this.progress = Math.min(100, Math.max(0, progress));
        this.progressColor = color;
        this.label = label;
        this.showPercentage = showPercentage;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 24));
    }

    public ModernProgressBar(double progress, Color color) {
        this(progress, color, "", false);
    }

    public void setProgress(double progress) {
        this.progress = Math.min(100, Math.max(0, progress));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arcSize = height;

        // Background
        g2.setColor(ColorPalette.BORDER_COLOR);
        g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

        // Progress
        int progressWidth = (int) ((width - 4) * (progress / 100.0));
        g2.setColor(progressColor);
        g2.fillRoundRect(2, 2, progressWidth, height - 4, arcSize - 2, arcSize - 2);

        // Text if needed
        if (showPercentage) {
            g2.setColor(ColorPalette.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String text = (int) progress + "%";
            FontMetrics fm = g2.getFontMetrics();
            int x = (width - fm.stringWidth(text)) / 2;
            int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, x, y);
        }
    }
}
