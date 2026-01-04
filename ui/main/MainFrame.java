package ui.main;

import ui.dashboard.DashboardPanel;
import ui.student.StudentsPanel;
import ui.pfe.PfePanel;
import ui.encadreur.EncadreurPanel;
import ui.soutenance.SoutenancePanel;
import ui.reports.ReportsPanel;
import ui.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private TopBar topBar; // Déclaré en variable d'instance pour y accéder
    private Sidebar sidebar;

    public MainFrame() {
        initFrame();
        initLayout();
    }

    private void initFrame() {
        setTitle("Gestion des PFEs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        // On utilise BorderLayout pour organiser Sidebar vs Reste de l'app
        setLayout(new BorderLayout());
    }

    private void initLayout() {
        // 1. Sidebar à GAUCHE (Prend toute la hauteur)
        sidebar = new Sidebar(this);
        add(sidebar, BorderLayout.WEST);

        // 2. Création du conteneur de DROITE (TopBar + Content)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(ColorPalette.CONTENT_BG);

        // 3. TopBar en HAUT du conteneur de droite
        topBar = new TopBar("Tableau de Bord");
        rightContainer.add(topBar, BorderLayout.NORTH);

        // 4. Content Panel au CENTRE du conteneur de droite
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false); // Pour voir le fond du rightContainer

        // Initialisation et Ajout des panels
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new StudentsPanel(), "STUDENTS");
        contentPanel.add(new PfePanel(), "PFE");
        contentPanel.add(new EncadreurPanel(), "ENCADREURS");
        contentPanel.add(new SoutenancePanel(), "SOUTENANCES");
        contentPanel.add(new ReportsPanel(), "REPORTS");

        rightContainer.add(contentPanel, BorderLayout.CENTER);

        // Ajout du conteneur de droite à la frame
        add(rightContainer, BorderLayout.CENTER);

        // Afficher dashboard par défaut
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Cette méthode est appelée par les boutons de la Sidebar
     */
    public void showPanel(String name, JButton clickedButton) {
        // Changer le panel
        cardLayout.show(contentPanel, name);
        
        // Mettre à jour le style du bouton dans la sidebar
        sidebar.setActiveButton(clickedButton);
        
        // Mettre à jour le titre dans la TopBar dynamiquement
        updateTopBarTitle(name);
    }

    private void updateTopBarTitle(String name) {
        String title = switch (name) {
            case "DASHBOARD" -> "Tableau de Bord";
            case "STUDENTS" -> "Gestion des Étudiants";
            case "PFE" -> "Gestion des Projets PFE";
            case "ENCADREURS" -> "Gestion des Encadreurs";
            case "SOUTENANCES" -> "Calendrier des Soutenances";
            case "REPORTS" -> "Rapports et Statistiques";
            default -> "Gestion des PFEs";
        };
        topBar.setTitle(title);
    }

    public void registerNavButton(String name, JButton button) {
        button.addActionListener(e -> showPanel(name, button));
    }

    // Global refresh hook to be callable from panels after CRUD actions
    public void refreshSoutenances() {
        for (Component c : contentPanel.getComponents()) {
            if (c instanceof ui.soutenance.SoutenancePanel) {
                ((ui.soutenance.SoutenancePanel) c).refresh();
            }
        }
    }
}  