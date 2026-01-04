package ui.main;

import ui.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sidebar extends JPanel {

    private MainFrame mainFrame;
    private JButton activeButton;

    public Sidebar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initSidebar();
    }

    private void initSidebar() {
        setPreferredSize(new Dimension(240, 0)); // Fixed width
        setBackground(ColorPalette.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0)); // No border

        // Header
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setBackground(ColorPalette.SIDEBAR_BG);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel appLogo = new JLabel("PFE MANAGER");
        appLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appLogo.setForeground(Color.WHITE);
        logoPanel.add(appLogo);
        add(logoPanel);

        add(Box.createVerticalStrut(10));

        // Navigation
        addNavButton("TABLEAU DE BORD", "DASHBOARD");
        addNavButton("GESTION ETUDIANTS", "STUDENTS");
        addNavButton("GESTION PFES", "PFE");
        addNavButton("ENCADREURS", "ENCADREURS");
        addNavButton("SOUTENANCES", "SOUTENANCES");
        addNavButton("RAPPORTS", "REPORTS");

        add(Box.createVerticalGlue());

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(ColorPalette.SIDEBAR_BG);
        footerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel footerText = new JLabel("Version 1.0.0");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerText.setForeground(Color.GRAY);
        footerPanel.add(footerText);
        add(footerPanel);
        add(Box.createVerticalStrut(10));
    }

    private void addNavButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setForeground(ColorPalette.SIDEBAR_TEXT);
        btn.setBackground(ColorPalette.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0)); // Left padding only
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(new Color(40, 40, 40));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(ColorPalette.SIDEBAR_BG);
                }
            }
        });

        mainFrame.registerNavButton(panelName, btn);
        add(btn);
        
        // Default active
        if (panelName.equals("DASHBOARD")) {
            setActiveButton(btn);
        }
    }

    public void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(ColorPalette.SIDEBAR_BG);
            activeButton.setForeground(ColorPalette.SIDEBAR_TEXT);
        }
        activeButton = btn;
        activeButton.setBackground(ColorPalette.SIDEBAR_ACTIVE);
        activeButton.setForeground(Color.WHITE);
    }
}
