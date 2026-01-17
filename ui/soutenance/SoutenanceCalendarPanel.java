package ui.soutenance;

import ui.components.RoundedPanel;
import ui.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SoutenanceCalendarPanel extends RoundedPanel {

    public SoutenanceCalendarPanel() {
        super(15, Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Prochaines Dates");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel datesList = new JPanel();
        datesList.setLayout(new BoxLayout(datesList, BoxLayout.Y_AXIS));
        datesList.setOpaque(false);

        // Exemple d'affichage rapide
        addDateRow(datesList, "15 Jan", "Soutenance IA - Salle A1");
        addDateRow(datesList, "20 Jan", "Soutenance Web - Salle B4");

        add(title, BorderLayout.NORTH);
        add(datesList, BorderLayout.CENTER);
    }

    private void addDateRow(JPanel p, String date, String desc) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblDate = new JLabel(date);
        lblDate.setForeground(ColorPalette.PRIMARY_BLUE);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        row.add(lblDate, BorderLayout.WEST);
        row.add(lblDesc, BorderLayout.CENTER);
        p.add(row);
    }
}