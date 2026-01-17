package ui.login;

import service.AuthService;
import service.AuthServiceImpl;
import ui.utils.ColorPalette;
import ui.utils.FontPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginDialog extends JDialog {
    private boolean succeeded = false;
    private final AuthService auth = new AuthServiceImpl();

    public LoginDialog(Frame parent) {
        super(parent, "Connexion", true);
        setUndecorated(true);
        setBackground(Color.BLACK);

        // Fullscreen size
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen);
        setLocationRelativeTo(null);

        // Root split: left branding, right form
        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // Left branding panel with gradient
        JPanel branding = new GradientPanel();
        branding.setPreferredSize(new Dimension((int) (screen.width * 0.45), screen.height));
        branding.setLayout(new GridBagLayout());
        JLabel brandTitle = new JLabel("Gestion des PFE");
        brandTitle.setFont(FontPalette.H1);
        brandTitle.setForeground(Color.WHITE);
        JLabel brandSubtitle = new JLabel("Plateforme moderne de gestion de projets de fin d'études");
        brandSubtitle.setFont(FontPalette.BODY);
        brandSubtitle.setForeground(new Color(255,255,255,200));
        Box brandBox = Box.createVerticalBox();
        brandBox.add(brandTitle);
        brandBox.add(Box.createVerticalStrut(8));
        brandBox.add(brandSubtitle);
        branding.add(brandBox, new GridBagConstraints());

        // Right form container
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(ColorPalette.CONTENT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0,0,0,0);

        // Card
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 360));

        JLabel title = new JLabel("Connexion");
        title.setFont(FontPalette.H2);
        title.setForeground(ColorPalette.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(FontPalette.BODY);
        JTextField txtEmail = new JTextField();
        txtEmail.setFont(FontPalette.BODY);
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR),
                new EmptyBorder(8,12,8,12)));

        JLabel lblPassword = new JLabel("Mot de passe");
        lblPassword.setFont(FontPalette.BODY);
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(FontPalette.BODY);
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BORDER_COLOR),
                new EmptyBorder(8,12,8,12)));

        JButton btnLogin = new JButton("Se connecter");
        btnLogin.setFont(FontPalette.BUTTON);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(ColorPalette.PRIMARY_BLUE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnQuit = new JButton("Quitter");
        btnQuit.setFont(FontPalette.BUTTON);
        btnQuit.setForeground(ColorPalette.TEXT_PRIMARY);
        btnQuit.setBackground(ColorPalette.CONTENT_BG);
        btnQuit.setFocusPainted(false);
        btnQuit.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btnQuit.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Actions
        Runnable submit = () -> {
            String email = txtEmail.getText().trim();
            String pw = new String(txtPassword.getPassword());
            if (auth.authenticate(email, pw)) {
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Identifiants invalides",
                        "Échec de la connexion",
                        JOptionPane.ERROR_MESSAGE);
                succeeded = false;
            }
        };

        btnLogin.addActionListener(e -> submit.run());
        btnQuit.addActionListener(e -> System.exit(0));

        KeyAdapter enterSubmits = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) submit.run();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
            }
        };
        txtEmail.addKeyListener(enterSubmits);
        txtPassword.addKeyListener(enterSubmits);

        // Assemble card
        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(lblEmail);
        card.add(Box.createVerticalStrut(6));
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(12));
        card.add(lblPassword);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnLogin);
        actions.add(btnQuit);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(actions);

        right.add(card, gbc);

        root.add(branding, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
    }

    public boolean isSucceeded() { return succeeded; }

    /** Gradient background panel */
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            Color c1 = new Color(24, 24, 27);
            Color c2 = new Color(37, 99, 235);
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }
}
