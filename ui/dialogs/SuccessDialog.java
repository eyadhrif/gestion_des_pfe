package ui.dialogs;

import ui.components.ModernButton;
import ui.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;

/**
 * Standardized success dialog for displaying success messages after operations
 */
public class SuccessDialog extends JDialog {
    
    public SuccessDialog(Frame parent, String title, String message) {
        super(parent, title, true);
        setLayout(new BorderLayout(15, 15));
        
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        JLabel icon = new JLabel("✓");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 36));
        icon.setForeground(ColorPalette.SUCCESS_GREEN);
        
        JLabel msg = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        content.add(icon, BorderLayout.WEST);
        content.add(msg, BorderLayout.CENTER);

        ModernButton btnOk = new ModernButton("OK", ColorPalette.SUCCESS_GREEN);
        btnOk.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnOk);
        
        add(content, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(350, 150));
        setLocationRelativeTo(parent);
    }
    
    /**
     * Convenience method to show a success dialog
     */
    public static void show(Component parent, String title, String message) {
        Frame frame = parent instanceof Frame ? (Frame) parent : 
                      (Frame) SwingUtilities.getWindowAncestor(parent);
        new SuccessDialog(frame, title, message).setVisible(true);
    }
}
