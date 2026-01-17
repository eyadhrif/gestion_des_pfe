package ui.components;

import java.awt.*;
import javax.swing.*;
import ui.utils.ColorPalette;

public class RoundedPanel extends JPanel {
    private int radius;

    public RoundedPanel() {
        this(12);
    }

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
        setBackground(ColorPalette.CARD_BG);
    }

    public RoundedPanel(int radius, Color bgColor) {
        this.radius = radius;
        setOpaque(false);
        setBackground(bgColor);
    }

    public RoundedPanel(LayoutManager layout) {
        this(12, ColorPalette.CARD_BG, layout);
    }

    public RoundedPanel(LayoutManager layout, Color bgColor) {
        this(12, bgColor, layout);
    }

    public RoundedPanel(int radius, Color bgColor, LayoutManager layout) {
        this.radius = radius;
        setOpaque(false);
        setBackground(bgColor);
        setLayout(layout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
    }
}