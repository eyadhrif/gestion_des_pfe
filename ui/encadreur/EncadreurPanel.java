package ui.encadreur;

import backend.dao.EncadreurDAOImpl;
import model.Encadreur;
import ui.components.RoundedPanel;
import ui.dialogs.DialogUtils;
import ui.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EncadreurPanel extends JPanel {
    private JTable table;
    private EncadreurTableModel model;
    private EncadreurDAOImpl dao;

    public EncadreurPanel() {
        this.dao = new EncadreurDAOImpl();
        this.model = new EncadreurTableModel();
        
        initializeUI();
        refresh();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.CONTENT_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gestion des Encadreurs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        
        JButton btnAdd = new JButton("+ Nouvel Encadreur");
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

        // Standard Table Setup
        table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(ColorPalette.PRIMARY_BLUE_LIGHT);
        table.setSelectionForeground(ColorPalette.PRIMARY_BLUE);
        table.setShowVerticalLines(false);
        table.setGridColor(ColorPalette.BORDER_COLOR);
        
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

    private void refresh() {
        SwingWorker<List<Encadreur>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Encadreur> doInBackground() throws Exception {
                return dao.getAll();
            }

            @Override
            protected void done() {
                try {
                    model.setListeEncadreurs(get());
                } catch (Exception e) {
                    DialogUtils.showDatabaseError(EncadreurPanel.this, e);
                }
            }
        };
        worker.execute();
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EncadreurFormDialog dlg = new EncadreurFormDialog(parent, null);
        dlg.setVisible(true);
        
        if (dlg.isSaved()) {
            try {
                if (dao.insert(dlg.getEncadreur()) > 0) {
                    DialogUtils.showSuccess(this, "Encadreur ajouté avec succès");
                    refresh();
                } else {
                    DialogUtils.showError(this, "Échec de l'ajout");
                }
            } catch (Exception e) {
                DialogUtils.showDatabaseError(this, e);
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            DialogUtils.showWarning(this, "Veuillez sélectionner un encadreur.");
            return;
        }
        
        Encadreur e = model.getEncadreurAt(table.convertRowIndexToModel(row));
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EncadreurFormDialog dlg = new EncadreurFormDialog(parent, e);
        dlg.setVisible(true);
        
        if (dlg.isSaved()) {
            try {
                if (dao.update(dlg.getEncadreur()) > 0) {
                    DialogUtils.showSuccess(this, "Encadreur modifié");
                    refresh();
                } else {
                    DialogUtils.showError(this, "Échec de la modification");
                }
            } catch (Exception ex) {
                DialogUtils.showDatabaseError(this, ex);
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            DialogUtils.showWarning(this, "Veuillez sélectionner un encadreur.");
            return;
        }
        
        Encadreur e = model.getEncadreurAt(table.convertRowIndexToModel(row));
        if (DialogUtils.confirmDelete(this, e.getNom() + " " + e.getPrenom())) {
            try {
                if (dao.delete(e) > 0) {
                    DialogUtils.showSuccess(this, "Encadreur supprimé");
                    refresh();
                } else {
                    DialogUtils.showError(this, "Échec de la suppression");
                }
            } catch (Exception ex) {
                DialogUtils.showDatabaseError(this, ex);
            }
        }
    }
}