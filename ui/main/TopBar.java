package ui.main;

import ui.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TopBar extends JPanel {
    private JLabel titleLabel;

    public TopBar(String titleText) {
        initTopBar(titleText);
    }

    private void initTopBar(String titleText) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 70));
        setBackground(ColorPalette.CARD_BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.BORDER_COLOR));

        // Left panel - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(ColorPalette.CARD_BG);
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        leftPanel.add(titleLabel);

        // Right panel - Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setBackground(ColorPalette.CARD_BG);
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));

        // Search field placeholder
        JTextField searchField = new JTextField("🔍 Search...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setForeground(ColorPalette.TEXT_TERTIARY);
        searchField.setBackground(ColorPalette.CONTENT_BG);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("🔍 Search...")) {
                    searchField.setText("");
                    searchField.setForeground(ColorPalette.TEXT_PRIMARY);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search...");
                    searchField.setForeground(ColorPalette.TEXT_TERTIARY);
                }
            }
        });
        rightPanel.add(searchField);

        // Notification button
        JButton btnNotif = createIconButton("🔔");
        rightPanel.add(btnNotif);

        // Settings button
        JButton btnSettings = createIconButton("⚙️");
        rightPanel.add(btnSettings);

        // Profile button
        JButton btnProfile = createIconButton("👤");
        rightPanel.add(btnProfile);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    private JButton createIconButton(String icon) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 40));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(ColorPalette.CONTENT_BG);
                btn.setBorderPainted(false);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
            }
        });

        return btn;
    }
}
