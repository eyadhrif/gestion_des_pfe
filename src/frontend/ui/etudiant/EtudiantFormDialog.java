package ui.etudiant;

import backend.dao.EtudiantDAOImpl;
import model.Etudiant;
import ui.utils.ColorPalette;
import ui.dialogs.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Dialog for adding or editing an Etudiant.
 * Provides form validation and standardized UI.
 */
public class EtudiantFormDialog extends JDialog {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtEmail;
    private JTextField txtClasse;
    private JButton btnSave;
    
    private boolean saved = false;
    private Etudiant etudiant;
    private final Etudiant editingEtudiant;
    private final EtudiantDAOImpl dao = new EtudiantDAOImpl();

    public EtudiantFormDialog(Frame parent, Etudiant etudiantToEdit) {
        super(parent, etudiantToEdit == null ? "Nouvel Etudiant" : "Modifier Etudiant", true);
        this.editingEtudiant = etudiantToEdit;
        
        initializeUI();
        if (etudiantToEdit != null) {
            populateForm(etudiantToEdit);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        getContentPane().setBackground(Color.WHITE);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 30, 15, 30));
        contentPanel.setBackground(Color.WHITE);

        // Title (No Icon)
        JLabel titleLabel = new JLabel(editingEtudiant == null ? "Ajouter un etudiant" : "Modifier l'etudiant");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Form fields
        txtNom = createFormField(contentPanel, "Nom *");
        txtPrenom = createFormField(contentPanel, "Prenom *");
        txtEmail = createFormField(contentPanel, "Email *");
        txtClasse = createFormField(contentPanel, "Classe *");

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(new Color(249, 250, 251));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorPalette.BORDER_COLOR));

        JButton btnCancel = new JButton("Annuler");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Enregistrer");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

    private JTextField createFormField(JPanel parent, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(5));

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(txt);
        parent.add(Box.createVerticalStrut(15));

        return txt;
    }

    private void populateForm(Etudiant e) {
        txtNom.setText(e.getNom());
        txtPrenom.setText(e.getPrenom());
        txtEmail.setText(e.getEmail());
        txtClasse.setText(e.getClasse());
    }

    private void onSave() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String classe = txtClasse.getText().trim();

        // 1. Basic Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || classe.isEmpty()) {
            DialogUtils.showWarning(this, "Tous les champs sont obligatoires.");
            return;
        }

        // 2. Format Validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            DialogUtils.showWarning(this, "Format d'email invalide.");
            return;
        }

        // 3. Duplicate Email Check (Async potentially, but here sync for dialog simplicity)
        try {
            // If new student OR editing and email changed
            boolean checkEmail = (editingEtudiant == null) || (!email.equals(editingEtudiant.getEmail()));
            
            if (checkEmail && dao.existsByEmail(email)) {
                DialogUtils.showError(this, "Cet email existe deja.");
                return;
            }
        } catch (Exception ex) {
            DialogUtils.showDatabaseError(this, ex);
            return;
        }

        // Create Object
        etudiant = new Etudiant();
        if (editingEtudiant != null) {
            etudiant.setIdEtudiant(editingEtudiant.getIdEtudiant());
        }
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setEmail(email);
        etudiant.setClasse(classe);

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }
}
