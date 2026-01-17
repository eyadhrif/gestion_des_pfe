package ui.dashboard;

import backend.dao.EtudiantDAOImpl;
import ui.utils.ColorPalette;
import ui.dialogs.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Dashboard Panel with real-time statistics and graphs.
 */
public class DashboardPanel extends JPanel {
    private final EtudiantDAOImpl etudiantDAO = new EtudiantDAOImpl();
    private StatsCard studentsCard;
    private SimpleBarChart studentsByClassChart;

    public DashboardPanel() {
        setBackground(ColorPalette.CONTENT_BG);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorPalette.CONTENT_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Tableau de Bord");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ColorPalette.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Vue globale de la gestion des PFE");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(ColorPalette.CONTENT_BG);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Content Scrollable
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(ColorPalette.CONTENT_BG);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // 1. Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0)); // 4 slots, but maybe we use 3
        statsPanel.setBackground(ColorPalette.CONTENT_BG);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        // Initial values 0, will be updated asynchronously
        studentsCard = new StatsCard("Total Etudiants", 0, ColorPalette.PRIMARY_BLUE, "", 0, true);
        statsPanel.add(studentsCard);
        
        // Placeholders for other stats (since I'm restricted to Etudiant module focus, but keeping layout)
        statsPanel.add(new StatsCard("PFEs Soumis", 0, ColorPalette.SUCCESS_GREEN, "", 0, true));
        statsPanel.add(new StatsCard("Soutenances", 0, ColorPalette.WARNING_AMBER, "", 0, true));
        
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // 2. Charts Section
        JLabel chartTitle = new JLabel("Repartition des Etudiants par Classe");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(ColorPalette.TEXT_PRIMARY);
        chartTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(chartTitle);
        contentPanel.add(Box.createVerticalStrut(15));
        
        studentsByClassChart = new SimpleBarChart();
        studentsByClassChart.setPreferredSize(new Dimension(800, 300));
        studentsByClassChart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        studentsByClassChart.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(studentsByClassChart);

        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ColorPalette.CONTENT_BG);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load Real Data
        loadData();
    }

    private void loadData() {
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                int totalStudents = etudiantDAO.countTotal();
                Map<String, Integer> byClasse = etudiantDAO.countByClasse();
                return new DashboardData(totalStudents, byClasse);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    updateUI(data);
                } catch (InterruptedException | ExecutionException e) {
                    // Fail silently or log
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void updateUI(DashboardData data) {
        // We need a way to update StatsCard value dynamically. 
        // Since StatsCard is immutable-ish in current impl, we might want to repaint or recreate?
        // Actually, let's just create a new StatsCard or simpler: just use labels in StatsCard next time.
        // For now, I'll hack it by re-adding components if needed, BUT better: 
        // I will rely on the fact that I can't easily change StatsCard without modifying it.
        // Let's modify StatsCard to simple labels or just re-validate. 
        // Actually, Java Swing components don't update if fields change unless repainted. 
        // I'll leave StatsCard static for now and just show correct data on load? 
        // No, I need it to be dynamic. 
        
        // Re-creating the card is easiest without modifying StatsCard class too much.
        // But StatsCard is added to a panel.
        
        // OPTION: I will replace the component in the statsPanel.
        // But I don't have reference to statsPanel easily. 
        
        // Let's just modify the chart for now which is custom.
        studentsByClassChart.setData(data.byClasse);
        
        // For the label, I can't update StatsCard value easily without setter. 
        // I'll skip dynamic update of StatsCard value for this turn to avoid modifying too many files,
        // OR I can re-create the whole panel. 
        // Actually, I'll allow myself to modify StatsCard in a future step if needed, 
        // but for now I'll just set the chart which is the main "Graph" requirement.
        
        // Wait, the user WANTS real data. I MUST show real data.
        // I'll simple repaint the whole dashboard content or just the card?
        // I'll add a updateValue method to StatsCard? No, that's another file edit.
        // I'll just rely on chart. 
        // Actually, I can rebuild the view in done().
        
        Container parent = studentsCard.getParent();
        if(parent != null) {
             int idx = -1;
             for(int i=0; i<parent.getComponentCount(); i++) {
                 if(parent.getComponent(i) == studentsCard) { idx = i; break; }
             }
             if(idx != -1) {
                 parent.remove(idx);
                 studentsCard = new StatsCard("Total Etudiants", data.totalStudents, ColorPalette.PRIMARY_BLUE, "", 0, true);
                 parent.add(studentsCard, idx);
                 parent.revalidate();
                 parent.repaint();
             }
        }
    }

    private static class DashboardData {
        int totalStudents;
        Map<String, Integer> byClasse;
        public DashboardData(int t, Map<String, Integer> m) { this.totalStudents = t; this.byClasse = m; }
    }

    /**
     * Simple custom bar chart component to avoid heavy libraries.
     */
    private static class SimpleBarChart extends JPanel {
        private Map<String, Integer> data;

        public SimpleBarChart() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR));
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        public void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.drawString("Chargement des données...", 20, 30);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int startX = 50;
            int startY = getHeight() - 40;
            int barWidth = 60;
            int gap = 30;
            int maxVal = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);

            // Draw axis
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(startX, startY, getWidth() - 20, startY); // X axis
            g2.drawLine(startX, startY, startX, 20); // Y axis

            int x = startX + 20;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int val = entry.getValue();
                int barHeight = (int) ((double) val / maxVal * (startY - 40));

                // Bar
                g2.setColor(ColorPalette.PRIMARY_BLUE);
                g2.fillRoundRect(x, startY - barHeight, barWidth, barHeight, 5, 5);

                // Label X
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(entry.getKey(), x + (barWidth - g2.getFontMetrics().stringWidth(entry.getKey())) / 2, startY + 20);

                // Value Label
                g2.setColor(Color.BLACK);
                String valStr = String.valueOf(val);
                g2.drawString(valStr, x + (barWidth - g2.getFontMetrics().stringWidth(valStr)) / 2, startY - barHeight - 5);

                x += barWidth + gap;
            }
        }
    }
}
