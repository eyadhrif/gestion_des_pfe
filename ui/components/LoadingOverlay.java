package ui.components;

import javax.swing.*;
import java.awt.*;

public class LoadingOverlay extends JPanel {
    public LoadingOverlay() {
        setOpaque(false);
        setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Voile semi-transparent
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void showLoading(String message) {
        removeAll();
        JPanel loader = new JPanel(new BorderLayout(10, 10));
        loader.setOpaque(false);
        
        JProgressBar spinner = new JProgressBar();
        spinner.setIndeterminate(true);
        
        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        loader.add(spinner, BorderLayout.CENTER);
        loader.add(lbl, BorderLayout.SOUTH);
        
        add(loader);
        revalidate();
    }
}