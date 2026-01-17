package ui.soutenance;

import model.Pfe;
import model.Soutenance;
import service.PfeService;
import ui.utils.ColorPalette;
import ui.dialogs.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SoutenanceFormDialog extends JDialog {
    private JComboBox<Pfe> comboPfe;
    private JTextField txtDate;
    private JTextField txtSalle;
    private JSpinner spinnerNote;
    
    private boolean saved = false;
    private Soutenance soutenance;
    private final Soutenance editingSoutenance;
    private final PfeService pfeService;

    public SoutenanceFormDialog(Frame parent, Soutenance soutenanceToEdit) {
        super(parent, soutenanceToEdit == null ? "Planifier une Soutenance" : "Modifier la Soutenance", true);
        this.editingSoutenance = soutenanceToEdit;
        this.pfeService = new PfeService();
        
        initializeUI();
        loadPfeData();
        
        if (soutenanceToEdit != null) {
            populateForm(soutenanceToEdit);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 30, 15, 30));
        contentPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("📅");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        JLabel descLabel = new JLabel("Détails de la soutenance");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createHorizontalStrut(10));
        headerPanel.add(descLabel);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Fields
        comboPfe = new JComboBox<>();
        addFormField(contentPanel, "Projet PFE *", comboPfe);
        
        txtDate = new JTextField();
        txtDate.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        addFormField(contentPanel, "Date (YYYY-MM-DD) *", txtDate);
        
        txtSalle = new JTextField();
        addFormField(contentPanel, "Salle *", txtSalle);
        
        spinnerNote = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 20.0, 0.5));
        addFormField(contentPanel, "Note (/20)", spinnerNote);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(new Color(249, 250, 251));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorPalette.BORDER_COLOR));

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());
        btnCancel.setBackground(Color.WHITE);
        
        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(ColorPalette.PRIMARY_BLUE);
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> onSave());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void addFormField(JPanel parent, String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        
        parent.add(p);
        parent.add(Box.createVerticalStrut(15));
    }

    private void loadPfeData() {
        try {
            List<Pfe> pfes = pfeService.getAllFull();
            comboPfe.addItem(null); // Allow empty selection initially
            for (Pfe p : pfes) {
                // In a real app, maybe filter only PFEs without schedulded defense?
                comboPfe.addItem(p);
            }
            
            // Render PFE nicely in combobox
            comboPfe.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Pfe) {
                        setText(((Pfe) value).getTitre());
                    } else {
                        setText("Sélectionner un PFE...");
                    }
                    return this;
                }
            });
        } catch (Exception e) {
            DialogUtils.showError(this, "Impossible de charger les PFEs: " + e.getMessage());
        }
    }

    private void populateForm(Soutenance s) {
        // Select the PFE properly
        if (s.getPfe() != null) {
            for (int i = 0; i < comboPfe.getItemCount(); i++) {
                Pfe p = comboPfe.getItemAt(i);
                if (p != null && p.getIdpfe() == s.getPfe().getIdpfe()) {
                    comboPfe.setSelectedIndex(i);
                    break;
                }
            }
        }
        txtDate.setText(s.getDate().toString());
        txtSalle.setText(s.getSalle());
        spinnerNote.setValue(s.getNote());
    }

    private void onSave() {
        if (!validateForm()) return;

        soutenance = new Soutenance(
            editingSoutenance != null ? editingSoutenance.getIdsoutenance() : 0,
            LocalDate.parse(txtDate.getText().trim()),
            txtSalle.getText().trim(),
            (Double) spinnerNote.getValue(),
            (Pfe) comboPfe.getSelectedItem()
        );

        saved = true;
        dispose();
    }

    private boolean validateForm() {
        if (comboPfe.getSelectedItem() == null) {
            DialogUtils.showWarning(this, "Veuillez sélectionner un projet PFE.");
            return false;
        }
        
        String dateStr = txtDate.getText().trim();
        if (dateStr.isEmpty()) {
            DialogUtils.showWarning(this, "Veuillez entrer une date.");
            return false;
        }
        try {
            LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            DialogUtils.showWarning(this, "Format de date invalide. Utilisez YYYY-MM-DD.");
            return false;
        }
        
        if (txtSalle.getText().trim().isEmpty()) {
            DialogUtils.showWarning(this, "Veuillez entrer une salle.");
            return false;
        }

        return true;
    }

    public boolean isSaved() { return saved; }
    public Soutenance getSoutenance() { return soutenance; }
}
