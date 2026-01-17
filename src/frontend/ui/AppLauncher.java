package ui;

import ui.main.MainFrame;
import ui.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;

public class AppLauncher {
    private static void applyGlobalUITheme() {
        // Fonts and default UI settings
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Panel.background", ColorPalette.CONTENT_BG);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 13));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Native LAF with tweaks
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                applyGlobalUITheme();

                // Show login first
                ui.login.LoginDialog login = new ui.login.LoginDialog(null);
                login.setLocationRelativeTo(null);
                login.setVisible(true);
                if (login.isSucceeded()) {
                    MainFrame frame = new MainFrame();
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } else {
                    System.exit(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erreur lors du lancement de l'application : " + e.getMessage(),
                        "Erreur Critique",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
