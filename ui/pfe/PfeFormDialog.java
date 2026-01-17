package ui.pfe;

import ui.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class PfeFormDialog extends JDialog {

    public PfeFormDialog(JFrame parent) {
        super(parent, "Ajouter un PFE", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Titre :"));
        add(new JTextField());

        add(new JLabel("Étudiant :"));
        add(new JTextField());

        add(new JLabel("Encadreur :"));
        add(new JTextField());

        add(new JLabel("État :"));
        add(new JTextField());

        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(ColorPalette.PRIMARY_BLUE);
        btnSave.setForeground(Color.WHITE);
        add(btnSave);

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
}
