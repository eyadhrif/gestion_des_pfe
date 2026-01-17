package ui.dashboard;

import ui.components.ActivityItem;
import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RecentActivityPanel extends RoundedPanel {

    public RecentActivityPanel() {
        super(15, Color.WHITE); // Utilisation de ton RoundedPanel
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // En-tête du panel
        JLabel title = new JLabel("Activité Récente");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ColorPalette.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        // Liste des activités
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        
        // Espacement après le titre
        listContainer.add(Box.createVerticalStrut(20));

        // Ajout d'exemples (Plus tard, on pourra boucler sur une liste venant de la BD)
        listContainer.add(new ActivityItem("✅", "PFE Validé", "Application de Gestion - Par Jean", "Il y a 2h", ColorPalette.SUCCESS_GREEN));
        listContainer.add(Box.createVerticalStrut(15));
        listContainer.add(new ActivityItem("📝", "Nouveau PFE", "Système de vote - Par Inès", "Il y a 5h", ColorPalette.PRIMARY_BLUE));
        listContainer.add(Box.createVerticalStrut(15));
        listContainer.add(new ActivityItem("🎤", "Soutenance", "Salle B204 programmée", "Hier", ColorPalette.WARNING_AMBER));
        listContainer.add(Box.createVerticalStrut(15));
        listContainer.add(new ActivityItem("👥", "Assignation", "Dr. Martin -> Projet IA", "Il y a 2j", Color.MAGENTA));

        add(listContainer, BorderLayout.CENTER);

        // Bouton "Voir tout" en bas
        JButton btnSeeAll = new JButton("Voir tout l'historique →");
        btnSeeAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSeeAll.setForeground(ColorPalette.PRIMARY_BLUE);
        btnSeeAll.setContentAreaFilled(false);
        btnSeeAll.setBorderPainted(false);
        btnSeeAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeeAll.setHorizontalAlignment(SwingConstants.RIGHT);
        
        add(btnSeeAll, BorderLayout.SOUTH);
    }
}