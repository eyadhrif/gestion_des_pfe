package ui.dialogs;

import ui.components.ModernButton;
import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JDialog {
    public ErrorDialog(Frame parent, String errorMsg) {
        super(parent, "Erreur", true);
        setLayout(new BorderLayout(15, 15));
        
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        JLabel icon = new JLabel("⚠️");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JLabel msg = new JLabel("<html><body style='width: 250px'>" + errorMsg + "</body></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        content.add(icon, BorderLayout.WEST);
        content.add(msg, BorderLayout.CENTER);

        ModernButton btnOk = new ModernButton("Compris", new Color(220, 53, 69)); // Rouge erreur
        btnOk.addActionListener(e -> dispose());
        
        add(content, BorderLayout.CENTER);
        add(btnOk, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }
}