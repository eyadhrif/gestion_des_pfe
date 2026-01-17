package ui.pfe;

import model.Pfe;
import model.Encadreur;
import model.Etudiant;
import service.PfeService;
import ui.dialogs.DialogUtils;
import ui.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PfeDialog extends JDialog {
    private boolean saved = false;
    private final PfeService service = new PfeService();

    private JTextField txtTitre;
    private JTextArea txtDesc;
    private JComboBox<String> cmbEtat;
    private JTextField txtDate;
    private JComboBox<Etudiant> cbEtudiant;
    private JList<Encadreur> listEncadreurs;

    private final Pfe editing;

    public PfeDialog(Frame parent, Pfe editing) {
        super(parent, editing == null ? "Nouveau Projet PFE" : "Modifier Projet PFE", true);
        this.editing = editing;

        initializeUI();
        loadSelections();
        
        if (editing != null) populateFrom(editing);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 25, 20, 25));
        content.setBackground(Color.WHITE);

        // Header
        JLabel icon = new JLabel(editing == null ? "✨" : "📝");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        content.add(icon);
        content.add(Box.createVerticalStrut(10));

        // Fields
        txtTitre = createTextField();
        addLabelAndField(content, "Titre du projet *", txtTitre);

        txtDesc = new JTextArea(3, 30);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        addLabelAndField(content, "Description", new JScrollPane(txtDesc));

        cmbEtat = new JComboBox<>(new String[]{"Proposé", "En cours", "Terminé", "Validé"});
        cmbEtat.setBackground(Color.WHITE);
        addLabelAndField(content, "État d'avancement", cmbEtat);
        
        txtDate = createTextField();
        txtDate.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD (Optionnel)");
        addLabelAndField(content, "Date probable de soutenance", txtDate);

        cbEtudiant = new JComboBox<>();
        addLabelAndField(content, "Étudiant assigné *", cbEtudiant);

        listEncadreurs = new JList<>();
        listEncadreurs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listEncadreurs.setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
        JScrollPane scrollEnc = new JScrollPane(listEncadreurs);
        scrollEnc.setPreferredSize(new Dimension(300, 80));
        addLabelAndField(content, "Encadreur(s) * (Ctrl+Click pour plusieurs)", scrollEnc);

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(new Color(249, 250, 251));
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorPalette.BORDER_COLOR));

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());
        
        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(ColorPalette.PRIMARY_BLUE);
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> onSave());
        
        buttons.add(btnCancel);
        buttons.add(btnSave);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private JTextField createTextField() {
        JTextField t = new JTextField(20);
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return t;
    }

    private void addLabelAndField(JPanel p, String labelText, JComponent field) {
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(field);
        p.add(Box.createVerticalStrut(12));
    }

    private void loadSelections() {
        try {
            // Load students
            List<Etudiant> etus = service.getAllEtudiants();
            cbEtudiant.addItem(null); // Empty option
            for (Etudiant e : etus) cbEtudiant.addItem(e);
            
            // Custom renderer for Student
            cbEtudiant.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Etudiant) {
                        Etudiant e = (Etudiant) value;
                        setText(e.getNom() + " " + e.getPrenom() + " (" + e.getClasse() + ")");
                    }
                    return this;
                }
            });

            // Load supervisors
            List<Encadreur> encs = service.getAllEncadreurs();
            DefaultListModel<Encadreur> lmodel = new DefaultListModel<>();
            for (Encadreur e : encs) lmodel.addElement(e);
            listEncadreurs.setModel(lmodel);
            
            // Custom renderer for Supervisor
            listEncadreurs.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Encadreur) {
                        Encadreur e = (Encadreur) value;
                        setText(e.getGrade() + " " + e.getNom() + " " + e.getPrenom());
                    }
                    return this;
                }
            });

        } catch (Exception ex) {
            DialogUtils.showDatabaseError(this, ex);
        }
    }

    private void populateFrom(Pfe p) {
        txtTitre.setText(p.getTitre());
        txtDesc.setText(p.getDescription());
        cmbEtat.setSelectedItem(p.getEtat());
        if (p.getDateSoutenance() != null) txtDate.setText(p.getDateSoutenance().toString());
        
        // Select student
        if (p.getEtudiant() != null) {
            for (int i=0; i<cbEtudiant.getItemCount(); i++) {
                Etudiant e = cbEtudiant.getItemAt(i);
                if (e != null && e.getIdEtudiant() == p.getEtudiant().getIdEtudiant()) {
                    cbEtudiant.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Select encadreurs
        if (p.getEncadreur() != null) {
            List<Integer> ids = p.getEncadreur().stream().map(Encadreur::getIdEncadreur).toList();
            List<Integer> selectedIndices = new ArrayList<>();
            ListModel<Encadreur> model = listEncadreurs.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (ids.contains(model.getElementAt(i).getIdEncadreur())) {
                    selectedIndices.add(i);
                }
            }
            listEncadreurs.setSelectedIndices(selectedIndices.stream().mapToInt(i->i).toArray());
        }
    }

    private void onSave() {
        if (!validateForm()) return;

        Pfe p = buildPfe();
        boolean success;
        
        try {
            if (editing == null) success = service.createPfe(p);
            else {
                p.setIdpfe(editing.getIdpfe());
                success = service.updatePfe(p);
            }
            
            if (!success) {
                DialogUtils.showError(this, "Erreur lors de l'enregistrement en base de données.");
            } else {
                saved = true;
                dispose();
            }
        } catch (Exception e) {
            DialogUtils.showDatabaseError(this, e);
        }
    }

    private boolean validateForm() {
        if (txtTitre.getText().trim().isEmpty()) {
            DialogUtils.showWarning(this, "Le titre est obligatoire");
            return false;
        }
        if (cbEtudiant.getSelectedItem() == null) {
            DialogUtils.showWarning(this, "Veuillez assigner un étudiant");
            return false;
        }
        if (listEncadreurs.getSelectedValuesList().isEmpty()) {
            DialogUtils.showWarning(this, "Veuillez sélectionner au moins un encadreur");
            return false;
        }
        
        String d = txtDate.getText().trim();
        if (!d.isEmpty()) {
            try { LocalDate.parse(d); } 
            catch (Exception e) { 
                DialogUtils.showWarning(this, "Date invalide. Format: YYYY-MM-DD");
                return false; 
            }
        }
        return true;
    }

    private Pfe buildPfe() {
        String d = txtDate.getText().trim();
        LocalDate date = d.isEmpty() ? null : LocalDate.parse(d);
        
        return new Pfe(
                txtTitre.getText().trim(),
                txtDesc.getText().trim(),
                (String) cmbEtat.getSelectedItem(),
                date,
                (Etudiant) cbEtudiant.getSelectedItem(),
                listEncadreurs.getSelectedValuesList()
        );
    }

    public boolean isSaved() { return saved; }
}
