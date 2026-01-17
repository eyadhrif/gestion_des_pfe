package ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class SidebarIcon implements Icon {
    private final String type;
    private final int size;
    private Color color;

    public SidebarIcon(String type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(color);
        
        // Scale to size (assuming base path is roughly 24x24)
        double scale = size / 24.0;
        g2.scale(scale, scale);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case "DASHBOARD":
                // 4 squares
                g2.fillRect(3, 3, 8, 8);
                g2.fillRect(13, 3, 8, 8);
                g2.fillRect(3, 13, 8, 8);
                g2.fillOval(13, 13, 8, 8); // Make one a circle for visual interest
                break;
                
            case "STUDENTS":
                // User icon
                g2.drawOval(8, 4, 8, 8); // Head
                g2.drawArc(4, 14, 16, 12, 0, 180); // Body
                break;
                
            case "PFE":
                // Folder
                Path2D folder = new Path2D.Double();
                folder.moveTo(4, 6);
                folder.lineTo(10, 6);
                folder.lineTo(12, 8);
                folder.lineTo(20, 8);
                folder.lineTo(20, 18);
                folder.lineTo(4, 18);
                folder.closePath();
                g2.draw(folder);
                // Line inside
                g2.drawLine(4, 10, 20, 10);
                break;
                
            case "ENCADREUR":
                // User with glasses/tie (Teacher) - Simple: Boxy glasses user
                g2.drawOval(8, 4, 8, 8); // Head
                g2.drawLine(8, 8, 16, 8); // Glasses bar
                g2.drawArc(4, 14, 16, 12, 0, 180); // Body
                g2.drawLine(12, 14, 12, 20); // Tie
                break;
                
            case "SOUTENANCE":
                // Calendar
                g2.drawRoundRect(5, 5, 14, 15, 3, 3);
                g2.drawLine(5, 9, 19, 9); // Header line
                g2.drawLine(9, 3, 9, 6); // Ring 1
                g2.drawLine(15, 3, 15, 6); // Ring 2
                // Checkmark inside
                // g2.drawLine(9, 14, 11, 16);
                // g2.drawLine(11, 16, 15, 12);
                break;
                
            case "REPORTS":
                // Bar chart
                g2.fillRect(5, 12, 4, 8);
                g2.fillRect(10, 8, 4, 12);
                g2.fillRect(15, 5, 4, 15);
                break;
                
            case "SETTINGS":
                // Hexagon (Gear simplified)
                Path2D gear = new Path2D.Double();
                int sides = 6;
                for (int i = 0; i < sides; i++) {
                    double angle = 2 * Math.PI * i / sides;
                    double px = 12 + 7 * Math.cos(angle);
                    double py = 12 + 7 * Math.sin(angle);
                    if (i == 0) gear.moveTo(px, py);
                    else gear.lineTo(px, py);
                }
                gear.closePath();
                g2.draw(gear);
                g2.drawOval(10, 10, 4, 4); // Center hole
                break;
                
            case "HELP":
                // Need to draw ? manually or text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.drawString("?", 8, 18);
                break;

            case "LOGOUT":
                g2.drawOval(6, 6, 12, 12);
                g2.setColor(Color.RED); // Little red accent
                g2.drawLine(12, 4, 12, 12); 
                break;

            case "LOGO":
                // Star shape for header
                Path2D star = new Path2D.Double();
                star.moveTo(12, 2);
                star.curveTo(12, 2, 14, 10, 22, 12);
                star.curveTo(22, 12, 14, 14, 12, 22);
                star.curveTo(12, 22, 10, 14, 2, 12);
                star.curveTo(2, 12, 10, 10, 12, 2);
                star.closePath();
                g2.fill(star);
                break;
        }
        
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
