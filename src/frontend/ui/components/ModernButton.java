package ui.components;

import javax.swing.*;
import java.awt.*;

import ui.utils.FontPalette;

public class ModernButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color pressColor;

    public ModernButton(String text, Color bgColor) {
        super(text);
        this.baseColor = bgColor;
        this.hoverColor = blend(bgColor, Color.WHITE, 0.12f);
        this.pressColor = blend(bgColor, Color.BLACK, 0.10f);

        setFont(FontPalette.BUTTON);
        setForeground(Color.WHITE);
        setBackground(baseColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 16, 10, 16));
        setPreferredSize(new Dimension(150, 40));

        // Rollover effects
        addChangeListener(e -> {
            if (getModel().isPressed()) {
                setBackground(pressColor);
            } else if (getModel().isRollover()) {
                setBackground(hoverColor);
            } else {
                setBackground(baseColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background rounded
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        // Draw text centered
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - 2;
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }

    private static Color blend(Color c1, Color c2, float ratio) {
        float ir = 1.0f - ratio;
        int r = (int) (c1.getRed() * ir + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * ir + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * ir + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }
}