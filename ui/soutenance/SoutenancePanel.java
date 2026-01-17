package ui.soutenance;

import backend.dao.SoutenanceDAOImpl;
import model.Soutenance;
import ui.components.RoundedPanel;
import ui.dialogs.DialogUtils;
import ui.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SoutenancePanel extends JPanel {
    private JTable table;
    private SoutenanceTableModel model;
    private final SoutenanceDAOImpl dao;
    private SoutenanceCalendarPanel calendar;

    public SoutenancePanel() {
        this.dao = new SoutenanceDAOImpl();
        this.model = new SoutenanceTableModel();
        
        initializeUI();
        refresh();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.CONTENT_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Panel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Calendrier des Soutenances");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ColorPalette.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        
        JButton btnAdd = new JButton("+ Planifier une session");
        btnAdd.setBackground(ColorPalette.PRIMARY_BLUE);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
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

        // Content
        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

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

        // Calendar sidebar
        calendar = new SoutenanceCalendarPanel();
        calendar.setPreferredSize(new Dimension(300, 0));

        mainContent.add(tableCard, BorderLayout.CENTER);
        mainContent.add(calendar, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(ColorPalette.CONTENT_BG);
        table.setSelectionBackground(ColorPalette.PRIMARY_BLUE_LIGHT);
        table.setSelectionForeground(ColorPalette.PRIMARY_BLUE);
        table.setShowVerticalLines(false);
        table.setGridColor(ColorPalette.BORDER_COLOR);
    }

    public void refresh() {
        SwingWorker<List<Soutenance>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Soutenance> doInBackground() throws Exception {
                // In a real app we need to fetch PFE details for each soutenance
                // Since DAO default implementation might not join PFE table efficiently
                // we might need a better service/DAO method. For now assuming simple fetch.
                return dao.getAll(); 
            }

            @Override
            protected void done() {
                try {
                    model.setSoutenances(get());
                    // Also refresh calendar if needed
                } catch (Exception e) {
                    DialogUtils.showDatabaseError(SoutenancePanel.this, e);
                }
            }
        };
        worker.execute();
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        SoutenanceFormDialog dlg = new SoutenanceFormDialog(parent, null);
        dlg.setVisible(true);
        
        if (dlg.isSaved()) {
            Soutenance s = dlg.getSoutenance();
            try {
                if (dao.insert(s) > 0) {
                    DialogUtils.showSuccess(this, "Soutenance planifiée avec succès");
                    refresh();
                } else {
                    DialogUtils.showError(this, "Échec de la planification");
                }
            } catch (Exception e) {
                DialogUtils.showDatabaseError(this, e);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            DialogUtils.showWarning(this, "Sélectionnez une soutenance à modifier");
            return;
        }
        
        Soutenance s = model.getSoutenanceAt(table.convertRowIndexToModel(row));
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        SoutenanceFormDialog dlg = new SoutenanceFormDialog(parent, s);
        dlg.setVisible(true);
        
        if (dlg.isSaved()) {
            Soutenance updated = dlg.getSoutenance();
            try {
                if (dao.update(updated) > 0) {
                    DialogUtils.showSuccess(this, "Soutenance modifiée avec succès");
                    refresh();
                } else {
                    DialogUtils.showError(this, "Échec de la modification");
                }
            } catch (Exception e) {
                DialogUtils.showDatabaseError(this, e);
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            DialogUtils.showWarning(this, "Sélectionnez une soutenance à supprimer");
            return;
        }
        
        Soutenance s = model.getSoutenanceAt(table.convertRowIndexToModel(row));
        if (DialogUtils.confirmDelete(this, "Soutenance du " + s.getDate())) {
            try {
                if (dao.delete(s) > 0) {
                    DialogUtils.showSuccess(this, "Soutenance supprimée");
                    refresh();
                } else {
                    DialogUtils.showError(this, "Échec de la suppression");
                }
            } catch (Exception e) {
                DialogUtils.showDatabaseError(this, e);
            }
        }
    }
}