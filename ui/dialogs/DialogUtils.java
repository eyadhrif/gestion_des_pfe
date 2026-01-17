package ui.dialogs;

import ui.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;

/**
 * Utility class for standardized dialog operations throughout the application.
 * Provides consistent error handling, confirmations, and success notifications.
 */
public class DialogUtils {
    
    /**
     * Show a standardized error message dialog
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show a standardized error message dialog with custom title
     */
    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show a standardized success message dialog
     */
    public static void showSuccess(Component parent, String message) {
        SuccessDialog.show(parent, "Succès", message);
    }
    
    /**
     * Show a standardized success message dialog with custom title
     */
    public static void showSuccess(Component parent, String title, String message) {
        SuccessDialog.show(parent, title, message);
    }
    
    /**
     * Show a standardized info message dialog
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Show a standardized warning message dialog
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Avertissement",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Show a confirmation dialog for delete operations
     * @return true if user confirmed, false otherwise
     */
    public static boolean confirmDelete(Component parent, String itemName) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            "Êtes-vous sûr de vouloir supprimer \"" + itemName + "\" ?\n\nCette action est irréversible.",
            "Confirmer la suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Show a generic confirmation dialog
     * @return true if user confirmed, false otherwise
     */
    public static boolean confirm(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Convert a technical exception to a user-friendly message
     */
    public static String getUserFriendlyError(Exception e) {
        String message = e.getMessage();
        if (message == null) message = e.getClass().getSimpleName();
        
        // Check for common database errors
        if (message.contains("Duplicate entry")) {
            if (message.contains("email")) {
                return "Cette adresse email est déjà utilisée.";
            }
            return "Cette entrée existe déjà dans la base de données.";
        }
        if (message.contains("foreign key constraint")) {
            return "Impossible de supprimer cet élément car il est lié à d'autres données.";
        }
        if (message.contains("Connection refused") || message.contains("Communications link failure")) {
            return "Impossible de se connecter à la base de données. Vérifiez que MySQL est démarré.";
        }
        if (message.contains("Access denied")) {
            return "Accès refusé à la base de données. Vérifiez vos identifiants.";
        }
        if (message.contains("Unknown database")) {
            return "La base de données n'existe pas. Veuillez la créer d'abord.";
        }
        
        // Return a generic message for unknown errors
        return "Une erreur s'est produite: " + message;
    }
    
    /**
     * Show a database error with user-friendly message
     */
    public static void showDatabaseError(Component parent, Exception e) {
        // Log the technical error
        System.err.println("Database error: " + e.getMessage());
        e.printStackTrace();
        
        // Show user-friendly message
        showError(parent, getUserFriendlyError(e));
    }
}
