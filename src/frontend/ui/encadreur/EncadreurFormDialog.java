package ui.encadreur;

import model.Encadreur;
import ui.utils.ColorPalette;
import ui.dialogs.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EncadreurFormDialog extends JDialog {
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JComboBox<String> comboGrade;
    private JTextField txtEmail;
    
    private boolean saved = false;
    private Encadreur encadreur;
    private final Encadreur editingEncadreur;

    public EncadreurFormDialog(Frame parent, Encadreur encadreurToEdit) {
        super(parent, encadreurToEdit == null ? "Nouvel Encadreur" : "Modifier Encadreur", true);
        this.editingEncadreur = encadreurToEdit;
        
        initializeUI();
        if (encadreurToEdit != null) populateForm(encadreurToEdit);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 30, 15, 30));
        contentPanel.setBackground(Color.WHITE);

        // Header
        JLabel iconLabel = new JLabel("👨‍🏫");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Fields
        txtNom = createTextField("Nom *");
        addFormField(contentPanel, "Nom de famille", txtNom);
        
        txtPrenom = createTextField("Prénom *");
        addFormField(contentPanel, "Prénom", txtPrenom);
        
        comboGrade = new JComboBox<>(new String[]{"PES", "PH", "PA", "PESA", "Assistant", "Vacataire"});
        comboGrade.setBackground(Color.WHITE);
        addFormField(contentPanel, "Grade Universitaire", comboGrade);
        
        txtEmail = createTextField("Email *");
        addFormField(contentPanel, "Adresse Email", txtEmail);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(249, 250, 251));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorPalette.BORDER_COLOR));

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());
        
        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(ColorPalette.PRIMARY_BLUE);
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> onSave());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private JTextField createTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(200, 35));
        return txt;
    }

    private void addFormField(JPanel parent, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(5));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
    }

    private void populateForm(Encadreur e) {
        txtNom.setText(e.getNom());
        txtPrenom.setText(e.getPrenom());
        comboGrade.setSelectedItem(e.getGrade());
        txtEmail.setText(e.getEmail());
    }

    private void onSave() {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            DialogUtils.showWarning(this, "Tous les champs sont obligatoires");
            return;
        }

        encadreur = new Encadreur(
            editingEncadreur != null ? editingEncadreur.getIdEncadreur() : 0,
            txtNom.getText().trim(),
            txtPrenom.getText().trim(),
            (String) comboGrade.getSelectedItem(),
            txtEmail.getText().trim()
        );

        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }
    public Encadreur getEncadreur() { return encadreur; }
}