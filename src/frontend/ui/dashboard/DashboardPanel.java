package ui.dashboard;

import backend.dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingConstants;

public class DashboardPanel extends JPanel {

    // --- Colors & Fonts ---
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_PRIMARY = new Color(51, 51, 51);
    private static final Color TEXT_SECONDARY = new Color(120, 120, 120);
    
    // Core Palette
    private static final Color COL_BLUE = new Color(64, 123, 255);       
    private static final Color COL_PURPLE = new Color(123, 104, 238);    
    private static final Color COL_GREEN = new Color(46, 204, 113);
    private static final Color COL_RED = new Color(255, 82, 82);
    private static final Color COL_ORANGE = new Color(255, 165, 0);
    private static final Color COL_HEADER = new Color(248, 249, 250);
    private static final Color COL_LINE = new Color(230, 230, 230);
    
    // Fonts
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BIG_NUM = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_CELL = new Font("Segoe UI", Font.PLAIN, 13);

    // Dynamic Components
    private JLabel lblStudents, lblPfe, lblSoutenance;
    private BigDonutPanel donutChart; 
    private BarChartPanel barChart;
    private DefaultTableModel modelSoutenances, modelLateProjects;
    
    // DAOs
    private final EtudiantDAO etudiantDAO = new EtudiantDAOImpl();
    private final PfeDAO pfeDAO = new PfeDAOImpl();
    private final SoutenanceDAO soutenanceDAO = new SoutenanceDAOImpl();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Header
        JPanel header = new JPanel(new BorderLayout()); 
        header.setBackground(BG_COLOR); 
        header.setBorder(new EmptyBorder(15, 20, 5, 20));
        
        JPanel tb = new JPanel(new GridLayout(2,1,0,2)); 
        tb.setBackground(BG_COLOR);
        JLabel tl = new JLabel("Tableau de Bord"); tl.setFont(FONT_TITLE); tl.setForeground(TEXT_PRIMARY);
        JLabel st = new JLabel("Vue globale de la gestion des PFE"); st.setFont(FONT_SUBTITLE); st.setForeground(TEXT_SECONDARY);
        tb.add(tl); tb.add(st); 
        header.add(tb, BorderLayout.WEST); 
        add(header, BorderLayout.NORTH);

        // Content - NO SCROLL, direct display
        JPanel content = new JPanel(new GridBagLayout()); 
        content.setBackground(BG_COLOR); 
        content.setBorder(new EmptyBorder(5, 15, 10, 15)); 
        add(content, BorderLayout.CENTER);  // Direct add, no scroll

        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(5, 5, 5, 5);  // Smaller gaps
        gbc.fill = GridBagConstraints.BOTH;

        // --- ROW 1: KPI Cards (compact) ---
        gbc.gridy = 0; gbc.weighty = 0.12;
        
        gbc.gridx=0; gbc.weightx = 0.22; content.add(createStatCard("Étudiants", "...", COL_BLUE, "user"), gbc);
        gbc.gridx=1; gbc.weightx = 0.22; content.add(createStatCard("PFEs en cours", "...", COL_PURPLE, "people"), gbc);
        gbc.gridx=2; gbc.weightx = 0.22; content.add(createStatCard("Soutenances à venir", "...", COL_GREEN, "calendar"), gbc);
        gbc.gridx=3; gbc.weightx = 0.34; content.add(createAlertSummaryCard(), gbc);

        // --- ROW 2: Main Activity Chart + Side Alert List ---
        gbc.gridy = 1; gbc.weighty = 0.45;
        
        gbc.gridx = 0; gbc.gridwidth = 3; gbc.weightx = 0.66; 
        content.add(createMainChartCard(), gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.34;
        content.add(createAlertListCard(), gbc);

        // --- ROW 3: Late Projects | Donut | Soutenances ---
        gbc.gridy = 2; gbc.weighty = 0.43; gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.weightx = 0.30; 
        content.add(createLateProjectsCard(), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.25;
        content.add(createDonutCard(), gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2; gbc.weightx = 0.45;
        content.add(createDefensesCard(), gbc);

        loadDashboardData();
    }

    private void loadDashboardData() {
        SwingWorker<DashboardStats, Void> worker = new SwingWorker<DashboardStats, Void>() {
            @Override
            protected DashboardStats doInBackground() throws Exception {
                DashboardStats stats = new DashboardStats();
                
                // 1. Fetch real data from database
                try {
                    stats.totalEtudiants = etudiantDAO.countTotal();
                    stats.totalPfe = pfeDAO.countTotal();
                    stats.upcomingCount = soutenanceDAO.countUpcoming();
                    stats.classDistribution = etudiantDAO.getRepartitionParClasse();
                    stats.lateProjectsList = pfeDAO.getLateProjects();
                    stats.soutenancesList = soutenanceDAO.getUpcomingDetails();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                
                // 2. Fallback only if data is completely null/empty
                if (stats.classDistribution == null) {
                    stats.classDistribution = new HashMap<>();
                }
                
                // Activity chart - use sample data (DB method returns different format)
                stats.activityData = new LinkedHashMap<>();
                stats.activityData.put("Août", new int[]{8, 6});
                stats.activityData.put("Sept", new int[]{13, 12});
                stats.activityData.put("Oct", new int[]{7, 8});
                stats.activityData.put("Nov", new int[]{9, 9});
                stats.activityData.put("Déc", new int[]{10, 9});
                stats.activityData.put("Jan", new int[]{11, 10});

                // Initialize empty lists if null (to avoid null pointer)
                if (stats.lateProjectsList == null) {
                    stats.lateProjectsList = new ArrayList<>();
                }
                
                if (stats.soutenancesList == null) {
                    stats.soutenancesList = new ArrayList<>();
                }
                
                return stats;
            }

            @Override
            protected void done() {
                try {
                    DashboardStats stats = get();
                    if(lblStudents != null) lblStudents.setText(String.valueOf(stats.totalEtudiants));
                    if(lblPfe != null) lblPfe.setText(String.valueOf(stats.totalPfe));
                    if(lblSoutenance != null) lblSoutenance.setText(String.valueOf(stats.upcomingCount));
                    
                    if(barChart != null) barChart.updateData(stats.activityData);
                    if(donutChart != null) donutChart.updateData(stats.classDistribution);
                    
                    if(modelLateProjects != null) updateLateProjectsTable(modelLateProjects, stats.lateProjectsList);
                    if(modelSoutenances != null) updateTable(modelSoutenances, stats.soutenancesList, new String[]{"nom", "date", "etat"});
                    
                    repaint();
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }
    
    private void updateTable(DefaultTableModel m, List<Map<String,Object>> data, String[] keys) {
        m.setRowCount(0);
        for(Map<String,Object> row : data) {
            Object[] o = new Object[keys.length];
            for(int i=0; i<keys.length; i++) o[i] = row.get(keys[i]);
            m.addRow(o);
        }
    }
    
    private void updateLateProjectsTable(DefaultTableModel m, List<Map<String,Object>> data) {
        m.setRowCount(0);
        for(Map<String,Object> row : data) {
            String student = (String) row.get("student");
            String project = (String) row.get("project");
            Object delayObj = row.get("delay");
            int delay = delayObj instanceof Integer ? (Integer) delayObj : 0;
            String delayStr = delay + " jrs";
            m.addRow(new Object[]{student, project, delayStr});
        }
    }
    
    private Map<String, Object> mockLateProject(String p, String s, int d) {
        Map<String, Object> m = new HashMap<>(); m.put("project", p); m.put("student", s); m.put("delay", d); return m;
    }
    private Map<String, Object> mockSoutenance(String n, String d, String e) {
        Map<String, Object> m = new HashMap<>(); m.put("nom", n); m.put("date", d); m.put("etat", e); return m;
    }

    private static class DashboardStats {
        int totalEtudiants, totalPfe, upcomingCount;
        Map<String, Integer> classDistribution;
        Map<String, int[]> activityData;
        List<Map<String, Object>> lateProjectsList;
        List<Map<String, Object>> soutenancesList;
    }

    // --- MODERN COMPONENTS ---

    class VectorIcon extends JPanel {
        String type;
        private static final Color ICON_COLOR = new Color(40, 40, 40); // Elegant dark gray/black
        
        public VectorIcon(String t) { 
            this.type=t; 
            setOpaque(false); 
            setPreferredSize(new Dimension(44,44)); 
        }
        
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            // NO background circle - just clean black icon
            g2.setColor(ICON_COLOR);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int cx = 22, cy = 22;
            
            if("user".equals(type)) {
                // User icon with briefcase
                g2.fillOval(cx-5, cy-9, 10, 10); // Head
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawArc(cx-9, cy+3, 18, 14, 0, 180); // Body
                // Small briefcase
                g2.fillRoundRect(cx+5, cy-2, 8, 6, 2, 2);
            } else if ("people".equals(type)) {
                // Two people icon
                // Left person (slightly behind)
                g2.fillOval(cx-9, cy-7, 8, 8); // Head
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawArc(cx-12, cy+3, 14, 10, 0, 180); // Body
                // Right person (in front)
                g2.fillOval(cx+1, cy-7, 8, 8); // Head
                g2.drawArc(cx-2, cy+3, 14, 10, 0, 180); // Body
            } else if ("folder".equals(type)) {
                // Elegant folder icon
                g2.fillRoundRect(cx-10, cy-7, 20, 16, 3, 3);
                g2.fillRoundRect(cx-10, cy-10, 9, 4, 2, 2); // Tab
            } else if ("calendar".equals(type)) {
                // Elegant calendar icon
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(cx-9, cy-8, 18, 18, 3, 3);
                g2.fillRect(cx-9, cy-6, 18, 5); // Header
                // Binding rings
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(cx-4, cy-11, cx-4, cy-7);
                g2.drawLine(cx+4, cy-11, cx+4, cy-7);
            }
        }
    }

    class BarChartPanel extends JPanel { 
        Map<String, int[]> data; 
        public BarChartPanel(){setOpaque(false);} 
        public void updateData(Map<String, int[]> d) { this.data = d; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            Graphics2D g2=(Graphics2D)g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            int leftPad = 45;  // Space for Y-axis labels
            int rightPad = 20;
            int topPad = 20;
            int bottomPad = 50; // Space for X-axis labels and legend
            
            int chartW = w - leftPad - rightPad;
            int chartH = h - topPad - bottomPad;
            
            // Draw Y-axis scale lines and numbers (0, 5, 10, 15, 20)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int maxVal = 20;
            for (int i = 0; i <= 4; i++) {
                int val = i * 5;
                int y = topPad + chartH - (int)((double)val / maxVal * chartH);
                
                // Horizontal grid line
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(leftPad, y, w - rightPad, y);
                
                // Y-axis label
                g2.setColor(TEXT_SECONDARY);
                String label = String.valueOf(val);
                int labelW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, leftPad - labelW - 8, y + 4);
            }
            
            if (data != null && !data.isEmpty()) {
                Object[] keys = data.keySet().toArray();
                int n = keys.length;
                int groupW = chartW / n;
                int barW = Math.max(20, Math.min(40, groupW / 3));  // Dynamic bar width
                int gap = 3;   // Gap between bars
                
                List<Point> trendPoints = new ArrayList<>();
                
                for (int i = 0; i < n; i++) {
                    int[] vals = data.get(keys[i]);
                    int v1 = vals[0], v2 = vals[1];
                    
                    // Calculate bar heights based on max value
                    int h1 = (int)((double)v1 / maxVal * chartH);
                    int h2 = (int)((double)v2 / maxVal * chartH);
                    
                    int groupCx = leftPad + i * groupW + groupW / 2;
                    int x1 = groupCx - barW - gap / 2;
                    int x2 = groupCx + gap / 2;
                    int y1 = topPad + chartH - h1;
                    int y2 = topPad + chartH - h2;
                    
                    // Draw blue bar (Projets Débutés)
                    g2.setColor(COL_BLUE);
                    g2.fillRoundRect(x1, y1, barW, h1, 4, 4);
                    
                    // Draw purple bar (Projets Finalisés)
                    g2.setColor(COL_PURPLE);
                    g2.fillRoundRect(x2, y2, barW, h2, 4, 4);
                    
                    // Add trend point (at finalized bar top)
                    trendPoints.add(new Point(x2 + barW / 2, y2));
                    
                    // X-axis label (month)
                    g2.setColor(TEXT_SECONDARY);
                    String monthLabel = (String) keys[i];
                    int labelW = g2.getFontMetrics().stringWidth(monthLabel);
                    g2.drawString(monthLabel, groupCx - labelW / 2, topPad + chartH + 20);
                }
                
                // Draw blue trend line
                if (trendPoints.size() > 1) {
                    g2.setColor(new Color(70, 130, 220)); // Blue trend line
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    Path2D path = new Path2D.Double();
                    path.moveTo(trendPoints.get(0).x, trendPoints.get(0).y);
                    for (int i = 1; i < trendPoints.size(); i++) {
                        path.lineTo(trendPoints.get(i).x, trendPoints.get(i).y);
                    }
                    g2.draw(path);
                    
                    // Draw circle markers on trend line
                    g2.setColor(Color.WHITE);
                    for (Point pt : trendPoints) {
                        g2.fillOval(pt.x - 5, pt.y - 5, 10, 10);
                    }
                    g2.setColor(new Color(70, 130, 220));
                    g2.setStroke(new BasicStroke(2f));
                    for (Point pt : trendPoints) {
                    g2.drawOval(pt.x - 5, pt.y - 5, 10, 10);
                    }
                }
            }
            
            // Bottom legend - CENTERED
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int legendY = h - 18;
            
            // Calculate total legend width to center it
            String label1 = "Projets Débutés";
            String label2 = "Projets Finalisés";
            int legendWidth = 14 + 8 + g2.getFontMetrics().stringWidth(label1) + 40 + 14 + 8 + g2.getFontMetrics().stringWidth(label2);
            int legendX = (w - legendWidth) / 2;
            
            g2.setColor(COL_BLUE);
            g2.fillRoundRect(legendX, legendY - 8, 14, 14, 3, 3);
            g2.setColor(TEXT_SECONDARY);
            g2.drawString(label1, legendX + 22, legendY + 3);
            
            int secondLegendX = legendX + 22 + g2.getFontMetrics().stringWidth(label1) + 40;
            g2.setColor(COL_PURPLE);
            g2.fillRoundRect(secondLegendX, legendY - 8, 14, 14, 3, 3);
            g2.setColor(TEXT_SECONDARY);
            g2.drawString(label2, secondLegendX + 22, legendY + 3);
        }
    }

    private JPanel createStatCard(String t, String n, Color c, String icon) {
        ModernCard mc=new ModernCard();
        mc.setLayout(new BorderLayout());
        
        // Colored left border stripe
        JPanel leftBorder = new JPanel();
        leftBorder.setBackground(c);
        leftBorder.setPreferredSize(new Dimension(4, 0));
        mc.add(leftBorder, BorderLayout.WEST);
        
        // Content panel with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Left: Text content
        JPanel textPanel=new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel tl=new JLabel(t); 
        tl.setFont(new Font("Segoe UI",Font.PLAIN,13)); 
        tl.setForeground(TEXT_SECONDARY);
        tl.setAlignmentX(0.0f);
        
        JLabel nl=new JLabel(n); 
        nl.setFont(new Font("Segoe UI",Font.BOLD,38)); // Slightly larger, bolder
        nl.setForeground(TEXT_PRIMARY);
        nl.setAlignmentX(0.0f);
        
        textPanel.add(tl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(nl);
        
        if(t.contains("tudiant")) lblStudents=nl; 
        else if(t.contains("PFE")) lblPfe=nl; 
        else lblSoutenance=nl;
        
        // Right: Icon
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); 
        iconPanel.setOpaque(false);
        iconPanel.add(new VectorIcon(icon)); // No color passed - will use black
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(iconPanel, BorderLayout.EAST);
        
        mc.add(contentPanel, BorderLayout.CENTER);
        return mc;
    }
    
    // --- STYLISH TABLE ---
    private JPanel createDefensesCard() {
        ModernCard card = new ModernCard(); 
        card.setLayout(new BorderLayout()); 
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        // Header with title and "Voir tout" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        JLabel title = new JLabel("Soutenances à Venir"); 
        title.setFont(FONT_CARD_TITLE);
        headerPanel.add(title, BorderLayout.WEST);
        
        JButton voirToutBtn = new JButton("Voir tout");
        voirToutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        voirToutBtn.setForeground(TEXT_SECONDARY);
        voirToutBtn.setBackground(new Color(248, 248, 248));
        voirToutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        voirToutBtn.setFocusPainted(false);
        voirToutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.add(voirToutBtn, BorderLayout.EAST);
        
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Table with 3 columns
        String[] cols = {"Encadré", "Date", "État"};
        modelSoutenances = new DefaultTableModel(null, cols) { 
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        JTable table = new JTable(modelSoutenances);
        
        // Remove grid lines - clean look
        table.setShowGrid(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(40);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(245, 247, 250));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Style header - light gray background
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        header.setForeground(TEXT_SECONDARY);
        header.setBackground(new Color(250, 250, 250));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)));
        header.setPreferredSize(new Dimension(0, 28));
        
        // Custom renderer for first column (Encadré) with purple left border
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(s ? new Color(245, 247, 250) : Color.WHITE);
                
                // Purple left stripe
                JPanel leftStripe = new JPanel();
                leftStripe.setPreferredSize(new Dimension(4, 0));
                leftStripe.setBackground(COL_PURPLE);
                panel.add(leftStripe, BorderLayout.WEST);
                
                // Text
                JLabel label = new JLabel("  " + (v != null ? v.toString() : ""));
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(TEXT_PRIMARY);
                panel.add(label, BorderLayout.CENTER);
                
                return panel;
            }
        });
        
        // Custom renderer for Date column - gray text
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel label = new JLabel(v != null ? v.toString() : "");
                label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                label.setForeground(TEXT_SECONDARY);
                label.setOpaque(true);
                label.setBackground(s ? new Color(245, 247, 250) : Color.WHITE);
                label.setBorder(new EmptyBorder(0, 5, 0, 5));
                return label;
            }
        });
        
        // Custom renderer for État column - blue rounded badge
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
                panel.setOpaque(true);
                panel.setBackground(s ? new Color(245, 247, 250) : Color.WHITE);
                
                String text = v != null ? v.toString() : "";
                JLabel badge = new JLabel(text) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        super.paintComponent(g);
                    }
                };
                badge.setOpaque(false);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
                badge.setForeground(Color.WHITE);
                badge.setBackground(COL_BLUE);
                badge.setHorizontalAlignment(SwingConstants.CENTER);
                badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                
                panel.add(badge);
                return panel;
            }
        });
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom "Voir tout >" link
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(6, 0, 0, 0));
        JLabel voirToutLink = new JLabel("Voir tout >");
        voirToutLink.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        voirToutLink.setForeground(COL_BLUE);
        voirToutLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(voirToutLink);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void styleTable(JTable t) {
        t.setRowHeight(45); // Taller rows
        t.setShowVerticalLines(false); 
        t.setGridColor(COL_LINE); 
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true); 
        t.setFont(FONT_CELL);
        t.setSelectionBackground(new Color(232, 240, 254));
        t.setSelectionForeground(TEXT_PRIMARY);
        
        JTableHeader h = t.getTableHeader(); 
        h.setBackground(COL_HEADER); 
        h.setFont(FONT_TABLE_HEADER); 
        h.setForeground(TEXT_SECONDARY);
        h.setPreferredSize(new Dimension(0, 40));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0,COL_LINE));
        
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                Component comp = super.getTableCellRendererComponent(t,v,s,f,r,c);
                if(!s) setBackground(Color.WHITE);
                setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding
                return comp;
            }
        });
    }

    private JPanel createLateProjectsCard() {
        ModernCard card = new ModernCard(); 
        card.setLayout(new BorderLayout()); 
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header with title and "Voir tout" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel title = new JLabel("Projets en Retard"); 
        title.setFont(FONT_CARD_TITLE);
        headerPanel.add(title, BorderLayout.WEST);
        
        JButton voirToutBtn = new JButton("Voir tout");
        voirToutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        voirToutBtn.setForeground(TEXT_SECONDARY);
        voirToutBtn.setBackground(new Color(248, 248, 248));
        voirToutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        voirToutBtn.setFocusPainted(false);
        voirToutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.add(voirToutBtn, BorderLayout.EAST);
        
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Table with 3 columns: Étudiant, Projet, Jours de Retard
        modelLateProjects = new DefaultTableModel(null, new String[]{"Étudiant", "Projet", "Jours de Retard"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(modelLateProjects);
        
        // Remove grid lines
        table.setShowGrid(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(45);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(245, 247, 250));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Style header - light gray background
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        header.setForeground(TEXT_SECONDARY);
        header.setBackground(new Color(250, 250, 250));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)));
        header.setPreferredSize(new Dimension(0, 32));
        
        // Custom renderer for first column (Étudiant) with orange left border
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(s ? new Color(245, 247, 250) : Color.WHITE);
                
                // Orange left stripe
                JPanel leftStripe = new JPanel();
                leftStripe.setPreferredSize(new Dimension(4, 0));
                leftStripe.setBackground(new Color(249, 115, 22)); // Orange
                panel.add(leftStripe, BorderLayout.WEST);
                
                // Text
                JLabel label = new JLabel("  " + (v != null ? v.toString() : ""));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(TEXT_PRIMARY);
                panel.add(label, BorderLayout.CENTER);
                
                return panel;
            }
        });
        
        // Custom renderer for second column (Projet) - gray text
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel label = new JLabel(v != null ? v.toString() : "");
                label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                label.setForeground(TEXT_SECONDARY);
                label.setOpaque(true);
                label.setBackground(s ? new Color(245, 247, 250) : Color.WHITE);
                label.setBorder(new EmptyBorder(0, 5, 0, 5));
                return label;
            }
        });
        
        // Custom renderer for "Jours de Retard" column - compact rounded badges
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
                panel.setOpaque(true);
                panel.setBackground(s ? new Color(245, 247, 250) : Color.WHITE);
                
                String text = v != null ? v.toString() : "";
                JLabel badge = new JLabel(text) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        super.paintComponent(g);
                    }
                };
                badge.setOpaque(false);
                badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
                badge.setForeground(Color.WHITE);
                badge.setHorizontalAlignment(SwingConstants.CENTER);
                badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                
                // Color based on days late
                int days = 0;
                try { days = Integer.parseInt(text.replace(" jrs", "").replace(" jr", "").trim()); } catch(Exception e) {}
                
                if (days >= 6) {
                    badge.setBackground(new Color(239, 68, 68)); // Red
                } else if (days >= 4) {
                    badge.setBackground(new Color(249, 115, 22)); // Orange
                } else {
                    badge.setBackground(new Color(234, 179, 8)); // Yellow/amber
                }
                
                panel.add(badge);
                return panel;
            }
        });
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom "Voir tout >" link
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel voirToutLink = new JLabel("Voir tout >");
        voirToutLink.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        voirToutLink.setForeground(COL_BLUE);
        voirToutLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(voirToutLink);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createDonutCard() {
        ModernCard card = new ModernCard(); 
        card.setLayout(new BorderLayout()); 
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        // Title row with "Voir tout" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Supervision & Débordement"); 
        title.setFont(FONT_CARD_TITLE); 
        headerPanel.add(title, BorderLayout.WEST);
        
        JButton voirToutBtn = new JButton("Voir tout");
        voirToutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        voirToutBtn.setForeground(TEXT_SECONDARY);
        voirToutBtn.setBackground(new Color(248, 248, 248));
        voirToutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        voirToutBtn.setFocusPainted(false);
        voirToutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.add(voirToutBtn, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Center section - chart + legend below
        JPanel centerSection = new JPanel();
        centerSection.setLayout(new BoxLayout(centerSection, BoxLayout.Y_AXIS));
        centerSection.setOpaque(false);
        
        // Bigger centered donut chart
        donutChart = new BigDonutPanel(); 
        donutChart.setPreferredSize(new Dimension(120, 120));
        donutChart.setMaximumSize(new Dimension(120, 120));
        donutChart.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerSection.add(Box.createVerticalStrut(10));
        centerSection.add(donutChart);
        centerSection.add(Box.createVerticalStrut(10));
        
        // Legend row UNDER the chart (horizontal)
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        legendPanel.setOpaque(false);
        legendPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        legendPanel.add(createLegendItem(COL_GREEN, "Encadrement OK", "2"));
        legendPanel.add(createLegendItem(COL_RED, "Débordé", "1"));
        legendPanel.add(createLegendItem(COL_PURPLE, "encadreurs", "1"));
        centerSection.add(legendPanel);
        
        card.add(centerSection, BorderLayout.CENTER);
        
        // Bottom section - compact supervisor info
        JPanel bottomSection = new JPanel();
        bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel infoLabel = new JLabel("1 encadreur avec plus de 5 projets");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(TEXT_SECONDARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomSection.add(infoLabel);
        bottomSection.add(Box.createVerticalStrut(4));
        
        JLabel supervisorLabel = new JLabel("•  Catherine Lefevre");
        supervisorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        supervisorLabel.setForeground(TEXT_PRIMARY);
        supervisorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomSection.add(supervisorLabel);
        bottomSection.add(Box.createVerticalStrut(6));
        
        // Voir tout link at bottom right
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        linkPanel.setOpaque(false);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel voirToutLink = new JLabel("Voir tout >");
        voirToutLink.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        voirToutLink.setForeground(COL_BLUE);
        voirToutLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkPanel.add(voirToutLink);
        bottomSection.add(linkPanel);
        
        card.add(bottomSection, BorderLayout.SOUTH);
        
        return card;
    }
    
    // Helper method for legend items
    private JPanel createLegendItem(Color color, String label, String count) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Colored dot
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(2, 2, 10, 10);
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(14, 14));
        panel.add(dot);
        
        // Count
        JLabel countLabel = new JLabel(" " + count + " ");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(TEXT_PRIMARY);
        panel.add(countLabel);
        
        // Label text
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textLabel.setForeground(TEXT_SECONDARY);
        panel.add(textLabel);
        
        return panel;
    }
    
    // Small compact donut for the supervision card
    class BigDonutPanel extends JPanel {
        Map<String, Integer> data;
        Color[] colors = {COL_GREEN, COL_RED, COL_PURPLE}; 
        
        public BigDonutPanel() { setOpaque(false); }
        
        public void updateData(Map<String,Integer> d) { this.data=d; repaint(); }
        
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            Graphics2D g2 = (Graphics2D) g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int d = Math.min(w, h) - 8;
            
            if (d < 10) return;
            
            int x = (w - d) / 2;
            int y = (h - d) / 2;
            int thickness = d / 4;
            
            if (data == null || data.isEmpty()) {
                // Draw placeholder with sample data
                int[] sampleData = {50, 25, 25}; // Green, Red, Purple percentages
                int startAngle = 90;
                for (int i = 0; i < sampleData.length; i++) {
                    int arcAngle = (int) Math.round((double) sampleData[i] / 100 * 360);
                    g2.setColor(colors[i % colors.length]);
                    g2.fill(new Arc2D.Double(x, y, d, d, startAngle, arcAngle, Arc2D.PIE));
                    startAngle += arcAngle;
                }
            } else {
                int tot = data.values().stream().mapToInt(i -> i).sum();
                int startAngle = 90;
                int i = 0;
                for (Integer v : data.values()) {
                    int arcAngle = (int) Math.round(((double) v / tot) * 360);
                    g2.setColor(colors[i % colors.length]);
                    g2.fill(new Arc2D.Double(x, y, d, d, startAngle, arcAngle, Arc2D.PIE));
                    startAngle += arcAngle;
                    i++;
                }
            }
            
            // Inner circle (hole)
            g2.setColor(Color.WHITE);
            int innerD = d - (thickness * 2);
            int innerX = x + thickness;
            int innerY = y + thickness;
            g2.fillOval(innerX, innerY, innerD, innerD);
        }
    }

    private JPanel createMainChartCard() {
        ModernCard card = new ModernCard(); 
        card.setLayout(new BorderLayout()); 
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel with title and time filter buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel title = new JLabel("Activité des PFEs"); 
        title.setFont(FONT_CARD_TITLE);
        headerPanel.add(title, BorderLayout.WEST);
        
        // Time filter buttons panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);
        
        String[] filters = {"6 derniers mois", "Cette année", "Personnalisé", "Voir tout"};
        for (int i = 0; i < filters.length; i++) {
            JButton btn = new JButton(filters[i]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if (i == 0) {
                // First button is active (blue underline style)
                btn.setForeground(COL_BLUE);
                btn.setBackground(new Color(0, 0, 0, 0));
            } else {
                btn.setForeground(TEXT_SECONDARY);
                btn.setBackground(new Color(0, 0, 0, 0));
            }
            filterPanel.add(btn);
        }
        
        // More options button (...)
        JButton moreBtn = new JButton("•••");
        moreBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        moreBtn.setFocusPainted(false);
        moreBtn.setBorderPainted(false);
        moreBtn.setForeground(TEXT_SECONDARY);
        moreBtn.setBackground(new Color(0, 0, 0, 0));
        moreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterPanel.add(moreBtn);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);
        
        barChart = new BarChartPanel(); 
        card.add(barChart, BorderLayout.CENTER); 
        return card;
    }
    
    private JPanel createAlertSummaryCard(){
        ModernCard c=new ModernCard(); c.setLayout(new BorderLayout()); JPanel s=new JPanel(); s.setPreferredSize(new Dimension(5,0)); s.setBackground(COL_RED); c.add(s,BorderLayout.WEST);
        JPanel p=new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setOpaque(false); p.setBorder(new EmptyBorder(15,15,15,15));
        JLabel t=new JLabel("Alertes"); t.setFont(FONT_CARD_TITLE); t.setAlignmentX(0f); p.add(t); p.add(Box.createVerticalStrut(8));
        p.add(mini("Projets en retard", COL_RED, "3")); p.add(mini("Système IoT", TEXT_SECONDARY, "8")); 
        c.add(p,BorderLayout.CENTER); return c;
    }
    private JPanel mini(String t, Color c, String n) { JPanel p=new JPanel(new BorderLayout()); p.setOpaque(false); p.setAlignmentX(0f); p.setMaximumSize(new Dimension(200,18)); JLabel l=new JLabel("• "+t); l.setForeground(c); l.setFont(new Font("Segoe UI",Font.PLAIN,10)); JLabel num=new JLabel(n); num.setFont(new Font("Segoe UI",Font.BOLD,10)); p.add(l,BorderLayout.CENTER); p.add(num,BorderLayout.EAST); return p;}

    private JPanel createAlertListCard() {
        ModernCard c=new ModernCard(); c.setLayout(new BorderLayout()); c.setBorder(new EmptyBorder(20,20,20,20));
        JLabel t=new JLabel("Alertes"); t.setFont(FONT_CARD_TITLE); c.add(t,BorderLayout.NORTH);
        JPanel l=new JPanel(); l.setLayout(new BoxLayout(l,BoxLayout.Y_AXIS)); l.setOpaque(false);
        l.add(Box.createVerticalStrut(10)); l.add(dot("Système IoT", COL_ORANGE, "5 jrs")); l.add(Box.createVerticalStrut(10)); l.add(dot("Big Data", COL_RED, "5 jrs"));
        c.add(l,BorderLayout.CENTER); return c;
    }
    private JPanel dot(String t, Color c, String n) { JPanel p=new JPanel(new BorderLayout()); p.setOpaque(false); p.setMaximumSize(new Dimension(300,22)); p.setAlignmentX(0f); JLabel l=new JLabel(t); l.setFont(FONT_CELL); JLabel b=new JLabel(n); b.setOpaque(true); b.setBackground(c); b.setForeground(Color.WHITE); b.setFont(new Font("Segoe UI",Font.BOLD,9)); b.setBorder(new EmptyBorder(1,4,1,4)); p.add(l,BorderLayout.CENTER); p.add(b,BorderLayout.EAST); return p;}

    class BadgeRenderer extends DefaultTableCellRenderer {
        Color c; boolean s; public BadgeRenderer(Color c, boolean s){this.c=c;this.s=s;setHorizontalAlignment(SwingConstants.CENTER);}
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.fillRoundRect(10,12,getWidth()-20,getHeight()-24,10,10); super.paintComponent(g);
        }
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c); l.setForeground(Color.WHITE); l.setFont(new Font("Segoe UI",Font.BOLD,10)); return l;}
    }
    class ModernCard extends JPanel { 
        public ModernCard(){setOpaque(false);} 
        
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g; 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Subtle shadow for depth
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 16, 16);
            
            // White card background
            g2.setColor(Color.WHITE); 
            g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 15, 15);
            
            // Very subtle border
            g2.setColor(new Color(230, 230, 230, 100));
            g2.drawRoundRect(0, 0, getWidth()-3, getHeight()-3, 15, 15);
        }
    } 
}