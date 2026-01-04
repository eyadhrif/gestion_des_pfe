package ui.pfe;

import model.Pfe;
import service.PfeService;
import ui.components.RoundedPanel;
import ui.dialogs.DialogUtils;
import ui.main.MainFrame;
import ui.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PfePanel extends JPanel {
    private PfeTableModel model;
    private JTable table;
    private final PfeService service;

    public PfePanel() {
        this.service = new PfeService();
        this.model = new PfeTableModel();
        
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.CONTENT_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gestion des Projets PFE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ColorPalette.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        
        JButton btnAdd = new JButton("+ Nouveau PFE");
        btnAdd.setBackground(ColorPalette.PRIMARY_BLUE);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.addActionListener(e -> onAdd());
        
        JButton btnEdit = new JButton("Modifier");
        btnEdit.setBackground(ColorPalette.WARNING_AMBER);
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> onEdit());
        
        JButton btnDelete = new JButton("Supprimer");
        btnDelete.setBackground(ColorPalette.ERROR_RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> onDelete());

        actions.add(btnEdit);
        actions.add(btnDelete);
        actions.add(btnAdd);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        table = new JTable(model);
        styleTable(table);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onEdit();
            }
        });

        RoundedPanel tableCard = new RoundedPanel(12, Color.WHITE, new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(tableCard, BorderLayout.CENTER);
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(ColorPalette.PRIMARY_BLUE_LIGHT);
        table.setSelectionForeground(ColorPalette.PRIMARY_BLUE);
        table.setShowVerticalLines(false);
        table.setGridColor(ColorPalette.BORDER_COLOR);
        
        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Titre
    }

    private void refreshData() {
        SwingWorker<List<Pfe>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Pfe> doInBackground() throws Exception {
                return service.getAllFull();
            }

            @Override
            protected void done() {
                try {
                    model.setPfes(get());
                } catch (Exception e) {
                    DialogUtils.showDatabaseError(PfePanel.this, e);
                }
            }
        };
        worker.execute();
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PfeDialog dlg = new PfeDialog(parent, null);
        dlg.setVisible(true);
        
        if (dlg.isSaved()) {
            DialogUtils.showSuccess(this, "Projet PFE créé avec succès");
            refreshData();
            notifyMainFrame();
        }
    }

    private void onEdit() {
        int r = table.getSelectedRow();
        if (r < 0) {
            DialogUtils.showWarning(this, "Veuillez sélectionner un projet.");
            return;
        }
        
        Pfe p = model.getAt(table.convertRowIndexToModel(r));
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PfeDialog dlg = new PfeDialog(parent, p);
        dlg.setVisible(true);
        
        if (dlg.isSaved()) {
            DialogUtils.showSuccess(this, "Projet PFE mis à jour");
            refreshData();
            notifyMainFrame();
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0) {
            DialogUtils.showWarning(this, "Veuillez sélectionner un projet.");
            return;
        }
        
        Pfe p = model.getAt(table.convertRowIndexToModel(r));
        if (DialogUtils.confirmDelete(this, p.getTitre())) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return service.deletePfe(p);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            DialogUtils.showSuccess(PfePanel.this, "Projet supprimé");
                            refreshData();
                            notifyMainFrame();
                        } else {
                            DialogUtils.showError(PfePanel.this, "Impossible de supprimer le projet");
                        }
                    } catch (Exception e) {
                        DialogUtils.showDatabaseError(PfePanel.this, e);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void notifyMainFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainFrame) {
            ((MainFrame) w).refreshSoutenances();
        }
    }
}