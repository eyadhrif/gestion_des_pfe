package ui;

import ui.main.MainFrame;
import javax.swing.*;
import java.awt.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Show login first
                ui.login.LoginDialog login = new ui.login.LoginDialog(null);
                login.setVisible(true);
                if (login.isSucceeded()) {
                    MainFrame frame = new MainFrame();
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.setVisible(true);
                } else {
                    // Exit if not logged in
                    System.exit(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors du lancement de l'application : " + e.getMessage(), 
                    "Erreur Critique", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
