package ui.main;

import ui.utils.ColorPalette;
import ui.utils.FontPalette;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TopBar extends JPanel {
    private JLabel titleLabel;
    private JTextField searchField;
    private final List<SearchListener> listeners = new ArrayList<>();
    private JPanel rightPanel; // Controls panel

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
        titleLabel.setFont(FontPalette.H2);
        titleLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        leftPanel.add(titleLabel);

        // Right panel - Actions
        rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setBackground(ColorPalette.CARD_BG);
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));

        // Global Search field (French)
        searchField = new JTextField("🔍 Rechercher...");
        searchField.setFont(FontPalette.BODY_SMALL);
        searchField.setForeground(ColorPalette.TEXT_TERTIARY);
        searchField.setBackground(ColorPalette.CONTENT_BG);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        searchField.setPreferredSize(new Dimension(260, 32));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("🔍 Rechercher...")) {
                    searchField.setText("");
                    searchField.setForeground(ColorPalette.TEXT_PRIMARY);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Rechercher...");
                    searchField.setForeground(ColorPalette.TEXT_TERTIARY);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { notifySearchChanged(); }
            public void removeUpdate(DocumentEvent e) { notifySearchChanged(); }
            public void changedUpdate(DocumentEvent e) { notifySearchChanged(); }
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

    /**
     * Shows or hides the search bar and action buttons.
     */
    public void setControlsVisible(boolean visible) {
        if (rightPanel != null) {
            rightPanel.setVisible(visible);
        }
    }

    public String getSearchText() {
        String t = searchField.getText();
        if ("🔍 Rechercher...".equals(t)) return "";
        return t == null ? "" : t.trim();
    }

    public void addSearchListener(SearchListener l) { listeners.add(l); }
    public void removeSearchListener(SearchListener l) { listeners.remove(l); }

    private void notifySearchChanged() {
        String q = getSearchText();
        for (SearchListener l : listeners) l.onSearchChanged(q);
    }

    public interface SearchListener {
        void onSearchChanged(String query);
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
