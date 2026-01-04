package ui.dialogs;

import ui.components.ModernButton;
import ui.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;

public class ConfirmDialog extends JDialog {
    private boolean confirmed = false;

    public ConfirmDialog(Frame parent, String title, String message) {
        super(parent, title, true);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(Color.WHITE);

        JLabel lblMsg = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", SwingConstants.CENTER);
        lblMsg.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(lblMsg, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        btnPanel.setOpaque(false);

        ModernButton btnYes = new ModernButton("Confirmer", ColorPalette.PRIMARY_BLUE);
        ModernButton btnNo = new ModernButton("Annuler", Color.GRAY);

        btnYes.addActionListener(e -> { confirmed = true; dispose(); });
        btnNo.addActionListener(e -> dispose());

        btnPanel.add(btnNo);
        btnPanel.add(btnYes);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() { return confirmed; }
}