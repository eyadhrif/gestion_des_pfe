package ui.etudiant;

import backend.dao.EtudiantDAOImpl;
import model.Etudiant;
import ui.components.RoundedPanel;
import ui.components.ModernButton;
import ui.dialogs.DialogUtils;
import ui.utils.ColorPalette;
import ui.utils.FontPalette;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel for managing Students (Etudiants) with full CRUD operations.
 * Supports Add, Edit, Delete, List, Search/Filter functionality.
 */
public class EtudiantPanel extends JPanel {
    private JTable tableEtudiants;
    private EtudiantTableModel modelEtudiants;
    private TableRowSorter<EtudiantTableModel> sorter;
        private final EtudiantDAOImpl etudiantDAO;

    public EtudiantPanel() {
        this.etudiantDAO = new EtudiantDAOImpl();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.CONTENT_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Table Panel
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(FontPalette.H1);
        titleLabel.setForeground(ColorPalette.TEXT_PRIMARY);

        // Global search is handled by TopBar. No local search field.

        // Action Buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton btnAdd = new ModernButton("Nouveau", ColorPalette.PRIMARY_BLUE);
        JButton btnEdit = new ModernButton("Modifier", ColorPalette.WARNING_AMBER);
        JButton btnDelete = new ModernButton("Supprimer", ColorPalette.ERROR_RED);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        actionsPanel.add(btnEdit);
        actionsPanel.add(btnDelete);
        actionsPanel.add(btnAdd);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        headerPanel.add(actionsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    // Buttons now use ModernButton for consistency

    private JPanel createTablePanel() {
        RoundedPanel cardTableau = new RoundedPanel(new BorderLayout(), ColorPalette.CARD_BG);
        cardTableau.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create table with custom model
        modelEtudiants = new EtudiantTableModel();
        tableEtudiants = new JTable(modelEtudiants);
        sorter = new TableRowSorter<>(modelEtudiants);
        tableEtudiants.setRowSorter(sorter);
        
        styleTable(tableEtudiants);
        
        // Double-click to edit
        tableEtudiants.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onEdit();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableEtudiants);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        cardTableau.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setOpaque(false);
        JLabel statusLabel = new JLabel("Double-cliquez sur une ligne pour modifier");
        statusLabel.setFont(FontPalette.CAPTION);
        statusLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        statusBar.add(statusLabel);
        cardTableau.add(statusBar, BorderLayout.SOUTH);

        return cardTableau;
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

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FontPalette.H3);
        header.setBackground(ColorPalette.CONTENT_BG);
        header.setForeground(ColorPalette.TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ColorPalette.BORDER_COLOR));
        header.setReorderingAllowed(false);

        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nom
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Prénom
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(4).setPreferredWidth(80); // Classe
        table.getColumnModel().getColumn(5).setPreferredWidth(250); // Sujet PFE - Wider
    }

    // Called by TopBar global search
    public void setSearchQuery(String text) {
        if (sorter == null) return;
        if (text == null || text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }
    }

    public void loadData() {
        SwingWorker<List<Etudiant>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Etudiant> doInBackground() throws Exception {
                return etudiantDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<Etudiant> list = get();
                    modelEtudiants.setListeEtudiants(list);
                } catch (Exception e) {
                    DialogUtils.showDatabaseError(EtudiantPanel.this, e);
                }
            }
        };
        worker.execute();
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EtudiantFormDialog dialog = new EtudiantFormDialog(parent, null);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            Etudiant newEtudiant = dialog.getEtudiant();
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return etudiantDAO.insert(newEtudiant) > 0;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            DialogUtils.showSuccess(EtudiantPanel.this, "Étudiant ajouté avec succès!");
                            loadData();
                        } else {
                            DialogUtils.showError(EtudiantPanel.this, "Échec de l'ajout de l'étudiant.");
                        }
                    } catch (Exception e) {
                        DialogUtils.showDatabaseError(EtudiantPanel.this, e);
                    }
                }
            };
            worker.execute();
        }
    }

    private void onEdit() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow < 0) {
            DialogUtils.showInfo(this, "Veuillez sélectionner un étudiant à modifier.");
            return;
        }

        // Convert view row to model row (important when table is sorted/filtered)
        int modelRow = tableEtudiants.convertRowIndexToModel(selectedRow);
        Etudiant etudiant = modelEtudiants.getEtudiantAt(modelRow);

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EtudiantFormDialog dialog = new EtudiantFormDialog(parent, etudiant);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Etudiant updatedEtudiant = dialog.getEtudiant();
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return etudiantDAO.update(updatedEtudiant) > 0;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            DialogUtils.showSuccess(EtudiantPanel.this, "Étudiant modifié avec succès!");
                            loadData();
                        } else {
                            DialogUtils.showError(EtudiantPanel.this, "Échec de la modification.");
                        }
                    } catch (Exception e) {
                        DialogUtils.showDatabaseError(EtudiantPanel.this, e);
                    }
                }
            };
            worker.execute();
        }
    }

    private void onDelete() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow < 0) {
            DialogUtils.showInfo(this, "Veuillez sélectionner un étudiant à supprimer.");
            return;
        }

        int modelRow = tableEtudiants.convertRowIndexToModel(selectedRow);
        Etudiant etudiant = modelEtudiants.getEtudiantAt(modelRow);

        if (!DialogUtils.confirmDelete(this, etudiant.getNom() + " " + etudiant.getPrenom())) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return etudiantDAO.delete(etudiant) > 0;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        DialogUtils.showSuccess(EtudiantPanel.this, "Étudiant supprimé avec succès!");
                        loadData();
                    } else {
                        DialogUtils.showError(EtudiantPanel.this, "Échec de la suppression.");
                    }
                } catch (Exception e) {
                    DialogUtils.showDatabaseError(EtudiantPanel.this, e);
                }
            }
        };
        worker.execute();
    }
}