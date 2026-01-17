package ui.soutenance;

import backend.dao.SoutenanceDAOImpl;
import model.Soutenance;
import ui.components.RoundedPanel;
import ui.dialogs.DialogUtils;
import ui.utils.ColorPalette;
import ui.utils.FontPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SoutenancePanel extends JPanel {
    private JTable table;
    private TableRowSorter<SoutenanceTableModel> sorter;
    private SoutenanceTableModel model;
    private final SoutenanceDAOImpl dao;
    
    // Changed: We use a standard JPanel for the dynamic list instead of a custom class
    private JPanel pnlProchainesDates; 

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

        // --- Header Panel ---
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

        // --- Content (Table + Sidebar) ---
        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 0, 0, 0));

        // 1. Table Area
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
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

        // 2. Sidebar Area (Prochaines Dates)
        RoundedPanel sidebarCard = new RoundedPanel(12, Color.WHITE, new BorderLayout());
        sidebarCard.setPreferredSize(new Dimension(300, 0));
        sidebarCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel sideTitle = new JLabel("Prochaines Dates");
        sideTitle.setFont(FontPalette.H3);
        sideTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        sidebarCard.add(sideTitle, BorderLayout.NORTH);

        // Container for the list items
        pnlProchainesDates = new JPanel();
        pnlProchainesDates.setLayout(new BoxLayout(pnlProchainesDates, BoxLayout.Y_AXIS));
        pnlProchainesDates.setBackground(Color.WHITE);
        
        // Wrap in ScrollPane in case there are many dates
        JScrollPane scrollSidebar = new JScrollPane(pnlProchainesDates);
        scrollSidebar.setBorder(null);
        scrollSidebar.getViewport().setBackground(Color.WHITE);
        
        sidebarCard.add(scrollSidebar, BorderLayout.CENTER);

        mainContent.add(tableCard, BorderLayout.CENTER);
        mainContent.add(sidebarCard, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
    }
    
    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(ColorPalette.BORDER_COLOR);
        table.setFont(FontPalette.BODY);
        table.setSelectionBackground(ColorPalette.PRIMARY_BLUE_LIGHT);
        table.setSelectionForeground(ColorPalette.PRIMARY_BLUE);
        table.setIntercellSpacing(new Dimension(0, 1));

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(FontPalette.H3);
        header.setBackground(ColorPalette.CONTENT_BG);
        header.setForeground(ColorPalette.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ColorPalette.BORDER_COLOR));
        header.setReorderingAllowed(false);

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        }
    }

    public void setSearchQuery(String text) {
        if (sorter == null) return;
        if (text == null || text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }
    }

    // --- REFRESH LOGIC ---
    public void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<Soutenance> tableData;
            List<Map<String, Object>> sidebarData;

            @Override
            protected Void doInBackground() throws Exception {
                // 1. Load Table Data
                tableData = dao.getAll(); 
                
                // 2. Load Sidebar Data (New Method)
                sidebarData = dao.getUpcomingDetails();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for errors
                    
                    // Update Table
                    model.setSoutenances(tableData);
                    
                    // Update Sidebar
                    updateSidePanel(sidebarData);
                    
                } catch (Exception e) {
                    DialogUtils.showDatabaseError(SoutenancePanel.this, e);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // Helper to populate the side panel
    private void updateSidePanel(List<Map<String, Object>> data) {
        pnlProchainesDates.removeAll();

        if (data.isEmpty()) {
            JLabel lbl = new JLabel("Aucune soutenance prévue.");
            lbl.setForeground(Color.GRAY);
            pnlProchainesDates.add(lbl);
        } else {
            for (Map<String, Object> row : data) {
                String dateStr = row.get("date").toString();
                String nomInfo = row.get("nom").toString();

                JPanel item = new JPanel(new BorderLayout());
                item.setBackground(Color.WHITE);
                item.setMaximumSize(new Dimension(300, 50));
                item.setBorder(new EmptyBorder(0, 0, 15, 0));

                JLabel lblDate = new JLabel(dateStr);
                lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblDate.setForeground(ColorPalette.PRIMARY_BLUE);

                JLabel lblName = new JLabel(nomInfo);
                lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lblName.setForeground(ColorPalette.TEXT_PRIMARY);

                item.add(lblDate, BorderLayout.NORTH);
                item.add(lblName, BorderLayout.CENTER);

                pnlProchainesDates.add(item);
            }
        }
        
        pnlProchainesDates.revalidate();
        pnlProchainesDates.repaint();
    }

    // --- CRUD ACTIONS ---
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