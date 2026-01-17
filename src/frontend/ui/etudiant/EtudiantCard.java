package ui.etudiant;

import model.Etudiant;
import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EtudiantCard extends RoundedPanel {
    
    public EtudiantCard(Etudiant etudiant) {
        super(20); // Arrondi de 20px
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(280, 180));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Zone du haut : Avatar + Infos principales
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        // Simulation d'un Avatar (Cercle avec Initiales)
        JLabel avatar = new JLabel(String.valueOf(etudiant.getNom().charAt(0)), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(239, 246, 255)); // Bleu clair
        avatar.setForeground(ColorPalette.PRIMARY_BLUE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        // Note: Pour un vrai cercle, il faudrait override paintComponent, mais restons simple ici.

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(etudiant.getNom() + " " + etudiant.getPrenom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel classLabel = new JLabel(etudiant.getClasse());
        classLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(classLabel);

        topPanel.add(avatar, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        // 2. Zone du milieu : Détails (Email)
        JPanel detailsPanel = new JPanel(new GridLayout(1, 1));
        detailsPanel.setOpaque(false);
        
        JLabel emailLabel = new JLabel("📧 " + etudiant.getEmail());
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailLabel.setForeground(new Color(107, 114, 128));

        // 3. Zone du bas : Bouton d'action rapide
        JButton btnProfile = new JButton("Voir le dossier");
        btnProfile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnProfile.setForeground(ColorPalette.PRIMARY_BLUE);
        btnProfile.setBorderPainted(false);
        btnProfile.setContentAreaFilled(false);
        btnProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProfile.setHorizontalAlignment(SwingConstants.RIGHT);

        add(topPanel, BorderLayout.NORTH);
        add(emailLabel, BorderLayout.CENTER);
        add(btnProfile, BorderLayout.SOUTH);
    }
}