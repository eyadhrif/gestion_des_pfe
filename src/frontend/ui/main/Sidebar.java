package ui.main;

import ui.utils.ColorPalette;
import ui.utils.FontPalette;
import ui.utils.SidebarIcon;

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
        setPreferredSize(new Dimension(280, 0)); // Wider for premium feel
        setBackground(ColorPalette.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(24, 20, 24, 20)); // More breathing room

        // --- Header Section ---
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setBackground(ColorPalette.SIDEBAR_BG);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logo Icon
        JLabel logoIcon = new JLabel(new SidebarIcon("LOGO", 28, ColorPalette.PRIMARY_BLUE));
        logoIcon.setBorder(new EmptyBorder(0, 0, 0, 12));

        // App Title - Cleaner Font
        JLabel appTitle = new JLabel("Gestion PFE");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Slightly more modern size
        appTitle.setForeground(Color.WHITE);

        logoPanel.add(logoIcon);
        logoPanel.add(appTitle);

        add(logoPanel);
        add(Box.createVerticalStrut(30)); // Spacing before menu

        // --- Main Navigation ---
        // Using custom Vector Icons
        addNavButton("Tableau de bord", "DASHBOARD", "DASHBOARD");
        addNavButton("Étudiants", "STUDENTS", "STUDENTS");
        addNavButton("Projets PFE", "PFE", "PFE");
        addNavButton("Encadreurs", "ENCADREURS", "ENCADREUR");
        addNavButton("Soutenances", "SOUTENANCES", "SOUTENANCE");
        // addNavButton("Rapports", "REPORTS", "REPORTS"); // Removed as per request

        add(Box.createVerticalGlue());

        // --- Footer Section ---
        // Separator line above footer
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setForeground(new Color(45, 45, 45));
        sep.setBackground(new Color(45, 45, 45));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep);
        add(Box.createVerticalStrut(15));

        addFooterButton("Paramètres", "SETTINGS");
        addFooterButton("Aide", "HELP");
        addFooterButton("Déconnexion", "LOGOUT");
    }

    private void addNavButton(String text, String panelName, String iconType) {
        JButton btn = createSidebarButton(text, iconType, true);

        mainFrame.registerNavButton(panelName, btn);
        add(btn);
        add(Box.createVerticalStrut(8)); // Consistent gap

        if ("DASHBOARD".equals(panelName)) {
            setActiveButton(btn);
        }
    }

    private void addFooterButton(String text, String iconType) {
        JButton btn = createSidebarButton(text, iconType, false);
        btn.addActionListener(e -> System.out.println("Action: " + text));
        add(btn);
        add(Box.createVerticalStrut(8));
    }

    private JButton createSidebarButton(String text, String iconType, boolean isNav) {
        // Create Icon
        SidebarIcon icon = new SidebarIcon(iconType, 20, ColorPalette.SIDEBAR_TEXT);

        JButton btn = new JButton(text);
        btn.setIcon(icon);
        btn.setIconTextGap(16); // Gap between icon and text
        
        // Font
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Regular weight for elegance
        btn.setForeground(ColorPalette.SIDEBAR_TEXT);
        
        // Layout Properties
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Taller Hit Area
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Internal Padding
        btn.setMargin(new Insets(10, 15, 10, 15)); // Margin doesn't always work with overridden paint, handled in border
        btn.setBorder(new EmptyBorder(10, 18, 10, 10)); // Inner padding

        // Background Properties
        btn.setBackground(ColorPalette.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Custom Painting
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean isActive = (b == activeButton);

                if (isActive) {
                    // Active Pill
                    g2.setColor(new Color(37, 99, 235)); // Vibrant Blue
                    g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 12, 12); // Slightly less rounded for professional look (or keep 16)
                } else if (b.getModel().isRollover()) {
                    // Hover Effect
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 12, 12);
                }

                super.paint(g, c);
                g2.dispose();
            }
        });

        // Mouse Listeners for Text/Icon Color changes
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setForeground(Color.WHITE);
                    icon.setColor(Color.WHITE);
                }
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setForeground(ColorPalette.SIDEBAR_TEXT);
                    icon.setColor(ColorPalette.SIDEBAR_TEXT);
                }
                btn.repaint();
            }
        });

        return btn;
    }

    public void setActiveButton(JButton btn) {
        if (activeButton != null) {
            // Reset previous
            activeButton.setForeground(ColorPalette.SIDEBAR_TEXT);
            activeButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            SidebarIcon oldIcon = (SidebarIcon) activeButton.getIcon();
            if (oldIcon != null) oldIcon.setColor(ColorPalette.SIDEBAR_TEXT);
            activeButton.repaint();
        }

        activeButton = btn;

        // Set Activate
        activeButton.setForeground(Color.WHITE);
        activeButton.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Slight bold on active
        SidebarIcon newIcon = (SidebarIcon) activeButton.getIcon();
        if (newIcon != null) newIcon.setColor(Color.WHITE);
        
        activeButton.repaint();
    }
}
