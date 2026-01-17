package ui.main;

import ui.dashboard.DashboardPanel;
import ui.etudiant.EtudiantPanel;
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
    private Map<String, JComponent> panelMap = new HashMap<>();
    private String currentPanelName = "DASHBOARD";

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
        topBar.addSearchListener(query -> forwardSearchQuery(query));
        rightContainer.add(topBar, BorderLayout.NORTH);

        // 4. Content Panel au CENTRE du conteneur de droite
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false); // Pour voir le fond du rightContainer

        // Initialisation et Ajout des panels
        DashboardPanel dash = new DashboardPanel();
        EtudiantPanel etu = new EtudiantPanel();
        PfePanel pfe = new PfePanel();
        EncadreurPanel enc = new EncadreurPanel();
        SoutenancePanel sou = new SoutenancePanel();
        ReportsPanel rep = new ReportsPanel();

        contentPanel.add(dash, "DASHBOARD");
        contentPanel.add(etu, "STUDENTS");
        contentPanel.add(pfe, "PFE");
        contentPanel.add(enc, "ENCADREURS");
        contentPanel.add(sou, "SOUTENANCES");
        contentPanel.add(rep, "REPORTS");

        panelMap.put("DASHBOARD", dash);
        panelMap.put("STUDENTS", etu);
        panelMap.put("PFE", pfe);
        panelMap.put("ENCADREURS", enc);
        panelMap.put("SOUTENANCES", sou);
        panelMap.put("REPORTS", rep);

        rightContainer.add(contentPanel, BorderLayout.CENTER);

        // Ajout du conteneur de droite à la frame
        add(rightContainer, BorderLayout.CENTER);

        // Afficher dashboard par défaut
        cardLayout.show(contentPanel, "DASHBOARD");
        // Initial setup for Dashboard visibility
        updateTopBarTitle("DASHBOARD");
    }

    /**
     * Cette méthode est appelée par les boutons de la Sidebar
     */
    public void showPanel(String name, JButton clickedButton) {
        // Changer le panel
        cardLayout.show(contentPanel, name);
        currentPanelName = name;
        
        // Mettre à jour le style du bouton dans la sidebar
        sidebar.setActiveButton(clickedButton);
        
        // Mettre à jour le titre dans la TopBar dynamiquement
        updateTopBarTitle(name);
        
        // Propager la requête de recherche courante au nouveau panel
        forwardSearchQuery(topBar.getSearchText());
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
        
        // Hide search bar and buttons on Dashboard page
        if (topBar != null) {
            topBar.setControlsVisible(!name.equals("DASHBOARD"));
        }
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

    private void forwardSearchQuery(String q) {
        JComponent panel = panelMap.get(currentPanelName);
        if (panel == null) return;
        try {
            if (panel instanceof ui.etudiant.EtudiantPanel) {
                ((ui.etudiant.EtudiantPanel) panel).setSearchQuery(q);
            } else if (panel instanceof ui.pfe.PfePanel) {
                ((ui.pfe.PfePanel) panel).setSearchQuery(q);
            } else if (panel instanceof ui.encadreur.EncadreurPanel) {
                ((ui.encadreur.EncadreurPanel) panel).setSearchQuery(q);
            } else if (panel instanceof ui.soutenance.SoutenancePanel) {
                ((ui.soutenance.SoutenancePanel) panel).setSearchQuery(q);
            }
        } catch (Exception ignored) {}
    }
}